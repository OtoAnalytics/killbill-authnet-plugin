package com.womply.billing.killbill.plugins.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;

import com.womply.billing.killbill.plugins.AuthorizeNetTransactionService;
import com.womply.billing.killbill.plugins.authentication.AuthorizeNetAuthenticationService;
import com.womply.billing.killbill.plugins.db.AuthorizeNetDAO;
import com.womply.billing.killbill.plugins.db.MysqlAdapter;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetPaymentMethodsRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;
import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentTransactionInfo;
import com.womply.billing.killbill.plugins.models.AuthorizeNetTransactionInfo;
import com.womply.billing.killbill.plugins.models.PluginRejectedTransactionInfo;

import net.authorize.api.contract.v1.TransactionTypeEnum;
import org.easymock.Capture;
import org.jooq.types.ULong;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;
import org.killbill.billing.tenant.api.TenantApiException;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillLogService;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Unit tests for RefundPaymentHelper class.
 */
public class RefundPaymentHelperTest {

    @Test
    public void refundPayment() throws TenantApiException {
        final UUID expectedTenantId = UUID.randomUUID();
        final UUID expectedKbAccountId = UUID.randomUUID();
        final UUID expectedKbPaymentId = UUID.randomUUID();
        final UUID expectedKbPaymentMethodId = UUID.randomUUID();
        final UUID expectedKbTransactionId = UUID.randomUUID();
        final TransactionType expectedKbTransactionType = TransactionType.REFUND;
        final TransactionTypeEnum expectedAuthNetTransactionType = TransactionTypeEnum.REFUND_TRANSACTION;
        final String expectedCustomerProfileId = "test profile id";
        final String expectedCustomerPaymentProfileId = "test payment profile id";
        final BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        final Currency expectedCurrency = Currency.USD;
        final String expectedAuthNetTransactionId = "original transaction id";

        AuthorizeNetPaymentMethodsRecord paymentMethodMock = createMock(AuthorizeNetPaymentMethodsRecord.class);
        expect(paymentMethodMock.getAuthorizeNetCustomerProfileId()).andReturn(expectedCustomerProfileId).anyTimes();
        expect(paymentMethodMock.getAuthorizeNetPaymentProfileId())
                .andReturn(expectedCustomerPaymentProfileId).anyTimes();

        final long expectedOriginalTransactionRecordId = 7890L;
        AuthorizeNetTransactionsRecord transactionRecordMock = createMock(AuthorizeNetTransactionsRecord.class);
        expect(transactionRecordMock.getRecordId())
                .andReturn(ULong.valueOf(expectedOriginalTransactionRecordId)).anyTimes();
        expect(transactionRecordMock.getAuthorizeNetCustomerProfileId()).andReturn(expectedCustomerProfileId);
        expect(transactionRecordMock.getAuthorizeNetPaymentProfileId()).andReturn(expectedCustomerPaymentProfileId);
        expect(transactionRecordMock.getAuthorizeNetTransactionId()).andReturn(expectedAuthNetTransactionId);
        expect(transactionRecordMock.getAmount()).andReturn(expectedAmount);

        List<AuthorizeNetTransactionsRecord> paymentTransactions = Arrays.asList(transactionRecordMock);

        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        expect(mockDao.getPaymentMethodForOperation(eq(expectedKbAccountId),
                eq(expectedKbPaymentMethodId), eq(expectedTenantId)))
                .andReturn(paymentMethodMock);
        expect(mockDao.getPurchaseTransactionsForPayment(eq(expectedKbAccountId), eq(expectedKbPaymentId),
                eq(expectedKbPaymentMethodId)))
                .andReturn(paymentTransactions);

        AuthorizeNetTransactionService transactionServiceMock = createMock(AuthorizeNetTransactionService.class);
        Capture<AuthorizeNetTransactionInfo> transactionCapture = newCapture();
        AuthorizeNetPaymentTransactionInfo transactionInfo = createMock(AuthorizeNetPaymentTransactionInfo.class);
        expect(transactionServiceMock.createTransactionOnPaymentProfile(capture(transactionCapture), eq(null)))
                .andReturn(transactionInfo);

        AuthorizeNetAuthenticationService authServiceMock = createMock(AuthorizeNetAuthenticationService.class);
        expect(authServiceMock.getAuthenticationForTenant(eq(expectedTenantId))).andReturn(null);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);

        replay(paymentMethodMock, transactionRecordMock, mockDao, transactionServiceMock);
        RefundPaymentHelper service = new RefundPaymentHelper(mockDao, authServiceMock,
                transactionServiceMock, logServiceMock);

