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

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import net.authorize.Environment;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.controller.base.ApiOperationBase;
import org.killbill.billing.tenant.api.TenantApiException;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.UUID;

/**
 * Unit tests for AuthorizeNetAuthenticationService class.
 */
public class AuthorizeNetAuthenticationServiceTest {

    @Test
    public void testSetAuthenticationForTenant() throws TenantApiException {
        final UUID expectedTenantId = UUID.randomUUID();
        String envProperty = "SANDBOX";
        String apiLoginId = "authNetLoginId";
        String transactionKey = "authNetTransactionKey";
        Properties properties = new Properties();
        properties.put(AuthorizeNetProperties.ENVIRONMENT, envProperty);
        properties.put(AuthorizeNetProperties.API_LOGIN_ID, apiLoginId);
        properties.put(AuthorizeNetProperties.TRANSACTION_KEY, transactionKey);

        AuthorizeNetProperties authorizeNetProperties = new AuthorizeNetProperties(properties);
        AuthorizeNetAuthentication authentication = new AuthorizeNetAuthentication(authorizeNetProperties);

        AuthorizeNetConfigurableHandler handlerMock = createMock(AuthorizeNetConfigurableHandler.class);
        expect(handlerMock.getConfigurable(eq(expectedTenantId))).andReturn(authentication);

        replay(handlerMock);

        AuthorizeNetAuthenticationService service = new AuthorizeNetAuthenticationService(handlerMock);
        MerchantAuthenticationType actualCredentials = service.getAuthenticationForTenant(expectedTenantId);

        verify(handlerMock);
        Environment actualEnv = ApiOperationBase.getEnvironment();
        assertThat(actualEnv).isNotNull().isEqualTo(Environment.SANDBOX);

        assertThat(actualCredentials).isNotNull();
        assertThat(actualCredentials.getName()).isEqualTo(apiLoginId);
        assertThat(actualCredentials.getTransactionKey()).isEqualTo(transactionKey);
    }
}
