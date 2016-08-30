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
