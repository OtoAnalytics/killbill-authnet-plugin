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

import static com.womply.billing.killbill.plugins.AuthorizeNetTransactionService.AUTH_NET_MERCHANT_DESCRIPTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.womply.billing.killbill.plugins.db.AuthorizeNetDAO;
import com.womply.billing.killbill.plugins.db.MysqlAdapter;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;
import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentTransactionInfo;
import com.womply.billing.killbill.plugins.models.AuthorizeNetTransactionInfo;

import net.authorize.api.contract.v1.CreateTransactionRequest;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.CustomerProfilePaymentType;
import net.authorize.api.contract.v1.PaymentProfile;
import net.authorize.api.contract.v1.SettingType;
import net.authorize.api.contract.v1.TransactionRequestType;
import net.authorize.api.contract.v1.TransactionResponse;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import net.authorize.api.controller.CreateTransactionController;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.jooq.types.ULong;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.osgi.libs.killbill.OSGIConfigPropertiesService;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;
import org.osgi.service.log.LogService;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Unit tests for AuthorizeNetTransactionService.
 */
public class AuthorizeNetTransactionServiceTest {
    @Test
    public void testChargePaymentProfile() {
        UUID expectedTenantId = UUID.randomUUID();
        UUID expectedKbAccountId = UUID.randomUUID();
        UUID expectedKbPaymentId = UUID.randomUUID();
        UUID expectedKbPaymentMethodId = UUID.randomUUID();
        UUID expectedKbTransactionId = UUID.randomUUID();
        TransactionType expectedKbTransactionType = TransactionType.PURCHASE;
        String expectedCustomerProfileId = "test profile id";
        String expectedCustomerPaymentProfileId = "test payment profile id";
        TransactionTypeEnum authNetTransactionType = TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION;
        BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        Currency expectedCurrency = Currency.USD;

        AuthorizeNetTransactionInfo transaction = new AuthorizeNetTransactionInfo();
        transaction.setCustomerProfileId(expectedCustomerProfileId);
        transaction.setCustomerPaymentProfileId(expectedCustomerPaymentProfileId);
        transaction.setKbAccountId(expectedKbAccountId);
        transaction.setKbPaymentId(expectedKbPaymentId);
        transaction.setKbPaymentMethodId(expectedKbPaymentMethodId);
        transaction.setKbTransactionId(expectedKbTransactionId);
        transaction.setKbTransactionType(expectedKbTransactionType);
        transaction.setTransactionType(authNetTransactionType);
        transaction.setTenantId(expectedTenantId);
        transaction.setAmount(expectedAmount);
        transaction.setCurrency(expectedCurrency);

        AuthorizeNetDAO daoMock = createMock(AuthorizeNetDAO.class);
        long expectedRequestId = 12345L;
        expect(daoMock.logTransactionRequest(eq(transaction))).andReturn(expectedRequestId);

        OSGIKillbillLogService logServiceMock = createMock(OSGIKillbillLogService.class);

        OSGIConfigPropertiesService propertiesMock = createMock(OSGIConfigPropertiesService.class);
        expect(propertiesMock.getString(eq(AUTH_NET_MERCHANT_DESCRIPTOR))).andReturn("MERCHANT 5035551234");
        replay(propertiesMock);

        AuthorizeNetTransactionService service = EasyMock.partialMockBuilder(AuthorizeNetTransactionService.class)
                .withConstructor(daoMock, logServiceMock, propertiesMock)
                .addMockedMethod("getNewTransactionRequest")
                .addMockedMethod("getNewTransactionController")
                .createMock();

        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
        expect(service.getNewTransactionRequest()).andReturn(apiRequest);

        String expectedTransId = "test Authorize.Net transaction id";
        String expectedAuthCode = "test auth code";

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
        expect(responseRecord.getKbPaymentPluginStatus())
                .andReturn(PaymentPluginStatus.PROCESSED.name()).anyTimes();

        expect(daoMock.logTransactionResponse(eq(transaction))).andReturn(responseRecord);

        CreateTransactionController controllerMock = createMock(CreateTransactionController.class);
        controllerMock.execute();
        expectLastCall().once();
        CreateTransactionResponse response = new CreateTransactionResponse();
        expect(controllerMock.getApiResponse()).andReturn(response);

        expect(service.getNewTransactionController(eq(apiRequest))).andReturn(controllerMock);
        replay(responseRecord, daoMock, service, controllerMock);

        AuthorizeNetPaymentTransactionInfo result = service.createTransactionOnPaymentProfile(transaction, null);
        assertThat(result).isNotNull();

        assertThat(transaction.getRequestId()).isNotNull().isEqualTo(expectedRequestId);

        // verify transaction request
        TransactionRequestType transactionRequest = apiRequest.getTransactionRequest();
        assertThat(transactionRequest).isNotNull();
        assertThat(transactionRequest.getAmount()).isEqualTo(expectedAmount);
        assertThat(transactionRequest.getTransactionType()).isEqualTo(authNetTransactionType.value());
        assertThat(transactionRequest.getPoNumber()).isEqualTo(transaction.getKbTransactionId().toString());
        assertThat(transactionRequest.getMerchantDescriptor()).isEqualTo("MERCHANT 5035551234");

        List<SettingType> settings = transactionRequest.getTransactionSettings().getSetting();
        assertThat(settings).hasSize(1);
        SettingType setting = settings.get(0);
        assertThat(setting.getSettingName()).isEqualTo("recurringBilling");
        assertThat(setting.getSettingValue()).isEqualTo("1");

        CustomerProfilePaymentType customerProfile = transactionRequest.getProfile();
        assertThat(customerProfile).isNotNull();
        assertThat(customerProfile.getCustomerProfileId()).isEqualTo(expectedCustomerProfileId);

        PaymentProfile paymentProfile = customerProfile.getPaymentProfile();
        assertThat(paymentProfile).isNotNull();
        assertThat(paymentProfile.getPaymentProfileId()).isEqualTo(expectedCustomerPaymentProfileId);

        // verify transaction response
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
        verify(responseRecord, daoMock, service, controllerMock, propertiesMock);
    }

