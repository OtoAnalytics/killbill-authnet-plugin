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

package com.womply.billing.killbill.plugins;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.womply.billing.killbill.plugins.AuthorizeNetServlet.Response;
import com.womply.billing.killbill.plugins.db.AuthorizeNetDAO;
import com.womply.killbill.resources.models.AuthorizeNetHealthResponse;
import com.womply.killbill.resources.models.PaymentGatewayAccount;

import net.authorize.api.contract.v1.AuthenticateTestResponse;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.MessagesType;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.tenant.api.Tenant;
import org.killbill.billing.tenant.api.TenantApiException;
import org.killbill.billing.tenant.api.TenantUserApi;
import org.osgi.service.log.LogService;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Unit tests for AuthorizeNetServlet.
 */
public class AuthorizeNetServletTest {

    @Test
    public void doGetHealthL2EmptyPing() throws TenantApiException, IOException, ServletException {
        String expectedTenantApiKey = "testTenant";

        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACTION)))
                .andReturn(AuthorizeNetServlet.ACTION_HEALTH_L2);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_TENANT_API_KEY)))
                .andReturn(expectedTenantApiKey);

        UUID tenantId = UUID.randomUUID();
        Tenant tenant = EasyMock.mock(Tenant.class);
        expect(tenant.getId()).andReturn(tenantId);

        TenantUserApi tenantUserApi = EasyMock.mock(TenantUserApi.class);
        expect(tenantUserApi.getTenantByApiKey(expectedTenantApiKey)).andReturn(tenant);

        OSGIKillbillAPI killbillAPI = EasyMock.mock(OSGIKillbillAPI.class);
        expect(killbillAPI.getTenantUserApi()).andReturn(tenantUserApi);

        AuthorizeNetService service = EasyMock.mock(AuthorizeNetService.class);
        expect(service.getAuthenticateTestResponse(eq(tenantId))).andReturn(null);

        HttpServletResponse responseMock = createNiceMock(HttpServletResponse.class);

        replay(requestMock, responseMock, tenantUserApi, killbillAPI, tenant, service);

        AuthorizeNetServlet servlet = EasyMock.partialMockBuilder(AuthorizeNetServlet.class)
                .withConstructor(
                        OSGIKillbillAPI.class,
                        AuthorizeNetDAO.class,
                        LogService.class,
                        AuthorizeNetService.class)
                .withArgs(killbillAPI, null, null, service)
                .addMockedMethod("writeResponseJson")
                .createMock();

        Capture<Response> responseCapture = newCapture();
        servlet.writeResponseJson(capture(responseCapture), eq(responseMock));
        expectLastCall().once();
        replay(servlet);

        servlet.doGet(requestMock, responseMock);

        verify(requestMock, responseMock, servlet, tenantUserApi, killbillAPI, tenant, service);

        assertThat(responseCapture.hasCaptured()).isTrue();
        Response response = responseCapture.getValue();
        assertThat(response.isOk()).isTrue();
        AuthorizeNetHealthResponse health = (AuthorizeNetHealthResponse)response.getData();
        assertThat(health.isHealthy()).isFalse();
        assertThat(health.getActionTime()).isEqualTo(-1);
        assertThat(health.getLevel()).isEqualTo(2);
        assertThat(health.getMessage()).isEqualTo("Got null AuthenticateTestResponse from Auth.Net");
        assertThat(health.getNumHeldForReviewTransactions()).isEqualTo(-1);
    }

    @Test
    public void doGetHealthL2NotOk() throws TenantApiException, IOException, ServletException {
        String expectedTenantApiKey = "testTenant";

        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACTION)))
                .andReturn(AuthorizeNetServlet.ACTION_HEALTH_L2);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_TENANT_API_KEY)))
                .andReturn(expectedTenantApiKey);

        UUID tenantId = UUID.randomUUID();
        Tenant tenant = EasyMock.mock(Tenant.class);
        expect(tenant.getId()).andReturn(tenantId);

        TenantUserApi tenantUserApi = EasyMock.mock(TenantUserApi.class);
        expect(tenantUserApi.getTenantByApiKey(expectedTenantApiKey)).andReturn(tenant);

        OSGIKillbillAPI killbillAPI = EasyMock.mock(OSGIKillbillAPI.class);
        expect(killbillAPI.getTenantUserApi()).andReturn(tenantUserApi);

        List<MessagesType.Message> messages = new ArrayList<>();
        MessagesType.Message message1 = new MessagesType.Message();
        message1.setCode("123");
        message1.setText("ABC");
        messages.add(message1);
        MessagesType.Message message2 = new MessagesType.Message();
        message2.setCode("124");
        message2.setText("DEF");
        messages.add(message2);

        MessagesType messagesType = mock(MessagesType.class);
        expect(messagesType.getResultCode()).andReturn(MessageTypeEnum.ERROR).anyTimes();
        expect(messagesType.getMessage()).andReturn(messages).once();
        replay(messagesType);

        AuthenticateTestResponse authTestResponse = new AuthenticateTestResponse();
        authTestResponse.setMessages(messagesType);

        AuthorizeNetService service = EasyMock.mock(AuthorizeNetService.class);
        expect(service.getAuthenticateTestResponse(eq(tenantId))).andReturn(authTestResponse);

        HttpServletResponse responseMock = createNiceMock(HttpServletResponse.class);

        replay(requestMock, responseMock, tenantUserApi, killbillAPI, tenant, service);

        AuthorizeNetServlet servlet = EasyMock.partialMockBuilder(AuthorizeNetServlet.class)
                .withConstructor(
                        OSGIKillbillAPI.class,
                        AuthorizeNetDAO.class,
                        LogService.class,
                        AuthorizeNetService.class)
                .withArgs(killbillAPI, null, null, service)
                .addMockedMethod("writeResponseJson")
                .createMock();

        Capture<Response> responseCapture = newCapture();
        servlet.writeResponseJson(capture(responseCapture), eq(responseMock));
        expectLastCall().once();
        replay(servlet);

        servlet.doGet(requestMock, responseMock);

        verify(requestMock, responseMock, servlet, tenantUserApi, killbillAPI, tenant, service);

        assertThat(responseCapture.hasCaptured()).isTrue();
        Response response = responseCapture.getValue();
        assertThat(response.isOk()).isTrue();
        AuthorizeNetHealthResponse health = (AuthorizeNetHealthResponse)response.getData();
        assertThat(health.isHealthy()).isFalse();
        assertThat(health.getActionTime()).isEqualTo(-1);
        assertThat(health.getLevel()).isEqualTo(2);
        assertThat(health.getMessage()).isEqualTo("123 : ABC\n124 : DEF\n");
        assertThat(health.getNumHeldForReviewTransactions()).isEqualTo(-1);
    }

    @Test
    public void doGetHealthL2HealthyCase() throws TenantApiException, IOException, ServletException {
        String expectedTenantApiKey = "testTenant";

        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACTION)))
                .andReturn(AuthorizeNetServlet.ACTION_HEALTH_L2);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_TENANT_API_KEY)))
                .andReturn(expectedTenantApiKey);

        UUID tenantId = UUID.randomUUID();
        Tenant tenant = EasyMock.mock(Tenant.class);
        expect(tenant.getId()).andReturn(tenantId);

        TenantUserApi tenantUserApi = EasyMock.mock(TenantUserApi.class);
        expect(tenantUserApi.getTenantByApiKey(expectedTenantApiKey)).andReturn(tenant);

        OSGIKillbillAPI killbillAPI = EasyMock.mock(OSGIKillbillAPI.class);
        expect(killbillAPI.getTenantUserApi()).andReturn(tenantUserApi);

        AuthorizeNetDAO dao = EasyMock.mock(AuthorizeNetDAO.class);
        expect(dao.getHeldForReviewTransactionCount(tenantId)).andReturn(7);

        List<MessagesType.Message> messages = new ArrayList<>();
        MessagesType.Message message1 = new MessagesType.Message();
        message1.setCode("123");
        message1.setText("ABC");
        messages.add(message1);
        MessagesType.Message message2 = new MessagesType.Message();
        message2.setCode("124");
        message2.setText("DEF");
        messages.add(message2);

        MessagesType messagesType = mock(MessagesType.class);
        expect(messagesType.getResultCode()).andReturn(MessageTypeEnum.OK).anyTimes();
        expect(messagesType.getMessage()).andReturn(messages).once();
        replay(messagesType);

        AuthenticateTestResponse authTestResponse = new AuthenticateTestResponse();
        authTestResponse.setMessages(messagesType);

        AuthorizeNetService service = EasyMock.mock(AuthorizeNetService.class);
        expect(service.getAuthenticateTestResponse(eq(tenantId))).andReturn(authTestResponse);

        HttpServletResponse responseMock = createNiceMock(HttpServletResponse.class);

        replay(requestMock, responseMock, tenantUserApi, dao, killbillAPI, tenant, service);

        AuthorizeNetServlet servlet = EasyMock.partialMockBuilder(AuthorizeNetServlet.class)
                .withConstructor(
                        OSGIKillbillAPI.class,
                        AuthorizeNetDAO.class,
                        LogService.class,
                        AuthorizeNetService.class)
                .withArgs(killbillAPI, dao, null, service)
                .addMockedMethod("writeResponseJson")
                .createMock();

        Capture<Response> responseCapture = newCapture();
        servlet.writeResponseJson(capture(responseCapture), eq(responseMock));
        expectLastCall().once();
        replay(servlet);

        servlet.doGet(requestMock, responseMock);

        verify(requestMock, responseMock, servlet, tenantUserApi, dao, killbillAPI, tenant, service);

        assertThat(responseCapture.hasCaptured()).isTrue();
        Response response = responseCapture.getValue();
        assertThat(response.isOk()).isTrue();
        AuthorizeNetHealthResponse health = (AuthorizeNetHealthResponse)response.getData();
        assertThat(health.isHealthy()).isTrue();
        assertThat(health.getActionTime()).isEqualTo(-1);
        assertThat(health.getLevel()).isEqualTo(2);
        assertThat(health.getMessage()).isEqualTo("Ping to Auth.Net was successful");
        assertThat(health.getNumHeldForReviewTransactions()).isEqualTo(7);
    }