        AuthorizeNetPaymentTransactionInfo result = service.refundPayment(expectedTenantId, expectedKbAccountId,
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
        assertThat(transaction.getTransactionType()).isEqualTo(expectedAuthNetTransactionType);
        assertThat(transaction.getTenantId()).isEqualTo(expectedTenantId);
        assertThat(transaction.getAmount()).isEqualTo(expectedAmount);
        assertThat(transaction.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(transaction.getKbReferencedTransactionRecordId()).isEqualTo(expectedOriginalTransactionRecordId);
        assertThat(transaction.getAuthorizeNetReferencedTransactionId()).isEqualTo(expectedAuthNetTransactionId);
    }

    @Test
    public void refundPaymentErrorNoPurchaseTransactions() throws TenantApiException {
        final UUID expectedTenantId = UUID.randomUUID();
        final UUID expectedKbAccountId = UUID.randomUUID();
        final UUID expectedKbPaymentId = UUID.randomUUID();
        final UUID expectedKbPaymentMethodId = UUID.randomUUID();
        final UUID expectedKbTransactionId = UUID.randomUUID();
        final TransactionType expectedKbTransactionType = TransactionType.REFUND;
        final TransactionTypeEnum expectedAuthNetTransactionType = TransactionTypeEnum.REFUND_TRANSACTION;
        final String expectedCustomerProfileId = "test profile id";
        final String expectedCustomerPaymentProfileId = "test payment profile id";
        final BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        final Currency expectedCurrency = Currency.USD;
        final String expectedErrorMessage = "No transactions to refund";

        AuthorizeNetPaymentMethodsRecord paymentMethodMock = createMock(AuthorizeNetPaymentMethodsRecord.class);
        expect(paymentMethodMock.getAuthorizeNetCustomerProfileId()).andReturn(expectedCustomerProfileId).anyTimes();
        expect(paymentMethodMock.getAuthorizeNetPaymentProfileId())
                .andReturn(expectedCustomerPaymentProfileId).anyTimes();

        // empty list of purchase transactions for the given payment
        List<AuthorizeNetTransactionsRecord> paymentTransactions = new ArrayList<>();

        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        expect(mockDao.getPaymentMethodForOperation(eq(expectedKbAccountId),
                eq(expectedKbPaymentMethodId), eq(expectedTenantId)))
                .andReturn(paymentMethodMock);
        expect(mockDao.getPurchaseTransactionsForPayment(eq(expectedKbAccountId), eq(expectedKbPaymentId),
                eq(expectedKbPaymentMethodId)))
                .andReturn(paymentTransactions);

        Timestamp now = new Timestamp(Instant.now().toEpochMilli());
        AuthorizeNetTransactionsRecord rejectedTransactionRecord = createMock(AuthorizeNetTransactionsRecord.class);
        expect(rejectedTransactionRecord.getKbPaymentId()).andReturn(expectedKbPaymentId.toString());
        expect(rejectedTransactionRecord.getKbPaymentTransactionId()).andReturn(expectedKbTransactionId.toString());
        expect(rejectedTransactionRecord.getKbTransactionType()).andReturn(expectedKbTransactionType.name());
        expect(rejectedTransactionRecord.getAmount()).andReturn(expectedAmount);
        expect(rejectedTransactionRecord.getCurrency()).andReturn(expectedCurrency.name());
        expect(rejectedTransactionRecord.getSuccess()).andReturn(MysqlAdapter.FALSE).anyTimes();
        expect(rejectedTransactionRecord.getAuthorizeNetTransactionId()).andReturn(null);
        expect(rejectedTransactionRecord.getAuthCode()).andReturn(null);
        expect(rejectedTransactionRecord.getCreatedAt()).andReturn(now).anyTimes();
        expect(rejectedTransactionRecord.getKbPaymentPluginStatus())
                .andReturn(PaymentPluginStatus.CANCELED.name()).anyTimes();
        expect(rejectedTransactionRecord.getTransactionError()).andReturn(expectedErrorMessage);
        expect(rejectedTransactionRecord.getTransactionStatus()).andReturn(null);

        Capture<PluginRejectedTransactionInfo> rejectedTransactionCapture = newCapture();
        expect(mockDao.logTransactionResponse(capture(rejectedTransactionCapture)))
                .andReturn(rejectedTransactionRecord);

        AuthorizeNetAuthenticationService authServiceMock = createMock(AuthorizeNetAuthenticationService.class);
        expect(authServiceMock.getAuthenticationForTenant(eq(expectedTenantId))).andReturn(null);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);

        replay(paymentMethodMock, mockDao, rejectedTransactionRecord);
        RefundPaymentHelper service = new RefundPaymentHelper(mockDao, authServiceMock,
                null, logServiceMock);

        AuthorizeNetPaymentTransactionInfo result = service.refundPayment(expectedTenantId, expectedKbAccountId,
                expectedKbPaymentId, expectedKbTransactionId, expectedKbPaymentMethodId, expectedAmount,
                expectedCurrency);

        assertThat(result).isNotNull();
        assertThat(result.getKbPaymentId()).isEqualTo(expectedKbPaymentId);
        assertThat(result.getKbTransactionPaymentId()).isEqualTo(expectedKbTransactionId);
        assertThat(result.getTransactionType()).isEqualTo(expectedKbTransactionType);
        assertThat(result.getAmount()).isEqualTo(expectedAmount);
        assertThat(result.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(result.getStatus()).isEqualTo(PaymentPluginStatus.CANCELED);
        assertThat(result.getGatewayError()).isEqualTo(expectedErrorMessage);
        assertThat(result.getFirstPaymentReferenceId()).isEqualTo(null);
        assertThat(result.getSecondPaymentReferenceId()).isEqualTo(null);

        assertThat(rejectedTransactionCapture.hasCaptured()).isTrue();
        PluginRejectedTransactionInfo rejectedTransaction = rejectedTransactionCapture.getValue();
        assertThat(rejectedTransaction.getCustomerProfileId()).isEqualTo(expectedCustomerProfileId);
        assertThat(rejectedTransaction.getCustomerPaymentProfileId()).isEqualTo(expectedCustomerPaymentProfileId);
        assertThat(rejectedTransaction.getKbAccountId()).isEqualTo(expectedKbAccountId);
        assertThat(rejectedTransaction.getKbPaymentId()).isEqualTo(expectedKbPaymentId);
        assertThat(rejectedTransaction.getKbPaymentMethodId()).isEqualTo(expectedKbPaymentMethodId);
        assertThat(rejectedTransaction.getKbTransactionId()).isEqualTo(expectedKbTransactionId);
        assertThat(rejectedTransaction.getKbTransactionType()).isEqualTo(expectedKbTransactionType);
        assertThat(rejectedTransaction.getTransactionType()).isEqualTo(expectedAuthNetTransactionType);
        assertThat(rejectedTransaction.getTenantId()).isEqualTo(expectedTenantId);
        assertThat(rejectedTransaction.getAmount()).isEqualTo(expectedAmount);
        assertThat(rejectedTransaction.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(rejectedTransaction.getKbReferencedTransactionRecordId()).isEqualTo(0L);
        assertThat(rejectedTransaction.getAuthorizeNetReferencedTransactionId()).isEqualTo(null);
    }

    @Test
    public void refundPaymentErrorMultiplePurchaseTransactions() throws TenantApiException {
        final UUID expectedTenantId = UUID.randomUUID();
        final UUID expectedKbAccountId = UUID.randomUUID();
        final UUID expectedKbPaymentId = UUID.randomUUID();
        final UUID expectedKbPaymentMethodId = UUID.randomUUID();
        final UUID expectedKbTransactionId = UUID.randomUUID();
        final TransactionType expectedKbTransactionType = TransactionType.REFUND;
        final TransactionTypeEnum expectedAuthNetTransactionType = TransactionTypeEnum.REFUND_TRANSACTION;
        final String expectedCustomerProfileId = "test profile id";
        final String expectedCustomerPaymentProfileId = "test payment profile id";
        final BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        final Currency expectedCurrency = Currency.USD;
        final String expectedErrorMessage = "Multiple transactions to refund";

        AuthorizeNetPaymentMethodsRecord paymentMethodMock = createMock(AuthorizeNetPaymentMethodsRecord.class);
        expect(paymentMethodMock.getAuthorizeNetCustomerProfileId()).andReturn(expectedCustomerProfileId).anyTimes();
        expect(paymentMethodMock.getAuthorizeNetPaymentProfileId())
                .andReturn(expectedCustomerPaymentProfileId).anyTimes();

        // a list of multiple purchase transactions for the given payment
        AuthorizeNetTransactionsRecord recordMock1 = createMock(AuthorizeNetTransactionsRecord.class);
        AuthorizeNetTransactionsRecord recordMock2 = createMock(AuthorizeNetTransactionsRecord.class);
        List<AuthorizeNetTransactionsRecord> paymentTransactions = Arrays.asList(recordMock1, recordMock2);

        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        expect(mockDao.getPaymentMethodForOperation(eq(expectedKbAccountId),
                eq(expectedKbPaymentMethodId), eq(expectedTenantId)))
                .andReturn(paymentMethodMock);
        expect(mockDao.getPurchaseTransactionsForPayment(eq(expectedKbAccountId), eq(expectedKbPaymentId),
                eq(expectedKbPaymentMethodId)))
                .andReturn(paymentTransactions);

        Timestamp now = new Timestamp(Instant.now().toEpochMilli());
        AuthorizeNetTransactionsRecord rejectedTransactionRecord = createMock(AuthorizeNetTransactionsRecord.class);
        expect(rejectedTransactionRecord.getKbPaymentId()).andReturn(expectedKbPaymentId.toString());
        expect(rejectedTransactionRecord.getKbPaymentTransactionId()).andReturn(expectedKbTransactionId.toString());
        expect(rejectedTransactionRecord.getKbTransactionType()).andReturn(expectedKbTransactionType.name());
        expect(rejectedTransactionRecord.getAmount()).andReturn(expectedAmount);
        expect(rejectedTransactionRecord.getCurrency()).andReturn(expectedCurrency.name());
        expect(rejectedTransactionRecord.getSuccess()).andReturn(MysqlAdapter.FALSE).anyTimes();
        expect(rejectedTransactionRecord.getAuthorizeNetTransactionId()).andReturn(null);
        expect(rejectedTransactionRecord.getAuthCode()).andReturn(null);
        expect(rejectedTransactionRecord.getCreatedAt()).andReturn(now).anyTimes();
        expect(rejectedTransactionRecord.getKbPaymentPluginStatus())
                .andReturn(PaymentPluginStatus.CANCELED.name()).anyTimes();
        expect(rejectedTransactionRecord.getTransactionError()).andReturn(expectedErrorMessage);
        expect(rejectedTransactionRecord.getTransactionStatus()).andReturn(null);

        Capture<PluginRejectedTransactionInfo> rejectedTransactionCapture = newCapture();
        expect(mockDao.logTransactionResponse(capture(rejectedTransactionCapture)))
                .andReturn(rejectedTransactionRecord);

        AuthorizeNetAuthenticationService authServiceMock = createMock(AuthorizeNetAuthenticationService.class);
        expect(authServiceMock.getAuthenticationForTenant(eq(expectedTenantId))).andReturn(null);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);

        replay(paymentMethodMock, mockDao, rejectedTransactionRecord);
        RefundPaymentHelper service = new RefundPaymentHelper(mockDao, authServiceMock,
                null, logServiceMock);

        AuthorizeNetPaymentTransactionInfo result = service.refundPayment(expectedTenantId, expectedKbAccountId,
                expectedKbPaymentId, expectedKbTransactionId, expectedKbPaymentMethodId, expectedAmount,
                expectedCurrency);

        assertThat(result).isNotNull();
        assertThat(result.getKbPaymentId()).isEqualTo(expectedKbPaymentId);
        assertThat(result.getKbTransactionPaymentId()).isEqualTo(expectedKbTransactionId);
        assertThat(result.getTransactionType()).isEqualTo(expectedKbTransactionType);
        assertThat(result.getAmount()).isEqualTo(expectedAmount);
        assertThat(result.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(result.getStatus()).isEqualTo(PaymentPluginStatus.CANCELED);
        assertThat(result.getGatewayError()).isEqualTo(expectedErrorMessage);
        assertThat(result.getFirstPaymentReferenceId()).isEqualTo(null);
        assertThat(result.getSecondPaymentReferenceId()).isEqualTo(null);

        assertThat(rejectedTransactionCapture.hasCaptured()).isTrue();
        PluginRejectedTransactionInfo rejectedTransaction = rejectedTransactionCapture.getValue();
        assertThat(rejectedTransaction.getCustomerProfileId()).isEqualTo(expectedCustomerProfileId);
        assertThat(rejectedTransaction.getCustomerPaymentProfileId()).isEqualTo(expectedCustomerPaymentProfileId);
        assertThat(rejectedTransaction.getKbAccountId()).isEqualTo(expectedKbAccountId);
        assertThat(rejectedTransaction.getKbPaymentId()).isEqualTo(expectedKbPaymentId);
        assertThat(rejectedTransaction.getKbPaymentMethodId()).isEqualTo(expectedKbPaymentMethodId);
        assertThat(rejectedTransaction.getKbTransactionId()).isEqualTo(expectedKbTransactionId);
        assertThat(rejectedTransaction.getKbTransactionType()).isEqualTo(expectedKbTransactionType);
        assertThat(rejectedTransaction.getTransactionType()).isEqualTo(expectedAuthNetTransactionType);
        assertThat(rejectedTransaction.getTenantId()).isEqualTo(expectedTenantId);
        assertThat(rejectedTransaction.getAmount()).isEqualTo(expectedAmount);
        assertThat(rejectedTransaction.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(rejectedTransaction.getKbReferencedTransactionRecordId()).isEqualTo(0L);
        assertThat(rejectedTransaction.getAuthorizeNetReferencedTransactionId()).isEqualTo(null);
    }

    @Test
    public void refundPaymentErrorMismatchOnCustomerProfileId() throws TenantApiException {
        final UUID expectedTenantId = UUID.randomUUID();
        final UUID expectedKbAccountId = UUID.randomUUID();
        final UUID expectedKbPaymentId = UUID.randomUUID();
        final UUID expectedKbPaymentMethodId = UUID.randomUUID();
        final UUID expectedKbTransactionId = UUID.randomUUID();
        final TransactionType expectedKbTransactionType = TransactionType.REFUND;
        final TransactionTypeEnum expectedAuthNetTransactionType = TransactionTypeEnum.REFUND_TRANSACTION;
        final String expectedCustomerProfileId = "test profile id";
        final String expectedCustomerPaymentProfileId = "test payment profile id";
        final BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        final Currency expectedCurrency = Currency.USD;
        final String expectedErrorMessage = "Mismatch on customer profile id";
        final String expectedAuthNetTransactionId = "original transaction id";

        AuthorizeNetPaymentMethodsRecord paymentMethodMock = createMock(AuthorizeNetPaymentMethodsRecord.class);
        expect(paymentMethodMock.getRecordId()).andReturn(ULong.valueOf(12234L)).anyTimes();
        expect(paymentMethodMock.getAuthorizeNetCustomerProfileId()).andReturn(expectedCustomerProfileId).anyTimes();
        expect(paymentMethodMock.getAuthorizeNetPaymentProfileId())
                .andReturn(expectedCustomerPaymentProfileId).anyTimes();

        final long expectedOriginalTransactionRecordId = 7890L;
        AuthorizeNetTransactionsRecord transactionRecordMock = createMock(AuthorizeNetTransactionsRecord.class);
        expect(transactionRecordMock.getRecordId())
                .andReturn(ULong.valueOf(expectedOriginalTransactionRecordId)).anyTimes();
        expect(transactionRecordMock.getAuthorizeNetCustomerProfileId()).andReturn("unexpected profile id");
        expect(transactionRecordMock.getAuthorizeNetPaymentProfileId()).andReturn(expectedCustomerPaymentProfileId);
        expect(transactionRecordMock.getAuthorizeNetTransactionId()).andReturn(expectedAuthNetTransactionId);
        expect(transactionRecordMock.getAmount()).andReturn(expectedAmount);

        List<AuthorizeNetTransactionsRecord> paymentTransactions = Arrays.asList(transactionRecordMock);

        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        expect(mockDao.getPaymentMethodForOperation(eq(expectedKbAccountId),
                eq(expectedKbPaymentMethodId), eq(expectedTenantId)))
                .andReturn(paymentMethodMock);
        expect(mockDao.getPurchaseTransactionsForPayment(eq(expectedKbAccountId), eq(expectedKbPaymentId),
                eq(expectedKbPaymentMethodId)))
                .andReturn(paymentTransactions);

        Timestamp now = new Timestamp(Instant.now().toEpochMilli());
        AuthorizeNetTransactionsRecord rejectedTransactionRecord = createMock(AuthorizeNetTransactionsRecord.class);
        expect(rejectedTransactionRecord.getKbPaymentId()).andReturn(expectedKbPaymentId.toString());
        expect(rejectedTransactionRecord.getKbPaymentTransactionId()).andReturn(expectedKbTransactionId.toString());
        expect(rejectedTransactionRecord.getKbTransactionType()).andReturn(expectedKbTransactionType.name());
        expect(rejectedTransactionRecord.getAmount()).andReturn(expectedAmount);
        expect(rejectedTransactionRecord.getCurrency()).andReturn(expectedCurrency.name());
        expect(rejectedTransactionRecord.getSuccess()).andReturn(MysqlAdapter.FALSE).anyTimes();
        expect(rejectedTransactionRecord.getAuthorizeNetTransactionId()).andReturn(null);
        expect(rejectedTransactionRecord.getAuthCode()).andReturn(null);
        expect(rejectedTransactionRecord.getCreatedAt()).andReturn(now).anyTimes();
        expect(rejectedTransactionRecord.getKbPaymentPluginStatus())
                .andReturn(PaymentPluginStatus.CANCELED.name()).anyTimes();
        expect(rejectedTransactionRecord.getTransactionError()).andReturn(expectedErrorMessage);
        expect(rejectedTransactionRecord.getTransactionStatus()).andReturn(null);

        Capture<PluginRejectedTransactionInfo> rejectedTransactionCapture = newCapture();
        expect(mockDao.logTransactionResponse(capture(rejectedTransactionCapture)))
                .andReturn(rejectedTransactionRecord);

        AuthorizeNetAuthenticationService authServiceMock = createMock(AuthorizeNetAuthenticationService.class);
        expect(authServiceMock.getAuthenticationForTenant(eq(expectedTenantId))).andReturn(null);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);

        replay(paymentMethodMock, transactionRecordMock, mockDao, rejectedTransactionRecord);
        RefundPaymentHelper service = new RefundPaymentHelper(mockDao, authServiceMock,
                null, logServiceMock);

        AuthorizeNetPaymentTransactionInfo result = service.refundPayment(expectedTenantId, expectedKbAccountId,
                expectedKbPaymentId, expectedKbTransactionId, expectedKbPaymentMethodId, expectedAmount,
                expectedCurrency);

        assertThat(result).isNotNull();
        assertThat(result.getKbPaymentId()).isEqualTo(expectedKbPaymentId);
        assertThat(result.getKbTransactionPaymentId()).isEqualTo(expectedKbTransactionId);
        assertThat(result.getTransactionType()).isEqualTo(expectedKbTransactionType);
        assertThat(result.getAmount()).isEqualTo(expectedAmount);
        assertThat(result.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(result.getStatus()).isEqualTo(PaymentPluginStatus.CANCELED);
        assertThat(result.getGatewayError()).isEqualTo(expectedErrorMessage);
        assertThat(result.getFirstPaymentReferenceId()).isEqualTo(null);
        assertThat(result.getSecondPaymentReferenceId()).isEqualTo(null);

        assertThat(rejectedTransactionCapture.hasCaptured()).isTrue();
        PluginRejectedTransactionInfo rejectedTransaction = rejectedTransactionCapture.getValue();
        assertThat(rejectedTransaction.getCustomerProfileId()).isEqualTo(expectedCustomerProfileId);
        assertThat(rejectedTransaction.getCustomerPaymentProfileId()).isEqualTo(expectedCustomerPaymentProfileId);
        assertThat(rejectedTransaction.getKbAccountId()).isEqualTo(expectedKbAccountId);
        assertThat(rejectedTransaction.getKbPaymentId()).isEqualTo(expectedKbPaymentId);
        assertThat(rejectedTransaction.getKbPaymentMethodId()).isEqualTo(expectedKbPaymentMethodId);
        assertThat(rejectedTransaction.getKbTransactionId()).isEqualTo(expectedKbTransactionId);
        assertThat(rejectedTransaction.getKbTransactionType()).isEqualTo(expectedKbTransactionType);
        assertThat(rejectedTransaction.getTransactionType()).isEqualTo(expectedAuthNetTransactionType);
        assertThat(rejectedTransaction.getTenantId()).isEqualTo(expectedTenantId);
        assertThat(rejectedTransaction.getAmount()).isEqualTo(expectedAmount);
        assertThat(rejectedTransaction.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(rejectedTransaction.getKbReferencedTransactionRecordId())
                .isEqualTo(expectedOriginalTransactionRecordId);
        assertThat(rejectedTransaction.getAuthorizeNetReferencedTransactionId()).isEqualTo(null);

    }

    @Test
    public void refundPaymentErrorMismatchOnCustomerPaymentProfileId() throws TenantApiException {
        final UUID expectedTenantId = UUID.randomUUID();
        final UUID expectedKbAccountId = UUID.randomUUID();
        final UUID expectedKbPaymentId = UUID.randomUUID();
        final UUID expectedKbPaymentMethodId = UUID.randomUUID();
        final UUID expectedKbTransactionId = UUID.randomUUID();
        final TransactionType expectedKbTransactionType = TransactionType.REFUND;
        final TransactionTypeEnum expectedAuthNetTransactionType = TransactionTypeEnum.REFUND_TRANSACTION;
        final String expectedCustomerProfileId = "test profile id";
        final String expectedCustomerPaymentProfileId = "test payment profile id";
        final BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        final Currency expectedCurrency = Currency.USD;
        final String expectedErrorMessage = "Mismatch on customer payment profile id";
        final String expectedAuthNetTransactionId = "original transaction id";

        AuthorizeNetPaymentMethodsRecord paymentMethodMock = createMock(AuthorizeNetPaymentMethodsRecord.class);
        expect(paymentMethodMock.getRecordId()).andReturn(ULong.valueOf(12234L)).anyTimes();
        expect(paymentMethodMock.getAuthorizeNetCustomerProfileId()).andReturn(expectedCustomerProfileId).anyTimes();
        expect(paymentMethodMock.getAuthorizeNetPaymentProfileId())
                .andReturn(expectedCustomerPaymentProfileId).anyTimes();

        final long expectedOriginalTransactionRecordId = 7890L;
        AuthorizeNetTransactionsRecord transactionRecordMock = createMock(AuthorizeNetTransactionsRecord.class);
        expect(transactionRecordMock.getRecordId())
                .andReturn(ULong.valueOf(expectedOriginalTransactionRecordId)).anyTimes();
        expect(transactionRecordMock.getAuthorizeNetCustomerProfileId()).andReturn(expectedCustomerProfileId);
        expect(transactionRecordMock.getAuthorizeNetPaymentProfileId()).andReturn("unexpected payment profile id");
        expect(transactionRecordMock.getAuthorizeNetTransactionId()).andReturn(expectedAuthNetTransactionId);
        expect(transactionRecordMock.getAmount()).andReturn(expectedAmount);

        List<AuthorizeNetTransactionsRecord> paymentTransactions = Arrays.asList(transactionRecordMock);

        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        expect(mockDao.getPaymentMethodForOperation(eq(expectedKbAccountId),
                eq(expectedKbPaymentMethodId), eq(expectedTenantId)))
                .andReturn(paymentMethodMock);
        expect(mockDao.getPurchaseTransactionsForPayment(eq(expectedKbAccountId), eq(expectedKbPaymentId),
                eq(expectedKbPaymentMethodId)))
                .andReturn(paymentTransactions);

        Timestamp now = new Timestamp(Instant.now().toEpochMilli());
        AuthorizeNetTransactionsRecord rejectedTransactionRecord = createMock(AuthorizeNetTransactionsRecord.class);
        expect(rejectedTransactionRecord.getKbPaymentId()).andReturn(expectedKbPaymentId.toString());
        expect(rejectedTransactionRecord.getKbPaymentTransactionId()).andReturn(expectedKbTransactionId.toString());
        expect(rejectedTransactionRecord.getKbTransactionType()).andReturn(expectedKbTransactionType.name());
        expect(rejectedTransactionRecord.getAmount()).andReturn(expectedAmount);
        expect(rejectedTransactionRecord.getCurrency()).andReturn(expectedCurrency.name());
        expect(rejectedTransactionRecord.getSuccess()).andReturn(MysqlAdapter.FALSE).anyTimes();
        expect(rejectedTransactionRecord.getAuthorizeNetTransactionId()).andReturn(null);
        expect(rejectedTransactionRecord.getAuthCode()).andReturn(null);
        expect(rejectedTransactionRecord.getCreatedAt()).andReturn(now).anyTimes();
        expect(rejectedTransactionRecord.getKbPaymentPluginStatus())
                .andReturn(PaymentPluginStatus.CANCELED.name()).anyTimes();
        expect(rejectedTransactionRecord.getTransactionError()).andReturn(expectedErrorMessage);
        expect(rejectedTransactionRecord.getTransactionStatus()).andReturn(null);

        Capture<PluginRejectedTransactionInfo> rejectedTransactionCapture = newCapture();
        expect(mockDao.logTransactionResponse(capture(rejectedTransactionCapture)))
                .andReturn(rejectedTransactionRecord);

        AuthorizeNetAuthenticationService authServiceMock = createMock(AuthorizeNetAuthenticationService.class);
        expect(authServiceMock.getAuthenticationForTenant(eq(expectedTenantId))).andReturn(null);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);

        replay(paymentMethodMock, transactionRecordMock, mockDao, rejectedTransactionRecord);
        RefundPaymentHelper service = new RefundPaymentHelper(mockDao, authServiceMock,
                null, logServiceMock);

        AuthorizeNetPaymentTransactionInfo result = service.refundPayment(expectedTenantId, expectedKbAccountId,
                expectedKbPaymentId, expectedKbTransactionId, expectedKbPaymentMethodId, expectedAmount,
                expectedCurrency);

        assertThat(result).isNotNull();
        assertThat(result.getKbPaymentId()).isEqualTo(expectedKbPaymentId);
        assertThat(result.getKbTransactionPaymentId()).isEqualTo(expectedKbTransactionId);
        assertThat(result.getTransactionType()).isEqualTo(expectedKbTransactionType);
        assertThat(result.getAmount()).isEqualTo(expectedAmount);
        assertThat(result.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(result.getStatus()).isEqualTo(PaymentPluginStatus.CANCELED);
        assertThat(result.getGatewayError()).isEqualTo(expectedErrorMessage);
        assertThat(result.getFirstPaymentReferenceId()).isEqualTo(null);
        assertThat(result.getSecondPaymentReferenceId()).isEqualTo(null);

        assertThat(rejectedTransactionCapture.hasCaptured()).isTrue();
        PluginRejectedTransactionInfo rejectedTransaction = rejectedTransactionCapture.getValue();
        assertThat(rejectedTransaction.getCustomerProfileId()).isEqualTo(expectedCustomerProfileId);
        assertThat(rejectedTransaction.getCustomerPaymentProfileId()).isEqualTo(expectedCustomerPaymentProfileId);
        assertThat(rejectedTransaction.getKbAccountId()).isEqualTo(expectedKbAccountId);
        assertThat(rejectedTransaction.getKbPaymentId()).isEqualTo(expectedKbPaymentId);
        assertThat(rejectedTransaction.getKbPaymentMethodId()).isEqualTo(expectedKbPaymentMethodId);
        assertThat(rejectedTransaction.getKbTransactionId()).isEqualTo(expectedKbTransactionId);
        assertThat(rejectedTransaction.getKbTransactionType()).isEqualTo(expectedKbTransactionType);
        assertThat(rejectedTransaction.getTransactionType()).isEqualTo(expectedAuthNetTransactionType);
        assertThat(rejectedTransaction.getTenantId()).isEqualTo(expectedTenantId);
        assertThat(rejectedTransaction.getAmount()).isEqualTo(expectedAmount);
        assertThat(rejectedTransaction.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(rejectedTransaction.getKbReferencedTransactionRecordId())
                .isEqualTo(expectedOriginalTransactionRecordId);
        assertThat(rejectedTransaction.getAuthorizeNetReferencedTransactionId()).isEqualTo(null);

    }

    @Test
    public void refundPaymentErrorAmountToRefundGreaterThanOriginalAmount() throws TenantApiException {
        final UUID expectedTenantId = UUID.randomUUID();
        final UUID expectedKbAccountId = UUID.randomUUID();
        final UUID expectedKbPaymentId = UUID.randomUUID();
        final UUID expectedKbPaymentMethodId = UUID.randomUUID();
        final UUID expectedKbTransactionId = UUID.randomUUID();
        final TransactionType expectedKbTransactionType = TransactionType.REFUND;
        final TransactionTypeEnum expectedAuthNetTransactionType = TransactionTypeEnum.REFUND_TRANSACTION;
        final String expectedCustomerProfileId = "test profile id";
        final String expectedCustomerPaymentProfileId = "test payment profile id";
        final BigDecimal originalAmount = BigDecimal.valueOf(70.03);
        final BigDecimal refundAmount = BigDecimal.valueOf(80.03);
        final Currency expectedCurrency = Currency.USD;
        final String expectedErrorMessage = "Requested refund amount exceeds original transaction amount";
        final String expectedAuthNetTransactionId = "original transaction id";

        AuthorizeNetPaymentMethodsRecord paymentMethodMock = createMock(AuthorizeNetPaymentMethodsRecord.class);
        expect(paymentMethodMock.getRecordId()).andReturn(ULong.valueOf(12234L)).anyTimes();
        expect(paymentMethodMock.getAuthorizeNetCustomerProfileId()).andReturn(expectedCustomerProfileId).anyTimes();
        expect(paymentMethodMock.getAuthorizeNetPaymentProfileId())
                .andReturn(expectedCustomerPaymentProfileId).anyTimes();

        final long expectedOriginalTransactionRecordId = 7890L;
        AuthorizeNetTransactionsRecord transactionRecordMock = createMock(AuthorizeNetTransactionsRecord.class);
        expect(transactionRecordMock.getRecordId())
                .andReturn(ULong.valueOf(expectedOriginalTransactionRecordId)).anyTimes();
        expect(transactionRecordMock.getAuthorizeNetCustomerProfileId()).andReturn(expectedCustomerProfileId);
        expect(transactionRecordMock.getAuthorizeNetPaymentProfileId()).andReturn(expectedCustomerPaymentProfileId);
        expect(transactionRecordMock.getAmount()).andReturn(originalAmount).anyTimes();

        List<AuthorizeNetTransactionsRecord> paymentTransactions = Arrays.asList(transactionRecordMock);

        AuthorizeNetDAO mockDao = createMock(AuthorizeNetDAO.class);
        expect(mockDao.getPaymentMethodForOperation(eq(expectedKbAccountId),
                eq(expectedKbPaymentMethodId), eq(expectedTenantId)))
                .andReturn(paymentMethodMock);
        expect(mockDao.getPurchaseTransactionsForPayment(eq(expectedKbAccountId), eq(expectedKbPaymentId),
                eq(expectedKbPaymentMethodId)))
                .andReturn(paymentTransactions);

        Timestamp now = new Timestamp(Instant.now().toEpochMilli());
        AuthorizeNetTransactionsRecord rejectedTransactionRecord = createMock(AuthorizeNetTransactionsRecord.class);
        expect(rejectedTransactionRecord.getKbPaymentId()).andReturn(expectedKbPaymentId.toString());
        expect(rejectedTransactionRecord.getKbPaymentTransactionId()).andReturn(expectedKbTransactionId.toString());
        expect(rejectedTransactionRecord.getKbTransactionType()).andReturn(expectedKbTransactionType.name());
        expect(rejectedTransactionRecord.getAmount()).andReturn(refundAmount);
        expect(rejectedTransactionRecord.getCurrency()).andReturn(expectedCurrency.name());
        expect(rejectedTransactionRecord.getSuccess()).andReturn(MysqlAdapter.FALSE).anyTimes();
        expect(rejectedTransactionRecord.getAuthorizeNetTransactionId()).andReturn(null);
        expect(rejectedTransactionRecord.getAuthCode()).andReturn(null);
        expect(rejectedTransactionRecord.getCreatedAt()).andReturn(now).anyTimes();
        expect(rejectedTransactionRecord.getKbPaymentPluginStatus())
                .andReturn(PaymentPluginStatus.CANCELED.name()).anyTimes();
        expect(rejectedTransactionRecord.getTransactionError()).andReturn(expectedErrorMessage);
        expect(rejectedTransactionRecord.getTransactionStatus()).andReturn(null);

        Capture<PluginRejectedTransactionInfo> rejectedTransactionCapture = newCapture();
        expect(mockDao.logTransactionResponse(capture(rejectedTransactionCapture)))
                .andReturn(rejectedTransactionRecord);

        AuthorizeNetAuthenticationService authServiceMock = createMock(AuthorizeNetAuthenticationService.class);
        expect(authServiceMock.getAuthenticationForTenant(eq(expectedTenantId))).andReturn(null);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);

        replay(paymentMethodMock, transactionRecordMock, mockDao, rejectedTransactionRecord);
        RefundPaymentHelper service = new RefundPaymentHelper(mockDao, authServiceMock,
                null, logServiceMock);

        AuthorizeNetPaymentTransactionInfo result = service.refundPayment(expectedTenantId, expectedKbAccountId,
                expectedKbPaymentId, expectedKbTransactionId, expectedKbPaymentMethodId, refundAmount,
                expectedCurrency);

        assertThat(result).isNotNull();
        assertThat(result.getKbPaymentId()).isEqualTo(expectedKbPaymentId);
        assertThat(result.getKbTransactionPaymentId()).isEqualTo(expectedKbTransactionId);
        assertThat(result.getTransactionType()).isEqualTo(expectedKbTransactionType);
        assertThat(result.getAmount()).isEqualTo(refundAmount);
        assertThat(result.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(result.getStatus()).isEqualTo(PaymentPluginStatus.CANCELED);
        assertThat(result.getGatewayError()).isEqualTo(expectedErrorMessage);
        assertThat(result.getFirstPaymentReferenceId()).isEqualTo(null);
        assertThat(result.getSecondPaymentReferenceId()).isEqualTo(null);

        assertThat(rejectedTransactionCapture.hasCaptured()).isTrue();
        PluginRejectedTransactionInfo rejectedTransaction = rejectedTransactionCapture.getValue();
        assertThat(rejectedTransaction.getCustomerProfileId()).isEqualTo(expectedCustomerProfileId);
        assertThat(rejectedTransaction.getCustomerPaymentProfileId()).isEqualTo(expectedCustomerPaymentProfileId);
        assertThat(rejectedTransaction.getKbAccountId()).isEqualTo(expectedKbAccountId);
        assertThat(rejectedTransaction.getKbPaymentId()).isEqualTo(expectedKbPaymentId);
        assertThat(rejectedTransaction.getKbPaymentMethodId()).isEqualTo(expectedKbPaymentMethodId);
        assertThat(rejectedTransaction.getKbTransactionId()).isEqualTo(expectedKbTransactionId);
        assertThat(rejectedTransaction.getKbTransactionType()).isEqualTo(expectedKbTransactionType);
        assertThat(rejectedTransaction.getTransactionType()).isEqualTo(expectedAuthNetTransactionType);
        assertThat(rejectedTransaction.getTenantId()).isEqualTo(expectedTenantId);
        assertThat(rejectedTransaction.getAmount()).isEqualTo(refundAmount);
        assertThat(rejectedTransaction.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(rejectedTransaction.getKbReferencedTransactionRecordId())
                .isEqualTo(expectedOriginalTransactionRecordId);
        assertThat(rejectedTransaction.getAuthorizeNetReferencedTransactionId()).isEqualTo(null);

    }



}
