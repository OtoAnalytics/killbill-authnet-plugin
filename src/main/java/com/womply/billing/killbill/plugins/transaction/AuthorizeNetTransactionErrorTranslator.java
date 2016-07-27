package com.womply.billing.killbill.plugins.transaction;

import net.authorize.api.contract.v1.TransactionResponse;
import org.killbill.billing.payment.api.TransactionType;

/**
 * Translates Authorize.Net transaction error messages into
 * messages to be displayed in Kaui.
 */
public class AuthorizeNetTransactionErrorTranslator {

    /**
     * Returns Authorize.Net error translated in a message to be displayed in Kaui.
     */
    public static String translateToKauiMessage(TransactionType kbTransactionType,
                                                TransactionResponse.Errors.Error error) {
        StringBuilder kauiMessage = new StringBuilder();
        String errorCode = error.getErrorCode();

        kauiMessage.append("Code ")
                .append(errorCode)
                .append(" -- ")
                .append(interpretErrorCode(kbTransactionType, errorCode, error.getErrorText()));

        return kauiMessage.toString();
    }

    private static String interpretErrorCode(TransactionType kbTransactionType, String errorCode, String errorText) {
        StringBuilder interpretedError = new StringBuilder();
        try {
            int code = Integer.parseInt(errorCode);
            interpretedError.append(interpretCode(kbTransactionType, code, errorText));
        } catch (NumberFormatException e) {
            interpretedError.append(errorText);
        }

        return interpretedError.toString();
    }

    private static String interpretCode(TransactionType kbTransactionType, int code, String errorText) {
        StringBuilder interpretedError = new StringBuilder();
        switch (code) {
            case 54:
                if (kbTransactionType == TransactionType.REFUND) {
                    interpretedError.append("Refund attempted on an unsettled transaction. " +
                            "Please re-try to refund tomorrow.")
                            .append(" (")
                            .append(errorText)
                            .append(')');
                } else {
                    interpretedError.append(errorText);
                }
                break;
            default: interpretedError.append(errorText);
        }

        return interpretedError.toString();
    }

}
