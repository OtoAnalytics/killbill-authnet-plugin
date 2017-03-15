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

package com.womply.billing.killbill.plugins.transaction;

import com.womply.billing.killbill.plugins.AuthorizeNetTransactionService;
import com.womply.billing.killbill.plugins.authentication.AuthorizeNetAuthenticationService;
import com.womply.billing.killbill.plugins.db.AuthorizeNetDAO;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetPaymentMethodsRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;
import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentTransactionInfo;
import com.womply.billing.killbill.plugins.models.AuthorizeNetTransactionInfo;
import com.womply.billing.killbill.plugins.models.PluginRejectedTransactionInfo;

import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.tenant.api.TenantApiException;
import org.osgi.service.log.LogService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Helper class for processing refund transactions.
 */
public class RefundPaymentHelper {

    private final AuthorizeNetDAO dao;
    private final AuthorizeNetAuthenticationService authenticationService;
    private final AuthorizeNetTransactionService transactionService;
    private final OSGIKillbillLogService logService;

    /**
     * Instantiates the class with the required services.
     */
    public RefundPaymentHelper(AuthorizeNetDAO dao,
                               AuthorizeNetAuthenticationService authenticationService,
                               AuthorizeNetTransactionService transactionService,
                               OSGIKillbillLogService logService) {
        this.dao = dao;
        this.authenticationService = authenticationService;
        this.transactionService = transactionService;
        this.logService = logService;
    }

    /**
     * Performs verification checks on the requested refund transaction and
     * executes the refund the verification checks pass.
     * @return A representation of the refund transaction.
     */
    public AuthorizeNetPaymentTransactionInfo refundPayment(final UUID kbTenantId, final UUID kbAccountId,
                                                            final UUID kbPaymentId, final UUID kbTransactionId,
                                                            final UUID kbPaymentMethodId, final BigDecimal amount,
                                                            final Currency currency)
            throws TenantApiException {

        final MerchantAuthenticationType authentication = authenticationService.getAuthenticationForTenant(kbTenantId);
        AuthorizeNetPaymentMethodsRecord paymentMethod = dao.getPaymentMethodForOperation(kbAccountId,
                kbPaymentMethodId, kbTenantId);
        List<AuthorizeNetTransactionsRecord> transactionsToRefund = dao.getPurchaseTransactionsForPayment(kbAccountId,
                kbPaymentId, kbPaymentMethodId);

        AuthorizeNetTransactionInfo transaction = new AuthorizeNetTransactionInfo();
        transaction.setCustomerProfileId(paymentMethod.getAuthorizeNetCustomerProfileId());
        transaction.setCustomerPaymentProfileId(paymentMethod.getAuthorizeNetPaymentProfileId());
        transaction.setKbAccountId(kbAccountId);
        transaction.setKbPaymentId(kbPaymentId);
        transaction.setKbPaymentMethodId(kbPaymentMethodId);
        transaction.setKbTransactionId(kbTransactionId);
        transaction.setKbTransactionType(TransactionType.REFUND);
        transaction.setTransactionType(TransactionTypeEnum.REFUND_TRANSACTION);
        transaction.setTenantId(kbTenantId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);

        Optional<AuthorizeNetPaymentTransactionInfo> rejectedTransaction =
                validateAndHandleErrors(transactionsToRefund,
                transaction, paymentMethod);
        if (rejectedTransaction.isPresent()) {
            return rejectedTransaction.get();
        }

        AuthorizeNetTransactionsRecord originalTransaction = transactionsToRefund.get(0);
        transaction.setKbReferencedTransactionRecordId(originalTransaction.getRecordId().longValue());
        transaction.setAuthorizeNetReferencedTransactionId(originalTransaction.getAuthorizeNetTransactionId());

        return transactionService.createTransactionOnPaymentProfile(transaction, authentication);
    }

