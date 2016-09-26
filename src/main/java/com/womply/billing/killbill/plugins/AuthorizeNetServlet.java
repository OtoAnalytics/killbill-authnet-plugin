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

import com.womply.billing.killbill.plugins.db.AuthorizeNetDAO;
import com.womply.billing.killbill.plugins.models.AuthorizeNetHealthResponse;
import com.womply.billing.killbill.plugins.models.PaymentGatewayAccount;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.authorize.api.contract.v1.AuthenticateTestResponse;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.MessagesType;
import org.apache.commons.lang3.StringUtils;
import org.killbill.billing.tenant.api.Tenant;
import org.killbill.billing.tenant.api.TenantApiException;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillAPI;
import org.osgi.service.log.LogService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for Authorize.Net.
 */
public class AuthorizeNetServlet extends HttpServlet {

    private static final long serialVersionUID = 201605161448L;

    public static final String PARAM_NAME_ACTION = "action";
    public static final String PARAM_NAME_TENANT_API_KEY = "tenantApiKey";
    public static final String PARAM_NAME_ACCOUNT_DATA = "accountData";

    public static final String ACTION_ADD_ACCOUNT = "addAccount";
    public static final String ACTION_HEALTH_L1 = "healthL1";
    public static final String ACTION_HEALTH_L2 = "healthL2";

    private final transient LogService logService;
    private final transient AuthorizeNetService service;
    private final transient OSGIKillbillAPI killbillAPI;
    private final transient AuthorizeNetDAO dao;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // Turn on pretty printing
        OBJECT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // Register necessary modules.
        OBJECT_MAPPER.registerModule(new Jdk8Module());
        OBJECT_MAPPER.registerModule(new GuavaModule());
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public AuthorizeNetServlet(final OSGIKillbillAPI killbillAPI, final AuthorizeNetDAO dao,
                               final LogService logService, final AuthorizeNetService service) {

        this.killbillAPI = killbillAPI;
        this.dao = dao;
        this.logService = logService;
        this.service = service;
    }

