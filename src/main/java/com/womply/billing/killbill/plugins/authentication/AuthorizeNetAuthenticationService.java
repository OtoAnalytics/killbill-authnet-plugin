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
