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
