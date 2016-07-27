package com.womply.billing.killbill.plugins.db;

import static com.womply.billing.killbill.plugins.jooq.tables.AuthorizeNetCustomerProfiles.AUTHORIZE_NET_CUSTOMER_PROFILES;
import static com.womply.billing.killbill.plugins.jooq.tables.AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS;

import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetCustomerProfilesRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetRequestsRecord;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.UUID;
import javax.sql.DataSource;

/**
 * DB methods used by tests.
 */
public class AuthorizeNetTestDAO {

    protected DSLContext db;

    public AuthorizeNetTestDAO(DataSource dataSource) {
        this.db = DSL.using(dataSource, SQLDialect.MYSQL);
    }

    protected AuthorizeNetCustomerProfilesRecord getCustomerProfile(String customerId) {
        return db.selectFrom(AUTHORIZE_NET_CUSTOMER_PROFILES)
                .where(AUTHORIZE_NET_CUSTOMER_PROFILES.CUSTOMER_ID.equal(customerId))
                .fetchOne();
    }

    protected AuthorizeNetRequestsRecord getAuthNetRequest(final UUID kbPaymentId, final UUID kbPaymentMethodId) {
        return db.selectFrom(AUTHORIZE_NET_REQUESTS)
                .where(AUTHORIZE_NET_REQUESTS.KB_PAYMENT_ID.equal(kbPaymentId.toString())
                        .and(AUTHORIZE_NET_REQUESTS.KB_PAYMENT_METHOD_ID.equal(kbPaymentMethodId.toString())))
                .fetchOne();
    }

}
