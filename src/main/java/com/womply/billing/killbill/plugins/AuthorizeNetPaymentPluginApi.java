package com.womply.billing.killbill.plugins;

import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.PaymentMethodPlugin;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.payment.plugin.api.GatewayNotification;
import org.killbill.billing.payment.plugin.api.HostedPaymentPageFormDescriptor;
import org.killbill.billing.payment.plugin.api.PaymentMethodInfoPlugin;
import org.killbill.billing.payment.plugin.api.PaymentPluginApi;
import org.killbill.billing.payment.plugin.api.PaymentPluginApiException;
import org.killbill.billing.payment.plugin.api.PaymentTransactionInfoPlugin;
import org.killbill.billing.plugin.api.PluginProperties;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.billing.util.callcontext.TenantContext;
import org.killbill.billing.util.entity.Pagination;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillLogService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * Implements KillBill plugin interface for Authorize.net.
 */
public class AuthorizeNetPaymentPluginApi implements PaymentPluginApi {

    private final Properties properties;
    private final OSGIKillbillLogService logService;
    private final AuthorizeNetService service;

    // this constructor is used by tests
    public AuthorizeNetPaymentPluginApi(final Properties properties, final OSGIKillbillLogService logService,
                                        final AuthorizeNetService service) {
        this.properties = properties;
        this.logService = logService;
        this.service = service;
    }

