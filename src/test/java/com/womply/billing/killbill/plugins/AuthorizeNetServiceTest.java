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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.womply.billing.killbill.plugins.authentication.AuthorizeNetAuthenticationService;
import com.womply.billing.killbill.plugins.db.AuthorizeNetDAO;
import com.womply.billing.killbill.plugins.db.MysqlAdapter;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetPaymentMethodsRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;
import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentMethod;
import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentMethodTest;
import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentTransactionInfo;
import com.womply.billing.killbill.plugins.models.AuthorizeNetTransactionInfo;
import com.womply.billing.killbill.plugins.transaction.RefundPaymentHelper;
import com.womply.killbill.resources.models.PaymentGatewayAccount;

import net.authorize.api.contract.v1.CreateCustomerProfileRequest;
import net.authorize.api.contract.v1.CreateCustomerProfileResponse;
import net.authorize.api.contract.v1.CustomerProfileType;
import net.authorize.api.contract.v1.DeleteCustomerPaymentProfileRequest;
import net.authorize.api.contract.v1.DeleteCustomerPaymentProfileResponse;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.MessagesType;
import net.authorize.api.controller.CreateCustomerProfileController;
import net.authorize.api.controller.DeleteCustomerPaymentProfileController;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.killbill.billing.ObjectType;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.payment.plugin.api.PaymentPluginApiException;
import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;
import org.killbill.billing.payment.plugin.api.PaymentTransactionInfoPlugin;
import org.killbill.billing.tenant.api.Tenant;
import org.killbill.billing.tenant.api.TenantApiException;
import org.killbill.billing.tenant.api.TenantUserApi;
import org.killbill.billing.util.api.CustomFieldUserApi;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.billing.util.customfield.CustomField;
import org.killbill.billing.util.customfield.StringCustomField;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Unit tests for AuthorizeNetService class.
 */
public class AuthorizeNetServiceTest {
    @Test
    public void addPayment() throws SQLException, PaymentPluginApiException {
        String expectedCustomerProfileId = "auth.net profile id";
        List<CustomField> customFields = new ArrayList();
        CustomField expected = new StringCustomField(AuthorizeNetService.AUTH_NET_CUSTOMER_PROFILE_ID,
                expectedCustomerProfileId, ObjectType.ACCOUNT, null, null);
        customFields.add(expected);

        UUID expectedAccountId = UUID.randomUUID();
        UUID expectedTenantId = UUID.randomUUID();
        CallContext expectedCallContext = createMock(CallContext.class);

        CustomFieldUserApi customFieldUserApiMock = createMock(CustomFieldUserApi.class);
        OSGIKillbillAPI killbillAPIMock = createMock(OSGIKillbillAPI.class);
        expect(killbillAPIMock.getCustomFieldUserApi()).andReturn(customFieldUserApiMock);
        expect(customFieldUserApiMock.getCustomFieldsForAccount(eq(expectedAccountId), eq(expectedCallContext)))
                .andReturn(customFields);
        expect(expectedCallContext.getTenantId()).andReturn(expectedTenantId);

        UUID expectedPaymentMethodId = UUID.randomUUID();
        boolean expectedIsDefault = false;
        Map<String, String> expectedProperties = new HashMap<>();
        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        mockDao.addPaymentMethod(eq(expectedAccountId), eq(expectedPaymentMethodId), eq(expectedIsDefault),
                eq(expectedCustomerProfileId), eq(expectedProperties), eq(expectedTenantId));
        expectLastCall().once();

        replay(expectedCallContext, customFieldUserApiMock, killbillAPIMock, mockDao);

        AuthorizeNetService service = new AuthorizeNetService(killbillAPIMock, null, mockDao, null, null);

        service.addPaymentMethod(expectedAccountId, expectedPaymentMethodId, expectedIsDefault,
                expectedProperties, expectedCallContext);

        verify(expectedCallContext, customFieldUserApiMock, killbillAPIMock, mockDao);
    }

