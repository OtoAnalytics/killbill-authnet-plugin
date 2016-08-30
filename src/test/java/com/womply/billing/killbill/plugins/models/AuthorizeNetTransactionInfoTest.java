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

package com.womply.billing.killbill.plugins.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.womply.billing.killbill.plugins.db.MysqlAdapter;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetRequestsRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;

import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.MessagesType;
import net.authorize.api.contract.v1.TransactionResponse;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import org.easymock.Capture;
import org.jooq.types.ULong;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Unit tests for AuthorizeNetTransactionInfo.
 */
public class AuthorizeNetTransactionInfoTest {

    @Test
    public void testSaveIntoRequest() {
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
        long expectedReferencedTransactionRecordId = 234567L;

        AuthorizeNetTransactionInfo transactionInfo = new AuthorizeNetTransactionInfo();
        transactionInfo.setTenantId(expectedTenantId);
        transactionInfo.setKbPaymentId(expectedKbPaymentId);
        transactionInfo.setKbPaymentMethodId(expectedKbPaymentMethodId);
        transactionInfo.setKbAccountId(expectedKbAccountId);
        transactionInfo.setKbTransactionId(expectedKbTransactionId);
        transactionInfo.setKbTransactionType(expectedKbTransactionType);
        transactionInfo.setCustomerProfileId(expectedCustomerProfileId);
        transactionInfo.setCustomerPaymentProfileId(expectedCustomerPaymentProfileId);
        transactionInfo.setTransactionType(authNetTransactionType);
        transactionInfo.setAmount(expectedAmount);
        transactionInfo.setCurrency(expectedCurrency);
        transactionInfo.setKbReferencedTransactionRecordId(expectedReferencedTransactionRecordId);

        AuthorizeNetRequestsRecord record = createMock(AuthorizeNetRequestsRecord.class);
        record.setKbTenantId(eq(expectedTenantId.toString()));
        expectLastCall().once();
        record.setKbAccountId(eq(expectedKbAccountId.toString()));
        expectLastCall().once();
        record.setKbPaymentId(eq(expectedKbPaymentId.toString()));
        expectLastCall().once();
        record.setKbPaymentMethodId(eq(expectedKbPaymentMethodId.toString()));
        expectLastCall().once();
        record.setKbPaymentTransactionId(eq(expectedKbTransactionId.toString()));
        expectLastCall().once();
        record.setKbTransactionType(eq(expectedKbTransactionType.toString()));
        expectLastCall().once();
        record.setAuthorizeNetCustomerProfileId(eq(expectedCustomerProfileId));
        expectLastCall().once();
        record.setAuthorizeNetPaymentProfileId(eq(expectedCustomerPaymentProfileId));
        expectLastCall().once();
        record.setTransactionType(eq(authNetTransactionType.value()));
        expectLastCall().once();
        record.setAmount(eq(expectedAmount));
        expectLastCall().once();
        record.setCurrency(eq(expectedCurrency.name()));
        expectLastCall().once();
        record.setKbRefTransactionRecordId(eq(ULong.valueOf(expectedReferencedTransactionRecordId)));
        expectLastCall().once();

        replay(record);
        transactionInfo.saveInto(record);
        verify(record);
    }