//    @Test
//    public void doGetHealthL1() throws TenantApiException, IOException, ServletException {
//        String expectedTenantApiKey = "testTenant";
//
//        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
//        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACTION)))
//                .andReturn(AuthorizeNetServlet.ACTION_HEALTH_L1);
//        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_TENANT_API_KEY)))
//                .andReturn(expectedTenantApiKey);
//
//        TenantUserApi tenantUserApi = EasyMock.mock(TenantUserApi.class);
//        UUID tenantId = UUID.randomUUID();
//        Tenant tenant = EasyMock.mock(Tenant.class);
//        expect(tenant.getId()).andReturn(tenantId);
//
//        TenantContext tenantContext = new DefaultTenantContext(tenantId);
//        expect(tenantUserApi.getTenantByApiKey(expectedTenantApiKey)).andReturn(tenant);
//
//        AccountUserApi accountUserApi = EasyMock.mock(AccountUserApi.class);
//        expect(accountUserApi.getAccounts(eq(0L), eq(30L), eq(tenantContext))).andReturn(null);
//
//        OSGIKillbillAPI killbillAPI = EasyMock.mock(OSGIKillbillAPI.class);
//        expect(killbillAPI.getTenantUserApi()).andReturn(tenantUserApi);
//        expect(killbillAPI.getAccountUserApi()).andReturn(accountUserApi);
//
//        HttpServletResponse responseMock = createNiceMock(HttpServletResponse.class);
//
//        replay(requestMock, responseMock, tenantUserApi, accountUserApi, killbillAPI, tenant);
//
//        AuthorizeNetServlet servlet = EasyMock.partialMockBuilder(AuthorizeNetServlet.class)
//                .withConstructor(
//                        OSGIKillbillAPI.class,
//                        AuthorizeNetDAO.class,
//                        LogService.class,
//                        AuthorizeNetService.class)
//                .withArgs(killbillAPI, null, null, null)
//                .addMockedMethod("writeResponseJson")
//                .createMock();
//
//        Capture<Response> responseCapture = newCapture();
//        servlet.writeResponseJson(capture(responseCapture), eq(responseMock));
//        expectLastCall().once();
//        replay(servlet);
//
//        servlet.doGet(requestMock, responseMock);
//
//        verify(requestMock, responseMock, servlet, tenantUserApi, accountUserApi, killbillAPI, tenant);
//
//        assertThat(responseCapture.hasCaptured()).isTrue();
//        Response response = responseCapture.getValue();
//        assertThat(response.isOk()).isTrue();
//        AuthorizeNetHealthResponse health = (AuthorizeNetHealthResponse)response.getData();
//        assertThat(health.isHealthy()).isTrue();
//        assertThat(health.getActionTime()).isLessThan(100);
//        assertThat(health.getLevel()).isEqualTo(1);
//        assertThat(health.getMessage()).isEqualTo("ok");
//        assertThat(health.getNumHeldForReviewTransactions()).isEqualTo(-1);
//    }

    @Test
    public void doPostAddAccount() throws TenantApiException, IOException, ServletException {
        String expectedTenantApiKey = "testTenant";

        String accountJson = "{\"merchantLocationId\":\"82\"}";
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACTION)))
                .andReturn(AuthorizeNetServlet.ACTION_ADD_ACCOUNT);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_TENANT_API_KEY)))
                .andReturn(expectedTenantApiKey);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACCOUNT_DATA)))
                .andReturn(accountJson);

        AuthorizeNetService serviceMock = createMock(AuthorizeNetService.class);
        String customerProfileId = "67890";
        Capture<PaymentGatewayAccount> accountCapture = newCapture();
        expect(serviceMock.addCustomerProfile(eq(expectedTenantApiKey), capture(accountCapture)))
                .andReturn(customerProfileId);

        HttpServletResponse responseMock = createNiceMock(HttpServletResponse.class);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);

        replay(requestMock, serviceMock, responseMock, logServiceMock);

        AuthorizeNetServlet servlet = EasyMock.partialMockBuilder(AuthorizeNetServlet.class)
                .withConstructor(
                        OSGIKillbillAPI.class,
                        AuthorizeNetDAO.class,
                        LogService.class,
                        AuthorizeNetService.class)
                .withArgs(null, null, logServiceMock, serviceMock)
                .addMockedMethod("writeResponseJson")
                .createMock();

        Capture<Response> responseCapture = newCapture();
        servlet.writeResponseJson(capture(responseCapture), eq(responseMock));
        expectLastCall().once();
        replay(servlet);

        servlet.doPost(requestMock, responseMock);

        verify(requestMock, serviceMock, responseMock, logServiceMock, servlet);

        int expectedMerchantLocationId = 82;
        assertThat(accountCapture.hasCaptured()).isTrue();
        PaymentGatewayAccount actualAccount = accountCapture.getValue();
        assertThat(actualAccount.getMerchantLocationId()).isEqualTo(expectedMerchantLocationId);

        assertThat(responseCapture.hasCaptured()).isTrue();
        Response response = responseCapture.getValue();
        assertThat(response.isOk()).isTrue();
        PaymentGatewayAccount createdAccount = (PaymentGatewayAccount)response.getData();
        assertThat(createdAccount.getMerchantLocationId()).isEqualTo(expectedMerchantLocationId);
        assertThat(createdAccount.getCustomerProfileId()).isEqualTo(customerProfileId);
    }

    @Test
    public void doPostMissingParamAction() throws TenantApiException, IOException, ServletException {
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACTION)))
                .andReturn(null);

        HttpServletResponse responseMock = createNiceMock(HttpServletResponse.class);
        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);
        AuthorizeNetService serviceMock = createNiceMock(AuthorizeNetService.class);

        replay(requestMock, responseMock, logServiceMock, serviceMock);

        AuthorizeNetServlet servlet = EasyMock.partialMockBuilder(AuthorizeNetServlet.class)
                .withConstructor(
                        OSGIKillbillAPI.class,
                        AuthorizeNetDAO.class,
                        LogService.class,
                        AuthorizeNetService.class)
                .withArgs(null, null, logServiceMock, serviceMock)
                .addMockedMethod("writeResponseJson")
                .createMock();

        Capture<Response> responseCapture = newCapture();
        servlet.writeResponseJson(capture(responseCapture), eq(responseMock));
        replay(servlet);

        servlet.doPost(requestMock, responseMock);

        assertThat(responseCapture.hasCaptured()).isTrue();
        Response response = responseCapture.getValue();
        assertThat(response.isOk()).isFalse();
        String error = response.getError();
        assertThat(error).isNotEmpty().contains(AuthorizeNetServlet.PARAM_NAME_ACTION);
    }

    @Test
    public void doPostMissingParamTenantApiKey() throws TenantApiException, IOException, ServletException {
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACTION)))
                .andReturn(AuthorizeNetServlet.ACTION_ADD_ACCOUNT);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_TENANT_API_KEY)))
                .andReturn(null);

        HttpServletResponse responseMock = createNiceMock(HttpServletResponse.class);
        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);
        AuthorizeNetService serviceMock = createNiceMock(AuthorizeNetService.class);

        replay(requestMock, responseMock, logServiceMock, serviceMock);

        AuthorizeNetServlet servlet = EasyMock.partialMockBuilder(AuthorizeNetServlet.class)
                .withConstructor(
                        OSGIKillbillAPI.class,
                        AuthorizeNetDAO.class,
                        LogService.class,
                        AuthorizeNetService.class)
                .withArgs(null, null, logServiceMock, serviceMock)
                .addMockedMethod("writeResponseJson")
                .createMock();

        Capture<Response> responseCapture = newCapture();
        servlet.writeResponseJson(capture(responseCapture), eq(responseMock));
        replay(servlet);

        servlet.doPost(requestMock, responseMock);

        assertThat(responseCapture.hasCaptured()).isTrue();
        Response response = responseCapture.getValue();
        assertThat(response.isOk()).isFalse();
        String error = response.getError();
        assertThat(error).isNotEmpty().contains(AuthorizeNetServlet.PARAM_NAME_TENANT_API_KEY);
    }

    @Test
    public void doPostMissingParamAccount() throws TenantApiException, IOException, ServletException {
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACTION)))
                .andReturn(AuthorizeNetServlet.ACTION_ADD_ACCOUNT);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_TENANT_API_KEY)))
                .andReturn("testTenantApiKey");
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACCOUNT_DATA)))
                .andReturn(null);

        HttpServletResponse responseMock = createNiceMock(HttpServletResponse.class);
        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);
        AuthorizeNetService serviceMock = createNiceMock(AuthorizeNetService.class);

        replay(requestMock, responseMock, logServiceMock, serviceMock);

        AuthorizeNetServlet servlet = EasyMock.partialMockBuilder(AuthorizeNetServlet.class)
                .withConstructor(
                        OSGIKillbillAPI.class,
                        AuthorizeNetDAO.class,
                        LogService.class,
                        AuthorizeNetService.class)
                .withArgs(null, null, logServiceMock, serviceMock)
                .addMockedMethod("writeResponseJson")
                .createMock();

        Capture<Response> responseCapture = newCapture();
        servlet.writeResponseJson(capture(responseCapture), eq(responseMock));
        replay(servlet);

        servlet.doPost(requestMock, responseMock);

        assertThat(responseCapture.hasCaptured()).isTrue();
        Response response = responseCapture.getValue();
        assertThat(response.isOk()).isFalse();
        String error = response.getError();
        assertThat(error).isNotEmpty().contains(AuthorizeNetServlet.PARAM_NAME_ACCOUNT_DATA);
    }

    @Test
    public void doPostMissingParamBadAccountJson() throws TenantApiException, IOException, ServletException {
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACTION)))
                .andReturn(AuthorizeNetServlet.ACTION_ADD_ACCOUNT);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_TENANT_API_KEY)))
                .andReturn("testTenantApiKey");
        String unparseableAccountJson = "{\"merchantLocId\":\"82\"}";
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACCOUNT_DATA)))
                .andReturn(unparseableAccountJson);

        HttpServletResponse responseMock = createNiceMock(HttpServletResponse.class);
        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);
        AuthorizeNetService serviceMock = createNiceMock(AuthorizeNetService.class);

        replay(requestMock, responseMock, logServiceMock, serviceMock);

        AuthorizeNetServlet servlet = EasyMock.partialMockBuilder(AuthorizeNetServlet.class)
                .withConstructor(
                        OSGIKillbillAPI.class,
                        AuthorizeNetDAO.class,
                        LogService.class,
                        AuthorizeNetService.class)
                .withArgs(null, null, logServiceMock, serviceMock)
                .addMockedMethod("writeResponseJson")
                .createMock();

        Capture<Response> responseCapture = newCapture();
        servlet.writeResponseJson(capture(responseCapture), eq(responseMock));
        replay(servlet);

        servlet.doPost(requestMock, responseMock);

        assertThat(responseCapture.hasCaptured()).isTrue();
        Response response = responseCapture.getValue();
        assertThat(response.isOk()).isFalse();
        String error = response.getError();
        assertThat(error).isNotEmpty().contains("Unable to parse JSON");
    }

    @Test
    public void doPostUnknownAction() throws TenantApiException, IOException, ServletException {
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACTION)))
                .andReturn("unknownAction");

        HttpServletResponse responseMock = createNiceMock(HttpServletResponse.class);
        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);
        AuthorizeNetService serviceMock = createNiceMock(AuthorizeNetService.class);

        replay(requestMock, responseMock, logServiceMock, serviceMock);

        AuthorizeNetServlet servlet = EasyMock.partialMockBuilder(AuthorizeNetServlet.class)
                .withConstructor(
                        OSGIKillbillAPI.class,
                        AuthorizeNetDAO.class,
                        LogService.class,
                        AuthorizeNetService.class)
                .withArgs(null, null, logServiceMock, serviceMock)
                .addMockedMethod("writeResponseJson")
                .createMock();

        Capture<Response> responseCapture = newCapture();
        servlet.writeResponseJson(capture(responseCapture), eq(responseMock));
        expectLastCall().once();
        replay(servlet);

        servlet.doPost(requestMock, responseMock);
        verify(servlet);

        assertThat(responseCapture.hasCaptured()).isTrue();
        Response response = responseCapture.getValue();
        assertThat(response.isOk()).isFalse();
        String error = response.getError();
        assertThat(error).isNotEmpty().contains("Unsupported action");
    }

    @Test
    public void doPostUnknownTenant() throws TenantApiException, IOException, ServletException {
        String expectedTenantApiKey = "testTenant";
        String accountJson = "{\"merchantLocationId\":\"82\"}";
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACTION)))
                .andReturn(AuthorizeNetServlet.ACTION_ADD_ACCOUNT);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_TENANT_API_KEY)))
                .andReturn(expectedTenantApiKey);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACCOUNT_DATA)))
                .andReturn(accountJson);

        AuthorizeNetService serviceMock = createMock(AuthorizeNetService.class);
        RuntimeException unknownTenantException = new RuntimeException("Unknown Tenant");
        TenantApiException testException = new TenantApiException(unknownTenantException, 1, "Unknown tenant");
        Capture<PaymentGatewayAccount> accountCapture = newCapture();
        expect(serviceMock.addCustomerProfile(eq(expectedTenantApiKey), capture(accountCapture)))
                .andThrow(testException);

        HttpServletResponse responseMock = createNiceMock(HttpServletResponse.class);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);

        replay(requestMock, serviceMock, responseMock, logServiceMock);

        AuthorizeNetServlet servlet = EasyMock.partialMockBuilder(AuthorizeNetServlet.class)
                .withConstructor(
                        OSGIKillbillAPI.class,
                        AuthorizeNetDAO.class,
                        LogService.class,
                        AuthorizeNetService.class)
                .withArgs(null, null, logServiceMock, serviceMock)
                .addMockedMethod("writeResponseJson")
                .createMock();

        Capture<Response> responseCapture = newCapture();
        servlet.writeResponseJson(capture(responseCapture), eq(responseMock));
        expectLastCall().once();
        replay(servlet);

        servlet.doPost(requestMock, responseMock);

        verify(servlet);
        verify(serviceMock);

        assertThat(responseCapture.hasCaptured()).isTrue();
        Response response = responseCapture.getValue();
        assertThat(response.isOk()).isFalse();
        String error = response.getError();
        assertThat(error).isNotEmpty().contains(expectedTenantApiKey);

    }

    @Test
    public void doPostAuthNetError() throws TenantApiException, IOException, ServletException {
        String expectedTenantApiKey = "testTenant";
        String accountJson = "{\"merchantLocationId\":\"82\"}";
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACTION)))
                .andReturn(AuthorizeNetServlet.ACTION_ADD_ACCOUNT);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_TENANT_API_KEY)))
                .andReturn(expectedTenantApiKey);
        expect(requestMock.getParameter(eq(AuthorizeNetServlet.PARAM_NAME_ACCOUNT_DATA)))
                .andReturn(accountJson);

        AuthorizeNetService serviceMock = createMock(AuthorizeNetService.class);
        RuntimeException duplicateEntryException = new RuntimeException("Test Exception: Auth.Net duplicate entry");
        Capture<PaymentGatewayAccount> accountCapture = newCapture();
        expect(serviceMock.addCustomerProfile(eq(expectedTenantApiKey), capture(accountCapture)))
                .andThrow(duplicateEntryException);

        HttpServletResponse responseMock = createNiceMock(HttpServletResponse.class);

        OSGIKillbillLogService logServiceMock = createNiceMock(OSGIKillbillLogService.class);

        replay(requestMock, serviceMock, responseMock, logServiceMock);

        AuthorizeNetServlet servlet = EasyMock.partialMockBuilder(AuthorizeNetServlet.class)
                .withConstructor(
                        OSGIKillbillAPI.class,
                        AuthorizeNetDAO.class,
                        LogService.class,
                        AuthorizeNetService.class)
                .withArgs(null, null, logServiceMock, serviceMock)
                .addMockedMethod("writeResponseJson")
                .createMock();

        Capture<Response> responseCapture = newCapture();
        servlet.writeResponseJson(capture(responseCapture), eq(responseMock));
        expectLastCall().once();
        replay(servlet);

        servlet.doPost(requestMock, responseMock);

        verify(servlet);
        verify(serviceMock);

        assertThat(responseCapture.hasCaptured()).isTrue();
        Response response = responseCapture.getValue();
        assertThat(response.isOk()).isFalse();
        String error = response.getError();
        assertThat(error).isNotEmpty().contains(duplicateEntryException.getMessage());

    }

}
