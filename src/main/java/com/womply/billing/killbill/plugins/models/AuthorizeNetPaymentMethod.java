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

import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.api.PluginProperties;
import org.killbill.billing.plugin.api.payment.PluginPaymentMethodPlugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Maps Authorize.Net PaymentMethod representation to that of KillBill.
 */
public class AuthorizeNetPaymentMethod extends PluginPaymentMethodPlugin {

    public static final String KAUI_FIELD_CARD_TYPE = "Card Type: ";
    public static final String KAUI_FIELD_CARD_EXPIRATION_MONTH = "Expiration month: ";
    public static final String KAUI_FIELD_CARD_EXPIRATION_YEAR = "Expiration year: ";
    public static final String KAUI_FIELD_CARD_LAST_4 = "Last 4 digits: ";
    public static final String KAUI_FIELD_ACH_ROUTING_NUMBER = "ACH Routing Number: ";
    public static final String KAUI_FIELD_ACH_INSTITUTION_NAME = "ACH Institution Name: ";
    public static final String KAUI_FIELD_ACH_ACCOUNT_NUMBER_LAST_4 = "ACH Account Number Last 4: ";
    public static final String KAUI_FIELD_CARD_ZIP = "Zip code: ";
    public static final String KAUI_FIELD_AUTHORIZE_NET_CUSTOMER_PROFILE_ID = "Authorize.net Customer Profile Id: ";
    public static final String KAUI_FIELD_AUTHORIZE_NET_PAYMENT_PROFILE_ID = "Authorize.net Payment Profile Id: ";


    public AuthorizeNetPaymentMethod(Map<String, Object> data) {
        this(
                extractPaymentMethodId(data),
                extractAuthNetPaymentProfileId(data),
                false,
                PluginProperties.buildPluginProperties(data)
        );
    }

    public AuthorizeNetPaymentMethod(UUID kbPaymentMethodId, String externalPaymentMethodId,
                                     boolean isDefaultPaymentMethod, List<PluginProperty> properties) {
        super(kbPaymentMethodId, externalPaymentMethodId, isDefaultPaymentMethod, properties);
    }

    protected static UUID extractPaymentMethodId(Map<String, Object> data) {
        return UUID.fromString((String)data.remove(AUTHORIZE_NET_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID.getName()));
    }

    protected static String extractAuthNetPaymentProfileId(Map<String, Object> data) {
        return (String)data.remove(AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_PAYMENT_PROFILE_ID.getName());
    }

}