    /**
     * POST method for Authorize.Net KB plugin. Is used for functionality that is not
     * supported through the PaymentPluginApi.
     * Required input parameters:
     *  -- action : specifies which action the POST should perform.
     *              Supported values:
     *              - addAccount : create a Customer Profile for the given merchantLocationId in
     *                             Authorize.Net.
     *                             Required Input Parameters:
     *                             - tenantApiKey : api key for the tenant to whom the account belongs
     *                             - accountData: json for PaymentGatewayAccount, e.g. {"merchantLocationId":"83"}
     *                             Response json:
     *                             SUCCESS:
     *                             {
     *                                 "ok": true,
     *                                 "data": {
     *                                      "merchantLocationId": 83,
     *                                      "customerProfileId": "40572629"
     *                                      }
     *                             }
     *                             FAILURE:
     *                             {
     *                                  "ok": false,
     *                                  "data": null,
     *                                  "error": "Undefined \"action\" parameter."
     *                             }
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        // Find me on http://127.0.0.1:8080/plugins/killbill-authorize-net
        try {
            String action = getParameterErrorOnEmpty(request, response, PARAM_NAME_ACTION);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");

            if (ACTION_ADD_ACCOUNT.equals(action.trim())) {
                handleAddCustomerProfile(request, response);
            } else {
                respondWithError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Unsupported action \"" + action + "\".");
            }
        } catch (MissingParameterException | SentErrorException e) {  // NOPMD -- we've already handled this
            // do nothing here, we have already sent an error response
        } catch (Exception e) {
            respondWithError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "", e);
        }
    }


    /**
     * For handling get requests like http://127.0.0.1:8080/plugins/killbill-authorize-net?action=healthL1
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = getParameterErrorOnEmpty(request, response, PARAM_NAME_ACTION);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");

            if (ACTION_HEALTH_L1.equals(action.trim())) {
                handleHealthL1(request, response);
            } else if (ACTION_HEALTH_L2.equals(action.trim())) {
                handleHealthL2(request, response);
            } else {
                respondWithError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Unsupported action \"" + action + "\".");
            }
        } catch (MissingParameterException | SentErrorException e) {  // NOPMD -- we've already handled this
            // do nothing here, we have already sent an error response
        } catch (Exception e) {
            respondWithError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "", e);
        }
    }

    protected void handleHealthL1(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, MissingParameterException, SentErrorException {
//        String tenantApiKey = getParameterErrorOnEmpty(request, response, PARAM_NAME_TENANT_API_KEY);

        AuthorizeNetHealthResponse health = new AuthorizeNetHealthResponse();
        health.setLevel(1);
        health.setHealthy(true);

        // PDXD-1114 - commented out for now.  This code appears to have been leaking db connections

//        Tenant tenant = null;
//        try {
//            tenant = killbillAPI.getTenantUserApi().getTenantByApiKey(tenantApiKey);
//        } catch (TenantApiException e) {
//            respondWithError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error getting tenant", e);
//        }
//        TenantContext tenantContext = new DefaultTenantContext(tenant.getId());
//
//        // do a query for accounts on this tenant as a way of making sure KB is warmed up
//        long startTime = System.currentTimeMillis();
//        killbillAPI.getAccountUserApi().getAccounts(0L, 30L, tenantContext);
//        long elapsedTime = System.currentTimeMillis() - startTime;
//
//        health.setActionTime(elapsedTime);
//
//        if (elapsedTime > 5000) {  // TODO: figure out the right value here
//            // this took too long, we're not healthy
//            health.setHealthy(false);
//            health.setMessage("Took too long to query first 30 accounts");
//        } else {
            health.setMessage("ok");
//        }

        sendResponseJson(health, response);
    }

    protected void handleHealthL2(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, MissingParameterException, SentErrorException {
        String tenantApiKey = getParameterErrorOnEmpty(request, response, PARAM_NAME_TENANT_API_KEY);

        AuthorizeNetHealthResponse health = new AuthorizeNetHealthResponse();
        health.setLevel(2);
        health.setHealthy(true);

        UUID tenantId = null;
        AuthenticateTestResponse authenticateTestResponse = null;

        try {
            // this call throws if the return would be null, so no need for null check here
            Tenant tenant = killbillAPI.getTenantUserApi().getTenantByApiKey(tenantApiKey);
            tenantId = tenant.getId();

            // verify credentials with Auth.Net to make sure we're configured correctly
            authenticateTestResponse = service.getAuthenticateTestResponse(tenantId);

        } catch (TenantApiException e) {
            respondWithError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error getting tenant", e);
        }

        if (authenticateTestResponse == null) {
            health.setHealthy(false);
            health.setMessage("Got null AuthenticateTestResponse from Auth.Net");
        } else if (authenticateTestResponse.getMessages().getResultCode() != MessageTypeEnum.OK) {
            health.setHealthy(false);

            StringBuilder detailMessage = new StringBuilder();
            for (MessagesType.Message message : authenticateTestResponse.getMessages().getMessage()) {
                detailMessage.append(message.getCode()).append(" : ").append(message.getText()).append('\n');
            }
            health.setMessage(detailMessage.toString());
        } else {
            health.setMessage("Ping to Auth.Net was successful");

            // Auth.Net ping is ok.  Now check how many transactions we have with a status of 4 ("Held for Review")
            health.setNumHeldForReviewTransactions(dao.getHeldForReviewTransactionCount(tenantId));

        }

        sendResponseJson(health, response);
    }

    protected void handleAddCustomerProfile(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, MissingParameterException, SentErrorException {
        String tenantApiKey = getParameterErrorOnEmpty(request, response, PARAM_NAME_TENANT_API_KEY);
        String accountDataJson = getParameterErrorOnEmpty(request, response, PARAM_NAME_ACCOUNT_DATA);

        PaymentGatewayAccount createdProfile = addCustomerProfile(tenantApiKey, accountDataJson, response);
        sendResponseJson(createdProfile, response);
    }

    protected PaymentGatewayAccount addCustomerProfile(String tenantApiKey, String accountDataJson,
                                                       final HttpServletResponse response)
            throws IOException, MissingParameterException, SentErrorException {

        PaymentGatewayAccount account = parseAccountFromJson(accountDataJson, response);

        PaymentGatewayAccount createdProfile = new PaymentGatewayAccount();
        try {
            String customerProfileId = service.addCustomerProfile(tenantApiKey, account);
            createdProfile.setMerchantLocationId(account.getMerchantLocationId());
            createdProfile.setCustomerProfileId(customerProfileId);
        } catch (TenantApiException e) {
            respondWithError(response, HttpServletResponse.SC_BAD_REQUEST, "Unknown tenantApiKey = " + tenantApiKey,
                    e);
            throw new SentErrorException(e);
        }

        return createdProfile;
    }

    protected PaymentGatewayAccount parseAccountFromJson(String accountDataJson, final HttpServletResponse response)
            throws IOException, MissingParameterException {
        PaymentGatewayAccount account = null;
        try {
            account = OBJECT_MAPPER.readValue(accountDataJson,
                    PaymentGatewayAccount.class);
        } catch (IOException e) {
            logService.log(LogService.LOG_DEBUG, "Error parsing PaymentGatewayAccount json", e);
        }
        if (account == null) {
            respondWithError(response, HttpServletResponse.SC_BAD_REQUEST, "Unable to parse JSON in \"" +
                    accountDataJson + "\" into " + PaymentGatewayAccount.class.getCanonicalName());
            throw new MissingParameterException();
        }

        return account;
    }

    protected String getParameterErrorOnEmpty(final HttpServletRequest request, final HttpServletResponse response,
                                              String paramName)
            throws MissingParameterException, IOException {
        String action = getParameter(request, paramName);

        if (StringUtils.isEmpty(action)) {
            respondWithError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Undefined \"" + paramName + "\" parameter." );
            throw new MissingParameterException();
        }
        return action;
    }

    protected String getParameter(final HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value != null) {
            value = value.trim();
        }
        return value;
    }

    protected void respondWithError(final HttpServletResponse response, int errorCode, String message)
            throws IOException {
        respondWithError(response, errorCode, message, null);
    }

    protected void respondWithError(final HttpServletResponse response, int errorCode, String message,
                                    Throwable cause)
            throws IOException {

        logService.log(LogService.LOG_ERROR, message, cause);

        response.setStatus(errorCode);
        String errorMsg = message;
        if (cause != null) {
            errorMsg += cause.getClass().getCanonicalName() + ": " + cause.getMessage();
        }

        Response<PaymentGatewayAccount> responseObject = new Response<>(false, null, errorMsg);
        writeResponseJson(responseObject, response);

    }

    protected <T> void sendResponseJson(final T object, final HttpServletResponse response)
            throws IOException {

        Response<T> responseObject = new Response<>(true, object, null);
        writeResponseJson(responseObject, response);
    }

    protected void writeResponseJson(final Response responseObject, final HttpServletResponse response)
            throws IOException {

        String responseJson = OBJECT_MAPPER.writeValueAsString(responseObject);
        try (PrintWriter out = response.getWriter()) {
            out.write(responseJson);
            out.flush();
        }
    }

    /**
     * Exception that is raised when a required parameter is missing.
     */
    public static class MissingParameterException extends Exception {

        public MissingParameterException() {
            super();
        }
    }

    public static class SentErrorException extends Exception {

        public SentErrorException() {
            super();
        }

        public SentErrorException(Throwable cause) {
            super(cause);
        }
    }

    @Data
    @AllArgsConstructor
    protected static class Response<T> {
        private boolean ok;
        private T data;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String error;
    }

}
