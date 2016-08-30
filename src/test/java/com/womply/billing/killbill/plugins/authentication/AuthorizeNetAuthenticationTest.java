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
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import net.authorize.Environment;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.controller.base.ApiOperationBase;
import org.testng.annotations.Test;

import java.util.Properties;

/**
 * Unit tests for AuthorizeNetAuthentication class
 */
public class AuthorizeNetAuthenticationTest {
    @Test
    public void configure() {
        String envProperty = "SANDBOX";
        String apiLoginId = "authNetLoginId";
        String transactionKey = "authNetTransactionKey";
        Properties properties = new Properties();
        properties.put(AuthorizeNetProperties.ENVIRONMENT, envProperty);
        properties.put(AuthorizeNetProperties.API_LOGIN_ID, apiLoginId);
        properties.put(AuthorizeNetProperties.TRANSACTION_KEY, transactionKey);

        AuthorizeNetProperties authorizeNetProperties = new AuthorizeNetProperties(properties);

        AuthorizeNetAuthentication authentication = new AuthorizeNetAuthentication(authorizeNetProperties);
        MerchantAuthenticationType actualCredentials = authentication.getAuthenticationSetEnvironment();

        Environment actualEnv = ApiOperationBase.getEnvironment();
        assertThat(actualEnv).isNotNull().isEqualTo(Environment.SANDBOX);

        assertThat(actualCredentials).isNotNull();
        assertThat(actualCredentials.getName()).isEqualTo(apiLoginId);
        assertThat(actualCredentials.getTransactionKey()).isEqualTo(transactionKey);
    }

    @Test
    public void configureNotAllPropertiesSet() {
        String envProperty = "SANDBOX";
        String apiLoginId = "authNetLoginId";
        Properties properties = new Properties();
        properties.put(AuthorizeNetProperties.ENVIRONMENT, envProperty);
        properties.put(AuthorizeNetProperties.API_LOGIN_ID, apiLoginId);

        // transaction key is not set
        AuthorizeNetProperties authorizeNetProperties = new AuthorizeNetProperties(properties);

        AuthorizeNetAuthentication authentication = new AuthorizeNetAuthentication(authorizeNetProperties);
        try {
            authentication.getAuthenticationSetEnvironment();
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Can not find all authentication properties");
        }
    }
}
