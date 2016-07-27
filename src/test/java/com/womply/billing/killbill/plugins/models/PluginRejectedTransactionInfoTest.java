package com.womply.billing.killbill.plugins.models;

import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.womply.billing.killbill.plugins.db.MysqlAdapter;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetRequestsRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;

import net.authorize.api.contract.v1.TransactionTypeEnum;
import org.jooq.types.ULong;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Unit tests for PluginRejectedTransactionInfo class.
 */
public class PluginRejectedTransactionInfoTest {

    @Test
    public void testSaveIntoResponse() {
        UUID expectedTenantId = UUID.randomUUID();
        UUID expectedKbAccountId = UUID.randomUUID();
        UUID expectedKbPaymentId = UUID.randomUUID();
        UUID expectedKbPaymentMethodId = UUID.randomUUID();
        UUID expectedKbTransactionId = UUID.randomUUID();
        TransactionType expectedKbTransactionType = TransactionType.REFUND;
        String expectedCustomerProfileId = "test profile id";
        String expectedCustomerPaymentProfileId = "test payment profile id";
        TransactionTypeEnum authNetTransactionType = TransactionTypeEnum.REFUND_TRANSACTION;
        BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        Currency expectedCurrency = Currency.USD;
        long expectedReferencedTransactionRecordId = 234567L;
        final String expectedErrorMessage = "Test transaction error message";

        AuthorizeNetTransactionInfo transactionInfo = new AuthorizeNetTransactionInfo();
        transactionInfo.setTenantId(expectedTenantId);
        transactionInfo.setKbPaymentId(expectedKbPaymentId);
        transactionInfo.setKbPaymentMethodId(expectedKbPaymentMethodId);
        transactionInfo.setKbAccountId(expectedKbAccountId);
        transactionInfo.setKbTransactionId(expectedKbTransactionId);
        transactionInfo.setKbTransactionType(expectedKbTransactionType);
        transactionInfo.setCustomerProfileId(expectedCustomerProfileId);
        transactionInfo.setCustomerPaymentProfileId(expectedCustomerPaymentProfileId);
        transactionInfo.setAmount(expectedAmount);
        transactionInfo.setCurrency(expectedCurrency);
        transactionInfo.setTransactionType(authNetTransactionType);
        transactionInfo.setKbReferencedTransactionRecordId(expectedReferencedTransactionRecordId);

        PluginRejectedTransactionInfo rejectedTransaction = new PluginRejectedTransactionInfo(transactionInfo);
        rejectedTransaction.setErrorMessage(expectedErrorMessage);

        try {
            AuthorizeNetRequestsRecord requestRecord = createMock(AuthorizeNetRequestsRecord.class);
            rejectedTransaction.saveInto(requestRecord);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            // expected
        }

        AuthorizeNetTransactionsRecord record = createMock(AuthorizeNetTransactionsRecord.class);
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
        record.setAmount(eq(expectedAmount));
        expectLastCall().once();
        record.setCurrency(eq(expectedCurrency.name()));
        expectLastCall().once();
        record.setKbRefTransactionRecordId(eq(ULong.valueOf(expectedReferencedTransactionRecordId)));
        expectLastCall().once();
        record.setSuccess(eq(MysqlAdapter.FALSE));
        expectLastCall().once();
        record.setTransactionError(eq(expectedErrorMessage));
        expectLastCall().once();
        record.setTransactionType(eq(authNetTransactionType.value()));
        expectLastCall().once();
        record.setKbPaymentPluginStatus(eq(PaymentPluginStatus.CANCELED.name()));
        expectLastCall().once();

        replay(record);
        rejectedTransaction.saveInto(record);
        verify(record);
    }

}
