package com.womply.billing.killbill.plugins.authentication;

import net.authorize.api.contract.v1.MerchantAuthenticationType;
import org.killbill.billing.tenant.api.TenantApiException;

import java.util.UUID;

/**
 * Sets authentication credentials for Authorize.Net.
 */
public class AuthorizeNetAuthenticationService {

    private final AuthorizeNetConfigurableHandler configurableHandler;

    public AuthorizeNetAuthenticationService(final AuthorizeNetConfigurableHandler configurableHandler) {
        this.configurableHandler = configurableHandler;
    }

    public MerchantAuthenticationType getAuthenticationForTenant(UUID tenantId) throws TenantApiException {
        AuthorizeNetAuthentication authentication = configurableHandler.getConfigurable(tenantId);
        return authentication.getAuthenticationSetEnvironment();
    }
}