    @Test
    public void addPaymentWithDbException() throws SQLException {
        String expectedCustomerProfileId = "auth.net profile id";
        List<CustomField> customFields = new ArrayList();
        CustomField expected = new StringCustomField(AuthorizeNetService.AUTH_NET_CUSTOMER_PROFILE_ID,
                expectedCustomerProfileId, ObjectType.ACCOUNT, null, null);
        customFields.add(expected);

        UUID expectedAccountId = UUID.randomUUID();
        UUID expectedTenantId = UUID.randomUUID();
        CallContext expectedCallContext = createMock(CallContext.class);

        CustomFieldUserApi customFieldUserApiMock = createMock(CustomFieldUserApi.class);
        OSGIKillbillAPI killbillAPIMock = createMock(OSGIKillbillAPI.class);
        expect(killbillAPIMock.getCustomFieldUserApi()).andReturn(customFieldUserApiMock);
        expect(customFieldUserApiMock.getCustomFieldsForAccount(eq(expectedAccountId), eq(expectedCallContext)))
                .andReturn(customFields);
        expect(expectedCallContext.getTenantId()).andReturn(expectedTenantId);

        UUID expectedPaymentMethodId = UUID.randomUUID();
        boolean expectedIsDefault = false;
        Map<String, String> expectedProperties = new HashMap<>();
        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        String sqlExceptionMsg = "Test Exception";
        SQLException sqlException = new SQLException(sqlExceptionMsg);
        mockDao.addPaymentMethod(eq(expectedAccountId), eq(expectedPaymentMethodId), eq(expectedIsDefault),
                eq(expectedCustomerProfileId), eq(expectedProperties), eq(expectedTenantId));
        expectLastCall().andThrow(sqlException);

        replay(expectedCallContext, customFieldUserApiMock, killbillAPIMock, mockDao);

        AuthorizeNetService service = new AuthorizeNetService(killbillAPIMock, null, mockDao, null, null);

        try {
            service.addPaymentMethod(expectedAccountId, expectedPaymentMethodId, expectedIsDefault,
                    expectedProperties, expectedCallContext);
            failBecauseExceptionWasNotThrown(SQLException.class);
        } catch (SQLException e) {
            assertThat(sqlExceptionMsg).isEqualTo(e.getMessage());
        }

        verify(expectedCallContext, customFieldUserApiMock, killbillAPIMock, mockDao);
    }

    @Test
    public void getAuthNetCustomerProfileIdFromCustomFields() throws PaymentPluginApiException {
        List<CustomField> customFields = new ArrayList();
        customFields.add(new StringCustomField("some field name", "field value", ObjectType.ACCOUNT, null, null));
        CustomField expected = new StringCustomField(AuthorizeNetService.AUTH_NET_CUSTOMER_PROFILE_ID,
                "expected value", ObjectType.ACCOUNT, null, null);
        customFields.add(expected);
        customFields.add(new StringCustomField("some field name 2", "field value 2", ObjectType.ACCOUNT, null, null));

        UUID accountId = UUID.randomUUID();
        AuthorizeNetService service = new AuthorizeNetService(null, null, null, null, null);

        String profileId = service.getAuthNetCustomerProfileIdFromCustomFields(accountId, customFields);
        assertThat(profileId).isEqualTo(expected.getFieldValue());
    }

    @Test
    public void getAuthNetCustomerProfileIdFromCustomFieldsNotFound() {
        List<CustomField> customFields = new ArrayList();
        customFields.add(new StringCustomField("some field name", "field value", ObjectType.ACCOUNT, null, null));
        customFields.add(new StringCustomField("some field name 2", "field value 2", ObjectType.ACCOUNT, null, null));

        UUID accountId = UUID.randomUUID();
        AuthorizeNetService service = new AuthorizeNetService(null, null, null, null, null);

        try {
            service.getAuthNetCustomerProfileIdFromCustomFields(accountId, customFields);
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isNotEmpty().contains(accountId.toString());
        }
    }

    @Test
    public void getPaymentMethod() {
        UUID expectedAccountId = UUID.randomUUID();
        UUID expectedPaymentMethodId = UUID.randomUUID();
        UUID expectedTenantId = UUID.randomUUID();
        Map<String, Object> paymentMethodData = AuthorizeNetPaymentMethodTest.getPaymentMethodData();

        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        expect(mockDao.getPaymentMethod(eq(expectedAccountId), eq(expectedPaymentMethodId),
                eq(expectedTenantId), eq(false))).andReturn(paymentMethodData);
        replay(mockDao);

        AuthorizeNetService service = new AuthorizeNetService(null, null, mockDao, null, null);
        Map<String, Object> expectedData = new HashMap<>(paymentMethodData);
        AuthorizeNetPaymentMethod paymentMethod = service.getPaymentMethod(expectedAccountId,
                expectedPaymentMethodId, expectedTenantId, false);

        assertThat(paymentMethod).isNotNull();
        AuthorizeNetPaymentMethodTest.verifyAuthorizeNetPaymentMethod(paymentMethod, expectedData);
    }

    @Test
    public void getPaymentMethodRaw() {
        UUID expectedAccountId = UUID.randomUUID();
        UUID expectedPaymentMethodId = UUID.randomUUID();
        UUID expectedTenantId = UUID.randomUUID();
        Map<String, Object> paymentMethodData = AuthorizeNetPaymentMethodTest.getPaymentMethodDataRaw();

        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        expect(mockDao.getPaymentMethod(eq(expectedAccountId), eq(expectedPaymentMethodId),
                eq(expectedTenantId), eq(true))).andReturn(paymentMethodData);
        replay(mockDao);

        AuthorizeNetService service = new AuthorizeNetService(null, null, mockDao, null, null);
        Map<String, Object> expectedData = new HashMap<>(paymentMethodData);
        AuthorizeNetPaymentMethod paymentMethod = service.getPaymentMethod(expectedAccountId,
                expectedPaymentMethodId, expectedTenantId, true);

        assertThat(paymentMethod).isNotNull();
        AuthorizeNetPaymentMethodTest.verifyAuthorizeNetPaymentMethod(paymentMethod, expectedData);
    }

