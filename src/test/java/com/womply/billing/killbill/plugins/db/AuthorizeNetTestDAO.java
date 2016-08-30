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
