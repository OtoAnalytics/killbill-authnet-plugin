/*
 *  Copyright 2016 Womply
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.womply.billing.killbill.plugins;

import com.womply.billing.killbill.plugins.authentication.AuthorizeNetAuthenticationService;
import com.womply.billing.killbill.plugins.db.AuthorizeNetDAO;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetPaymentMethodsRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;
import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentMethod;
import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentTransactionInfo;
import com.womply.billing.killbill.plugins.models.AuthorizeNetTransactionInfo;
import com.womply.billing.killbill.plugins.transaction.RefundPaymentHelper;
import com.womply.killbill.resources.models.PaymentGatewayAccount;

import net.authorize.api.contract.v1.ANetApiResponse;
import net.authorize.api.contract.v1.AuthenticateTestRequest;
import net.authorize.api.contract.v1.AuthenticateTestResponse;
import net.authorize.api.contract.v1.CreateCustomerProfileRequest;
import net.authorize.api.contract.v1.CreateCustomerProfileResponse;
import net.authorize.api.contract.v1.CustomerProfileType;
import net.authorize.api.contract.v1.DeleteCustomerPaymentProfileRequest;
import net.authorize.api.contract.v1.DeleteCustomerPaymentProfileResponse;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.MessagesType;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import net.authorize.api.controller.AuthenticateTestController;
import net.authorize.api.controller.CreateCustomerProfileController;
import net.authorize.api.controller.DeleteCustomerPaymentProfileController;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.payment.plugin.api.PaymentTransactionInfoPlugin;
import org.killbill.billing.tenant.api.Tenant;
import org.killbill.billing.tenant.api.TenantApiException;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.billing.util.customfield.CustomField;
import org.osgi.service.log.LogService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Helper class for Authorize Net Plugin Api.
 */
public class AuthorizeNetService {

    public static final String AUTH_NET_CUSTOMER_PROFILE_ID = "auth-net-profile-id";
    public static final String AUTH_NET_CUSTOMER_ID_PREFIX = "W_";

    private final OSGIKillbillAPI osgiKillbillAPI;
    private final OSGIKillbillLogService logService;
    private final AuthorizeNetDAO dao;
    private final AuthorizeNetAuthenticationService authenticationService;
    private final AuthorizeNetTransactionService transactionService;

    public AuthorizeNetService(OSGIKillbillAPI osgiKillbillAPI, OSGIKillbillLogService logService,
                               AuthorizeNetDAO dao, AuthorizeNetAuthenticationService authenticationService,
                               AuthorizeNetTransactionService transactionService) {
        this.osgiKillbillAPI = osgiKillbillAPI;
        this.logService = logService;
        this.dao = dao;
        this.authenticationService = authenticationService;
        this.transactionService = transactionService;
    }

    /**
     * Creates a Customer Profile in Authorize.Net with the given accountData.
     */
    public String addCustomerProfile(String tenantApiKey, PaymentGatewayAccount accountData)
            throws TenantApiException {
        Tenant tenant = osgiKillbillAPI.getTenantUserApi().getTenantByApiKey(tenantApiKey);
        UUID tenantId = tenant.getId();
        MerchantAuthenticationType authentication = authenticationService.getAuthenticationForTenant(tenantId);

        long merchantLocationId = accountData.getMerchantLocationId();
        String merchantCustomerId = AUTH_NET_CUSTOMER_ID_PREFIX + merchantLocationId;
        CustomerProfileType customerProfile = new CustomerProfileType();
        customerProfile.setMerchantCustomerId(merchantCustomerId);

        CreateCustomerProfileRequest apiRequest = getCreateCustomerProfileRequest();
        apiRequest.setMerchantAuthentication(authentication);
        apiRequest.setProfile(customerProfile);

        CreateCustomerProfileController controller = getCreateCustomerProfileController(apiRequest);
        controller.execute();
        CreateCustomerProfileResponse response = controller.getApiResponse();

        verifyAuthNetResponseLogErrors("Customer Profile creation for " + merchantLocationId, response);

        String customerProfileId = response.getCustomerProfileId();
        dao.logCustomerProfileCreation(merchantCustomerId, customerProfileId, tenantId);
        return customerProfileId;
    }

