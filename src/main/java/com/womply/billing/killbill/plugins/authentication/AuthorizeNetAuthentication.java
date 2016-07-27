package com.womply.billing.killbill.plugins.authentication;

import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.controller.base.ApiOperationBase;

/**
 * Sets authentication credentials for Authorize.Net.
 */
public class AuthorizeNetAuthentication {

    private AuthorizeNetProperties config;

    public AuthorizeNetAuthentication(AuthorizeNetProperties config) {
        this.config = config;
    }

    /**
     * Returns a Authorize.Net authentication based on the plugin properties for the given tenant.
     */
    public MerchantAuthenticationType getAuthenticationSetEnvironment() {
        if (config.areAllPropertiesSet()) {
            setEnvironment();
            return getAuthenticationWithEncryptedProperties();
        } else {
            throw new RuntimeException("Can not find all authentication properties for the given tenant");
        }
    }

    /**
     * Sets the environment once only.
     */
    private synchronized void setEnvironment() {
        if (ApiOperationBase.getEnvironment() == null) {
            ApiOperationBase.setEnvironment(config.getEnvironment());
        }
    }

    /**
     * TODO: store Auth.Net credentials encrypted and decrypt here
     * Need to figure out OSGI config to be able to use the CryptConfig.decrypt.
     * Current OSGI config results in:
     * java.security.NoSuchAlgorithmException: PBEWITHSHA256AND256BITAES-CBC-BC SecretKeyFactory not available
     * Need to enable bouncy castle classes and then some....
     * Possibly relevant posts:
     * http://stackoverflow.com/questions/19449002/jce-security-provider-in-osgi
     * http://stackoverflow.com/questions/26624104/bundle-will-not-start-when-bouncy-castle-is-imported#26624569
     */
    protected MerchantAuthenticationType getAuthenticationWithEncryptedProperties() {
        MerchantAuthenticationType merchantAuthenticationType = new MerchantAuthenticationType();
        merchantAuthenticationType.setName(config.getApiLoginId());
        merchantAuthenticationType.setTransactionKey(config.getTransactionKey());

        return merchantAuthenticationType;
    }

}