    @Test
    public void addCustomerProfile() throws TenantApiException {
        PaymentGatewayAccount account = new PaymentGatewayAccount();
        account.setMerchantLocationId(12345);
        UUID expectedTenantId = UUID.randomUUID();
        String expectedTenantApiKey = "testTenantApiKey";
        Tenant tenantMock = createMock(Tenant.class);
        OSGIKillbillAPI killbillAPIMock = createMock(OSGIKillbillAPI.class);
        TenantUserApi tenantUserApiMock = createMock(TenantUserApi.class);
        expect(tenantMock.getId()).andReturn(expectedTenantId);
        expect(tenantUserApiMock.getTenantByApiKey(eq(expectedTenantApiKey))).andReturn(tenantMock);
        expect(killbillAPIMock.getTenantUserApi()).andReturn(tenantUserApiMock);

        String expectedCustomerProfileId = "987654321";
        CreateCustomerProfileResponse apiResponse = new CreateCustomerProfileResponse();
        apiResponse.setCustomerProfileId(expectedCustomerProfileId);
        MessagesType message = new MessagesType();
        message.setResultCode(MessageTypeEnum.OK);
        apiResponse.setMessages(message);

        CreateCustomerProfileController controllerMock = createMock(CreateCustomerProfileController.class);
        controllerMock.execute();
        expectLastCall().once();
        expect(controllerMock.getApiResponse()).andReturn(apiResponse);

        AuthorizeNetDAO daoMock = createMock(AuthorizeNetDAO.class);
        String expectedCustomerId =
                AuthorizeNetService.AUTH_NET_CUSTOMER_ID_PREFIX + account.getMerchantLocationId();
        daoMock.logCustomerProfileCreation(eq(expectedCustomerId), eq(expectedCustomerProfileId),
                eq(expectedTenantId));

        AuthorizeNetAuthenticationService authServiceMock = createMock(AuthorizeNetAuthenticationService.class);
        expect(authServiceMock.getAuthenticationForTenant(eq(expectedTenantId))).andReturn(null);

        OSGIKillbillLogService logServiceMock = createMock(OSGIKillbillLogService.class);
        AuthorizeNetTransactionService transactionServiceMock =
                createMock(AuthorizeNetTransactionService.class);

        replay(tenantMock, killbillAPIMock, tenantUserApiMock, controllerMock, authServiceMock, logServiceMock,
                transactionServiceMock);

        AuthorizeNetService service = EasyMock.partialMockBuilder(AuthorizeNetService.class)
                .withConstructor(killbillAPIMock, logServiceMock, daoMock, authServiceMock, transactionServiceMock)
                .addMockedMethod("getCreateCustomerProfileRequest")
                .addMockedMethod("getCreateCustomerProfileController")
                .createMock();

        CreateCustomerProfileRequest apiRequest = new CreateCustomerProfileRequest();
        expect(service.getCreateCustomerProfileRequest()).andReturn(apiRequest);
        expect(service.getCreateCustomerProfileController(eq(apiRequest))).andReturn(controllerMock);
        replay(service);

        String actualCustomerProfileId = service.addCustomerProfile(expectedTenantApiKey, account);

        verify(tenantMock, killbillAPIMock, tenantUserApiMock, controllerMock, authServiceMock, logServiceMock);
        assertThat(actualCustomerProfileId).isNotNull().isEqualTo(expectedCustomerProfileId);

        CustomerProfileType requestProfile = apiRequest.getProfile();
        assertThat(requestProfile).isNotNull();
        assertThat(requestProfile.getMerchantCustomerId()).isNotNull().isEqualTo(expectedCustomerId);
    }

