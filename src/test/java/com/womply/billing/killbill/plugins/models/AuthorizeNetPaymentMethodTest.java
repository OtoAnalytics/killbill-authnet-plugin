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

package com.womply.billing.killbill.plugins.models;

import static com.womply.billing.killbill.plugins.jooq.tables.AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS;
import static org.assertj.core.api.Assertions.assertThat;

import org.killbill.billing.payment.api.PluginProperty;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Unit tests for AuthorizeNetPaymentMethod class.
 */
public class AuthorizeNetPaymentMethodTest {

    @Test
    public void testConstructor() {
        Map<String, Object> record = getPaymentMethodData();
        // copy the record as it will be modified
        Map<String, Object> expected = new HashMap<>(record);

        AuthorizeNetPaymentMethod paymentMethod = new AuthorizeNetPaymentMethod(record);

        verifyAuthorizeNetPaymentMethod(paymentMethod, expected);
    }

    /**
     * @return A sample map of payment method data.
     */
    public static Map<String, Object> getPaymentMethodData() {
        Map<String, Object> record = new HashMap<>();
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID.getName(),
                "4845c2df-e8a6-4584-9449-fe9a8041d75e");
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_PAYMENT_PROFILE_ID.getName(),
                "36759476");
        record.put(AuthorizeNetPaymentMethod.KAUI_FIELD_AUTHORIZE_NET_CUSTOMER_PROFILE_ID,
                "40405709");
        record.put(AuthorizeNetPaymentMethod.KAUI_FIELD_AUTHORIZE_NET_PAYMENT_PROFILE_ID,
                "36759476");
        record.put(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_TYPE, "VISA");
        record.put(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_EXPIRATION_MONTH, 10);
        record.put(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_EXPIRATION_YEAR, 2020);
        record.put(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_LAST_4, "3456");
        record.put(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_ZIP, "90210");

        return record;
    }

    /**
     * @return A sample map of payment method data with raw column names.
     */
    public static Map<String, Object> getPaymentMethodDataRaw() {
        Map<String, Object> record = new HashMap<>();
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID.getName(),
                "4845c2df-e8a6-4584-9449-fe9a8041d75e");
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_PAYMENT_PROFILE_ID.getName(),
                "36759476");
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_CUSTOMER_PROFILE_ID.getName(),
                "40405709");
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.CC_TYPE.getName(), "VISA");
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.CC_EXP_MONTH.getName(), 10);
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.CC_EXP_YEAR.getName(), 2020);
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.CC_LAST_4.getName(), "3456");
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.CC_FIRST_NAME.getName(), "Rusty");
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.CC_LAST_NAME.getName(), "Shackleford");
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.ADDRESS.getName(), "123 Fake St");
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.CITY.getName(), "Beverly Hills");
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.STATE.getName(), "CA");
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.ZIP.getName(), "90210");
        record.put(AUTHORIZE_NET_PAYMENT_METHODS.COUNTRY.getName(), "USA");

        return record;
    }

    /**
     * Verifies that the given paymentMethod is consistent with expectedData.
     */
    public static void verifyAuthorizeNetPaymentMethod(AuthorizeNetPaymentMethod paymentMethod,
                                                Map<String, Object> expectedData) {
        UUID expectedPaymentMethodId =
                UUID.fromString((String)expectedData.get(AUTHORIZE_NET_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID.getName()));
        assertThat(paymentMethod.getKbPaymentMethodId()).isNotNull().isEqualTo(expectedPaymentMethodId);
        assertThat(paymentMethod.getExternalPaymentMethodId()).isNotNull().isEqualTo(
                (String)expectedData.get(AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_PAYMENT_PROFILE_ID.getName())
        );
        assertThat(paymentMethod.isDefaultPaymentMethod()).isFalse();

        List<PluginProperty> pluginProperties = paymentMethod.getProperties();
        assertThat(pluginProperties).isNotEmpty();
        for (PluginProperty property : pluginProperties) {
            assertThat(property.getValue()).isEqualTo(expectedData.get(property.getKey()));
        }
    }

}
