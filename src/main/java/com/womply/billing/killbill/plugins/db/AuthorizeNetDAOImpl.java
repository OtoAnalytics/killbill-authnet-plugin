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

import static com.womply.billing.killbill.plugins.jooq.tables.AuthorizeNetCustomerProfiles.AUTHORIZE_NET_CUSTOMER_PROFILES;
import static com.womply.billing.killbill.plugins.jooq.tables.AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS;
import static com.womply.billing.killbill.plugins.jooq.tables.AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS;
import static com.womply.billing.killbill.plugins.jooq.tables.AuthorizeNetTransactions.AUTHORIZE_NET_TRANSACTIONS;

import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetCustomerProfilesRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetPaymentMethodsRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetRequestsRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;
import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentMethod;
import com.womply.billing.killbill.plugins.models.AuthorizeNetTransactionInfo;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.killbill.billing.payment.api.TransactionType;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.sql.DataSource;


/**
 * Database operations for AuthorizeNet plugin.
 */
public class AuthorizeNetDAOImpl implements AuthorizeNetDAO {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_DELETED = "DELETED";

    protected DSLContext db;

    public AuthorizeNetDAOImpl(DataSource dataSource) {
        this.db = DSL.using(dataSource, SQLDialect.MYSQL);
    }

    /**
     * The map <code>properties</code> contains column name <--> value mapping for the population of
     * the authorize_net_payment_methods table.
     */
    @Override
    public void addPaymentMethod(final UUID kbAccountId, final UUID kbPaymentMethodId,
                                 final boolean isDefault, String authNetCustomerProfileId,
                                 final Map<String, String> properties,
                                 final UUID kbTenantId) throws SQLException {

        verifyPaymentMethodProperties(kbAccountId, kbPaymentMethodId, properties);

        /* Clone our properties, what we have been given might be unmodifiable */
        Map<String, String> dataFields = new HashMap(properties);
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_CUSTOMER_PROFILE_ID.getName(),
                authNetCustomerProfileId);
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.KB_ACCOUNT_ID.getName(), kbAccountId.toString());
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID.getName(), kbPaymentMethodId.toString());
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.KB_TENANT_ID.getName(), kbTenantId.toString());
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.STATUS.getName(), STATUS_ACTIVE);

        AuthorizeNetPaymentMethodsRecord record = db.newRecord(AUTHORIZE_NET_PAYMENT_METHODS);
        record.fromMap(dataFields);

        Timestamp now = now();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);

        record.store();
    }

    protected void verifyPaymentMethodProperties(final UUID kbAccountId, final UUID kbPaymentMethodId,
                                                 final Map<String, String> properties) {

        if (StringUtils.isEmpty(
                properties.get(AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_PAYMENT_PROFILE_ID.getName()))) {
            throw new RuntimeException("Empty Authorize.Net payment profile id for kbAccountId = " + kbAccountId
                + ", kbPaymentMethodId = " + kbPaymentMethodId);
        }
    }

    @Override
    public Map<String, Object> getPaymentMethod(UUID kbAccountId, UUID kbPaymentMethodId,
                                                UUID tenantId, boolean rawColumns) {
        final SelectConditionStep getPaymentMethodStep;
        if (rawColumns) {
            getPaymentMethodStep = getPaymentMethodRawQuery(kbAccountId, kbPaymentMethodId, tenantId);
        } else {
            getPaymentMethodStep = getPaymentMethodForKauiQuery(kbAccountId, kbPaymentMethodId, tenantId);
        }

        return getPaymentMethodStep.fetchOne().intoMap();
    }

    @Override
    public void logCustomerProfileCreation(String customerId, String customerProfileId, UUID tenantId) {
        AuthorizeNetCustomerProfilesRecord record = db.newRecord(AUTHORIZE_NET_CUSTOMER_PROFILES);
        record.setCustomerId(customerId);
        record.setCustomerProfileId(customerProfileId);
        record.setKbTenantId(tenantId.toString());

        Timestamp now = now();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);

        record.store();
    }

    public AuthorizeNetPaymentMethodsRecord getPaymentMethodForOperation(UUID kbAccountId, UUID kbPaymentMethodId,
                                                                         UUID tenantId) {
        return getPaymentMethodRawQuery(kbAccountId, kbPaymentMethodId, tenantId).fetchOne();
    }

    @Override
    public long logTransactionRequest(AuthorizeNetTransactionInfo transaction) {
        AuthorizeNetRequestsRecord record = db.newRecord(AUTHORIZE_NET_REQUESTS);
        transaction.saveInto(record);

        Timestamp now = now();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);

        record.store();
        return record.getRecordId().longValue();
    }

    @Override
    public AuthorizeNetTransactionsRecord logTransactionResponse(AuthorizeNetTransactionInfo transaction) {

        AuthorizeNetTransactionsRecord record = db.newRecord(AUTHORIZE_NET_TRANSACTIONS);
        transaction.saveInto(record);

        Timestamp now = now();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);

        record.store();
        return record;
    }

    @Override
    public List<AuthorizeNetTransactionsRecord> getTransactionsForPayment(
            final UUID kbAccountId, final UUID kbPaymentId) {
        Result<AuthorizeNetTransactionsRecord> result =
                getTransactionsForPaymentQuery(kbAccountId, kbPaymentId).fetch();
        return result.into(AuthorizeNetTransactionsRecord.class);
    }

    @Override
    public void deactivatePaymentMethod(AuthorizeNetPaymentMethodsRecord paymentMethod) {
        paymentMethod.setStatus(STATUS_DELETED);
        paymentMethod.setUpdatedAt(now());
        paymentMethod.update();
    }

    @Override
    public int getHeldForReviewTransactionCount(final UUID tenantId) {
        return getHeldForReviewTransactionCountQuery(tenantId).fetchOne().value1();
    }

    @Override
    public List<AuthorizeNetTransactionsRecord> getPurchaseTransactionsForPayment(UUID kbAccountId,
                                                                               UUID kbPaymentId,
                                                                               UUID kbPaymentMethodId) {
        Result<AuthorizeNetTransactionsRecord> result =
                getPurchaseTransactionsForPaymentQuery(kbAccountId, kbPaymentId, kbPaymentMethodId).fetch();
        return result.into(AuthorizeNetTransactionsRecord.class);
    }

    protected Timestamp now() {
        return new Timestamp(Instant.now().toEpochMilli());
    }


    /********** JOOQ Queries **********/

    protected SelectConditionStep getPaymentMethodForKauiQuery(UUID kbAccountId, UUID kbPaymentMethodId,
                                                               UUID tenantId) {
        return db.select(
                AUTHORIZE_NET_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID,
                AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_CUSTOMER_PROFILE_ID
                        .as(AuthorizeNetPaymentMethod.KAUI_FIELD_AUTHORIZE_NET_CUSTOMER_PROFILE_ID),
                AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_PAYMENT_PROFILE_ID, // selecting this field twice
                AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_PAYMENT_PROFILE_ID
                        .as(AuthorizeNetPaymentMethod.KAUI_FIELD_AUTHORIZE_NET_PAYMENT_PROFILE_ID),
                AUTHORIZE_NET_PAYMENT_METHODS.CC_TYPE
                        .as(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_TYPE),
                AUTHORIZE_NET_PAYMENT_METHODS.CC_EXP_MONTH
                        .as(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_EXPIRATION_MONTH),
                AUTHORIZE_NET_PAYMENT_METHODS.CC_EXP_YEAR
                        .as(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_EXPIRATION_YEAR),
                AUTHORIZE_NET_PAYMENT_METHODS.CC_LAST_4
                        .as(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_LAST_4),
                AUTHORIZE_NET_PAYMENT_METHODS.ZIP
                        .as(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_ZIP)
        )
                .from(AUTHORIZE_NET_PAYMENT_METHODS)
                .where(AUTHORIZE_NET_PAYMENT_METHODS.KB_ACCOUNT_ID.equal(kbAccountId.toString())
                        .and(AUTHORIZE_NET_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID.equal(kbPaymentMethodId.toString())
                                .and(AUTHORIZE_NET_PAYMENT_METHODS.KB_TENANT_ID.equal(tenantId.toString())
                                        .and(AUTHORIZE_NET_PAYMENT_METHODS.STATUS.equal(STATUS_ACTIVE)))));
    }

    protected SelectConditionStep<AuthorizeNetPaymentMethodsRecord> getPaymentMethodRawQuery(UUID kbAccountId,
                                                                                             UUID kbPaymentMethodId,
                                                                                             UUID tenantId) {
        return db.selectFrom(AUTHORIZE_NET_PAYMENT_METHODS)
                .where(AUTHORIZE_NET_PAYMENT_METHODS.KB_ACCOUNT_ID.equal(kbAccountId.toString())
                        .and(AUTHORIZE_NET_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID.equal(kbPaymentMethodId.toString())
                                .and(AUTHORIZE_NET_PAYMENT_METHODS.KB_TENANT_ID.equal(tenantId.toString())
                                        .and(AUTHORIZE_NET_PAYMENT_METHODS.STATUS.equal(STATUS_ACTIVE)))));
    }


    protected SelectConditionStep<AuthorizeNetTransactionsRecord> getTransactionsForPaymentQuery(UUID kbAccountId,
                                                                                                  UUID kbPaymentId) {
        // return transactions regardless of the current STATUS of the card
        return db.selectFrom(AUTHORIZE_NET_TRANSACTIONS)
                .where(AUTHORIZE_NET_TRANSACTIONS.KB_ACCOUNT_ID.equal(kbAccountId.toString())
                .and(AUTHORIZE_NET_TRANSACTIONS.KB_PAYMENT_ID.equal(kbPaymentId.toString())));
    }

    protected SelectConditionStep<Record1<Integer>> getHeldForReviewTransactionCountQuery(UUID tenantId) {

        return db.selectCount()
                .from(AUTHORIZE_NET_TRANSACTIONS)
                .where(AUTHORIZE_NET_TRANSACTIONS.TRANSACTION_STATUS.equal("4"))
                .and(AUTHORIZE_NET_TRANSACTIONS.KB_TENANT_ID.equal(tenantId.toString()));
    }

    protected SelectConditionStep<AuthorizeNetTransactionsRecord> getPurchaseTransactionsForPaymentQuery(
            UUID kbAccountId,
            UUID kbPaymentId,
            UUID kbPaymentMethodId) {
        return db.selectFrom(AUTHORIZE_NET_TRANSACTIONS)
                .where(AUTHORIZE_NET_TRANSACTIONS.KB_ACCOUNT_ID.equal(kbAccountId.toString())
                        .and(AUTHORIZE_NET_TRANSACTIONS.KB_PAYMENT_ID.equal(kbPaymentId.toString()))
                        .and(AUTHORIZE_NET_TRANSACTIONS.KB_PAYMENT_METHOD_ID.equal(kbPaymentMethodId.toString()))
                        .and(AUTHORIZE_NET_TRANSACTIONS.KB_TRANSACTION_TYPE.equal(TransactionType.PURCHASE.toString()))
                );
    }

}