    @Test
    public void addCustomerProfileAuthNetError() throws TenantApiException {
        PaymentGatewayAccount account = new PaymentGatewayAccount();
        account.setMerchantLocationId(12345);
        UUID expectedTenantId = UUID.randomUUID();
        String expectedTenantApiKey = "testTenantApiKey";
        Tenant tenantMock = createMock(Tenant.class);
        OSGIKillbillAPI killbillAPIMock = createMock(OSGIKillbillAPI.class);
        TenantUserApi tenantUserApiMock = createMock(TenantUserApi.class);
        expect(tenantMock.getId()).andReturn(expectedTenantId);
        expect(tenantUserApiMock.getTenantByApiKey(eq(expectedTenantApiKey))).andReturn(tenantMock);
        expect(killbillAPIMock.getTenantUserApi()).andReturn(tenantUserApiMock);

        String expectedCustomerProfileId = "987654321";
        CreateCustomerProfileResponse apiResponse = new CreateCustomerProfileResponse();
        apiResponse.setCustomerProfileId(expectedCustomerProfileId);

        MessagesType messages = new MessagesType();
        messages.setResultCode(MessageTypeEnum.ERROR);
        MessagesType.Message message = new MessagesType.Message();
        message.setCode("E12345");
        message.setText("Account already exists");
        messages.getMessage().add(message);
        apiResponse.setMessages(messages);

        CreateCustomerProfileController controllerMock = createMock(CreateCustomerProfileController.class);
        controllerMock.execute();
        expectLastCall().once();
        expect(controllerMock.getApiResponse()).andReturn(apiResponse);

        AuthorizeNetAuthenticationService authServiceMock = createMock(AuthorizeNetAuthenticationService.class);
        expect(authServiceMock.getAuthenticationForTenant(eq(expectedTenantId))).andReturn(null);

        OSGIKillbillLogService logServiceMock = createMock(OSGIKillbillLogService.class);
        logServiceMock.log(anyInt(), anyString());
        expectLastCall().atLeastOnce();

        AuthorizeNetTransactionService transactionServiceMock = createMock(AuthorizeNetTransactionService.class);

        AuthorizeNetDAO daoMock = createMock(AuthorizeNetDAO.class);
        replay(tenantMock, killbillAPIMock, tenantUserApiMock, controllerMock, authServiceMock, logServiceMock,
                transactionServiceMock);

        AuthorizeNetService service = EasyMock.partialMockBuilder(AuthorizeNetService.class)
                .withConstructor(killbillAPIMock, logServiceMock, daoMock, authServiceMock, transactionServiceMock)
                .addMockedMethod("getCreateCustomerProfileRequest")
                .addMockedMethod("getCreateCustomerProfileController")
                .createMock();

        CreateCustomerProfileRequest apiRequest = new CreateCustomerProfileRequest();
        expect(service.getCreateCustomerProfileRequest()).andReturn(apiRequest);
        expect(service.getCreateCustomerProfileController(eq(apiRequest))).andReturn(controllerMock);
        replay(service);

        try {
            service.addCustomerProfile(expectedTenantApiKey, account);
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains(message.getCode());
            assertThat(e.getMessage()).contains("" + account.getMerchantLocationId());
        }

        verify(tenantMock, killbillAPIMock, tenantUserApiMock, controllerMock, authServiceMock, logServiceMock);
    }

    @Test
    public void addCustomerProfileNullResponse() throws TenantApiException {
        PaymentGatewayAccount account = new PaymentGatewayAccount();
        account.setMerchantLocationId(12345);
        UUID expectedTenantId = UUID.randomUUID();
        String expectedTenantApiKey = "testTenantApiKey";
        Tenant tenantMock = createMock(Tenant.class);
        OSGIKillbillAPI killbillAPIMock = createMock(OSGIKillbillAPI.class);
        TenantUserApi tenantUserApiMock = createMock(TenantUserApi.class);
        expect(tenantMock.getId()).andReturn(expectedTenantId);
        expect(tenantUserApiMock.getTenantByApiKey(eq(expectedTenantApiKey))).andReturn(tenantMock);
        expect(killbillAPIMock.getTenantUserApi()).andReturn(tenantUserApiMock);

        CreateCustomerProfileController controllerMock = createMock(CreateCustomerProfileController.class);
        controllerMock.execute();
        expectLastCall().once();
        expect(controllerMock.getApiResponse()).andReturn(null);

        AuthorizeNetAuthenticationService authServiceMock = createMock(AuthorizeNetAuthenticationService.class);
        expect(authServiceMock.getAuthenticationForTenant(eq(expectedTenantId))).andReturn(null);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);

        AuthorizeNetTransactionService transactionServiceMock = createMock(AuthorizeNetTransactionService.class);

        AuthorizeNetDAO daoMock = createMock(AuthorizeNetDAO.class);
        replay(tenantMock, killbillAPIMock, tenantUserApiMock, controllerMock, authServiceMock, logServiceMock,
                transactionServiceMock);

        AuthorizeNetService service = EasyMock.partialMockBuilder(AuthorizeNetService.class)
                .withConstructor(killbillAPIMock, logServiceMock, daoMock, authServiceMock, transactionServiceMock)
                .addMockedMethod("getCreateCustomerProfileRequest")
                .addMockedMethod("getCreateCustomerProfileController")
                .createMock();

        CreateCustomerProfileRequest apiRequest = new CreateCustomerProfileRequest();
        expect(service.getCreateCustomerProfileRequest()).andReturn(apiRequest);
        expect(service.getCreateCustomerProfileController(eq(apiRequest))).andReturn(controllerMock);
        replay(service);

