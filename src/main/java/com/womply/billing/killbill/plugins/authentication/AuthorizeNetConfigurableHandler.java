package com.womply.billing.killbill.plugins.authentication;

import org.killbill.billing.plugin.api.notification.PluginConfigurationHandler;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillLogService;

import java.util.Properties;
import java.util.UUID;

/**
 * Creates an instance of AuthorizeNetAuthentication configured with
 * Authorize.Net plugin properties.
 *
 * <p>We want to re-read plugin properties from Cache every time
 * getConfigurable is called. Therefore we can't subclass
 * PluginTenantConfigurableConfigurationHandler class.
 *
 */
public class AuthorizeNetConfigurableHandler extends
        PluginConfigurationHandler {

    public AuthorizeNetConfigurableHandler(final String pluginName,
                                           final OSGIKillbillAPI osgiKillbillAPI,
                                           final OSGIKillbillLogService osgiKillbillLogService) {
        super(pluginName, osgiKillbillAPI, osgiKillbillLogService);
    }

    /**
     * @return AuthorizeNetAuthentication configured with plugin properties re-read from
     *      KillBill.
     */
    public AuthorizeNetAuthentication getConfigurable(final UUID kbTenantId) {
        if (kbTenantId == null) {
            throw new RuntimeException("Can't configure a null tenant id.");
        }

        final Properties properties = getTenantProperties(kbTenantId);
        if (properties == null) {
            throw new RuntimeException("No plugin properties defined for tenantId = " + kbTenantId);
        }

        return createConfigurable(properties);
    }

    // hook for tests
    protected Properties getTenantProperties(final UUID kbTenantId) {
        return getTenantConfigurationAsProperties(kbTenantId);
    }

    @Override
    protected void configure(final UUID kbTenantId) {
        // Nothing to do here.
        // We create AuthorizeNetAuthentication object directly in getConfigurable method.
    }

    protected AuthorizeNetAuthentication createConfigurable(final Properties properties) {
        final AuthorizeNetProperties configProperties = new AuthorizeNetProperties(properties);
        return new AuthorizeNetAuthentication(configProperties);
    }

}