    @Override
    public PaymentTransactionInfoPlugin authorizePayment(final UUID kbAccountId, final UUID kbPaymentId, final UUID kbTransactionId, final UUID kbPaymentMethodId, final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentTransactionInfoPlugin capturePayment(final UUID kbAccountId, final UUID kbPaymentId, final UUID kbTransactionId, final UUID kbPaymentMethodId, final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentTransactionInfoPlugin purchasePayment(final UUID kbAccountId, final UUID kbPaymentId, final UUID kbTransactionId, final UUID kbPaymentMethodId, final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        try {
            return service.purchasePayment(context.getTenantId(), kbAccountId, kbPaymentId, kbTransactionId,
                    kbPaymentMethodId, amount, currency);
        } catch (final Exception e) {
            throw new PaymentPluginApiException("Unable to perform purchasePayment for kbPaymentMethodId = " +
                    kbPaymentMethodId + ", kbTransactionId = " + kbTransactionId + " kbAccountId = " + kbAccountId, e);
        }
    }

    @Override
    public PaymentTransactionInfoPlugin voidPayment(final UUID kbAccountId, final UUID kbPaymentId, final UUID kbTransactionId, final UUID kbPaymentMethodId, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentTransactionInfoPlugin creditPayment(final UUID kbAccountId, final UUID kbPaymentId, final UUID kbTransactionId, final UUID kbPaymentMethodId, final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentTransactionInfoPlugin refundPayment(final UUID kbAccountId, final UUID kbPaymentId, final UUID kbTransactionId, final UUID kbPaymentMethodId, final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        try {
            return service.refundPayment(context.getTenantId(), kbAccountId, kbPaymentId, kbTransactionId,
                    kbPaymentMethodId, amount, currency);
        } catch (final Exception e) {
            throw new PaymentPluginApiException("Unable to perform refundPayment for kbPaymentId = " + kbPaymentId +
                    ", " + kbPaymentId + ", kbPaymentMethodId  = " +
                    kbPaymentMethodId + ", kbTransactionId = " + kbTransactionId + " kbAccountId = " + kbAccountId, e);
        }
    }

    @Override
    public List<PaymentTransactionInfoPlugin> getPaymentInfo(final UUID kbAccountId, final UUID kbPaymentId, final Iterable<PluginProperty> properties, final TenantContext context) throws PaymentPluginApiException {
        try {
            return service.getPaymentInfo(kbAccountId, kbPaymentId);
        } catch (final Exception e) {
            throw new PaymentPluginApiException("Unable to perform getPaymentInfo for kbPaymentId = " +
                    kbPaymentId + ", kbAccountId = " + kbAccountId, e);
        }
    }

    @Override
    public Pagination<PaymentTransactionInfoPlugin> searchPayments(final String searchKey, final Long offset, final Long limit, final Iterable<PluginProperty> properties, final TenantContext context) throws PaymentPluginApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addPaymentMethod(final UUID kbAccountId, final UUID kbPaymentMethodId,
                                 final PaymentMethodPlugin paymentMethodProps, final boolean setDefault,
                                 final Iterable<PluginProperty> properties, final CallContext context)
            throws PaymentPluginApiException {

        try {
            // Note: input properties in <code>properties</code> take precedence over
            // the plugin properties in <code>paymentMethodProps.getProperties</code>
            final Map<String, String> mergedProperties =
                    PluginProperties.toStringMap(paymentMethodProps.getProperties(), properties);

            service.addPaymentMethod(kbAccountId, kbPaymentMethodId, setDefault, mergedProperties, context);
        } catch (final Exception e) {
            throw new PaymentPluginApiException("Unable to add payment method for kbPaymentMethodId " +
                    kbPaymentMethodId, e);
        }
    }

    @Override
    public void deletePaymentMethod(final UUID kbAccountId, final UUID kbPaymentMethodId, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        try {
            service.deactivatePaymentMethod(kbAccountId, kbPaymentMethodId, context.getTenantId());
        } catch (final Exception e) {
            throw new PaymentPluginApiException("Unable to delete payment method for kbPaymentMethodId " +
                    kbPaymentMethodId + ", accountId = " + kbAccountId, e);
        }
    }

    @Override
    public PaymentMethodPlugin getPaymentMethodDetail(final UUID kbAccountId, final UUID kbPaymentMethodId, final Iterable<PluginProperty> properties, final TenantContext context) throws PaymentPluginApiException {
        boolean rawColumnNames = false;
        if (properties != null) {
            for (PluginProperty property : properties) {
                if (property.getKey().equals("return-raw-db-column-names") && property.getValue().equals("true")) {
                    rawColumnNames = true;
                }
            }
        }
        try {
            return service.getPaymentMethod(kbAccountId, kbPaymentMethodId, context.getTenantId(), rawColumnNames);
        } catch (final Exception e) {
            throw new PaymentPluginApiException("Unable to retrieve payment method for kbPaymentMethodId "
                    + kbPaymentMethodId, e);
        }
    }

    @Override
    public void setDefaultPaymentMethod(final UUID kbAccountId, final UUID kbPaymentMethodId, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        // We don't need to do anything
        // Handled by Killbill's core
    }

    /**
     * This method is only called from Killbill's PaymentMethodProcessor.refreshPaymentMethods.
     * refreshPaymentMethods supports cases "when adding payment method does not flow through KB because of
     * PCI compliance issues" (quote from KB javadocs). It appears to call getPaymentMethods from the plugin,
     * sync up the payment methods in KB core (add new pms, update existing ones) based on this list, and
     * call resetPaymentMethods on the plugin with the final list of synced up pms.
     *
     * <p>Anyway, I don't think we need it for V1 of Billing Service.
     */
    @Override
    public List<PaymentMethodInfoPlugin> getPaymentMethods(final UUID kbAccountId, final boolean refreshFromGateway, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pagination<PaymentMethodPlugin> searchPaymentMethods(final String searchKey, final Long offset, final Long limit, final Iterable<PluginProperty> properties, final TenantContext context) throws PaymentPluginApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetPaymentMethods(final UUID kbAccountId, final List<PaymentMethodInfoPlugin> paymentMethods, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public HostedPaymentPageFormDescriptor buildFormDescriptor(final UUID kbAccountId, final Iterable<PluginProperty> customFields, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GatewayNotification processNotification(final String notification, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        throw new UnsupportedOperationException();
    }
}
