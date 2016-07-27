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