    protected void verifyAuthNetResponseLogErrors(String authorizeNetOperation, ANetApiResponse response) {
        if (response == null) {
            throw new RuntimeException("Got NULL response from Authorize.Net on " + authorizeNetOperation);
        }

        if (response.getMessages().getResultCode() != MessageTypeEnum.OK) {
            logService.log(LogService.LOG_ERROR,
                    "Authorize.Net Failure on " + authorizeNetOperation);
            List<String> errorCodes = new ArrayList<>();
            for (MessagesType.Message message : response.getMessages().getMessage()) {
                logService.log(LogService.LOG_ERROR, "Authorize.Net Failure on " + authorizeNetOperation + ": "
                        + message.getText() + " Error code: " + message.getCode());
                errorCodes.add(message.getCode());
            }
            throw new RuntimeException("Authorize.Net Failure on " + authorizeNetOperation +
                    ". Authorize.Net Error Codes: " + errorCodes.toString());
        }
    }

    // hook for the tests
    protected CreateCustomerProfileRequest getCreateCustomerProfileRequest() {
        return new CreateCustomerProfileRequest();
    }

    //hook for the tests
    protected CreateCustomerProfileController getCreateCustomerProfileController(
            CreateCustomerProfileRequest apiRequest) {
        return new CreateCustomerProfileController(apiRequest);
    }

    /**
     * Add a payment method. Persists the given information to DB.
     */
    public void addPaymentMethod(final UUID kbAccountId, final UUID kbPaymentMethodId,
                                 final boolean setDefault,
                                 final Map<String, String> properties, final CallContext context)
            throws SQLException {

        // get auth-net customer profile id
        List<CustomField> customFields =
                osgiKillbillAPI.getCustomFieldUserApi().getCustomFieldsForAccount(kbAccountId, context);
        String authNetCustomerProfileId = getAuthNetCustomerProfileIdFromCustomFields(kbAccountId, customFields);

        dao.addPaymentMethod(kbAccountId, kbPaymentMethodId, setDefault, authNetCustomerProfileId,
                properties, context.getTenantId());
    }

    protected String getAuthNetCustomerProfileIdFromCustomFields(final UUID kbAccountId,
                                                                 final List<CustomField> customFields) {
        for (CustomField field : customFields) {
            if (AUTH_NET_CUSTOMER_PROFILE_ID.equals(field.getFieldName())) {
                return field.getFieldValue();
            }
        }
        throw new RuntimeException("Can not find custom field \"" + AUTH_NET_CUSTOMER_PROFILE_ID + "\" " +
                        "for account id = " + kbAccountId.toString());
    }

    /**
     * @return AuthorizeNetPaymentMethod representation of the payment method corresponding to the given
     *      account id, payment method id and tenant id.
     */
    public AuthorizeNetPaymentMethod getPaymentMethod(final UUID kbAccountId, final UUID kbPaymentMethodId,
                                                      final UUID tenantId, boolean rawColumnNames) {
        Map<String, Object> paymentMethodData = dao.getPaymentMethod(kbAccountId, kbPaymentMethodId,
                tenantId, rawColumnNames);

        return new AuthorizeNetPaymentMethod(paymentMethodData);
    }

    /**
     * Perform the requested charge with Authorize.Net.
     * @return resulting transaction information
     */
    public AuthorizeNetPaymentTransactionInfo purchasePayment(final UUID kbTenantId, final UUID kbAccountId,
                                                              final UUID kbPaymentId, final UUID kbTransactionId,
                                                              final UUID kbPaymentMethodId, final BigDecimal amount,
                                                              final Currency currency)
            throws TenantApiException {

        final MerchantAuthenticationType authentication = authenticationService.getAuthenticationForTenant(kbTenantId);
        AuthorizeNetPaymentMethodsRecord paymentMethod = dao.getPaymentMethodForOperation(kbAccountId,
                kbPaymentMethodId, kbTenantId);

        AuthorizeNetTransactionInfo transaction = new AuthorizeNetTransactionInfo();
        transaction.setCustomerProfileId(paymentMethod.getAuthorizeNetCustomerProfileId());
        transaction.setCustomerPaymentProfileId(paymentMethod.getAuthorizeNetPaymentProfileId());
        transaction.setKbAccountId(kbAccountId);
        transaction.setKbPaymentId(kbPaymentId);
        transaction.setKbPaymentMethodId(kbPaymentMethodId);
        transaction.setKbTransactionId(kbTransactionId);
        transaction.setKbTransactionType(TransactionType.PURCHASE);
        transaction.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION);
        transaction.setTenantId(kbTenantId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);

