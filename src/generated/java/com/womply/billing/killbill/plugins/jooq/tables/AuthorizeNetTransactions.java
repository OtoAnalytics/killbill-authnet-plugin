/**
 * This class is generated by jOOQ
 */
package com.womply.billing.killbill.plugins.jooq.tables;


import com.womply.billing.killbill.plugins.jooq.DefaultSchema;
import com.womply.billing.killbill.plugins.jooq.Keys;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetTransactionsRecord;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.2"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AuthorizeNetTransactions extends TableImpl<AuthorizeNetTransactionsRecord> {

	private static final long serialVersionUID = 352175505;

	/**
	 * The reference instance of <code>authorize_net_transactions</code>
	 */
	public static final AuthorizeNetTransactions AUTHORIZE_NET_TRANSACTIONS = new AuthorizeNetTransactions();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<AuthorizeNetTransactionsRecord> getRecordType() {
		return AuthorizeNetTransactionsRecord.class;
	}

	/**
	 * The column <code>authorize_net_transactions.record_id</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, ULong> RECORD_ID = createField("record_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

	/**
	 * The column <code>authorize_net_transactions.request_id</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, ULong> REQUEST_ID = createField("request_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

	/**
	 * The column <code>authorize_net_transactions.kb_payment_id</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> KB_PAYMENT_ID = createField("kb_payment_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>authorize_net_transactions.kb_payment_method_id</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> KB_PAYMENT_METHOD_ID = createField("kb_payment_method_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>authorize_net_transactions.kb_payment_transaction_id</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> KB_PAYMENT_TRANSACTION_ID = createField("kb_payment_transaction_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>authorize_net_transactions.kb_transaction_type</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> KB_TRANSACTION_TYPE = createField("kb_transaction_type", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false), this, "");

	/**
	 * The column <code>authorize_net_transactions.transaction_type</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> TRANSACTION_TYPE = createField("transaction_type", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_transactions.authorize_net_customer_profile_id</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> AUTHORIZE_NET_CUSTOMER_PROFILE_ID = createField("authorize_net_customer_profile_id", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_transactions.authorize_net_payment_profile_id</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> AUTHORIZE_NET_PAYMENT_PROFILE_ID = createField("authorize_net_payment_profile_id", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_transactions.authorize_net_transaction_id</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> AUTHORIZE_NET_TRANSACTION_ID = createField("authorize_net_transaction_id", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_transactions.amount</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, BigDecimal> AMOUNT = createField("amount", org.jooq.impl.SQLDataType.DECIMAL.precision(8, 2), this, "");

	/**
	 * The column <code>authorize_net_transactions.currency</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> CURRENCY = createField("currency", org.jooq.impl.SQLDataType.VARCHAR.length(36), this, "");

	/**
	 * The column <code>authorize_net_transactions.auth_code</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> AUTH_CODE = createField("auth_code", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_transactions.avs_result_code</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> AVS_RESULT_CODE = createField("avs_result_code", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_transactions.cvv_result_code</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> CVV_RESULT_CODE = createField("cvv_result_code", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_transactions.cavv_result_code</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> CAVV_RESULT_CODE = createField("cavv_result_code", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_transactions.account_type</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> ACCOUNT_TYPE = createField("account_type", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_transactions.response_status</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> RESPONSE_STATUS = createField("response_status", org.jooq.impl.SQLDataType.VARCHAR.length(32), this, "");

	/**
	 * The column <code>authorize_net_transactions.response_message</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> RESPONSE_MESSAGE = createField("response_message", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_transactions.transaction_status</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> TRANSACTION_STATUS = createField("transaction_status", org.jooq.impl.SQLDataType.VARCHAR.length(32), this, "");

	/**
	 * The column <code>authorize_net_transactions.transaction_message</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> TRANSACTION_MESSAGE = createField("transaction_message", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_transactions.transaction_error</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> TRANSACTION_ERROR = createField("transaction_error", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_transactions.test_request</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> TEST_REQUEST = createField("test_request", org.jooq.impl.SQLDataType.VARCHAR.length(16), this, "");

	/**
	 * The column <code>authorize_net_transactions.success</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, Byte> SUCCESS = createField("success", org.jooq.impl.SQLDataType.TINYINT, this, "");

	/**
	 * The column <code>authorize_net_transactions.created_at</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, Timestamp> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

	/**
	 * The column <code>authorize_net_transactions.updated_at</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, Timestamp> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

	/**
	 * The column <code>authorize_net_transactions.kb_account_id</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> KB_ACCOUNT_ID = createField("kb_account_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>authorize_net_transactions.kb_tenant_id</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> KB_TENANT_ID = createField("kb_tenant_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>authorize_net_transactions.kb_payment_plugin_status</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, String> KB_PAYMENT_PLUGIN_STATUS = createField("kb_payment_plugin_status", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>authorize_net_transactions.kb_ref_transaction_record_id</code>.
	 */
	public final TableField<AuthorizeNetTransactionsRecord, ULong> KB_REF_TRANSACTION_RECORD_ID = createField("kb_ref_transaction_record_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

	/**
	 * Create a <code>authorize_net_transactions</code> table reference
	 */
	public AuthorizeNetTransactions() {
		this("authorize_net_transactions", null);
	}

	/**
	 * Create an aliased <code>authorize_net_transactions</code> table reference
	 */
	public AuthorizeNetTransactions(String alias) {
		this(alias, AUTHORIZE_NET_TRANSACTIONS);
	}

	private AuthorizeNetTransactions(String alias, Table<AuthorizeNetTransactionsRecord> aliased) {
		this(alias, aliased, null);
	}

	private AuthorizeNetTransactions(String alias, Table<AuthorizeNetTransactionsRecord> aliased, Field<?>[] parameters) {
		super(alias, DefaultSchema.DEFAULT_SCHEMA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<AuthorizeNetTransactionsRecord, ULong> getIdentity() {
		return Keys.IDENTITY_AUTHORIZE_NET_TRANSACTIONS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<AuthorizeNetTransactionsRecord> getPrimaryKey() {
		return Keys.KEY_AUTHORIZE_NET_TRANSACTIONS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<AuthorizeNetTransactionsRecord>> getKeys() {
		return Arrays.<UniqueKey<AuthorizeNetTransactionsRecord>>asList(Keys.KEY_AUTHORIZE_NET_TRANSACTIONS_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetTransactions as(String alias) {
		return new AuthorizeNetTransactions(alias, this);
	}

	/**
	 * Rename this table
	 */
	public AuthorizeNetTransactions rename(String name) {
		return new AuthorizeNetTransactions(name, null);
	}
}
