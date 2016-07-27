package com.womply.billing.killbill.plugins.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import net.authorize.api.contract.v1.TransactionResponse;
import org.killbill.billing.payment.api.TransactionType;
import org.testng.annotations.Test;

/**
 * Unit tests for AuthorizeNetTransactionErrorTranslator class.
 */
public class AuthorizeNetTransactionErrorTranslatorTest {

    public static final String REFUND_UNSETTLED_TRANSACTION_ERROR_CODE = "54";

    @Test
    public void translateToKauiMessageRefundUnsettledTransaction() {
        TransactionType kbTransactionType = TransactionType.REFUND;
        TransactionResponse.Errors.Error error = new TransactionResponse.Errors.Error();
        error.setErrorCode(REFUND_UNSETTLED_TRANSACTION_ERROR_CODE);
        error.setErrorText("The referenced transaction does not meet the criteria for issuing a credit.");

        String message = AuthorizeNetTransactionErrorTranslator.translateToKauiMessage(kbTransactionType, error);

        String expectedMessage = "Code 54 -- Refund attempted on an unsettled transaction. " +
                "Please re-try to refund tomorrow. (The referenced transaction does not " +
                "meet the criteria for issuing a credit.)";
        assertThat(message).isEqualTo(expectedMessage);
    }

    @Test
    public void translateToKauiMessageDeclinedTransaction() {
        TransactionType kbTransactionType = TransactionType.PURCHASE;
        TransactionResponse.Errors.Error error = new TransactionResponse.Errors.Error();
        error.setErrorCode("2");
        error.setErrorText("This transaction has been declined.");

        String message = AuthorizeNetTransactionErrorTranslator.translateToKauiMessage(kbTransactionType, error);

        String expectedMessage = "Code 2 -- This transaction has been declined.";
        assertThat(message).isEqualTo(expectedMessage);
    }
}
