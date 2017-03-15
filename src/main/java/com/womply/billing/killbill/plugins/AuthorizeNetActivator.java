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

import com.womply.billing.killbill.plugins.authentication.AuthorizeNetAuthenticationService;
import com.womply.billing.killbill.plugins.authentication.AuthorizeNetConfigurableHandler;
import com.womply.billing.killbill.plugins.db.AuthorizeNetDAO;
import com.womply.billing.killbill.plugins.db.AuthorizeNetDAOImpl;

import org.killbill.billing.osgi.api.OSGIPluginProperties;
import org.killbill.billing.osgi.libs.killbill.KillbillActivatorBase;
import org.killbill.billing.payment.plugin.api.PaymentPluginApi;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

/**
 * Activator class for Authorize.Net plugin. Creates <code>AuthorizeNetPaymentPluginApi</code>
 * and registers it with KillBill.
 */
public class AuthorizeNetActivator extends KillbillActivatorBase {

    public static final String PLUGIN_NAME = "killbill-authorize-net";

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);

        // Register a payment plugin api
        final AuthorizeNetDAO dao = new AuthorizeNetDAOImpl(dataSource.getDataSource());
        final AuthorizeNetConfigurableHandler configurableHandler =
                new AuthorizeNetConfigurableHandler(PLUGIN_NAME, killbillAPI, logService);
        final AuthorizeNetAuthenticationService authenticationService =
                new AuthorizeNetAuthenticationService(configurableHandler);
        final AuthorizeNetTransactionService transactionService = new AuthorizeNetTransactionService(dao, logService);
        final AuthorizeNetService service =
                new AuthorizeNetService(killbillAPI, logService, dao, authenticationService, transactionService);
        final PaymentPluginApi paymentPluginApi =
                new AuthorizeNetPaymentPluginApi(configProperties.getProperties(), logService, service);
        registerPaymentPluginApi(context, paymentPluginApi);

        // Register a servlet
        final AuthorizeNetServlet servlet = new AuthorizeNetServlet(killbillAPI, dao, logService, service);
        registerServlet(context, servlet);
    }

    private void registerServlet(final BundleContext context, final HttpServlet servlet) {
        final Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, Servlet.class, servlet, props);
    }

    private void registerPaymentPluginApi(final BundleContext context, final PaymentPluginApi api) {
        final Hashtable<String, String> props = new Hashtable<>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, PaymentPluginApi.class, api, props);

    }
}
