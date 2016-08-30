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

import net.authorize.Environment;
import org.testng.annotations.Test;

import java.util.Properties;

/**
 * Unit test for AuthorizeNetProperties class.
 */
public class AuthorizeNetPropertiesTest {

    @Test
    public void getProperties() {
        String envProperty = "SANDBOX";
        String apiLoginId = "authNetLoginId";
        String transactionKey = "authNetTransactionKey";
        Properties properties = new Properties();
        properties.put(AuthorizeNetProperties.ENVIRONMENT, envProperty);
        properties.put(AuthorizeNetProperties.API_LOGIN_ID, apiLoginId);
        properties.put(AuthorizeNetProperties.TRANSACTION_KEY, transactionKey);

        AuthorizeNetProperties authorizeNetProperties = new AuthorizeNetProperties(properties);

        Environment actualEnv = authorizeNetProperties.getEnvironment();
        assertThat(actualEnv).isNotNull().isEqualTo(Environment.SANDBOX);

        String actualApiLogin = authorizeNetProperties.getApiLoginId();
        assertThat(actualApiLogin).isNotEmpty().isEqualTo(apiLoginId);

        String actualTransactionKey = authorizeNetProperties.getTransactionKey();
        assertThat(actualTransactionKey).isNotEmpty().isEqualTo(transactionKey);

        assertThat(authorizeNetProperties.areAllPropertiesSet()).isTrue();
    }

    @Test
    public void areAllPropertiesSet() {
        Properties properties = new Properties();
        AuthorizeNetProperties authorizeNetProperties = new AuthorizeNetProperties(properties);
        assertThat(authorizeNetProperties.areAllPropertiesSet()).isFalse();

        properties.put(AuthorizeNetProperties.ENVIRONMENT, "SANDBOX");
        assertThat(authorizeNetProperties.areAllPropertiesSet()).isFalse();

        properties.put(AuthorizeNetProperties.API_LOGIN_ID, "testId");
        assertThat(authorizeNetProperties.areAllPropertiesSet()).isFalse();

        properties.put(AuthorizeNetProperties.TRANSACTION_KEY, "testTransactionKey");
        assertThat(authorizeNetProperties.areAllPropertiesSet()).isTrue();
    }
}
