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

import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.plugin.api.notification.PluginConfigurationHandler;

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
public class AuthorizeNetConfigurableHandler extends PluginConfigurationHandler {

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

