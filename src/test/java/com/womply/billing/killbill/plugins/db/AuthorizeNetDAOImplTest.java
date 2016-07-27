package com.womply.billing.killbill.plugins.db;

import static com.womply.billing.killbill.plugins.jooq.tables.AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Unit tests for AuthorizeNetDAOImpl.
 */
public class AuthorizeNetDAOImplTest {

    private AuthorizeNetDAOImpl dao = new AuthorizeNetDAOImpl(null);

    @Test
    public void getPaymentMethodQuery() {
        UUID accountId = UUID.randomUUID();
        UUID paymentMethodId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        String sql = dao.getPaymentMethodForKauiQuery(accountId, paymentMethodId, tenantId).getSQL();
        assertThat(sql)
                .isEqualTo(
                        "select `authorize_net_payment_methods`.`kb_payment_method_id`, " +
                                "`authorize_net_payment_methods`.`authorize_net_customer_profile_id` " +
                                    "as `Authorize.net Customer Profile Id: `, " +
                                "`authorize_net_payment_methods`.`authorize_net_payment_profile_id`, " +
                                "`authorize_net_payment_methods`.`authorize_net_payment_profile_id` " +
                                    "as `Authorize.net Payment Profile Id: `, " +
                                "`authorize_net_payment_methods`.`cc_type` as `Card Type: `, " +
                                "`authorize_net_payment_methods`.`cc_exp_month` as `Expiration month: `, " +
                                "`authorize_net_payment_methods`.`cc_exp_year` as `Expiration year: `, " +
                                "`authorize_net_payment_methods`.`cc_last_4` as `Last 4 digits: `, " +
                                "`authorize_net_payment_methods`.`zip` as `Zip code: ` " +
                                "from `authorize_net_payment_methods` " +
                                "where (`authorize_net_payment_methods`.`kb_account_id` = ? " +
                                "and `authorize_net_payment_methods`.`kb_payment_method_id` = ? " +
                                "and `authorize_net_payment_methods`.`kb_tenant_id` = ? " +
                                "and `authorize_net_payment_methods`.`status` = ?)"
                );
    }

    @Test
    public void getPaymentMethodQueryRaw() {
        UUID accountId = UUID.randomUUID();
        UUID paymentMethodId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        String sql = dao.getPaymentMethodRawQuery(accountId, paymentMethodId, tenantId).getSQL();
        assertThat(sql)
                .isEqualTo(
                        "select `authorize_net_payment_methods`.`record_id`, " +
                                "`authorize_net_payment_methods`.`kb_account_id`, " +
                                "`authorize_net_payment_methods`.`kb_payment_method_id`, " +
                                "`authorize_net_payment_methods`.`authorize_net_customer_profile_id`, " +
                                "`authorize_net_payment_methods`.`authorize_net_payment_profile_id`, " +
                                "`authorize_net_payment_methods`.`cc_first_name`, " +
                                "`authorize_net_payment_methods`.`cc_last_name`, " +
                                "`authorize_net_payment_methods`.`cc_type`, " +
                                "`authorize_net_payment_methods`.`cc_exp_month`, " +
                                "`authorize_net_payment_methods`.`cc_exp_year`, " +
                                "`authorize_net_payment_methods`.`cc_last_4`, " +
                                "`authorize_net_payment_methods`.`address`, " +
                                "`authorize_net_payment_methods`.`city`, " +
                                "`authorize_net_payment_methods`.`state`, " +
                                "`authorize_net_payment_methods`.`zip`, " +
                                "`authorize_net_payment_methods`.`country`, " +
                                "`authorize_net_payment_methods`.`created_at`, " +
                                "`authorize_net_payment_methods`.`updated_at`, " +
                                "`authorize_net_payment_methods`.`kb_tenant_id`, " +
                                "`authorize_net_payment_methods`.`status` " +
                                "from `authorize_net_payment_methods` " +
                                "where (`authorize_net_payment_methods`.`kb_account_id` = ? " +
                                "and `authorize_net_payment_methods`.`kb_payment_method_id` = ? " +
                                "and `authorize_net_payment_methods`.`kb_tenant_id` = ? " +
                                "and `authorize_net_payment_methods`.`status` = ?)"
                );
    }

