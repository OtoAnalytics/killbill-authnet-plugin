package com.womply.billing.killbill.plugins.db;

import static com.womply.billing.killbill.plugins.jooq.tables.AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetCustomerProfilesRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetPaymentMethodsRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetRequestsRecord;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;
import com.womply.billing.killbill.plugins.models.AuthorizeNetPaymentMethod;
import com.womply.billing.killbill.plugins.models.AuthorizeNetTransactionInfo;

import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.MessagesType;
import net.authorize.api.contract.v1.TransactionResponse;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import org.apache.commons.dbcp2.BasicDataSource;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.TransactionType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Integration tests for the AuthorizeNetDAOImpl class.
 */
public class AuthorizeNetDAOImplIT {

    public static final String PLUGIN_FIELDS_EXT_TOKEN = "authorize_net_payment_profile_id";
    public static final String PLUGIN_FIELDS_FIRST_NAME = "cc_first_name";
    public static final String PLUGIN_FIELDS_LAST_NAME = "cc_last_name";
    public static final String PLUGIN_FIELDS_TYPE = "cc_type";
    public static final String PLUGIN_FIELDS_EXP_MONTH = "cc_exp_month";
    public static final String PLUGIN_FIELDS_EXP_YEAR = "cc_exp_year";
    public static final String PLUGIN_FIELDS_LAST_FOUR = "cc_last_4";
    public static final String PLUGIN_FIELDS_ADDRESS = "address";
    public static final String PLUGIN_FIELDS_CITY = "city";
    public static final String PLUGIN_FIELDS_STATE = "state";
    public static final String PLUGIN_FIELDS_POSTAL_CODE = "zip";
    public static final String PLUGIN_FIELDS_COUNTRY = "country";

    private static final String dbUser = System.getProperty("db.username");
    private static final String dbPassword = System.getProperty("db.password");
    private static final String dbUrl = System.getProperty("db.url");

    private AuthorizeNetDAOImpl dao;
    private AuthorizeNetTestDAO testDao;

    /**
     * Point the DAO to the test database running in Docker.
     */
    @BeforeClass
    public void init() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(dbUrl);
        ds.setUsername(dbUser);
        ds.setPassword(dbPassword);