    @Test
    public void testChargePaymentProfileErrorResponse() {
        UUID expectedTenantId = UUID.randomUUID();
        UUID expectedKbAccountId = UUID.randomUUID();
        UUID expectedKbPaymentId = UUID.randomUUID();
        UUID expectedKbPaymentMethodId = UUID.randomUUID();
        UUID expectedKbTransactionId = UUID.randomUUID();
        TransactionType expectedKbTransactionType = TransactionType.PURCHASE;
        String expectedCustomerProfileId = "test profile id";
        String expectedCustomerPaymentProfileId = "test payment profile id";
        TransactionTypeEnum authNetTransactionType = TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION;
        BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        Currency expectedCurrency = Currency.USD;

        AuthorizeNetTransactionInfo transaction = new AuthorizeNetTransactionInfo();
        transaction.setCustomerProfileId(expectedCustomerProfileId);
        transaction.setCustomerPaymentProfileId(expectedCustomerPaymentProfileId);
        transaction.setKbAccountId(expectedKbAccountId);
        transaction.setKbPaymentId(expectedKbPaymentId);
        transaction.setKbPaymentMethodId(expectedKbPaymentMethodId);
        transaction.setKbTransactionId(expectedKbTransactionId);
        transaction.setKbTransactionType(expectedKbTransactionType);
        transaction.setTransactionType(authNetTransactionType);
        transaction.setTenantId(expectedTenantId);
        transaction.setAmount(expectedAmount);
        transaction.setCurrency(expectedCurrency);

        AuthorizeNetDAO daoMock = createMock(AuthorizeNetDAO.class);
        long expectedRequestId = 12345L;
        expect(daoMock.logTransactionRequest(eq(transaction))).andReturn(expectedRequestId);

        OSGIKillbillLogService logServiceMock = createMock(OSGIKillbillLogService.class);

        OSGIConfigPropertiesService propertiesMock = createMock(OSGIConfigPropertiesService.class);
        expect(propertiesMock.getString(AUTH_NET_MERCHANT_DESCRIPTOR)).andReturn("MERCHANT 5035551234");
        replay(propertiesMock);

        AuthorizeNetTransactionService service = EasyMock.partialMockBuilder(AuthorizeNetTransactionService.class)
                .withConstructor(daoMock, logServiceMock, propertiesMock)
                .addMockedMethod("getNewTransactionRequest")
                .addMockedMethod("getNewTransactionController")
                .createMock();

        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
        expect(service.getNewTransactionRequest()).andReturn(apiRequest);

        String expectedTransId = "test Authorize.Net transaction id";
        String expectedErrorCode = "2";
        String expectedErrorText = "Transaction declined.";

        Timestamp now = new Timestamp(Instant.now().toEpochMilli());
        AuthorizeNetTransactionsRecord responseRecord = createMock(AuthorizeNetTransactionsRecord.class);
        expect(responseRecord.getKbPaymentId()).andReturn(expectedKbPaymentId.toString());
        expect(responseRecord.getKbPaymentTransactionId()).andReturn(expectedKbTransactionId.toString());
        expect(responseRecord.getKbTransactionType()).andReturn(expectedKbTransactionType.name());
        expect(responseRecord.getAmount()).andReturn(expectedAmount);
        expect(responseRecord.getCurrency()).andReturn(expectedCurrency.name());
        expect(responseRecord.getSuccess()).andReturn(MysqlAdapter.FALSE).anyTimes();
        expect(responseRecord.getAuthorizeNetTransactionId()).andReturn(expectedTransId);
        expect(responseRecord.getAuthCode()).andReturn(null);
        expect(responseRecord.getCreatedAt()).andReturn(now).anyTimes();
        expect(responseRecord.getTransactionError()).andReturn(expectedErrorText);
        expect(responseRecord.getTransactionStatus()).andReturn(expectedErrorCode);
        expect(responseRecord.getKbPaymentPluginStatus())
                .andReturn(PaymentPluginStatus.ERROR.name()).anyTimes();

        expect(daoMock.logTransactionResponse(eq(transaction))).andReturn(responseRecord);

        CreateTransactionController controllerMock = createMock(CreateTransactionController.class);
        controllerMock.execute();
        expectLastCall().once();
        CreateTransactionResponse response = new CreateTransactionResponse();
        expect(controllerMock.getApiResponse()).andReturn(response);

        expect(service.getNewTransactionController(eq(apiRequest))).andReturn(controllerMock);
        replay(responseRecord, daoMock, service, controllerMock);

        AuthorizeNetPaymentTransactionInfo result = service.createTransactionOnPaymentProfile(transaction, null);
        assertThat(result).isNotNull();

        assertThat(transaction.getRequestId()).isNotNull().isEqualTo(expectedRequestId);

        // verify transaction request
        TransactionRequestType transactionRequest = apiRequest.getTransactionRequest();
        assertThat(transactionRequest).isNotNull();
        assertThat(transactionRequest.getAmount()).isEqualTo(expectedAmount);
        assertThat(transactionRequest.getTransactionType()).isEqualTo(authNetTransactionType.value());

        CustomerProfilePaymentType customerProfile = transactionRequest.getProfile();
        assertThat(customerProfile).isNotNull();
        assertThat(customerProfile.getCustomerProfileId()).isEqualTo(expectedCustomerProfileId);

        PaymentProfile paymentProfile = customerProfile.getPaymentProfile();
        assertThat(paymentProfile).isNotNull();
        assertThat(paymentProfile.getPaymentProfileId()).isEqualTo(expectedCustomerPaymentProfileId);

        // verify transaction response
        assertThat(result.getKbPaymentId()).isEqualTo(expectedKbPaymentId);
        assertThat(result.getKbTransactionPaymentId()).isEqualTo(expectedKbTransactionId);
        assertThat(result.getTransactionType()).isEqualTo(expectedKbTransactionType);
        assertThat(result.getAmount()).isEqualTo(expectedAmount);
        assertThat(result.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(result.getStatus()).isEqualTo(PaymentPluginStatus.ERROR);
        assertThat(result.getGatewayError()).isNotEmpty().isEqualTo(expectedErrorText);
        assertThat(result.getGatewayErrorCode()).isNotEmpty().isEqualTo(expectedErrorCode);
        assertThat(result.getFirstPaymentReferenceId()).isEqualTo(expectedTransId);
        assertThat(result.getSecondPaymentReferenceId()).isNull();
        verify(responseRecord, daoMock, service, controllerMock, propertiesMock);
    }

    @Test
    public void testChargePaymentProfileNullResponse() {
        UUID expectedTenantId = UUID.randomUUID();
        UUID expectedKbAccountId = UUID.randomUUID();
        UUID expectedKbPaymentId = UUID.randomUUID();
        UUID expectedKbPaymentMethodId = UUID.randomUUID();
        UUID expectedKbTransactionId = UUID.randomUUID();
        TransactionType expectedKbTransactionType = TransactionType.PURCHASE;
        String expectedCustomerProfileId = "test profile id";
        String expectedCustomerPaymentProfileId = "test payment profile id";
        TransactionTypeEnum authNetTransactionType = TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION;
        BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        Currency expectedCurrency = Currency.USD;

        AuthorizeNetTransactionInfo transaction = new AuthorizeNetTransactionInfo();
        transaction.setCustomerProfileId(expectedCustomerProfileId);
        transaction.setCustomerPaymentProfileId(expectedCustomerPaymentProfileId);
        transaction.setKbAccountId(expectedKbAccountId);
        transaction.setKbPaymentId(expectedKbPaymentId);
        transaction.setKbPaymentMethodId(expectedKbPaymentMethodId);
        transaction.setKbTransactionId(expectedKbTransactionId);
        transaction.setKbTransactionType(expectedKbTransactionType);
        transaction.setTransactionType(authNetTransactionType);
        transaction.setTenantId(expectedTenantId);
        transaction.setAmount(expectedAmount);
        transaction.setCurrency(expectedCurrency);

        AuthorizeNetDAO daoMock = createMock(AuthorizeNetDAO.class);
        ULong expectedRequestId = ULong.valueOf(12345L);
        expect(daoMock.logTransactionRequest(eq(transaction))).andReturn(expectedRequestId.longValue());

        OSGIKillbillLogService logServiceMock = createMock(OSGIKillbillLogService.class);

        OSGIConfigPropertiesService propertiesMock = createMock(OSGIConfigPropertiesService.class);
        expect(propertiesMock.getString(AUTH_NET_MERCHANT_DESCRIPTOR)).andReturn("MERCHANT 5035551234");
        replay(propertiesMock);

        AuthorizeNetTransactionService service = EasyMock.partialMockBuilder(AuthorizeNetTransactionService.class)
                .withConstructor(daoMock, logServiceMock, propertiesMock)
                .addMockedMethod("getNewTransactionRequest")
                .addMockedMethod("getNewTransactionController")
                .createMock();

        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
        expect(service.getNewTransactionRequest()).andReturn(apiRequest);

        CreateTransactionController controllerMock = createMock(CreateTransactionController.class);
        controllerMock.execute();
        expectLastCall().once();
        expect(controllerMock.getApiResponse()).andReturn(null);

        expect(service.getNewTransactionController(eq(apiRequest))).andReturn(controllerMock);
        replay(daoMock, service, controllerMock);

        try {
            service.createTransactionOnPaymentProfile(transaction, null);
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isNotEmpty().contains("was null");
        }
        verify(daoMock, service, controllerMock, propertiesMock);
    }

    @Test
    public void testChargePaymentProfileInvalidCurrency() {
        Currency expectedCurrency = Currency.RUB;

        AuthorizeNetTransactionInfo transaction = new AuthorizeNetTransactionInfo();
        transaction.setCurrency(expectedCurrency);

        OSGIConfigPropertiesService propertiesMock = createMock(OSGIConfigPropertiesService.class);
        expect(propertiesMock.getString(AUTH_NET_MERCHANT_DESCRIPTOR)).andReturn("MERCHANT 5035551234");
        replay(propertiesMock);

        AuthorizeNetTransactionService service = new AuthorizeNetTransactionService(null, null, propertiesMock);

        try {
            service.createTransactionOnPaymentProfile(transaction, null);
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isNotEmpty().contains("Unsupported currency for transaction");
        }
        verify(propertiesMock);
    }

    @Test
    public void testChargePaymentProfileFailToPersistResponse() {
        UUID expectedTenantId = UUID.randomUUID();
        UUID expectedKbAccountId = UUID.randomUUID();
        UUID expectedKbPaymentId = UUID.randomUUID();
        UUID expectedKbPaymentMethodId = UUID.randomUUID();
        UUID expectedKbTransactionId = UUID.randomUUID();
        TransactionType expectedKbTransactionType = TransactionType.PURCHASE;
        String expectedCustomerProfileId = "test profile id";
        String expectedCustomerPaymentProfileId = "test payment profile id";
        TransactionTypeEnum authNetTransactionType = TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION;
        BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        Currency expectedCurrency = Currency.USD;

        AuthorizeNetTransactionInfo transaction = new AuthorizeNetTransactionInfo();
        transaction.setCustomerProfileId(expectedCustomerProfileId);
        transaction.setCustomerPaymentProfileId(expectedCustomerPaymentProfileId);
        transaction.setKbAccountId(expectedKbAccountId);
        transaction.setKbPaymentId(expectedKbPaymentId);
        transaction.setKbPaymentMethodId(expectedKbPaymentMethodId);
        transaction.setKbTransactionId(expectedKbTransactionId);
        transaction.setKbTransactionType(expectedKbTransactionType);
        transaction.setTransactionType(authNetTransactionType);
        transaction.setTenantId(expectedTenantId);
        transaction.setAmount(expectedAmount);
        transaction.setCurrency(expectedCurrency);

        AuthorizeNetDAO daoMock = createMock(AuthorizeNetDAO.class);
        long expectedRequestId = 12345L;
        expect(daoMock.logTransactionRequest(eq(transaction))).andReturn(expectedRequestId);
        RuntimeException testException = new RuntimeException("Test Exception");
        expect(daoMock.logTransactionResponse(eq(transaction))).andThrow(testException);

        OSGIKillbillLogService logServiceMock = createMock(OSGIKillbillLogService.class);
        Capture<String> messageCapture = newCapture();
        Capture<String> messageCapture2 = newCapture();
        logServiceMock.log(eq(LogService.LOG_INFO), capture(messageCapture));
        expectLastCall().once();
        logServiceMock.log(eq(LogService.LOG_ERROR), capture(messageCapture2), eq(testException));
        expectLastCall().once();

        OSGIConfigPropertiesService propertiesMock = createMock(OSGIConfigPropertiesService.class);
        expect(propertiesMock.getString(AUTH_NET_MERCHANT_DESCRIPTOR)).andReturn("MERCHANT 5035551234");
        replay(propertiesMock);

        AuthorizeNetTransactionService service = EasyMock.partialMockBuilder(AuthorizeNetTransactionService.class)
                .withConstructor(daoMock, logServiceMock, propertiesMock)
                .addMockedMethod("getNewTransactionRequest")
                .addMockedMethod("getNewTransactionController")
                .createMock();

        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
        expect(service.getNewTransactionRequest()).andReturn(apiRequest);

        CreateTransactionController controllerMock = createMock(CreateTransactionController.class);
        controllerMock.execute();
        expectLastCall().once();
        CreateTransactionResponse response = new CreateTransactionResponse();
        TransactionResponse transactionResponse = new TransactionResponse();
        String expectedTransId = "test Authorize.Net transaction id";
        transactionResponse.setTransId(expectedTransId);
        response.setTransactionResponse(transactionResponse);
        expect(controllerMock.getApiResponse()).andReturn(response);

        expect(service.getNewTransactionController(eq(apiRequest))).andReturn(controllerMock);
        replay(daoMock, service, controllerMock, logServiceMock);

        try {
            service.createTransactionOnPaymentProfile(transaction, null);
        } catch (RuntimeException e) {
            assertThat(e).isEqualTo(testException);
        }

        assertThat(messageCapture.hasCaptured()).isTrue();
        String message = messageCapture.getValue();
        assertThat(message).startsWith("Executed transaction for ");

        assertThat(messageCapture2.hasCaptured()).isTrue();
        message = messageCapture2.getValue();
        assertThat(message).contains("" + expectedRequestId)
                .contains(expectedTransId);

        verify(daoMock, service, controllerMock, propertiesMock);
    }

}