    @Test
    public void getTransactionsForPaymentQuery() {
        UUID accountId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        String sql = dao.getTransactionsForPaymentQuery(accountId, paymentId).getSQL();
        assertThat(sql).isEqualTo(
                "select `authorize_net_transactions`.`record_id`, " +
                        "`authorize_net_transactions`.`request_id`, " +
                        "`authorize_net_transactions`.`kb_payment_id`, " +
                        "`authorize_net_transactions`.`kb_payment_method_id`, " +
                        "`authorize_net_transactions`.`kb_payment_transaction_id`, " +
                        "`authorize_net_transactions`.`kb_transaction_type`, " +
                        "`authorize_net_transactions`.`transaction_type`, " +
                        "`authorize_net_transactions`.`authorize_net_customer_profile_id`, " +
                        "`authorize_net_transactions`.`authorize_net_payment_profile_id`, " +
                        "`authorize_net_transactions`.`authorize_net_transaction_id`, " +
                        "`authorize_net_transactions`.`amount`, " +
                        "`authorize_net_transactions`.`currency`, " +
                        "`authorize_net_transactions`.`auth_code`, " +
                        "`authorize_net_transactions`.`avs_result_code`, " +
                        "`authorize_net_transactions`.`cvv_result_code`, " +
                        "`authorize_net_transactions`.`cavv_result_code`, " +
                        "`authorize_net_transactions`.`account_type`, " +
                        "`authorize_net_transactions`.`response_status`, " +
                        "`authorize_net_transactions`.`response_message`, " +
                        "`authorize_net_transactions`.`transaction_status`, " +
                        "`authorize_net_transactions`.`transaction_message`, " +
                        "`authorize_net_transactions`.`transaction_error`, " +
                        "`authorize_net_transactions`.`test_request`, " +
                        "`authorize_net_transactions`.`success`, " +
                        "`authorize_net_transactions`.`created_at`, " +
                        "`authorize_net_transactions`.`updated_at`, " +
                        "`authorize_net_transactions`.`kb_account_id`, " +
                        "`authorize_net_transactions`.`kb_tenant_id`, " +
                        "`authorize_net_transactions`.`kb_payment_plugin_status`, " +
                        "`authorize_net_transactions`.`kb_ref_transaction_record_id` " +
                        "from `authorize_net_transactions` " +
                        "where (`authorize_net_transactions`.`kb_account_id` = ? " +
                        "and `authorize_net_transactions`.`kb_payment_id` = ?)"
        );
    }

    @Test
    public void getPurchaseTransactionsForPaymentQuery() {
        UUID accountId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        UUID paymentMethodId = UUID.randomUUID();
        String sql = dao.getPurchaseTransactionsForPaymentQuery(accountId, paymentId, paymentMethodId).getSQL();
        System.out.println(sql);
        assertThat(sql).isEqualTo(
                "select `authorize_net_transactions`.`record_id`, " +
                        "`authorize_net_transactions`.`request_id`, " +
                        "`authorize_net_transactions`.`kb_payment_id`, " +
                        "`authorize_net_transactions`.`kb_payment_method_id`, " +
                        "`authorize_net_transactions`.`kb_payment_transaction_id`, " +
                        "`authorize_net_transactions`.`kb_transaction_type`, " +
                        "`authorize_net_transactions`.`transaction_type`, " +
                        "`authorize_net_transactions`.`authorize_net_customer_profile_id`, " +
                        "`authorize_net_transactions`.`authorize_net_payment_profile_id`, " +
                        "`authorize_net_transactions`.`authorize_net_transaction_id`, " +
                        "`authorize_net_transactions`.`amount`, " +
                        "`authorize_net_transactions`.`currency`, " +
                        "`authorize_net_transactions`.`auth_code`, " +
                        "`authorize_net_transactions`.`avs_result_code`, " +
                        "`authorize_net_transactions`.`cvv_result_code`, " +
                        "`authorize_net_transactions`.`cavv_result_code`, " +
                        "`authorize_net_transactions`.`account_type`, " +
                        "`authorize_net_transactions`.`response_status`, " +
                        "`authorize_net_transactions`.`response_message`, " +
                        "`authorize_net_transactions`.`transaction_status`, " +
                        "`authorize_net_transactions`.`transaction_message`, " +
                        "`authorize_net_transactions`.`transaction_error`, " +
                        "`authorize_net_transactions`.`test_request`, " +
                        "`authorize_net_transactions`.`success`, " +
                        "`authorize_net_transactions`.`created_at`, " +
                        "`authorize_net_transactions`.`updated_at`, " +
                        "`authorize_net_transactions`.`kb_account_id`, " +
                        "`authorize_net_transactions`.`kb_tenant_id`, " +
                        "`authorize_net_transactions`.`kb_payment_plugin_status`, " +
                        "`authorize_net_transactions`.`kb_ref_transaction_record_id` " +
                        "from `authorize_net_transactions` " +
                        "where (`authorize_net_transactions`.`kb_account_id` = ? " +
                        "and `authorize_net_transactions`.`kb_payment_id` = ? " +
                        "and `authorize_net_transactions`.`kb_payment_method_id` = ? " +
                        "and `authorize_net_transactions`.`kb_transaction_type` = ?)"
        );
    }

    @Test
    public void verifyPaymentMethodProperties() {
        Map<String, String> props = new HashMap<>();
        props.put(AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_PAYMENT_PROFILE_ID.getName(), "test-payment-profile-id");
        dao.verifyPaymentMethodProperties(null, null, props);
        // expect no Exception
    }

    @Test
    public void verifyPaymentMethodPropertiesError() {
        UUID expectedAccountId = UUID.randomUUID();
        UUID expectedPaymentMethodId = UUID.randomUUID();
        Map<String, String> props = new HashMap<>();
        try {
            dao.verifyPaymentMethodProperties(expectedAccountId, expectedPaymentMethodId, props);
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isNotEmpty()
                    .contains("Empty Authorize.Net payment profile id")
                    .contains(expectedAccountId.toString())
                    .contains(expectedPaymentMethodId.toString());
        }
    }

}