    private Optional<AuthorizeNetPaymentTransactionInfo> validateAndHandleErrors(
            List<AuthorizeNetTransactionsRecord> transactionsToRefund,
            AuthorizeNetTransactionInfo refundTransaction,
            AuthorizeNetPaymentMethodsRecord paymentMethod
            ) {

        Optional<AuthorizeNetTransactionsRecord> transactionsToRefundErrors =
                validateTransactionsAndHandleErrors(transactionsToRefund, refundTransaction);

        if (transactionsToRefundErrors.isPresent()) {
            return Optional.of(new AuthorizeNetPaymentTransactionInfo(transactionsToRefundErrors.get()));
        }

        AuthorizeNetTransactionsRecord originalTransaction = transactionsToRefund.get(0);
        refundTransaction.setKbReferencedTransactionRecordId(originalTransaction.getRecordId().longValue());
        Optional<AuthorizeNetTransactionsRecord> originalTransactionErrors =
                validateOriginalTransactionAndHandleErrors(refundTransaction, originalTransaction, paymentMethod);

        if (originalTransactionErrors.isPresent()) {
            return Optional.of(new AuthorizeNetPaymentTransactionInfo(originalTransactionErrors.get()));
        }

        return Optional.empty();
    }

    private Optional<AuthorizeNetTransactionsRecord> validateTransactionsAndHandleErrors(
            List<AuthorizeNetTransactionsRecord> transactionsToRefund,
            AuthorizeNetTransactionInfo refundTransaction) {

        AuthorizeNetTransactionsRecord record = null;
        if (transactionsToRefund.isEmpty()) {
             record = handleRefundError(refundTransaction, "No transactions to refund are found for the given " +
                     "kbPaymentId");
        } else if (transactionsToRefund.size() > 1) {
            record = handleRefundError(refundTransaction, "Multiple transactions to refund are found for the given " +
                    "kbPaymentId");
        }

        return Optional.ofNullable(record);
    }

    private AuthorizeNetTransactionsRecord handleRefundError(AuthorizeNetTransactionInfo refundTransaction,
                                                             String errorMessage) {
        logService.log(LogService.LOG_ERROR,
                "REFUND PAYMENT ERROR: " + errorMessage + " kbPaymentId = " +
                        refundTransaction.getKbPaymentId() + " kbPaymentMethodId = " +
                        refundTransaction.getKbPaymentMethodId() + " kbAccountId = "
                        + refundTransaction.getKbAccountId());
        return saveRefundError(refundTransaction, errorMessage);

    }

    private AuthorizeNetTransactionsRecord saveRefundError(AuthorizeNetTransactionInfo refundTransaction,
                                                          String errorMessage) {
        PluginRejectedTransactionInfo rejectedTransaction = new PluginRejectedTransactionInfo(refundTransaction);
        rejectedTransaction.setErrorMessage(errorMessage);
        return dao.logTransactionResponse(rejectedTransaction);
    }

    private Optional<AuthorizeNetTransactionsRecord> validateOriginalTransactionAndHandleErrors(
            AuthorizeNetTransactionInfo refundTransaction,
            AuthorizeNetTransactionsRecord originalTransaction,
            AuthorizeNetPaymentMethodsRecord paymentMethod) {

        AuthorizeNetTransactionsRecord record = null;
        if (!originalTransaction.getAuthorizeNetCustomerProfileId().equals(
                paymentMethod.getAuthorizeNetCustomerProfileId())) {
            record = handleRefundError(refundTransaction, "Mismatch on customer profile id between original " +
                    "transaction(record_id = " + originalTransaction.getRecordId() + ") and given payment method " +
                    "(record_id = " + paymentMethod.getRecordId() + ").");

        } else if (!originalTransaction.getAuthorizeNetPaymentProfileId().equals(
                paymentMethod.getAuthorizeNetPaymentProfileId())) {
            record = handleRefundError(refundTransaction, "Mismatch on customer payment profile id between original " +
                    "transaction(record_id = " + originalTransaction.getRecordId() + ") and given payment method " +
                    "(record_id = " + paymentMethod.getRecordId() + ").");

        } if (originalTransaction.getAmount().compareTo(refundTransaction.getAmount()) < 0) {
            record = handleRefundError(refundTransaction, "Requested refund amount " + refundTransaction.getAmount() +
                    " exceeds the amount of the original transaction (" + originalTransaction.getAmount() + ").");
        }

        return Optional.ofNullable(record);
    }

}
