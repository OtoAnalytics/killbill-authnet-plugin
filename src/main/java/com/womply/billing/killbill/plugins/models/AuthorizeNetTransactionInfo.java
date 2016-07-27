package com.womply.billing.killbill.plugins.models;

import com.womply.billing.killbill.plugins.db.MysqlAdapter;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetRequestsRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;
import com.womply.billing.killbill.plugins.transaction.AuthorizeNetTransactionErrorTranslator;

import lombok.Data;
import net.authorize.api.contract.v1.ANetApiResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.MessagesType;
import net.authorize.api.contract.v1.TransactionResponse;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import org.jooq.types.ULong;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Representation for Authorize.Net transaction.
 */
@Data
public class AuthorizeNetTransactionInfo {

    public static final String TRANSACTION_RESULT_SUCCESS_CODE = "1";

    protected long requestId;

    protected UUID kbAccountId;
    protected UUID kbPaymentId;
    protected UUID kbPaymentMethodId;
    protected UUID kbTransactionId;
    protected UUID tenantId;
    protected TransactionType kbTransactionType; // KB's transaction type
    protected String customerProfileId;
    protected String customerPaymentProfileId;
    protected BigDecimal amount;
    protected Currency currency;
    protected TransactionTypeEnum transactionType; // Auth.Net transaction type
    protected long kbReferencedTransactionRecordId;
    protected String authorizeNetReferencedTransactionId; // used for refund transactions
    protected String kbPaymentPluginStatus;

    protected CreateTransactionResponse response;

    public boolean doesReferenceTransaction() {
        return kbReferencedTransactionRecordId > 0L;
    }

    /**
     * @return AuthorizeNetRequestsRecord object populated with information for this
     *      transaction.
     */
    public AuthorizeNetRequestsRecord saveInto(AuthorizeNetRequestsRecord record) {
        record.setKbTenantId(tenantId.toString());
        record.setKbAccountId(kbAccountId.toString());
        record.setKbPaymentId(kbPaymentId.toString());
        record.setKbPaymentMethodId(kbPaymentMethodId.toString());
        record.setKbPaymentTransactionId(kbTransactionId.toString());
        record.setKbTransactionType(kbTransactionType.toString());
        record.setAuthorizeNetCustomerProfileId(customerProfileId);
        record.setAuthorizeNetPaymentProfileId(customerPaymentProfileId);
        record.setTransactionType(transactionType.value());
        record.setAmount(amount);
        record.setCurrency(currency.name());

        if (kbReferencedTransactionRecordId > 0L) {
            record.setKbRefTransactionRecordId(ULong.valueOf(kbReferencedTransactionRecordId));
        }

        return record;
    }

    /**
     * @return AuthorizeNetTransactionsRecord object populated with information for this
     *      transaction.
     */
    public AuthorizeNetTransactionsRecord saveInto(AuthorizeNetTransactionsRecord record) {

        saveBaseFields(record);
        record.setRequestId(ULong.valueOf(requestId));

        boolean transactionSuccess = isSuccess(response);
        record.setSuccess(MysqlAdapter.getValueAsByte(transactionSuccess));
        if (transactionSuccess) {
            record.setKbPaymentPluginStatus(PaymentPluginStatus.PROCESSED.name());
        } else {
            record.setKbPaymentPluginStatus(PaymentPluginStatus.ERROR.name());
        }

        // save response
        record.setResponseStatus(getResultCode(response).value());
        record.setResponseMessage(collectMessages(response));

        TransactionResponse result = response.getTransactionResponse();
        if (result != null) {
            // save transaction response
            record.setAuthorizeNetTransactionId(result.getTransId());
            record.setTransactionStatus(getTransactionResultCode(result));
            record.setAuthCode(result.getAuthCode());
            record.setAvsResultCode(result.getAvsResultCode());
            record.setCvvResultCode(result.getCvvResultCode());
            record.setCavvResultCode(result.getCavvResultCode());
            record.setAccountType(result.getAccountType());
            record.setTestRequest(result.getTestRequest());

            record.setTransactionMessage(collectTransactionMessages(result));
            record.setTransactionError(collectTransactionErrors(result));
        }

        return record;
    }

    protected void saveBaseFields(AuthorizeNetTransactionsRecord record) {
        record.setKbTenantId(tenantId.toString());
        record.setKbAccountId(kbAccountId.toString());
        record.setKbPaymentId(kbPaymentId.toString());
        record.setKbPaymentMethodId(kbPaymentMethodId.toString());
        record.setKbPaymentTransactionId(kbTransactionId.toString());
        record.setKbTransactionType(kbTransactionType.toString());
        record.setAuthorizeNetCustomerProfileId(customerProfileId);
        record.setAuthorizeNetPaymentProfileId(customerPaymentProfileId);
        record.setTransactionType(transactionType.value());
        record.setAmount(amount);
        record.setCurrency(currency.name());

        if (kbReferencedTransactionRecordId > 0L) {
            record.setKbRefTransactionRecordId(ULong.valueOf(kbReferencedTransactionRecordId));
        }
    }

    // TODO: Open Question: How to handle 'Held for Review' transactions?
    protected boolean isSuccess(CreateTransactionResponse response) {
        if (getResultCode(response) != MessageTypeEnum.OK) {
            return false;
        }
        TransactionResponse result = response.getTransactionResponse();
        if (result == null) {
            return false;
        }
        if (!result.getResponseCode().equals(TRANSACTION_RESULT_SUCCESS_CODE)) {
            return false;
        }

        return true;
    }

    public static MessageTypeEnum getResultCode(ANetApiResponse response) {
        return response.getMessages().getResultCode();
    }

    public static String getTransactionResultCode(TransactionResponse response) {
        return response.getResponseCode();
    }

    protected String collectMessages(ANetApiResponse response) {
        StringBuilder messages = new StringBuilder();
        if (response.getMessages() != null) {
            int idx = 1;
            for (MessagesType.Message message : response.getMessages().getMessage()) {
                messages.append(idx).append(": Code ")
                        .append(message.getCode()).append(" -- ")
                        .append(message.getText());
                messages.append('\n');
                idx++;
            }
        }
        return messages.toString();
    }

    protected String collectTransactionMessages(TransactionResponse response) {
        StringBuilder messages = new StringBuilder();
        if (response.getMessages() != null) {
            int idx = 1;
            for (TransactionResponse.Messages.Message message : response.getMessages().getMessage()) {
                messages.append(idx).append(": Code ")
                        .append(message.getCode()).append(" -- ")
                        .append(message.getDescription());
                messages.append('\n');
                idx++;
            }
        }
        return messages.toString();
    }

    protected String collectTransactionErrors(TransactionResponse response) {
        StringBuilder errors = new StringBuilder();
        if (response.getErrors() != null) {
            int idx = 1;
            for (TransactionResponse.Errors.Error error : response.getErrors().getError()) {
                errors.append(idx)
                        .append(": ")
                        .append(AuthorizeNetTransactionErrorTranslator
                                .translateToKauiMessage(kbTransactionType, error));
                errors.append('\n');
                idx++;
            }
        }
        return errors.toString();
    }
}