    @Test
    public void testSaveIntoResponse() {
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
        long expectedReferencedTransactionRecordId = 234567L;

        AuthorizeNetTransactionInfo transactionInfo = new AuthorizeNetTransactionInfo();
        transactionInfo.setTenantId(expectedTenantId);
        transactionInfo.setKbPaymentId(expectedKbPaymentId);
        transactionInfo.setKbPaymentMethodId(expectedKbPaymentMethodId);
        transactionInfo.setKbAccountId(expectedKbAccountId);
        transactionInfo.setKbTransactionId(expectedKbTransactionId);
        transactionInfo.setKbTransactionType(expectedKbTransactionType);
        transactionInfo.setCustomerProfileId(expectedCustomerProfileId);
        transactionInfo.setCustomerPaymentProfileId(expectedCustomerPaymentProfileId);
        transactionInfo.setTransactionType(authNetTransactionType);
        transactionInfo.setAmount(expectedAmount);
        transactionInfo.setCurrency(expectedCurrency);
        transactionInfo.setKbReferencedTransactionRecordId(expectedReferencedTransactionRecordId);

        MessagesType messages = new MessagesType();
        messages.setResultCode(MessageTypeEnum.OK);
        MessagesType.Message message = new MessagesType.Message();
        message.setCode("I00001");
        message.setText("Successful.");
        messages.getMessage().add(message);
        CreateTransactionResponse response = new CreateTransactionResponse();
        response.setMessages(messages);

        String expectedTransId = "test Authorize.Net transaction id";
        String expectedAuthCode = "test auth code";
        String expectedAvsResultCode = "A";
        String expectedCvvResultCode = "C";
        String expectedCavvResultCode = "Ca";
        String expectedAccountType = "Visa";
        String expectedTestRequest = "0";

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setResponseCode(AuthorizeNetTransactionInfo.TRANSACTION_RESULT_SUCCESS_CODE);
        transactionResponse.setTransId(expectedTransId);
        transactionResponse.setAuthCode(expectedAuthCode);
        transactionResponse.setAvsResultCode(expectedAvsResultCode);
        transactionResponse.setCvvResultCode(expectedCvvResultCode);
        transactionResponse.setCavvResultCode(expectedCavvResultCode);
        transactionResponse.setAccountType(expectedAccountType);
        transactionResponse.setTestRequest(expectedTestRequest);
        TransactionResponse.Messages transactionMessages = new TransactionResponse.Messages();
        TransactionResponse.Messages.Message transactionMessage = new TransactionResponse.Messages.Message();
        transactionMessage.setCode(AuthorizeNetTransactionInfo.TRANSACTION_RESULT_SUCCESS_CODE);
        transactionMessage.setDescription("This transaction has been approved.");
        transactionMessages.getMessage().add(transactionMessage);
        transactionResponse.setMessages(transactionMessages);

        response.setTransactionResponse(transactionResponse);
        transactionInfo.setResponse(response);

        ULong expectedRequestId = ULong.valueOf(12345L);
        transactionInfo.setRequestId(expectedRequestId.longValue());

        AuthorizeNetTransactionsRecord record = createMock(AuthorizeNetTransactionsRecord.class);
        record.setRequestId(eq(expectedRequestId));
        expectLastCall().once();
        record.setKbTenantId(eq(expectedTenantId.toString()));
        expectLastCall().once();
        record.setKbAccountId(eq(expectedKbAccountId.toString()));
        expectLastCall().once();
        record.setKbPaymentId(eq(expectedKbPaymentId.toString()));
        expectLastCall().once();
        record.setKbPaymentMethodId(eq(expectedKbPaymentMethodId.toString()));
        expectLastCall().once();
        record.setKbPaymentTransactionId(eq(expectedKbTransactionId.toString()));
        expectLastCall().once();
        record.setKbTransactionType(eq(expectedKbTransactionType.toString()));
        expectLastCall().once();
        record.setAuthorizeNetCustomerProfileId(eq(expectedCustomerProfileId));
        expectLastCall().once();
        record.setAuthorizeNetPaymentProfileId(eq(expectedCustomerPaymentProfileId));
        expectLastCall().once();
        record.setTransactionType(eq(authNetTransactionType.value()));
        expectLastCall().once();
        record.setAmount(eq(expectedAmount));
        expectLastCall().once();
        record.setCurrency(eq(expectedCurrency.name()));
        expectLastCall().once();
        record.setKbRefTransactionRecordId(eq(ULong.valueOf(expectedReferencedTransactionRecordId)));
        expectLastCall().once();
        record.setSuccess(eq(MysqlAdapter.TRUE));
        expectLastCall().once();
        record.setKbPaymentPluginStatus(PaymentPluginStatus.PROCESSED.name());
        expectLastCall().once();
        record.setResponseStatus(eq(MessageTypeEnum.OK.value()));
        expectLastCall().once();
        Capture<String> responseMessageCapture = newCapture();
        record.setResponseMessage(capture(responseMessageCapture));
        expectLastCall().once();

        // check transaction response properties
        record.setAuthorizeNetTransactionId(eq(expectedTransId));
        expectLastCall().once();
        record.setTransactionStatus(eq(AuthorizeNetTransactionInfo.TRANSACTION_RESULT_SUCCESS_CODE));
        expectLastCall().once();
        record.setAuthCode(eq(expectedAuthCode));
        expectLastCall().once();
        record.setAvsResultCode(eq(expectedAvsResultCode));
        expectLastCall().once();
        record.setCvvResultCode(eq(expectedCvvResultCode));
        expectLastCall().once();
        record.setCavvResultCode(eq(expectedCavvResultCode));
        expectLastCall().once();
        record.setAccountType(eq(expectedAccountType));
        expectLastCall().once();
        record.setTestRequest(eq(expectedTestRequest));
        expectLastCall().once();
        Capture<String> responseTransactionMessageCapture = newCapture();
        record.setTransactionMessage(capture(responseTransactionMessageCapture));
        expectLastCall().once();
        record.setTransactionError(eq(""));
        expectLastCall().once();

        replay(record);
        transactionInfo.saveInto(record);
        verify(record);

        assertThat(responseMessageCapture.hasCaptured()).isTrue();
        assertThat(responseTransactionMessageCapture.hasCaptured()).isTrue();

        String responseMessage = responseMessageCapture.getValue();
        assertThat(responseMessage).isNotEmpty().contains(message.getCode()).contains(message.getText());

        String transactionResponseMessage = responseTransactionMessageCapture.getValue();
        assertThat(transactionResponseMessage).isNotEmpty().contains(transactionMessage.getCode())
                .contains(transactionMessage.getDescription());
    }