        return transactionService.createTransactionOnPaymentProfile(transaction, authentication);
    }

    /**
     * Perform the requested refund on an existing transaction with Authorize.Net.
     * @return resulting transaction information
     */
    public AuthorizeNetPaymentTransactionInfo refundPayment(final UUID kbTenantId, final UUID kbAccountId,
                                                              final UUID kbPaymentId, final UUID kbTransactionId,
                                                              final UUID kbPaymentMethodId, final BigDecimal amount,
                                                              final Currency currency)
            throws TenantApiException {

        RefundPaymentHelper helper = getNewRefundPaymentHelper();
        return helper.refundPayment(kbTenantId, kbAccountId, kbPaymentId, kbTransactionId, kbPaymentMethodId,
                amount, currency);
    }

    protected RefundPaymentHelper getNewRefundPaymentHelper() {
        return new RefundPaymentHelper(dao, authenticationService, transactionService, logService);
    }

    /**
     * @return A list of transactions for the given <code>kbPaymentId</code>
     */
    public List<PaymentTransactionInfoPlugin> getPaymentInfo(final UUID kbAccountId, final UUID kbPaymentId) {
        List<AuthorizeNetTransactionsRecord> records = dao.getTransactionsForPayment(kbAccountId, kbPaymentId);
        List<PaymentTransactionInfoPlugin> result = new ArrayList<>();
        for (AuthorizeNetTransactionsRecord record : records) {
            result.add(new AuthorizeNetPaymentTransactionInfo(record));
        }

        return result;
    }

    /**
     * Deactivate the payment method with the given <code>kbPaymentMethodId</code>:
     *  - Delete the corresponding payment profile in Authorize.Net
     *  - Change the payment method status to DELETED in db
     *  TODO: Address the case when we delete a default payment method, but another non-default payment
     *  method is present for the account. Post V1.
     */
    public void deactivatePaymentMethod(final UUID kbAccountId, final UUID kbPaymentMethodId, final UUID kbTenantId)
            throws TenantApiException {
        AuthorizeNetPaymentMethodsRecord paymentMethod = dao.getPaymentMethodForOperation(kbAccountId,
                kbPaymentMethodId, kbTenantId);

        MerchantAuthenticationType authentication = authenticationService.getAuthenticationForTenant(kbTenantId);

        DeleteCustomerPaymentProfileRequest apiRequest = getNewDeleteCustomerPaymentProfileRequest();
        apiRequest.setMerchantAuthentication(authentication);
        apiRequest.setCustomerProfileId(paymentMethod.getAuthorizeNetCustomerProfileId());
        apiRequest.setCustomerPaymentProfileId(paymentMethod.getAuthorizeNetPaymentProfileId());

        DeleteCustomerPaymentProfileController controller = getNewDeleteCustomerPaymentProfileController(apiRequest);
        controller.execute();

        DeleteCustomerPaymentProfileResponse response = controller.getApiResponse();

        verifyAuthNetResponseLogErrors("Delete Payment Profile for kbPaymentMethodId = " + kbPaymentMethodId
                + " kbAccountId = " + kbAccountId, response);

        dao.deactivatePaymentMethod(paymentMethod);
    }

    protected DeleteCustomerPaymentProfileRequest getNewDeleteCustomerPaymentProfileRequest() {
        return new DeleteCustomerPaymentProfileRequest();
    }

    protected DeleteCustomerPaymentProfileController getNewDeleteCustomerPaymentProfileController(
            DeleteCustomerPaymentProfileRequest apiRequest) {
        return new DeleteCustomerPaymentProfileController(apiRequest);
    }

    /**
     * Pings Auth.Net to verify our authentication credentials
     */
    public AuthenticateTestResponse getAuthenticateTestResponse(final UUID kbTenantId) throws TenantApiException {

        AuthenticateTestRequest request = new AuthenticateTestRequest();

        MerchantAuthenticationType authentication = authenticationService.getAuthenticationForTenant(kbTenantId);
        request.setMerchantAuthentication(authentication);

        AuthenticateTestController controller = new AuthenticateTestController(request);
        controller.execute();
        return controller.getApiResponse();
    }


}