        dao = new AuthorizeNetDAOImpl(ds);
        testDao = new AuthorizeNetTestDAO(ds);
    }

    @Test
    public void addPaymentMethod() throws SQLException {
        final UUID kbAccountId = UUID.randomUUID();
        final UUID kbPaymentMethodId = UUID.randomUUID();
        final String authNetCustomerProfileId = "test-auth-net-cust-profile-id";
        final String authMetPaymentProfileId = "test-auth-net-payment-profile-id";
        final UUID kbTenantId = UUID.randomUUID();
        final Map<String, String> properties = new HashMap<>();

        properties.put(PLUGIN_FIELDS_FIRST_NAME, "Donald");
        properties.put(PLUGIN_FIELDS_LAST_NAME, "Knuth");
        properties.put(PLUGIN_FIELDS_TYPE, "VISA");
        properties.put(PLUGIN_FIELDS_EXP_MONTH, "08");
        properties.put(PLUGIN_FIELDS_EXP_YEAR, "2019");
        properties.put(PLUGIN_FIELDS_LAST_FOUR, "1234");
        properties.put(PLUGIN_FIELDS_ADDRESS, "1223 Main Str.");
        properties.put(PLUGIN_FIELDS_CITY, "Stanford");
        properties.put(PLUGIN_FIELDS_STATE, "CA");
        properties.put(PLUGIN_FIELDS_POSTAL_CODE, "94305");
        properties.put(PLUGIN_FIELDS_COUNTRY, "USA");
        properties.put(PLUGIN_FIELDS_EXT_TOKEN, authMetPaymentProfileId);

        dao.addPaymentMethod(kbAccountId, kbPaymentMethodId, false, authNetCustomerProfileId, properties, kbTenantId);

        Map<String, Object> persistedData = dao.getPaymentMethod(kbAccountId, kbPaymentMethodId,
                kbTenantId, true);

        // check that the payment method fields returned to Billing Service look good
        Map<String, String> expectedCreditCardFields = new HashMap<>(properties);
        expectedCreditCardFields.remove(PLUGIN_FIELDS_EXT_TOKEN);
        Map<String, String> actualCreditCardFields = getCreditCardFields(persistedData);
        assertThat(actualCreditCardFields).isEqualTo(expectedCreditCardFields);

        Map<String, Object> expectedDataFields = getExpectedDataFields(kbAccountId, kbPaymentMethodId,
                authNetCustomerProfileId, authMetPaymentProfileId, kbTenantId);
        assertThat(persistedData).containsAllEntriesOf(expectedDataFields);

        // check that the payment method fields returned to Kaui look good
        Map<String, Object> kauiData = dao.getPaymentMethod(kbAccountId, kbPaymentMethodId,
                kbTenantId, false);

        Map<String, Object> expectedKauiFields = getExpectedKauiFields(kbPaymentMethodId, authNetCustomerProfileId,
                authMetPaymentProfileId, properties);

        assertThat(kauiData).isEqualTo(expectedKauiFields);

        // check that the payment method fields returned for Transactions look good
        AuthorizeNetPaymentMethodsRecord record = dao.getPaymentMethodForOperation(kbAccountId, kbPaymentMethodId,
                kbTenantId);

        Map<String, Object> actualDataFields = getActualDataFields(record);
        assertThat(actualDataFields).isEqualTo(expectedDataFields);
    }

    @Test
    public void addPaymentMethodWithMinimalFieldsSet() throws SQLException {
        final UUID kbAccountId = UUID.randomUUID();
        final UUID kbPaymentMethodId = UUID.randomUUID();
        final String authNetCustomerProfileId = "test-auth-net-cust-profile-id";
        final String authMetPaymentProfileId = "test-auth-net-payment-profile-id";
        final UUID kbTenantId = UUID.randomUUID();
        final Map<String, String> properties = new HashMap<>();

        properties.put(PLUGIN_FIELDS_TYPE, "VISA");
        properties.put(PLUGIN_FIELDS_EXP_MONTH, "08");
        properties.put(PLUGIN_FIELDS_EXP_YEAR, "2019");
        properties.put(PLUGIN_FIELDS_LAST_FOUR, "1234");
        properties.put(PLUGIN_FIELDS_POSTAL_CODE, "94305");
        properties.put(PLUGIN_FIELDS_EXT_TOKEN, authMetPaymentProfileId);

        dao.addPaymentMethod(kbAccountId, kbPaymentMethodId, false, authNetCustomerProfileId, properties, kbTenantId);

        Map<String, Object> persistedData = dao.getPaymentMethod(kbAccountId, kbPaymentMethodId,
                kbTenantId, true);

        // check that the payment method fields returned to Billing Service look good
        Map<String, String> expectedCreditCardFields = new HashMap<>(properties);
        expectedCreditCardFields.remove(PLUGIN_FIELDS_EXT_TOKEN);
        Map<String, String> actualCreditCardFields = getCreditCardFields(persistedData, true);
        assertThat(actualCreditCardFields).isEqualTo(expectedCreditCardFields);

        Map<String, Object> expectedDataFields = getExpectedDataFields(kbAccountId, kbPaymentMethodId,
                authNetCustomerProfileId, authMetPaymentProfileId, kbTenantId);
        assertThat(persistedData).containsAllEntriesOf(expectedDataFields);

        // check that the payment method fields returned to Kaui look good
        Map<String, Object> kauiData = dao.getPaymentMethod(kbAccountId, kbPaymentMethodId,
                kbTenantId, false);

        Map<String, Object> expectedKauiFields = getExpectedKauiFields(kbPaymentMethodId, authNetCustomerProfileId,
                authMetPaymentProfileId, properties);

        assertThat(kauiData).isEqualTo(expectedKauiFields);

        // check that the payment method fields returned for Transactions look good
        AuthorizeNetPaymentMethodsRecord record = dao.getPaymentMethodForOperation(kbAccountId, kbPaymentMethodId,
                kbTenantId);

        Map<String, Object> actualDataFields = getActualDataFields(record);
        assertThat(actualDataFields).isEqualTo(expectedDataFields);
    }

    @Test
    public void addPaymentMethodNoAuthNetPaymentMethodId() throws SQLException {
        final UUID kbAccountId = UUID.randomUUID();
        final UUID kbPaymentMethodId = UUID.randomUUID();
        final String authNetCustomerProfileId = "test-auth-net-cust-profile-id";
        final UUID kbTenantId = UUID.randomUUID();
        final Map<String, String> properties = new HashMap<>();

        properties.put(PLUGIN_FIELDS_FIRST_NAME, "Donald");
        properties.put(PLUGIN_FIELDS_LAST_NAME, "Knuth");
        properties.put(PLUGIN_FIELDS_TYPE, "VISA");
        properties.put(PLUGIN_FIELDS_EXP_MONTH, "08");
        properties.put(PLUGIN_FIELDS_EXP_YEAR, "2019");
        properties.put(PLUGIN_FIELDS_LAST_FOUR, "1234");
        properties.put(PLUGIN_FIELDS_ADDRESS, "1223 Main Str.");
        properties.put(PLUGIN_FIELDS_CITY, "Stanford");
        properties.put(PLUGIN_FIELDS_STATE, "CA");
        properties.put(PLUGIN_FIELDS_POSTAL_CODE, "94305");
        properties.put(PLUGIN_FIELDS_COUNTRY, "USA");
        // no Auth.Net payment method id

        try {
            dao.addPaymentMethod(kbAccountId, kbPaymentMethodId, false, authNetCustomerProfileId,
                    properties, kbTenantId);
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isNotEmpty()
                    .contains("Empty Authorize.Net payment profile id")
                    .contains(kbAccountId.toString())
                    .contains(kbPaymentMethodId.toString());
        }
    }

    @Test
    public void logCustomerProfileCreation() {
        String customerId = "W_12345";
        String customerProfileId = "987654";
        UUID tenantId = UUID.randomUUID();

        dao.logCustomerProfileCreation(customerId, customerProfileId, tenantId);

        AuthorizeNetCustomerProfilesRecord record = testDao.getCustomerProfile(customerId);

        assertThat(record).isNotNull();
        assertThat(record.getCustomerId()).isEqualTo(customerId);
        assertThat(record.getCustomerProfileId()).isEqualTo(customerProfileId);
        assertThat(record.getKbTenantId()).isEqualTo(tenantId.toString());
        assertThat(record.getCreatedAt()).isNotNull();
        assertThat(record.getUpdatedAt()).isNotNull();
    }

    @Test
    public void logTransactionRequest() {
        String authNetCustomerProfileId = "test profile id";
        String authNetPaymentProfileId = "test payment id";
        UUID kbAccountId = UUID.randomUUID();
        UUID kbPaymentId = UUID.randomUUID();
        UUID kbTransactionId = UUID.randomUUID();
        UUID kbPaymentMethodId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(70.03);
        Currency currency = Currency.USD;
        UUID kbTenantId = UUID.randomUUID();
        AuthorizeNetTransactionInfo transaction = new AuthorizeNetTransactionInfo();
        transaction.setCustomerProfileId(authNetCustomerProfileId);
        transaction.setCustomerPaymentProfileId(authNetPaymentProfileId);
        transaction.setKbAccountId(kbAccountId);
        transaction.setKbPaymentId(kbPaymentId);
        transaction.setKbPaymentMethodId(kbPaymentMethodId);
        transaction.setKbTransactionId(kbTransactionId);
        transaction.setKbTransactionType(TransactionType.PURCHASE);
        transaction.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION);
        transaction.setTenantId(kbTenantId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);

        dao.logTransactionRequest(transaction);

        AuthorizeNetRequestsRecord record = testDao.getAuthNetRequest(kbPaymentId, kbPaymentMethodId);

        assertThat(record.getKbAccountId()).isEqualTo(kbAccountId.toString());
        assertThat(record.getKbPaymentId()).isEqualTo(kbPaymentId.toString());
        assertThat(record.getKbPaymentMethodId()).isEqualTo(kbPaymentMethodId.toString());
        assertThat(record.getKbPaymentTransactionId()).isEqualTo(kbTransactionId.toString());
        assertThat(record.getKbTransactionType()).isEqualTo(TransactionType.PURCHASE.toString());
        assertThat(record.getAuthorizeNetCustomerProfileId()).isEqualTo(authNetCustomerProfileId);
        assertThat(record.getAuthorizeNetPaymentProfileId()).isEqualTo(authNetPaymentProfileId);
        assertThat(record.getAmount()).isEqualTo(amount);
        assertThat(record.getCurrency()).isEqualTo(currency.toString());
        assertThat(record.getKbTenantId()).isEqualTo(kbTenantId.toString());
        assertThat(record.getCreatedAt()).isNotNull();
        assertThat(record.getUpdatedAt()).isNotNull();
    }

    @Test
    public void logTransactionResponse() {
        String authNetCustomerProfileId = "test profile id";
        String authNetPaymentProfileId = "test payment id";
        UUID kbAccountId = UUID.randomUUID();
        UUID kbPaymentId = UUID.randomUUID();
        UUID kbTransactionId = UUID.randomUUID();
        UUID kbPaymentMethodId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(70.03);
        Currency currency = Currency.USD;
        UUID kbTenantId = UUID.randomUUID();

        AuthorizeNetTransactionInfo transaction = new AuthorizeNetTransactionInfo();
        transaction.setCustomerProfileId(authNetCustomerProfileId);
        transaction.setCustomerPaymentProfileId(authNetPaymentProfileId);
        transaction.setKbAccountId(kbAccountId);
        transaction.setKbPaymentId(kbPaymentId);
        transaction.setKbPaymentMethodId(kbPaymentMethodId);
        transaction.setKbTransactionId(kbTransactionId);
        transaction.setKbTransactionType(TransactionType.PURCHASE);
        transaction.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION);
        transaction.setTenantId(kbTenantId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);

        MessagesType messages = new MessagesType();
        messages.setResultCode(MessageTypeEnum.OK);
        MessagesType.Message message = new MessagesType.Message();
        message.setCode("I00001");
        message.setText("Successful.");
        messages.getMessage().add(message);
        CreateTransactionResponse response = new CreateTransactionResponse();
        response.setMessages(messages);

        String expectedTransId = "test Authorize.Net transaction id";
        String expectedAuthCode = "test auth code";
        String expectedAvsResultCode = "A";
        String expectedCvvResultCode = "C";
        String expectedCavvResultCode = "Ca";
        String expectedAccountType = "Visa";
        String expectedTestRequest = "0";

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setResponseCode(AuthorizeNetTransactionInfo.TRANSACTION_RESULT_SUCCESS_CODE);
        transactionResponse.setTransId(expectedTransId);
        transactionResponse.setAuthCode(expectedAuthCode);
        transactionResponse.setAvsResultCode(expectedAvsResultCode);
        transactionResponse.setCvvResultCode(expectedCvvResultCode);
        transactionResponse.setCavvResultCode(expectedCavvResultCode);
        transactionResponse.setAccountType(expectedAccountType);
        transactionResponse.setTestRequest(expectedTestRequest);
        TransactionResponse.Messages transactionMessages = new TransactionResponse.Messages();
        TransactionResponse.Messages.Message transactionMessage = new TransactionResponse.Messages.Message();
        transactionMessage.setCode(AuthorizeNetTransactionInfo.TRANSACTION_RESULT_SUCCESS_CODE);
        transactionMessage.setDescription("This transaction has been approved.");
        transactionMessages.getMessage().add(transactionMessage);
        transactionResponse.setMessages(transactionMessages);

        response.setTransactionResponse(transactionResponse);
        transaction.setResponse(response);

        AuthorizeNetTransactionsRecord record = dao.logTransactionResponse(transaction);

        assertThat(record.getKbAccountId()).isEqualTo(kbAccountId.toString());
        assertThat(record.getKbPaymentId()).isEqualTo(kbPaymentId.toString());
        assertThat(record.getKbPaymentMethodId()).isEqualTo(kbPaymentMethodId.toString());
        assertThat(record.getKbPaymentTransactionId()).isEqualTo(kbTransactionId.toString());
        assertThat(record.getKbTransactionType()).isEqualTo(TransactionType.PURCHASE.toString());
        assertThat(record.getAuthorizeNetCustomerProfileId()).isEqualTo(authNetCustomerProfileId);
        assertThat(record.getAuthorizeNetPaymentProfileId()).isEqualTo(authNetPaymentProfileId);
        assertThat(record.getAmount()).isEqualTo(amount);
        assertThat(record.getCurrency()).isEqualTo(currency.toString());
        assertThat(record.getKbTenantId()).isEqualTo(kbTenantId.toString());
        assertThat(record.getCreatedAt()).isNotNull();
        assertThat(record.getUpdatedAt()).isNotNull();

        assertThat(record.getTransactionType()).isEqualTo(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
        assertThat(record.getAuthorizeNetTransactionId()).isEqualTo(expectedTransId);
        assertThat(record.getAuthCode()).isEqualTo(expectedAuthCode);
        assertThat(record.getAvsResultCode()).isEqualTo(expectedAvsResultCode);
        assertThat(record.getCvvResultCode()).isEqualTo(expectedCvvResultCode);
        assertThat(record.getCavvResultCode()).isEqualTo(expectedCavvResultCode);
        assertThat(record.getAccountType()).isEqualTo(expectedAccountType);
        assertThat(record.getTestRequest()).isEqualTo(expectedTestRequest);
        assertThat(record.getResponseMessage()).isNotEmpty()
                .contains(message.getCode())
                .contains(message.getText());
        assertThat(record.getResponseStatus()).isEqualTo(messages.getResultCode().value());
        assertThat(record.getTransactionStatus()).isEqualTo(transactionResponse.getResponseCode());
        assertThat(record.getTransactionMessage()).isNotEmpty()
                .contains(transactionMessage.getCode())
                .contains(transactionMessage.getDescription());
        assertThat(record.getSuccess()).isEqualTo(MysqlAdapter.TRUE);

        // check that we can query transaction info
        List<AuthorizeNetTransactionsRecord> paymentInfoList = dao.getTransactionsForPayment(kbAccountId, kbPaymentId);
        assertThat(paymentInfoList ).isNotEmpty().hasSize(1);
        AuthorizeNetTransactionsRecord paymentInfo = paymentInfoList.get(0);
        assertThat(paymentInfo).isEqualToIgnoringGivenFields(record, "originals", "values");
    }

    @Test
    public void logTransactionErrorResponse() {
        String authNetCustomerProfileId = "test profile id";
        String authNetPaymentProfileId = "test payment id";
        UUID kbAccountId = UUID.randomUUID();
        UUID kbPaymentId = UUID.randomUUID();
        UUID kbTransactionId = UUID.randomUUID();
        UUID kbPaymentMethodId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(70.03);
        Currency currency = Currency.USD;
        UUID kbTenantId = UUID.randomUUID();

        AuthorizeNetTransactionInfo transaction = new AuthorizeNetTransactionInfo();
        transaction.setCustomerProfileId(authNetCustomerProfileId);
        transaction.setCustomerPaymentProfileId(authNetPaymentProfileId);
        transaction.setKbAccountId(kbAccountId);
        transaction.setKbPaymentId(kbPaymentId);
        transaction.setKbPaymentMethodId(kbPaymentMethodId);
        transaction.setKbTransactionId(kbTransactionId);
        transaction.setKbTransactionType(TransactionType.PURCHASE);
        transaction.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION);
        transaction.setTenantId(kbTenantId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);

        MessagesType messages = new MessagesType();
        messages.setResultCode(MessageTypeEnum.OK);
        MessagesType.Message message = new MessagesType.Message();
        message.setCode("I00001");
        message.setText("Successful.");
        messages.getMessage().add(message);
        CreateTransactionResponse response = new CreateTransactionResponse();
        response.setMessages(messages);

        String expectedTransId = "test Authorize.Net transaction id";
        String expectedAuthCode = "test auth code";
        String expectedAvsResultCode = "A";
        String expectedCvvResultCode = "C";
        String expectedCavvResultCode = "Ca";
        String expectedAccountType = "Visa";
        String expectedTestRequest = "0";

        String expectedErrorCode = "2";
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setResponseCode(expectedErrorCode);
        transactionResponse.setTransId(expectedTransId);
        transactionResponse.setAuthCode(expectedAuthCode);
        transactionResponse.setAvsResultCode(expectedAvsResultCode);
        transactionResponse.setCvvResultCode(expectedCvvResultCode);
        transactionResponse.setCavvResultCode(expectedCavvResultCode);
        transactionResponse.setAccountType(expectedAccountType);
        transactionResponse.setTestRequest(expectedTestRequest);
        transactionResponse.setMessages(null);
        TransactionResponse.Errors errors = new TransactionResponse.Errors();
        TransactionResponse.Errors.Error error = new TransactionResponse.Errors.Error();
        error.setErrorCode(expectedErrorCode);
        error.setErrorText("This transaction has been declined.");
        errors.getError().add(error);
        transactionResponse.setErrors(errors);

        response.setTransactionResponse(transactionResponse);
        transaction.setResponse(response);

        AuthorizeNetTransactionsRecord record = dao.logTransactionResponse(transaction);

        assertThat(record.getKbAccountId()).isEqualTo(kbAccountId.toString());
        assertThat(record.getKbPaymentId()).isEqualTo(kbPaymentId.toString());
        assertThat(record.getKbPaymentMethodId()).isEqualTo(kbPaymentMethodId.toString());
        assertThat(record.getKbPaymentTransactionId()).isEqualTo(kbTransactionId.toString());
        assertThat(record.getKbTransactionType()).isEqualTo(TransactionType.PURCHASE.toString());
        assertThat(record.getAuthorizeNetCustomerProfileId()).isEqualTo(authNetCustomerProfileId);
        assertThat(record.getAuthorizeNetPaymentProfileId()).isEqualTo(authNetPaymentProfileId);
        assertThat(record.getAmount()).isEqualTo(amount);
        assertThat(record.getCurrency()).isEqualTo(currency.toString());
        assertThat(record.getKbTenantId()).isEqualTo(kbTenantId.toString());
        assertThat(record.getCreatedAt()).isNotNull();
        assertThat(record.getUpdatedAt()).isNotNull();

        assertThat(record.getTransactionType()).isEqualTo(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
        assertThat(record.getAuthorizeNetTransactionId()).isEqualTo(expectedTransId);
        assertThat(record.getAuthCode()).isEqualTo(expectedAuthCode);
        assertThat(record.getAvsResultCode()).isEqualTo(expectedAvsResultCode);
        assertThat(record.getCvvResultCode()).isEqualTo(expectedCvvResultCode);
        assertThat(record.getCavvResultCode()).isEqualTo(expectedCavvResultCode);
        assertThat(record.getAccountType()).isEqualTo(expectedAccountType);
        assertThat(record.getTestRequest()).isEqualTo(expectedTestRequest);
        assertThat(record.getResponseMessage()).isNotEmpty()
                .contains(message.getCode())
                .contains(message.getText());
        assertThat(record.getResponseStatus()).isEqualTo(messages.getResultCode().value());
        assertThat(record.getTransactionStatus()).isEqualTo(transactionResponse.getResponseCode());
        assertThat(record.getTransactionMessage()).isEmpty();
        assertThat(record.getTransactionError()).isNotEmpty()
                .contains(error.getErrorCode())
                .contains(error.getErrorText());
        assertThat(record.getSuccess()).isEqualTo(MysqlAdapter.FALSE);

        // check that we can query transaction info
        List<AuthorizeNetTransactionsRecord> paymentInfoList = dao.getTransactionsForPayment(kbAccountId, kbPaymentId);
        assertThat(paymentInfoList ).isNotEmpty().hasSize(1);
        AuthorizeNetTransactionsRecord paymentInfo = paymentInfoList.get(0);
        assertThat(paymentInfo).isEqualToIgnoringGivenFields(record, "originals", "values");
    }

    @Test
    public void deactivatePaymentMethod() throws SQLException {
        final UUID kbAccountId = UUID.randomUUID();
        final UUID kbPaymentMethodId = UUID.randomUUID();
        final String authNetCustomerProfileId = "test-auth-net-cust-profile-id";
        final String authMetPaymentProfileId = "test-auth-net-payment-profile-id";
        final UUID kbTenantId = UUID.randomUUID();
        final Map<String, String> properties = new HashMap<>();

        properties.put(PLUGIN_FIELDS_FIRST_NAME, "Donald");
        properties.put(PLUGIN_FIELDS_LAST_NAME, "Knuth");
        properties.put(PLUGIN_FIELDS_TYPE, "VISA");
        properties.put(PLUGIN_FIELDS_EXP_MONTH, "08");
        properties.put(PLUGIN_FIELDS_EXP_YEAR, "2019");
        properties.put(PLUGIN_FIELDS_LAST_FOUR, "1234");
        properties.put(PLUGIN_FIELDS_ADDRESS, "1223 Main Str.");
        properties.put(PLUGIN_FIELDS_CITY, "Stanford");
        properties.put(PLUGIN_FIELDS_STATE, "CA");
        properties.put(PLUGIN_FIELDS_POSTAL_CODE, "94305");
        properties.put(PLUGIN_FIELDS_COUNTRY, "USA");
        properties.put(PLUGIN_FIELDS_EXT_TOKEN, authMetPaymentProfileId);

        dao.addPaymentMethod(kbAccountId, kbPaymentMethodId, false, authNetCustomerProfileId, properties, kbTenantId);

        AuthorizeNetPaymentMethodsRecord paymentMethod = dao.getPaymentMethodForOperation(kbAccountId,
                kbPaymentMethodId, kbTenantId);
        dao.deactivatePaymentMethod(paymentMethod);

        // check that we can't query a deactivated payment method
        AuthorizeNetPaymentMethodsRecord deactivatedPaymentMethod = dao.getPaymentMethodForOperation(kbAccountId,
                kbPaymentMethodId, kbTenantId);

        assertThat(deactivatedPaymentMethod).isNull();
    }

    private Map<String, Object> getExpectedKauiFields(UUID kbPaymentMethodId,
                                                      String authNetCustomerProfileId, String authNetPaymentProfileId,
                                                      Map<String, String> expectedProperties) {
        Map<String, Object> kauiFields = new HashMap<>();
        kauiFields.put(AUTHORIZE_NET_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID.getName(), kbPaymentMethodId.toString());
        kauiFields.put(AuthorizeNetPaymentMethod.KAUI_FIELD_AUTHORIZE_NET_CUSTOMER_PROFILE_ID,
                authNetCustomerProfileId);
        kauiFields.put(AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_PAYMENT_PROFILE_ID.getName(),
                authNetPaymentProfileId);
        kauiFields.put(AuthorizeNetPaymentMethod.KAUI_FIELD_AUTHORIZE_NET_PAYMENT_PROFILE_ID,
                authNetPaymentProfileId);
        kauiFields.put(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_TYPE, expectedProperties.get(PLUGIN_FIELDS_TYPE));
        kauiFields.put(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_EXPIRATION_MONTH,
                expectedProperties.get(PLUGIN_FIELDS_EXP_MONTH));
        kauiFields.put(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_EXPIRATION_YEAR,
                expectedProperties.get(PLUGIN_FIELDS_EXP_YEAR));
        kauiFields.put(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_LAST_4,
                expectedProperties.get(PLUGIN_FIELDS_LAST_FOUR));
        kauiFields.put(AuthorizeNetPaymentMethod.KAUI_FIELD_CARD_ZIP,
                expectedProperties.get(PLUGIN_FIELDS_POSTAL_CODE));

        return kauiFields;
    }

    private Map<String, String> getCreditCardFields(Map<String, Object> persistedData) {
        return getCreditCardFields(persistedData, false);
    }

    private Map<String, String> getCreditCardFields(Map<String, Object> persistedData, boolean removeNullFields) {

        Map<String, String> creditCard = new HashMap<>();
        creditCard.put(PLUGIN_FIELDS_FIRST_NAME, (String)persistedData.get(PLUGIN_FIELDS_FIRST_NAME));
        creditCard.put(PLUGIN_FIELDS_LAST_NAME, (String)persistedData.get(PLUGIN_FIELDS_LAST_NAME));
        creditCard.put(PLUGIN_FIELDS_TYPE, (String)persistedData.get(PLUGIN_FIELDS_TYPE));
        creditCard.put(PLUGIN_FIELDS_EXP_MONTH, (String)persistedData.get(PLUGIN_FIELDS_EXP_MONTH));
        creditCard.put(PLUGIN_FIELDS_EXP_YEAR, (String)persistedData.get(PLUGIN_FIELDS_EXP_YEAR));
        creditCard.put(PLUGIN_FIELDS_LAST_FOUR, (String)persistedData.get(PLUGIN_FIELDS_LAST_FOUR));
        creditCard.put(PLUGIN_FIELDS_ADDRESS, (String)persistedData.get(PLUGIN_FIELDS_ADDRESS));
        creditCard.put(PLUGIN_FIELDS_CITY, (String)persistedData.get(PLUGIN_FIELDS_CITY));
        creditCard.put(PLUGIN_FIELDS_STATE, (String)persistedData.get(PLUGIN_FIELDS_STATE));
        creditCard.put(PLUGIN_FIELDS_POSTAL_CODE, (String)persistedData.get(PLUGIN_FIELDS_POSTAL_CODE));
        creditCard.put(PLUGIN_FIELDS_COUNTRY, (String)persistedData.get(PLUGIN_FIELDS_COUNTRY));

        if (removeNullFields) {
            creditCard = removeNullEntries(creditCard);
        }

        return creditCard;
    }

    private Map<String, String> removeNullEntries(Map<String, String> map) {
        Iterator<Map.Entry<String,String>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String,String> entry = iter.next();
            if (entry.getValue() == null) {
                iter.remove();
            }
        }
        return map;
    }

    private Map<String, Object> getExpectedDataFields(final UUID kbAccountId, final UUID kbPaymentMethodId,
                                                      String authNetCustomerProfileId, String authNetPaymentProfileId,
                                                      final UUID kbTenantId) {
        Map<String, Object> dataFields = new HashMap<>();
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_CUSTOMER_PROFILE_ID.getName(),
                authNetCustomerProfileId);
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_PAYMENT_PROFILE_ID.getName(),
                authNetPaymentProfileId);
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.KB_ACCOUNT_ID.getName(), kbAccountId.toString());
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID.getName(), kbPaymentMethodId.toString());
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.KB_TENANT_ID.getName(), kbTenantId.toString());
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.STATUS.getName(), AuthorizeNetDAOImpl.STATUS_ACTIVE);

        return dataFields;
    }

    private Map<String, Object> getActualDataFields(AuthorizeNetPaymentMethodsRecord record) {
        Map<String, Object> dataFields = new HashMap<>();
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_CUSTOMER_PROFILE_ID.getName(),
                record.getAuthorizeNetCustomerProfileId());
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_PAYMENT_PROFILE_ID.getName(),
                record.getAuthorizeNetPaymentProfileId());
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.KB_ACCOUNT_ID.getName(), record.getKbAccountId());
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID.getName(), record.getKbPaymentMethodId());
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.KB_TENANT_ID.getName(), record.getKbTenantId());
        dataFields.put(AUTHORIZE_NET_PAYMENT_METHODS.STATUS.getName(), record.getStatus());

        return dataFields;
    }

}
