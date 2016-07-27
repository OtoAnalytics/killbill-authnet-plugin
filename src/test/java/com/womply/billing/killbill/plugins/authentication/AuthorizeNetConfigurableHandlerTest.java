package com.womply.billing.killbill.plugins.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import net.authorize.Environment;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.controller.base.ApiOperationBase;
import org.easymock.EasyMock;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillLogService;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.UUID;

/**
 * Unit tests for AuthorizeNetConfigurableHandler class.
 */
public class AuthorizeNetConfigurableHandlerTest {
    @Test
    public void createConfigurable() {
        final UUID expectedTenantId = UUID.randomUUID();
        String envProperty = "SANDBOX";
        String apiLoginId = "authNetLoginId";
        String transactionKey = "authNetTransactionKey";
        Properties properties = new Properties();
        properties.put(AuthorizeNetProperties.ENVIRONMENT, envProperty);
        properties.put(AuthorizeNetProperties.API_LOGIN_ID, apiLoginId);
        properties.put(AuthorizeNetProperties.TRANSACTION_KEY, transactionKey);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);
        OSGIKillbillAPI killbillAPIMock = createMock(OSGIKillbillAPI.class);

        AuthorizeNetConfigurableHandler handler = EasyMock.partialMockBuilder(AuthorizeNetConfigurableHandler.class)
                .withConstructor("auth-net-plugin", killbillAPIMock, logServiceMock)
                .addMockedMethod("getTenantProperties")
                .createMock();

        expect(handler.getTenantProperties(eq(expectedTenantId))).andReturn(properties);
        replay(handler, logServiceMock, killbillAPIMock);

        AuthorizeNetAuthentication authentication = handler.getConfigurable(expectedTenantId);
        MerchantAuthenticationType actualCredentials = authentication.getAuthenticationSetEnvironment();

        Environment actualEnv = ApiOperationBase.getEnvironment();
        assertThat(actualEnv).isNotNull().isEqualTo(Environment.SANDBOX);

        assertThat(actualCredentials).isNotNull();
        assertThat(actualCredentials.getName()).isEqualTo(apiLoginId);
        assertThat(actualCredentials.getTransactionKey()).isEqualTo(transactionKey);

        verify(handler);
    }
}
