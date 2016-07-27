package com.womply.billing.killbill.plugins.db;

import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetPaymentMethodsRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;
import com.womply.billing.killbill.plugins.models.AuthorizeNetTransactionInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Database operation definition.
 */
public interface AuthorizeNetDAO {

    void addPaymentMethod(final UUID kbAccountId, final UUID kbPaymentMethodId,
                          final boolean isDefault, String authNetCustomerProfileId,
                          final Map<String, String> properties,
                          final UUID kbTenantId) throws SQLException;

    Map<String, Object> getPaymentMethod(UUID kbAccountId, UUID kbPaymentMethodId,
                                         UUID tenantId, boolean rawColumns);

    void logCustomerProfileCreation(String customerId, String customerProfileId, UUID tenantId);

    AuthorizeNetPaymentMethodsRecord getPaymentMethodForOperation(UUID kbAccountId, UUID kbPaymentMethodId,
                                                                  UUID tenantId);

    long logTransactionRequest(AuthorizeNetTransactionInfo transaction);

    AuthorizeNetTransactionsRecord logTransactionResponse(AuthorizeNetTransactionInfo transaction);

    List<AuthorizeNetTransactionsRecord> getTransactionsForPayment(final UUID kbAccountId, final UUID kbPaymentId);

    void deactivatePaymentMethod(AuthorizeNetPaymentMethodsRecord paymentMethod);

    int getHeldForReviewTransactionCount(final UUID tenantId);

    List<AuthorizeNetTransactionsRecord> getPurchaseTransactionsForPayment(UUID kbAccountId,
                                                                        UUID kbPaymentId,
                                                                        UUID kbPaymentMethodId);
}