        try {
            service.addCustomerProfile(expectedTenantApiKey, account);
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Got NULL response from Authorize.Net on Customer Profile creation");
            assertThat(e.getMessage()).contains("" + account.getMerchantLocationId());
        }

        verify(tenantMock, killbillAPIMock, tenantUserApiMock, controllerMock, authServiceMock);
    }


    @Test
    public void testPurchasePayment() throws TenantApiException {
        final UUID expectedTenantId = UUID.randomUUID();
        final UUID expectedKbAccountId = UUID.randomUUID();
        final UUID expectedKbPaymentId = UUID.randomUUID();
        final UUID expectedKbPaymentMethodId = UUID.randomUUID();
        final UUID expectedKbTransactionId = UUID.randomUUID();
        final TransactionType expectedKbTransactionType = TransactionType.PURCHASE;
        final String expectedCustomerProfileId = "test profile id";
        final String expectedCustomerPaymentProfileId = "test payment profile id";
        final BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        final Currency expectedCurrency = Currency.USD;

        AuthorizeNetPaymentMethodsRecord recordMock = createMock(AuthorizeNetPaymentMethodsRecord.class);
        expect(recordMock.getAuthorizeNetCustomerProfileId()).andReturn(expectedCustomerProfileId);
        expect(recordMock.getAuthorizeNetPaymentProfileId()).andReturn(expectedCustomerPaymentProfileId);

        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        expect(mockDao.getPaymentMethodForOperation(eq(expectedKbAccountId),
                eq(expectedKbPaymentMethodId), eq(expectedTenantId)))
                .andReturn(recordMock);

        AuthorizeNetTransactionService transactionServiceMock = createMock(AuthorizeNetTransactionService.class);
        Capture<AuthorizeNetTransactionInfo> transactionCapture = newCapture();
        AuthorizeNetPaymentTransactionInfo transactionInfo = createMock(AuthorizeNetPaymentTransactionInfo.class);
        expect(transactionServiceMock.createTransactionOnPaymentProfile(capture(transactionCapture), eq(null)))
                .andReturn(transactionInfo);

        AuthorizeNetAuthenticationService authServiceMock = createMock(AuthorizeNetAuthenticationService.class);
        expect(authServiceMock.getAuthenticationForTenant(eq(expectedTenantId))).andReturn(null);

        replay(recordMock, mockDao, transactionServiceMock);
        AuthorizeNetService service = new AuthorizeNetService(null, null, mockDao, authServiceMock,
                transactionServiceMock);

        AuthorizeNetPaymentTransactionInfo result = service.purchasePayment(expectedTenantId, expectedKbAccountId,
                expectedKbPaymentId, expectedKbTransactionId, expectedKbPaymentMethodId, expectedAmount,
                expectedCurrency);

        assertThat(result).isNotNull().isEqualTo(transactionInfo);

        assertThat(transactionCapture.hasCaptured()).isTrue();
        AuthorizeNetTransactionInfo transaction = transactionCapture.getValue();
        assertThat(transaction.getCustomerProfileId()).isEqualTo(expectedCustomerProfileId);
        assertThat(transaction.getCustomerPaymentProfileId()).isEqualTo(expectedCustomerPaymentProfileId);
        assertThat(transaction.getKbAccountId()).isEqualTo(expectedKbAccountId);
        assertThat(transaction.getKbPaymentId()).isEqualTo(expectedKbPaymentId);
        assertThat(transaction.getKbPaymentMethodId()).isEqualTo(expectedKbPaymentMethodId);
        assertThat(transaction.getKbTransactionId()).isEqualTo(expectedKbTransactionId);
        assertThat(transaction.getKbTransactionType()).isEqualTo(expectedKbTransactionType);
        assertThat(transaction.getTenantId()).isEqualTo(expectedTenantId);
        assertThat(transaction.getAmount()).isEqualTo(expectedAmount);
        assertThat(transaction.getCurrency()).isEqualTo(expectedCurrency);
    }

    @Test
    public void refundPayment() throws TenantApiException {
        final UUID expectedKbTenantId = UUID.randomUUID();
        final UUID expectedKbAccountId = UUID.randomUUID();
        final UUID expectedKbPaymentId = UUID.randomUUID();
        final UUID expectedKbPaymentMethodId = UUID.randomUUID();
        final UUID expectedKbTransactionId = UUID.randomUUID();
        final BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        final Currency expectedCurrency = Currency.USD;

        final OSGIKillbillAPI killbillAPIMock = createMock(OSGIKillbillAPI.class);
        final OSGIKillbillLogService logServiceMock = createMock(OSGIKillbillLogService.class);
        final AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        final AuthorizeNetAuthenticationService authServiceMock =
                createMock(AuthorizeNetAuthenticationService.class);
        final AuthorizeNetTransactionService transactionServiceMock = createMock(AuthorizeNetTransactionService.class);

        replay(killbillAPIMock, logServiceMock, mockDao, authServiceMock, transactionServiceMock);

        AuthorizeNetService service = EasyMock.partialMockBuilder(AuthorizeNetService.class)
                .withConstructor(killbillAPIMock, logServiceMock, mockDao, authServiceMock, transactionServiceMock)
                .addMockedMethod("getNewRefundPaymentHelper")
                .createMock();

        AuthorizeNetPaymentTransactionInfo transactionInfoMock = createMock(AuthorizeNetPaymentTransactionInfo.class);
        RefundPaymentHelper refundPaymentHelperMock = createMock(RefundPaymentHelper.class);
        expect(refundPaymentHelperMock
                .refundPayment(eq(expectedKbTenantId), eq(expectedKbAccountId), eq(expectedKbPaymentId),
                        eq(expectedKbTransactionId), eq(expectedKbPaymentMethodId), eq(expectedAmount),
                        eq(expectedCurrency)))
                .andReturn(transactionInfoMock);
        expect(service.getNewRefundPaymentHelper()).andReturn(refundPaymentHelperMock);

        replay(transactionInfoMock, refundPaymentHelperMock, service);

        AuthorizeNetPaymentTransactionInfo result = service.refundPayment(expectedKbTenantId, expectedKbAccountId,
                expectedKbPaymentId, expectedKbTransactionId, expectedKbPaymentMethodId, expectedAmount,
                expectedCurrency);

        assertThat(result).isNotNull().isEqualTo(transactionInfoMock);
        verify(transactionInfoMock, refundPaymentHelperMock, service);
    }

    @Test
    public void testGetPaymentInfo() {
        final UUID expectedKbAccountId = UUID.randomUUID();
        final UUID expectedKbPaymentId = UUID.randomUUID();
        final UUID expectedKbTransactionId = UUID.randomUUID();
        final TransactionType expectedKbTransactionType = TransactionType.PURCHASE;
        final BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        final Currency expectedCurrency = Currency.USD;

        final String expectedTransId = "test Authorize.Net transaction id";
        final String expectedAuthCode = "test auth code";

        Timestamp now = new Timestamp(Instant.now().toEpochMilli());
        AuthorizeNetTransactionsRecord responseRecord = createMock(AuthorizeNetTransactionsRecord.class);
        expect(responseRecord.getKbPaymentId()).andReturn(expectedKbPaymentId.toString());
        expect(responseRecord.getKbPaymentTransactionId()).andReturn(expectedKbTransactionId.toString());
        expect(responseRecord.getKbTransactionType()).andReturn(expectedKbTransactionType.name());
        expect(responseRecord.getAmount()).andReturn(expectedAmount);
        expect(responseRecord.getCurrency()).andReturn(expectedCurrency.name());
        expect(responseRecord.getSuccess()).andReturn(MysqlAdapter.TRUE).anyTimes();
        expect(responseRecord.getAuthorizeNetTransactionId()).andReturn(expectedTransId);
        expect(responseRecord.getAuthCode()).andReturn(expectedAuthCode);
        expect(responseRecord.getCreatedAt()).andReturn(now).anyTimes();
        expect(responseRecord.getKbPaymentPluginStatus()).andReturn(PaymentPluginStatus.PROCESSED.name()).anyTimes();

        List<AuthorizeNetTransactionsRecord> responses = new ArrayList<>();
        responses.add(responseRecord);

        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        expect(mockDao.getTransactionsForPayment(eq(expectedKbAccountId),
                eq(expectedKbPaymentId)))
                .andReturn(responses);

        replay(responseRecord, mockDao);
        AuthorizeNetService service = new AuthorizeNetService(null, null, mockDao, null,
                null);

        List<PaymentTransactionInfoPlugin> resultList =
                service.getPaymentInfo(expectedKbAccountId, expectedKbPaymentId);
        assertThat(resultList).isNotEmpty().hasSize(1);

        PaymentTransactionInfoPlugin result = resultList.get(0);
        assertThat(result.getKbPaymentId()).isEqualTo(expectedKbPaymentId);
        assertThat(result.getKbTransactionPaymentId()).isEqualTo(expectedKbTransactionId);
        assertThat(result.getTransactionType()).isEqualTo(expectedKbTransactionType);
        assertThat(result.getAmount()).isEqualTo(expectedAmount);
        assertThat(result.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(result.getStatus()).isEqualTo(PaymentPluginStatus.PROCESSED);
        assertThat(result.getGatewayError()).isEmpty();
        assertThat(result.getGatewayErrorCode()).isEmpty();
        assertThat(result.getFirstPaymentReferenceId()).isEqualTo(expectedTransId);
        assertThat(result.getSecondPaymentReferenceId()).isEqualTo(expectedAuthCode);
    }

    @Test
    public void deletePaymentMethod() throws TenantApiException {
        final UUID expectedAccountId = UUID.randomUUID();
        final UUID expectedPaymentMethodId = UUID.randomUUID();
        final UUID expectedTenantId = UUID.randomUUID();
        final String expectedAuthNetCustomerProfileId = "auth-net-customer-profile-test";
        final String expectedAuthNetPaymentProfileId = "auth-net-payment-profile-test";

        AuthorizeNetPaymentMethodsRecord paymentMethodMock = createMock(AuthorizeNetPaymentMethodsRecord.class);
        expect(paymentMethodMock.getAuthorizeNetCustomerProfileId()).andReturn(expectedAuthNetCustomerProfileId);
        expect(paymentMethodMock.getAuthorizeNetPaymentProfileId()).andReturn(expectedAuthNetPaymentProfileId);

        AuthorizeNetAuthenticationService authServiceMock = createMock(AuthorizeNetAuthenticationService.class);
        expect(authServiceMock.getAuthenticationForTenant(eq(expectedTenantId))).andReturn(null);

        DeleteCustomerPaymentProfileResponse apiResponse = new DeleteCustomerPaymentProfileResponse();
        MessagesType message = new MessagesType();
        message.setResultCode(MessageTypeEnum.OK);
        apiResponse.setMessages(message);

        DeleteCustomerPaymentProfileController controllerMock =
                createMock(DeleteCustomerPaymentProfileController.class);
        controllerMock.execute();
        expectLastCall().once();
        expect(controllerMock.getApiResponse()).andReturn(apiResponse);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);
        AuthorizeNetTransactionService transactionServiceMock = createMock(AuthorizeNetTransactionService.class);
        OSGIKillbillAPI killbillAPIMock = createMock(OSGIKillbillAPI.class);

        replay(paymentMethodMock, authServiceMock, controllerMock, logServiceMock, transactionServiceMock,
                killbillAPIMock);

        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        expect(mockDao.getPaymentMethodForOperation(eq(expectedAccountId), eq(expectedPaymentMethodId),
                eq(expectedTenantId))).andReturn(paymentMethodMock);
        mockDao.deactivatePaymentMethod(paymentMethodMock);
        expectLastCall().once();
        replay(mockDao);

        AuthorizeNetService service = EasyMock.partialMockBuilder(AuthorizeNetService.class)
                .withConstructor(killbillAPIMock, logServiceMock, mockDao, authServiceMock, transactionServiceMock)
                .addMockedMethod("getNewDeleteCustomerPaymentProfileRequest")
                .addMockedMethod("getNewDeleteCustomerPaymentProfileController")
                .createMock();

        DeleteCustomerPaymentProfileRequest apiRequest = new DeleteCustomerPaymentProfileRequest();
        expect(service.getNewDeleteCustomerPaymentProfileRequest()).andReturn(apiRequest);
        expect(service.getNewDeleteCustomerPaymentProfileController(eq(apiRequest))).andReturn(controllerMock);
        replay(service);

        service.deactivatePaymentMethod(expectedAccountId, expectedPaymentMethodId, expectedTenantId);

        verify(service);
        verify(paymentMethodMock, authServiceMock, controllerMock, logServiceMock, transactionServiceMock,
                killbillAPIMock);
    }

    @Test
    public void deletePaymentMethodErrorResponse() throws TenantApiException {
        final UUID expectedAccountId = UUID.randomUUID();
        final UUID expectedPaymentMethodId = UUID.randomUUID();
        final UUID expectedTenantId = UUID.randomUUID();
        final String expectedAuthNetCustomerProfileId = "auth-net-customer-profile-test";
        final String expectedAuthNetPaymentProfileId = "auth-net-payment-profile-test";

        AuthorizeNetPaymentMethodsRecord paymentMethodMock = createMock(AuthorizeNetPaymentMethodsRecord.class);
        expect(paymentMethodMock.getAuthorizeNetCustomerProfileId()).andReturn(expectedAuthNetCustomerProfileId);
        expect(paymentMethodMock.getAuthorizeNetPaymentProfileId()).andReturn(expectedAuthNetPaymentProfileId);

        AuthorizeNetAuthenticationService authServiceMock = createMock(AuthorizeNetAuthenticationService.class);
        expect(authServiceMock.getAuthenticationForTenant(eq(expectedTenantId))).andReturn(null);

        final DeleteCustomerPaymentProfileResponse apiResponse = new DeleteCustomerPaymentProfileResponse();
        MessagesType messages = new MessagesType();
        messages.setResultCode(MessageTypeEnum.ERROR);
        MessagesType.Message message = new MessagesType.Message();
        message.setCode("E00040");
        message.setText("The record cannot be found.");
        messages.getMessage().add(message);
        apiResponse.setMessages(messages);

        DeleteCustomerPaymentProfileController controllerMock =
                createMock(DeleteCustomerPaymentProfileController.class);
        controllerMock.execute();
        expectLastCall().once();
        expect(controllerMock.getApiResponse()).andReturn(apiResponse);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);
        AuthorizeNetTransactionService transactionServiceMock = createMock(AuthorizeNetTransactionService.class);
        OSGIKillbillAPI killbillAPIMock = createMock(OSGIKillbillAPI.class);

        replay(paymentMethodMock, authServiceMock, controllerMock, logServiceMock, transactionServiceMock,
                killbillAPIMock);

        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        expect(mockDao.getPaymentMethodForOperation(eq(expectedAccountId), eq(expectedPaymentMethodId),
                eq(expectedTenantId))).andReturn(paymentMethodMock);
        replay(mockDao);

        AuthorizeNetService service = EasyMock.partialMockBuilder(AuthorizeNetService.class)
                .withConstructor(killbillAPIMock, logServiceMock, mockDao, authServiceMock, transactionServiceMock)
                .addMockedMethod("getNewDeleteCustomerPaymentProfileRequest")
                .addMockedMethod("getNewDeleteCustomerPaymentProfileController")
                .createMock();

        DeleteCustomerPaymentProfileRequest apiRequest = new DeleteCustomerPaymentProfileRequest();
        expect(service.getNewDeleteCustomerPaymentProfileRequest()).andReturn(apiRequest);
        expect(service.getNewDeleteCustomerPaymentProfileController(eq(apiRequest))).andReturn(controllerMock);
        replay(service);

        try {
            service.deactivatePaymentMethod(expectedAccountId, expectedPaymentMethodId, expectedTenantId);
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains(message.getCode());
        }

        verify(service);
        verify(paymentMethodMock, authServiceMock, controllerMock, logServiceMock, transactionServiceMock,
                killbillAPIMock);
    }

    @Test
    public void deletePaymentMethodNullResponse() throws TenantApiException {
        final UUID expectedAccountId = UUID.randomUUID();
        final UUID expectedPaymentMethodId = UUID.randomUUID();
        final UUID expectedTenantId = UUID.randomUUID();
        final String expectedAuthNetCustomerProfileId = "auth-net-customer-profile-test";
        final String expectedAuthNetPaymentProfileId = "auth-net-payment-profile-test";

        AuthorizeNetPaymentMethodsRecord paymentMethodMock = createMock(AuthorizeNetPaymentMethodsRecord.class);
        expect(paymentMethodMock.getAuthorizeNetCustomerProfileId()).andReturn(expectedAuthNetCustomerProfileId);
        expect(paymentMethodMock.getAuthorizeNetPaymentProfileId()).andReturn(expectedAuthNetPaymentProfileId);

        AuthorizeNetAuthenticationService authServiceMock = createMock(AuthorizeNetAuthenticationService.class);
        expect(authServiceMock.getAuthenticationForTenant(eq(expectedTenantId))).andReturn(null);

        DeleteCustomerPaymentProfileController controllerMock =
                createMock(DeleteCustomerPaymentProfileController.class);
        controllerMock.execute();
        expectLastCall().once();
        expect(controllerMock.getApiResponse()).andReturn(null);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);
        AuthorizeNetTransactionService transactionServiceMock = createMock(AuthorizeNetTransactionService.class);
        OSGIKillbillAPI killbillAPIMock = createMock(OSGIKillbillAPI.class);

        replay(paymentMethodMock, authServiceMock, controllerMock, logServiceMock, transactionServiceMock,
                killbillAPIMock);

        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        expect(mockDao.getPaymentMethodForOperation(eq(expectedAccountId), eq(expectedPaymentMethodId),
                eq(expectedTenantId))).andReturn(paymentMethodMock);
        replay(mockDao);

        AuthorizeNetService service = EasyMock.partialMockBuilder(AuthorizeNetService.class)
                .withConstructor(killbillAPIMock, logServiceMock, mockDao, authServiceMock, transactionServiceMock)
                .addMockedMethod("getNewDeleteCustomerPaymentProfileRequest")
                .addMockedMethod("getNewDeleteCustomerPaymentProfileController")
                .createMock();

        DeleteCustomerPaymentProfileRequest apiRequest = new DeleteCustomerPaymentProfileRequest();
        expect(service.getNewDeleteCustomerPaymentProfileRequest()).andReturn(apiRequest);
        expect(service.getNewDeleteCustomerPaymentProfileController(eq(apiRequest))).andReturn(controllerMock);
        replay(service);

        try {
            service.deactivatePaymentMethod(expectedAccountId, expectedPaymentMethodId, expectedTenantId);
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Got NULL response from Authorize.Net");
        }

        verify(service);
        verify(paymentMethodMock, authServiceMock, controllerMock, logServiceMock, transactionServiceMock,
                killbillAPIMock);
    }

}
