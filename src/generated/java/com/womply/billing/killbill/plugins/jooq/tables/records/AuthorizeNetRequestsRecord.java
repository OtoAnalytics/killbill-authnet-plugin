/**
 * This class is generated by jOOQ
 */
package com.womply.billing.killbill.plugins.jooq.tables.records;


import com.womply.billing.killbill.plugins.jooq.tables.AuthorizeNetRequests;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record15;
import org.jooq.Row15;
import org.jooq.impl.UpdatableRecordImpl;
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
public class AuthorizeNetRequestsRecord extends UpdatableRecordImpl<AuthorizeNetRequestsRecord> implements Record15<ULong, String, String, String, String, String, String, String, BigDecimal, String, Timestamp, Timestamp, String, String, ULong> {

	private static final long serialVersionUID = 585928831;

	/**
	 * Setter for <code>authorize_net_requests.record_id</code>.
	 */
	public void setRecordId(ULong value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.record_id</code>.
	 */
	public ULong getRecordId() {
		return (ULong) getValue(0);
	}

	/**
	 * Setter for <code>authorize_net_requests.kb_payment_id</code>.
	 */
	public void setKbPaymentId(String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.kb_payment_id</code>.
	 */
	public String getKbPaymentId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>authorize_net_requests.kb_payment_method_id</code>.
	 */
	public void setKbPaymentMethodId(String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.kb_payment_method_id</code>.
	 */
	public String getKbPaymentMethodId() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>authorize_net_requests.kb_payment_transaction_id</code>.
	 */
	public void setKbPaymentTransactionId(String value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.kb_payment_transaction_id</code>.
	 */
	public String getKbPaymentTransactionId() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>authorize_net_requests.kb_transaction_type</code>.
	 */
	public void setKbTransactionType(String value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.kb_transaction_type</code>.
	 */
	public String getKbTransactionType() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>authorize_net_requests.transaction_type</code>.
	 */
	public void setTransactionType(String value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.transaction_type</code>.
	 */
	public String getTransactionType() {
		return (String) getValue(5);
	}

	/**
	 * Setter for <code>authorize_net_requests.authorize_net_customer_profile_id</code>.
	 */
	public void setAuthorizeNetCustomerProfileId(String value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.authorize_net_customer_profile_id</code>.
	 */
	public String getAuthorizeNetCustomerProfileId() {
		return (String) getValue(6);
	}

	/**
	 * Setter for <code>authorize_net_requests.authorize_net_payment_profile_id</code>.
	 */
	public void setAuthorizeNetPaymentProfileId(String value) {
		setValue(7, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.authorize_net_payment_profile_id</code>.
	 */
	public String getAuthorizeNetPaymentProfileId() {
		return (String) getValue(7);
	}

	/**
	 * Setter for <code>authorize_net_requests.amount</code>.
	 */
	public void setAmount(BigDecimal value) {
		setValue(8, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.amount</code>.
	 */
	public BigDecimal getAmount() {
		return (BigDecimal) getValue(8);
	}

	/**
	 * Setter for <code>authorize_net_requests.currency</code>.
	 */
	public void setCurrency(String value) {
		setValue(9, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.currency</code>.
	 */
	public String getCurrency() {
		return (String) getValue(9);
	}

	/**
	 * Setter for <code>authorize_net_requests.created_at</code>.
	 */
	public void setCreatedAt(Timestamp value) {
		setValue(10, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.created_at</code>.
	 */
	public Timestamp getCreatedAt() {
		return (Timestamp) getValue(10);
	}

	/**
	 * Setter for <code>authorize_net_requests.updated_at</code>.
	 */
	public void setUpdatedAt(Timestamp value) {
		setValue(11, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.updated_at</code>.
	 */
	public Timestamp getUpdatedAt() {
		return (Timestamp) getValue(11);
	}

	/**
	 * Setter for <code>authorize_net_requests.kb_account_id</code>.
	 */
	public void setKbAccountId(String value) {
		setValue(12, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.kb_account_id</code>.
	 */
	public String getKbAccountId() {
		return (String) getValue(12);
	}

	/**
	 * Setter for <code>authorize_net_requests.kb_tenant_id</code>.
	 */
	public void setKbTenantId(String value) {
		setValue(13, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.kb_tenant_id</code>.
	 */
	public String getKbTenantId() {
		return (String) getValue(13);
	}

	/**
	 * Setter for <code>authorize_net_requests.kb_ref_transaction_record_id</code>.
	 */
	public void setKbRefTransactionRecordId(ULong value) {
		setValue(14, value);
	}

	/**
	 * Getter for <code>authorize_net_requests.kb_ref_transaction_record_id</code>.
	 */
	public ULong getKbRefTransactionRecordId() {
		return (ULong) getValue(14);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<ULong> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record15 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row15<ULong, String, String, String, String, String, String, String, BigDecimal, String, Timestamp, Timestamp, String, String, ULong> fieldsRow() {
		return (Row15) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row15<ULong, String, String, String, String, String, String, String, BigDecimal, String, Timestamp, Timestamp, String, String, ULong> valuesRow() {
		return (Row15) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<ULong> field1() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.RECORD_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.KB_PAYMENT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.KB_PAYMENT_METHOD_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.KB_PAYMENT_TRANSACTION_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.KB_TRANSACTION_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.TRANSACTION_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field7() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.AUTHORIZE_NET_CUSTOMER_PROFILE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field8() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.AUTHORIZE_NET_PAYMENT_PROFILE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<BigDecimal> field9() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.AMOUNT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field10() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.CURRENCY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field11() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.CREATED_AT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field12() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.UPDATED_AT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field13() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.KB_ACCOUNT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field14() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.KB_TENANT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<ULong> field15() {
		return AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS.KB_REF_TRANSACTION_RECORD_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ULong value1() {
		return getRecordId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getKbPaymentId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getKbPaymentMethodId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getKbPaymentTransactionId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getKbTransactionType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getTransactionType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value7() {
		return getAuthorizeNetCustomerProfileId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value8() {
		return getAuthorizeNetPaymentProfileId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigDecimal value9() {
		return getAmount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value10() {
		return getCurrency();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value11() {
		return getCreatedAt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value12() {
		return getUpdatedAt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value13() {
		return getKbAccountId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value14() {
		return getKbTenantId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ULong value15() {
		return getKbRefTransactionRecordId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value1(ULong value) {
		setRecordId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value2(String value) {
		setKbPaymentId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value3(String value) {
		setKbPaymentMethodId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value4(String value) {
		setKbPaymentTransactionId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value5(String value) {
		setKbTransactionType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value6(String value) {
		setTransactionType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value7(String value) {
		setAuthorizeNetCustomerProfileId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value8(String value) {
		setAuthorizeNetPaymentProfileId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value9(BigDecimal value) {
		setAmount(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value10(String value) {
		setCurrency(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value11(Timestamp value) {
		setCreatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value12(Timestamp value) {
		setUpdatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value13(String value) {
		setKbAccountId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value14(String value) {
		setKbTenantId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord value15(ULong value) {
		setKbRefTransactionRecordId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetRequestsRecord values(ULong value1, String value2, String value3, String value4, String value5, String value6, String value7, String value8, BigDecimal value9, String value10, Timestamp value11, Timestamp value12, String value13, String value14, ULong value15) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		value9(value9);
		value10(value10);
		value11(value11);
		value12(value12);
		value13(value13);
		value14(value14);
		value15(value15);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached AuthorizeNetRequestsRecord
	 */
	public AuthorizeNetRequestsRecord() {
		super(AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS);
	}

	/**
	 * Create a detached, initialised AuthorizeNetRequestsRecord
	 */
	public AuthorizeNetRequestsRecord(ULong recordId, String kbPaymentId, String kbPaymentMethodId, String kbPaymentTransactionId, String kbTransactionType, String transactionType, String authorizeNetCustomerProfileId, String authorizeNetPaymentProfileId, BigDecimal amount, String currency, Timestamp createdAt, Timestamp updatedAt, String kbAccountId, String kbTenantId, ULong kbRefTransactionRecordId) {
		super(AuthorizeNetRequests.AUTHORIZE_NET_REQUESTS);

		setValue(0, recordId);
		setValue(1, kbPaymentId);
		setValue(2, kbPaymentMethodId);
		setValue(3, kbPaymentTransactionId);
		setValue(4, kbTransactionType);
		setValue(5, transactionType);
		setValue(6, authorizeNetCustomerProfileId);
		setValue(7, authorizeNetPaymentProfileId);
		setValue(8, amount);
		setValue(9, currency);
		setValue(10, createdAt);
		setValue(11, updatedAt);
		setValue(12, kbAccountId);
		setValue(13, kbTenantId);
		setValue(14, kbRefTransactionRecordId);
	}
}
