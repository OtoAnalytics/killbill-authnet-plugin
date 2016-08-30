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
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentMethod;
import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentMethodTest;
import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentTransactionInfo;

import org.easymock.Capture;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.PaymentMethodPlugin;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.payment.plugin.api.PaymentPluginApiException;
import org.killbill.billing.payment.plugin.api.PaymentTransactionInfoPlugin;
import org.killbill.billing.tenant.api.TenantApiException;
import org.killbill.billing.util.callcontext.CallContext;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Unit tests for AuthorizeNetPaymentPluginApi.
 */
public class AuthorizeNetPaymentPluginApiTest {

    @Test
    public void addPaymentMethod() throws PaymentPluginApiException, SQLException {
        UUID expectedAccountId = UUID.randomUUID();
        CallContext expectedCallContext = createMock(CallContext.class);

        UUID expectedPaymentMethodId = UUID.randomUUID();
        boolean expectedIsDefault = false;
        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);
        Capture<Map<String, String>> propertiesCapture = newCapture();
        mockService.addPaymentMethod(eq(expectedAccountId), eq(expectedPaymentMethodId), eq(expectedIsDefault),
                 capture(propertiesCapture), eq(expectedCallContext));
        expectLastCall().once();

        List<PluginProperty> pluginProperties = new ArrayList<>();
        pluginProperties.add(new PluginProperty("cc_type", "Visa", false));
        pluginProperties.add(new PluginProperty("country", "USA", false));

        PaymentMethodPlugin paymentMethodPluginMock = createMock(PaymentMethodPlugin.class);
        expect(paymentMethodPluginMock.getProperties()).andReturn(pluginProperties);

        replay(expectedCallContext, mockService, paymentMethodPluginMock);

