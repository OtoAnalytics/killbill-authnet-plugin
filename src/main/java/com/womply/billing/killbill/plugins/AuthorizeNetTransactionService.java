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

import com.womply.billing.killbill.plugins.db.AuthorizeNetDAO;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;
import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentTransactionInfo;
import com.womply.billing.killbill.plugins.models.AuthorizeNetTransactionInfo;

import net.authorize.api.contract.v1.ArrayOfSetting;
import net.authorize.api.contract.v1.CreateTransactionRequest;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.CustomerProfilePaymentType;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.contract.v1.PaymentProfile;
import net.authorize.api.contract.v1.SettingType;
import net.authorize.api.contract.v1.TransactionRequestType;
import net.authorize.api.controller.CreateTransactionController;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.osgi.libs.killbill.OSGIConfigPropertiesService;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.osgi.service.log.LogService;

/**
 * Performs transaction operations with Authorize.Net.
 */
public class AuthorizeNetTransactionService {

    public static final String AUTH_NET_MERCHANT_DESCRIPTOR =
            "org.killbill.billing.plugin.authorizenet.gateway.merchant.descriptor";

    private final AuthorizeNetDAO dao;
    private final OSGIKillbillLogService logService;
    private final String merchantDescriptor;

    public AuthorizeNetTransactionService(AuthorizeNetDAO dao, OSGIKillbillLogService logService,
                                          OSGIConfigPropertiesService configProperties) {
        this.dao = dao;
        this.logService = logService;
        this.merchantDescriptor = configProperties.getString(AUTH_NET_MERCHANT_DESCRIPTOR);
    }

    /**
     * Perform the transaction specified in the given <code>transaction</code> object.
     * @return AuthorizeNetPaymentTransactionInfo representation of the performed transaction.
     */
    public AuthorizeNetPaymentTransactionInfo createTransactionOnPaymentProfile(
            AuthorizeNetTransactionInfo transaction,
            MerchantAuthenticationType authentication) {
        validate(transaction);

        long requestId = dao.logTransactionRequest(transaction);
        transaction.setRequestId(requestId);

        PaymentProfile paymentProfile = new PaymentProfile();
        paymentProfile.setPaymentProfileId(transaction.getCustomerPaymentProfileId());

        CustomerProfilePaymentType customerPaymentProfile = new CustomerProfilePaymentType();
        customerPaymentProfile.setCustomerProfileId(transaction.getCustomerProfileId());
        customerPaymentProfile.setPaymentProfile(paymentProfile);

        TransactionRequestType txnRequest = new TransactionRequestType();
        txnRequest.setTransactionType(transaction.getTransactionType().value());
        txnRequest.setProfile(customerPaymentProfile);
        txnRequest.setAmount(transaction.getAmount());
        txnRequest.setPoNumber(transaction.getKbTransactionId().toString());

        // Set the recurringBilling flag to true for ALL charges (this may need to be more nuanced in the future)
        SettingType recurringBillingSetting = new SettingType();
        recurringBillingSetting.setSettingName("recurringBilling");
        recurringBillingSetting.setSettingValue("1");

        ArrayOfSetting settings = new ArrayOfSetting();
        settings.getSetting().add(recurringBillingSetting);
        txnRequest.setTransactionSettings(settings);

        // Not all processors support  setting the merchant descriptor at a per-transaction level.
        // If a merchantDescriptor is configured, set it
        if (merchantDescriptor != null && !merchantDescriptor.isEmpty()) {
            txnRequest.setMerchantDescriptor(merchantDescriptor);
        }

        if (transaction.doesReferenceTransaction()) {
            txnRequest.setRefTransId(transaction.getAuthorizeNetReferencedTransactionId());
        }

        CreateTransactionRequest apiRequest = getNewTransactionRequest();
        apiRequest.setMerchantAuthentication(authentication);
        apiRequest.setTransactionRequest(txnRequest);

        CreateTransactionController controller = getNewTransactionController(apiRequest);
        controller.execute();

        CreateTransactionResponse response = controller.getApiResponse();

        if (response == null) {
            throw new RuntimeException("Authorize.Net response to createTransactionRequest was null");
        }

        try {
            transaction.setResponse(response);
            AuthorizeNetTransactionsRecord transactionResult = dao.logTransactionResponse(transaction);
            AuthorizeNetPaymentTransactionInfo transactionInfo =
                    new AuthorizeNetPaymentTransactionInfo(transactionResult);
            return transactionInfo;
        } catch (RuntimeException e) {
            // log the response since it may not have been persisted
            String transactionId = (response.getTransactionResponse() == null ? "unknown"
                    : response.getTransactionResponse().getTransId());
            logService.log(LogService.LOG_ERROR, "Failed to persist transaction response for requestID = " +
                    requestId + ", Authorize.Net transactionID = " + transactionId, e);
            throw e;
        }
    }

    protected void validate(AuthorizeNetTransactionInfo transaction) {
        if (transaction.getCurrency() != Currency.USD) {
            throw new RuntimeException("Unsupported currency for transaction: " + transaction.toString());
        }
    }

    // hook for the tests
    protected CreateTransactionRequest getNewTransactionRequest() {
        return new CreateTransactionRequest();
    }

    // hook for the tests
    protected CreateTransactionController getNewTransactionController(CreateTransactionRequest request) {
        return new CreateTransactionController(request);
    }
}
