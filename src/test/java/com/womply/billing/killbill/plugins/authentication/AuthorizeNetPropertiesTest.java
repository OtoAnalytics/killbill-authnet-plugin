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
