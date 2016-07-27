package com.womply.billing.killbill.plugins.authentication;

import net.authorize.Environment;

import java.util.Properties;

/**
 * Represents Authorize.Net Gateway plugin properties set in KillBill.
 * These properties exist on a per-tenant basis and are found in the
 * tenant_kvs table in KB's database.
 */
public class AuthorizeNetProperties {

    public static final String ENVIRONMENT = "org.killbill.billing.plugin.killbill-authorize-net.environment";
    public static final String API_LOGIN_ID = "org.killbill.billing.plugin.killbill-authorize-net.api-login-id";
    public static final String TRANSACTION_KEY = "org.killbill.billing.plugin.killbill-authorize-net.transaction-key";

    private Properties properties;

    public AuthorizeNetProperties(final Properties properties) {
        this.properties = properties;
    }

    /**
     * @return Authorize.Net Environment
     */
    public Environment getEnvironment() {
        String envProperty = (String)properties.getProperty(ENVIRONMENT);
        if (envProperty == null) {
            return null;
        }
        return Environment.valueOf(envProperty);
    }

    /**
     * @return Authorize.Net Api Login Id
     */
    public String getApiLoginId() {
        return (String)properties.get(API_LOGIN_ID);
    }

    /**
     * @return Authorize.Net Transaction key
     */
    public String getTransactionKey() {
        return (String)properties.getProperty(TRANSACTION_KEY);
    }

    /**
     * @return true if all Authorize.Net properties are set
     */
    public boolean areAllPropertiesSet() {
        return (getEnvironment() != null &&
                getApiLoginId() != null &&
                getTransactionKey() != null);
    }

}
