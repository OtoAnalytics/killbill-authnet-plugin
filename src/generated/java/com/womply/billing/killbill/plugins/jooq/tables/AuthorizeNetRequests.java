/**
 * This class is generated by jOOQ
 */
package com.womply.billing.killbill.plugins.jooq.tables;


import com.womply.billing.killbill.plugins.jooq.DefaultSchema;
import com.womply.billing.killbill.plugins.jooq.Keys;
import com.womply.billing.killbill.plugins.jooq.tables.records.AuthorizeNetRequestsRecord;

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
public class AuthorizeNetRequests extends TableImpl<AuthorizeNetRequestsRecord> {

	private static final long serialVersionUID = 1573862727;

	/**
	 * The reference instance of <code>authorize_net_requests</code>
	 */
	public static final AuthorizeNetRequests AUTHORIZE_NET_REQUESTS = new AuthorizeNetRequests();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<AuthorizeNetRequestsRecord> getRecordType() {
		return AuthorizeNetRequestsRecord.class;
	}

	/**
	 * The column <code>authorize_net_requests.record_id</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, ULong> RECORD_ID = createField("record_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

	/**
	 * The column <code>authorize_net_requests.kb_payment_id</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, String> KB_PAYMENT_ID = createField("kb_payment_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>authorize_net_requests.kb_payment_method_id</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, String> KB_PAYMENT_METHOD_ID = createField("kb_payment_method_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>authorize_net_requests.kb_payment_transaction_id</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, String> KB_PAYMENT_TRANSACTION_ID = createField("kb_payment_transaction_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>authorize_net_requests.kb_transaction_type</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, String> KB_TRANSACTION_TYPE = createField("kb_transaction_type", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false), this, "");

	/**
	 * The column <code>authorize_net_requests.transaction_type</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, String> TRANSACTION_TYPE = createField("transaction_type", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false), this, "");

	/**
	 * The column <code>authorize_net_requests.authorize_net_customer_profile_id</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, String> AUTHORIZE_NET_CUSTOMER_PROFILE_ID = createField("authorize_net_customer_profile_id", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_requests.authorize_net_payment_profile_id</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, String> AUTHORIZE_NET_PAYMENT_PROFILE_ID = createField("authorize_net_payment_profile_id", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>authorize_net_requests.amount</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, BigDecimal> AMOUNT = createField("amount", org.jooq.impl.SQLDataType.DECIMAL.precision(8, 2), this, "");

	/**
	 * The column <code>authorize_net_requests.currency</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, String> CURRENCY = createField("currency", org.jooq.impl.SQLDataType.VARCHAR.length(36), this, "");

	/**
	 * The column <code>authorize_net_requests.created_at</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, Timestamp> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

	/**
	 * The column <code>authorize_net_requests.updated_at</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, Timestamp> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

	/**
	 * The column <code>authorize_net_requests.kb_account_id</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, String> KB_ACCOUNT_ID = createField("kb_account_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>authorize_net_requests.kb_tenant_id</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, String> KB_TENANT_ID = createField("kb_tenant_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>authorize_net_requests.kb_ref_transaction_record_id</code>.
	 */
	public final TableField<AuthorizeNetRequestsRecord, ULong> KB_REF_TRANSACTION_RECORD_ID = createField("kb_ref_transaction_record_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

	/**
	 * Create a <code>authorize_net_requests</code> table reference
	 */
	public AuthorizeNetRequests() {
		this("authorize_net_requests", null);
	}

	/**
	 * Create an aliased <code>authorize_net_requests</code> table reference
	 */
	public AuthorizeNetRequests(String alias) {
		this(alias, AUTHORIZE_NET_REQUESTS);
	}

	private AuthorizeNetRequests(String alias, Table<AuthorizeNetRequestsRecord> aliased) {
		this(alias, aliased, null);
	}

	private AuthorizeNetRequests(String alias, Table<AuthorizeNetRequestsRecord> aliased, Field<?>[] parameters) {
		super(alias, DefaultSchema.DEFAULT_SCHEMA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<AuthorizeNetRequestsRecord, ULong> getIdentity() {
		return Keys.IDENTITY_AUTHORIZE_NET_REQUESTS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<AuthorizeNetRequestsRecord> getPrimaryKey() {
		return Keys.KEY_AUTHORIZE_NET_REQUESTS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<AuthorizeNetRequestsRecord>> getKeys() {
		return Arrays.<UniqueKey<AuthorizeNetRequestsRecord>>asList(Keys.KEY_AUTHORIZE_NET_REQUESTS_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequests as(String alias) {
		return new AuthorizeNetRequests(alias, this);
	}

	/**
	 * Rename this table
	 */
	public AuthorizeNetRequests rename(String name) {
		return new AuthorizeNetRequests(name, null);
	}
}