    @Test
    public void testSaveIntoResponseAuthorizeNetError() {
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
        long expectedReferencedTransactionRecordId = 234567L;

        AuthorizeNetTransactionInfo transactionInfo = new AuthorizeNetTransactionInfo();
        transactionInfo.setTenantId(expectedTenantId);
        transactionInfo.setKbPaymentId(expectedKbPaymentId);
        transactionInfo.setKbPaymentMethodId(expectedKbPaymentMethodId);
        transactionInfo.setKbAccountId(expectedKbAccountId);
        transactionInfo.setKbTransactionId(expectedKbTransactionId);
        transactionInfo.setKbTransactionType(expectedKbTransactionType);
        transactionInfo.setCustomerProfileId(expectedCustomerProfileId);
        transactionInfo.setCustomerPaymentProfileId(expectedCustomerPaymentProfileId);
        transactionInfo.setTransactionType(authNetTransactionType);
        transactionInfo.setAmount(expectedAmount);
        transactionInfo.setCurrency(expectedCurrency);
        transactionInfo.setKbReferencedTransactionRecordId(expectedReferencedTransactionRecordId);

        // make outer response return SUCCESS
        MessagesType messages = new MessagesType();
        messages.setResultCode(MessageTypeEnum.OK);
        MessagesType.Message message = new MessagesType.Message();
        message.setCode("I00001");
        message.setText("Successful.");
        messages.getMessage().add(message);
        CreateTransactionResponse response = new CreateTransactionResponse();
        response.setMessages(messages);

        String expectedTransId = "test Authorize.Net transaction id";
        String expectedAuthCode = "test auth code";
        String expectedAvsResultCode = "A";
        String expectedCvvResultCode = "C";
        String expectedCavvResultCode = "Ca";
        String expectedAccountType = "Visa";
        String expectedTestRequest = "0";

        // but the transaction response returns ERROR
        String expectedErrorCode = "2";
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setResponseCode(expectedErrorCode);
        transactionResponse.setTransId(expectedTransId);
        transactionResponse.setAuthCode(expectedAuthCode);
        transactionResponse.setAvsResultCode(expectedAvsResultCode);
        transactionResponse.setCvvResultCode(expectedCvvResultCode);
        transactionResponse.setCavvResultCode(expectedCavvResultCode);
        transactionResponse.setAccountType(expectedAccountType);
        transactionResponse.setTestRequest(expectedTestRequest);
        transactionResponse.setMessages(null);
        TransactionResponse.Errors errors = new TransactionResponse.Errors();
        TransactionResponse.Errors.Error error = new TransactionResponse.Errors.Error();
        error.setErrorCode(expectedErrorCode);
        error.setErrorText("This transaction has been declined.");
        errors.getError().add(error);
        transactionResponse.setErrors(errors);

        response.setTransactionResponse(transactionResponse);
        transactionInfo.setResponse(response);

        ULong expectedRequestId = ULong.valueOf(12345L);
        transactionInfo.setRequestId(expectedRequestId.longValue());

        AuthorizeNetTransactionsRecord record = createMock(AuthorizeNetTransactionsRecord.class);
        record.setRequestId(eq(expectedRequestId));
        expectLastCall().once();
        record.setKbTenantId(eq(expectedTenantId.toString()));
        expectLastCall().once();
        record.setKbAccountId(eq(expectedKbAccountId.toString()));
        expectLastCall().once();
        record.setKbPaymentId(eq(expectedKbPaymentId.toString()));
        expectLastCall().once();
        record.setKbPaymentMethodId(eq(expectedKbPaymentMethodId.toString()));
        expectLastCall().once();
        record.setKbPaymentTransactionId(eq(expectedKbTransactionId.toString()));
        expectLastCall().once();
        record.setKbTransactionType(eq(expectedKbTransactionType.toString()));
        expectLastCall().once();
        record.setAuthorizeNetCustomerProfileId(eq(expectedCustomerProfileId));
        expectLastCall().once();
        record.setAuthorizeNetPaymentProfileId(eq(expectedCustomerPaymentProfileId));
        expectLastCall().once();
        record.setTransactionType(eq(authNetTransactionType.value()));
        expectLastCall().once();
        record.setAmount(eq(expectedAmount));
        expectLastCall().once();
        record.setCurrency(eq(expectedCurrency.name()));
        expectLastCall().once();
        record.setKbRefTransactionRecordId(eq(ULong.valueOf(expectedReferencedTransactionRecordId)));
        expectLastCall().once();
        record.setSuccess(eq(MysqlAdapter.FALSE));
        expectLastCall().once();
        record.setKbPaymentPluginStatus(PaymentPluginStatus.ERROR.name());
        expectLastCall().once();
        record.setResponseStatus(eq(MessageTypeEnum.OK.value()));
        expectLastCall().once();
        Capture<String> responseMessageCapture = newCapture();
        record.setResponseMessage(capture(responseMessageCapture));
        expectLastCall().once();

        // check transaction response properties
        record.setAuthorizeNetTransactionId(eq(expectedTransId));
        expectLastCall().once();
        record.setTransactionStatus(eq(expectedErrorCode));
        expectLastCall().once();
        record.setAuthCode(eq(expectedAuthCode));
        expectLastCall().once();
        record.setAvsResultCode(eq(expectedAvsResultCode));
        expectLastCall().once();
        record.setCvvResultCode(eq(expectedCvvResultCode));
        expectLastCall().once();
        record.setCavvResultCode(eq(expectedCavvResultCode));
        expectLastCall().once();
        record.setAccountType(eq(expectedAccountType));
        expectLastCall().once();
        record.setTestRequest(eq(expectedTestRequest));
        expectLastCall().once();
        record.setTransactionMessage(eq(""));
        expectLastCall().once();
        Capture<String> responseErrorsCapture = newCapture();
        record.setTransactionError(capture(responseErrorsCapture));
        expectLastCall().once();

        replay(record);
        transactionInfo.saveInto(record);
        verify(record);

        assertThat(responseMessageCapture.hasCaptured()).isTrue();
        assertThat(responseErrorsCapture.hasCaptured()).isTrue();

        String responseMessage = responseMessageCapture.getValue();
        assertThat(responseMessage).isNotEmpty().contains(message.getCode()).contains(message.getText());

        String transactionResponseMessage = responseErrorsCapture.getValue();
        assertThat(transactionResponseMessage).isNotEmpty().contains(error.getErrorCode())
                .contains(error.getErrorText());

    }

}