        List<PluginProperty> inputProperties = new ArrayList<>();
        inputProperties.add(new PluginProperty("cc_first_name", "Ostap", false));
        inputProperties.add(new PluginProperty("cc_last_name", "Bender", false));
        inputProperties.add(new PluginProperty("cc_type", "MasterCard", false));

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);
        plugin.addPaymentMethod(expectedAccountId, expectedPaymentMethodId, paymentMethodPluginMock,
                expectedIsDefault, inputProperties, expectedCallContext);

        //expect inputProperties to override pluginProperties
        Map<String, String> expectedProperties = new HashMap<>();
        expectedProperties.put("cc_first_name", "Ostap");
        expectedProperties.put("cc_last_name", "Bender");
        expectedProperties.put("cc_type", "MasterCard");
        expectedProperties.put("country", "USA");

        assertThat(propertiesCapture.hasCaptured()).isTrue();
        Map<String, String> actualProperties = propertiesCapture.getValue();

        assertThat(actualProperties).isEqualTo(expectedProperties);

        verify(expectedCallContext, mockService, paymentMethodPluginMock);
    }

    @Test
    public void addPaymentMethodError() throws PaymentPluginApiException, SQLException {

        UUID expectedAccountId = UUID.randomUUID();
        CallContext expectedCallContext = createMock(CallContext.class);

        UUID expectedPaymentMethodId = UUID.randomUUID();
        boolean expectedIsDefault = false;
        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);
        Capture<Map<String, String>> propertiesCapture = newCapture();
        mockService.addPaymentMethod(eq(expectedAccountId), eq(expectedPaymentMethodId), eq(expectedIsDefault),
                capture(propertiesCapture), eq(expectedCallContext));
        expectLastCall().andThrow(new RuntimeException());

        List<PluginProperty> pluginProperties = new ArrayList<>();
        pluginProperties.add(new PluginProperty("cc_type", "Visa", false));
        pluginProperties.add(new PluginProperty("country", "USA", false));

        PaymentMethodPlugin paymentMethodPluginMock = createMock(PaymentMethodPlugin.class);
        expect(paymentMethodPluginMock.getProperties()).andReturn(pluginProperties);

        replay(expectedCallContext, mockService, paymentMethodPluginMock);

        List<PluginProperty> inputProperties = new ArrayList<>();
        inputProperties.add(new PluginProperty("cc_first_name", "Ostap", false));
        inputProperties.add(new PluginProperty("cc_last_name", "Bender", false));
        inputProperties.add(new PluginProperty("cc_type", "MasterCard", false));

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);
        try {
            plugin.addPaymentMethod(expectedAccountId, expectedPaymentMethodId, paymentMethodPluginMock,
                    expectedIsDefault, inputProperties, expectedCallContext);
            failBecauseExceptionWasNotThrown(PaymentPluginApiException.class);
        } catch (PaymentPluginApiException e) {
            assertThat(e.getMessage()).contains(expectedPaymentMethodId.toString());
        }

        verify(expectedCallContext, mockService, paymentMethodPluginMock);
    }

    @Test
    public void getPaymentMethodDetail() throws PaymentPluginApiException {
        UUID expectedAccountId = UUID.randomUUID();
        UUID expectedPaymentMethodId = UUID.randomUUID();
        UUID expectedTenantId = UUID.randomUUID();
        CallContext mockCallContext = createMock(CallContext.class);
        expect(mockCallContext.getTenantId()).andReturn(expectedTenantId);

        AuthorizeNetPaymentMethod paymentMethod =
                new AuthorizeNetPaymentMethod(AuthorizeNetPaymentMethodTest.getPaymentMethodData());
        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);
        expect(mockService.getPaymentMethod(eq(expectedAccountId), eq(expectedPaymentMethodId),
                eq(expectedTenantId), eq(false))).andReturn(paymentMethod);

        replay(mockCallContext, mockService);

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);
        PaymentMethodPlugin actualPaymentMethod =
                plugin.getPaymentMethodDetail(expectedAccountId, expectedPaymentMethodId,
                        Collections.emptyList(), mockCallContext);

        assertThat(actualPaymentMethod).isNotNull().isInstanceOf(AuthorizeNetPaymentMethod.class);
        assertThat(actualPaymentMethod).isEqualTo(paymentMethod);

        verify(mockService);
    }

    @Test
    public void getPaymentMethodDetailRaw() throws PaymentPluginApiException {
        UUID expectedAccountId = UUID.randomUUID();
        UUID expectedPaymentMethodId = UUID.randomUUID();
        UUID expectedTenantId = UUID.randomUUID();
        CallContext mockCallContext = createMock(CallContext.class);
        expect(mockCallContext.getTenantId()).andReturn(expectedTenantId);

        final List<PluginProperty> props = Collections.singletonList(
                new PluginProperty("return-raw-db-column-names", "true", false));

        AuthorizeNetPaymentMethod paymentMethod =
                new AuthorizeNetPaymentMethod(AuthorizeNetPaymentMethodTest.getPaymentMethodDataRaw());
        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);
        expect(mockService.getPaymentMethod(eq(expectedAccountId), eq(expectedPaymentMethodId),
                eq(expectedTenantId), eq(true))).andReturn(paymentMethod);

        replay(mockCallContext, mockService);

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);
        PaymentMethodPlugin actualPaymentMethod =
                plugin.getPaymentMethodDetail(expectedAccountId, expectedPaymentMethodId, props, mockCallContext);

        assertThat(actualPaymentMethod).isNotNull().isInstanceOf(AuthorizeNetPaymentMethod.class);
        assertThat(actualPaymentMethod).isEqualTo(paymentMethod);

        verify(mockService);
    }

    @Test
    public void getPaymentMethodDetailApiException() throws PaymentPluginApiException {
        UUID expectedAccountId = UUID.randomUUID();
        UUID expectedPaymentMethodId = UUID.randomUUID();
        UUID expectedTenantId = UUID.randomUUID();
        CallContext mockCallContext = createMock(CallContext.class);
        expect(mockCallContext.getTenantId()).andReturn(expectedTenantId);

       RuntimeException runtimeException = new RuntimeException("Test Exception");
        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);
        expect(mockService.getPaymentMethod(eq(expectedAccountId), eq(expectedPaymentMethodId),
                eq(expectedTenantId), eq(false))).andThrow(runtimeException);

        replay(mockCallContext, mockService);

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);
        try {
            plugin.getPaymentMethodDetail(expectedAccountId, expectedPaymentMethodId, null, mockCallContext);
            failBecauseExceptionWasNotThrown(PaymentPluginApiException.class);
        }  catch (PaymentPluginApiException e) {
            assertThat(e.getCause()).isEqualTo(runtimeException);
        }

        verify(mockService);
    }

    @Test
    public void getPaymentInfo() throws PaymentPluginApiException {
        UUID expectedAccountId = UUID.randomUUID();
        UUID expectedPaymentId = UUID.randomUUID();

        PaymentTransactionInfoPlugin result = createMock(PaymentTransactionInfoPlugin.class);
        List<PaymentTransactionInfoPlugin> resultList = new ArrayList<>();
        resultList.add(result);
        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);
        expect(mockService.getPaymentInfo(eq(expectedAccountId), eq(expectedPaymentId)))
                .andReturn(resultList);

        replay(result, mockService);

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);
        List<PaymentTransactionInfoPlugin> actualList =
                plugin.getPaymentInfo(expectedAccountId, expectedPaymentId, null, null);
        assertThat(actualList).isNotEmpty().hasSize(1);
        assertThat(actualList.get(0)).isEqualTo(result);
        verify(mockService);
    }

    @Test
    public void getPaymentInfoError() throws PaymentPluginApiException {
        UUID expectedAccountId = UUID.randomUUID();
        UUID expectedPaymentId = UUID.randomUUID();

        RuntimeException testException = new RuntimeException("Test Exception");
        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);
        expect(mockService.getPaymentInfo(eq(expectedAccountId), eq(expectedPaymentId)))
                .andThrow(testException);

        replay(mockService);

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);
        try {
            plugin.getPaymentInfo(expectedAccountId, expectedPaymentId, null, null);
            failBecauseExceptionWasNotThrown(PaymentPluginApiException.class);
        } catch (PaymentPluginApiException e) {
            assertThat(e.getMessage()).contains(expectedAccountId.toString())
                    .contains(expectedPaymentId.toString());
        }
        verify(mockService);
    }

    @Test
    public void purchasePayment() throws TenantApiException, PaymentPluginApiException {
        UUID expectedTenantId = UUID.randomUUID();
        UUID expectedKbAccountId = UUID.randomUUID();
        UUID expectedKbPaymentId = UUID.randomUUID();
        UUID expectedKbPaymentMethodId = UUID.randomUUID();
        UUID expectedKbTransactionId = UUID.randomUUID();
        BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        Currency expectedCurrency = Currency.USD;

        CallContext mockCallContext = createMock(CallContext.class);
        expect(mockCallContext.getTenantId()).andReturn(expectedTenantId);

        AuthorizeNetPaymentTransactionInfo transactionMock = createMock(AuthorizeNetPaymentTransactionInfo.class);
        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);
        expect(mockService.purchasePayment(eq(expectedTenantId), eq(expectedKbAccountId),
                eq(expectedKbPaymentId), eq(expectedKbTransactionId), eq(expectedKbPaymentMethodId),
                eq(expectedAmount), eq(expectedCurrency)))
        .andReturn(transactionMock);

        replay(mockCallContext, mockService, transactionMock);

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);

        PaymentTransactionInfoPlugin chargePayment = plugin.purchasePayment(expectedKbAccountId, expectedKbPaymentId,
                expectedKbTransactionId, expectedKbPaymentMethodId, expectedAmount, expectedCurrency, null,
                mockCallContext);

        assertThat(chargePayment).isNotNull().isEqualTo(transactionMock);
        verify(mockService);
    }

    @Test
    public void purchasePaymentError() throws TenantApiException, PaymentPluginApiException {
        UUID expectedTenantId = UUID.randomUUID();
        UUID expectedKbAccountId = UUID.randomUUID();
        UUID expectedKbPaymentId = UUID.randomUUID();
        UUID expectedKbPaymentMethodId = UUID.randomUUID();
        UUID expectedKbTransactionId = UUID.randomUUID();
        BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        Currency expectedCurrency = Currency.USD;

        CallContext mockCallContext = createMock(CallContext.class);
        expect(mockCallContext.getTenantId()).andReturn(expectedTenantId);

        RuntimeException testException = new RuntimeException("Test Exception");
        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);
        expect(mockService.purchasePayment(eq(expectedTenantId), eq(expectedKbAccountId),
                eq(expectedKbPaymentId), eq(expectedKbTransactionId), eq(expectedKbPaymentMethodId),
                eq(expectedAmount), eq(expectedCurrency)))
                .andThrow(testException);

        replay(mockCallContext, mockService);

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);

        try {
            plugin.purchasePayment(expectedKbAccountId, expectedKbPaymentId,
                    expectedKbTransactionId, expectedKbPaymentMethodId, expectedAmount, expectedCurrency, null,
                    mockCallContext);
            failBecauseExceptionWasNotThrown(PaymentPluginApiException.class);
        } catch (PaymentPluginApiException e) {
            assertThat(e.getMessage()).contains(expectedKbPaymentMethodId.toString())
                    .contains(expectedKbTransactionId.toString())
                    .contains(expectedKbAccountId.toString());
        }
        verify(mockService);
    }

    @Test
    public void refundPayment() throws TenantApiException, PaymentPluginApiException {
        UUID expectedTenantId = UUID.randomUUID();
        UUID expectedKbAccountId = UUID.randomUUID();
        UUID expectedKbPaymentId = UUID.randomUUID();
        UUID expectedKbPaymentMethodId = UUID.randomUUID();
        UUID expectedKbTransactionId = UUID.randomUUID();
        BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        Currency expectedCurrency = Currency.USD;

        CallContext mockCallContext = createMock(CallContext.class);
        expect(mockCallContext.getTenantId()).andReturn(expectedTenantId);

        AuthorizeNetPaymentTransactionInfo transactionMock = createMock(AuthorizeNetPaymentTransactionInfo.class);
        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);
        expect(mockService.refundPayment(eq(expectedTenantId), eq(expectedKbAccountId),
                eq(expectedKbPaymentId), eq(expectedKbTransactionId), eq(expectedKbPaymentMethodId),
                eq(expectedAmount), eq(expectedCurrency)))
                .andReturn(transactionMock);

        replay(mockCallContext, mockService, transactionMock);

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);

        PaymentTransactionInfoPlugin chargePayment = plugin.refundPayment(expectedKbAccountId, expectedKbPaymentId,
                expectedKbTransactionId, expectedKbPaymentMethodId, expectedAmount, expectedCurrency, null,
                mockCallContext);

        assertThat(chargePayment).isNotNull().isEqualTo(transactionMock);
        verify(mockService);
    }

    @Test
    public void refundPaymentError() throws TenantApiException, PaymentPluginApiException {
        UUID expectedTenantId = UUID.randomUUID();
        UUID expectedKbAccountId = UUID.randomUUID();
        UUID expectedKbPaymentId = UUID.randomUUID();
        UUID expectedKbPaymentMethodId = UUID.randomUUID();
        UUID expectedKbTransactionId = UUID.randomUUID();
        BigDecimal expectedAmount = BigDecimal.valueOf(70.03);
        Currency expectedCurrency = Currency.USD;

        CallContext mockCallContext = createMock(CallContext.class);
        expect(mockCallContext.getTenantId()).andReturn(expectedTenantId);

        RuntimeException testException = new RuntimeException("Test Exception");
        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);
        expect(mockService.refundPayment(eq(expectedTenantId), eq(expectedKbAccountId),
                eq(expectedKbPaymentId), eq(expectedKbTransactionId), eq(expectedKbPaymentMethodId),
                eq(expectedAmount), eq(expectedCurrency)))
                .andThrow(testException);

        replay(mockCallContext, mockService);

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);

        try {
            plugin.refundPayment(expectedKbAccountId, expectedKbPaymentId,
                    expectedKbTransactionId, expectedKbPaymentMethodId, expectedAmount, expectedCurrency, null,
                    mockCallContext);
            failBecauseExceptionWasNotThrown(PaymentPluginApiException.class);
        } catch (PaymentPluginApiException e) {
            assertThat(e.getMessage()).contains(expectedKbPaymentMethodId.toString())
                    .contains(expectedKbTransactionId.toString())
                    .contains(expectedKbAccountId.toString());
        }
        verify(mockService);
    }


    @Test
    public void deletePaymentMethod() throws PaymentPluginApiException, TenantApiException {
        UUID expectedAccountId = UUID.randomUUID();
        UUID expectedPaymentMethodId = UUID.randomUUID();
        UUID expectedTenantId = UUID.randomUUID();

        CallContext mockCallContext = createMock(CallContext.class);
        expect(mockCallContext.getTenantId()).andReturn(expectedTenantId);

        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);
        mockService.deactivatePaymentMethod(eq(expectedAccountId), eq(expectedPaymentMethodId),
                eq(expectedTenantId));
        expectLastCall().once();

        replay(mockCallContext, mockService);

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);
        plugin.deletePaymentMethod(expectedAccountId, expectedPaymentMethodId, null, mockCallContext);

        verify(mockCallContext, mockService);
    }

    @Test
    public void deletePaymentMethodError() throws PaymentPluginApiException, TenantApiException {
        UUID expectedAccountId = UUID.randomUUID();
        UUID expectedPaymentMethodId = UUID.randomUUID();
        UUID expectedTenantId = UUID.randomUUID();

        CallContext mockCallContext = createMock(CallContext.class);
        expect(mockCallContext.getTenantId()).andReturn(expectedTenantId);

        RuntimeException testException = new RuntimeException("Test Exception");
        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);
        mockService.deactivatePaymentMethod(eq(expectedAccountId), eq(expectedPaymentMethodId),
                eq(expectedTenantId));
        expectLastCall().andThrow(testException);

        replay(mockCallContext, mockService);

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);
        try {
            plugin.deletePaymentMethod(expectedAccountId, expectedPaymentMethodId, null, mockCallContext);
            failBecauseExceptionWasNotThrown(PaymentPluginApiException.class);
        } catch (PaymentPluginApiException e) {
            assertThat(e.getMessage()).isNotEmpty()
                    .contains(expectedPaymentMethodId.toString())
                    .contains(expectedAccountId.toString());
        }

        verify(mockCallContext, mockService);
    }

    @Test
    public void setDefaultPaymentMethod() throws PaymentPluginApiException, TenantApiException {
        UUID expectedAccountId = UUID.randomUUID();
        UUID expectedPaymentMethodId = UUID.randomUUID();

        CallContext mockCallContext = createMock(CallContext.class);
        AuthorizeNetService mockService = createMock(AuthorizeNetService.class);

        replay(mockCallContext, mockService);

        AuthorizeNetPaymentPluginApi plugin = new AuthorizeNetPaymentPluginApi(null, null, mockService);
        plugin.setDefaultPaymentMethod(expectedAccountId, expectedPaymentMethodId, null, mockCallContext);

        // verify that no calls were made
        verify(mockCallContext, mockService);
    }
}
