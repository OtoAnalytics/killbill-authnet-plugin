/**
 * This class is generated by jOOQ
 */
package com.womply.billing.killbill.plugins.jooq.tables.records;


import com.womply.billing.killbill.plugins.jooq.tables.AuthorizeNetPaymentMethods;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record20;
import org.jooq.Row20;
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
public class AuthorizeNetPaymentMethodsRecord extends UpdatableRecordImpl<AuthorizeNetPaymentMethodsRecord> implements Record20<ULong, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, Timestamp, Timestamp, String, String> {

	private static final long serialVersionUID = -413162343;

	/**
	 * Setter for <code>authorize_net_payment_methods.record_id</code>.
	 */
	public void setRecordId(ULong value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.record_id</code>.
	 */
	public ULong getRecordId() {
		return (ULong) getValue(0);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.kb_account_id</code>.
	 */
	public void setKbAccountId(String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.kb_account_id</code>.
	 */
	public String getKbAccountId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.kb_payment_method_id</code>.
	 */
	public void setKbPaymentMethodId(String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.kb_payment_method_id</code>.
	 */
	public String getKbPaymentMethodId() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.authorize_net_customer_profile_id</code>.
	 */
	public void setAuthorizeNetCustomerProfileId(String value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.authorize_net_customer_profile_id</code>.
	 */
	public String getAuthorizeNetCustomerProfileId() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.authorize_net_payment_profile_id</code>.
	 */
	public void setAuthorizeNetPaymentProfileId(String value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.authorize_net_payment_profile_id</code>.
	 */
	public String getAuthorizeNetPaymentProfileId() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.cc_first_name</code>.
	 */
	public void setCcFirstName(String value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.cc_first_name</code>.
	 */
	public String getCcFirstName() {
		return (String) getValue(5);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.cc_last_name</code>.
	 */
	public void setCcLastName(String value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.cc_last_name</code>.
	 */
	public String getCcLastName() {
		return (String) getValue(6);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.cc_type</code>.
	 */
	public void setCcType(String value) {
		setValue(7, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.cc_type</code>.
	 */
	public String getCcType() {
		return (String) getValue(7);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.cc_exp_month</code>.
	 */
	public void setCcExpMonth(String value) {
		setValue(8, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.cc_exp_month</code>.
	 */
	public String getCcExpMonth() {
		return (String) getValue(8);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.cc_exp_year</code>.
	 */
	public void setCcExpYear(String value) {
		setValue(9, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.cc_exp_year</code>.
	 */
	public String getCcExpYear() {
		return (String) getValue(9);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.cc_last_4</code>.
	 */
	public void setCcLast_4(String value) {
		setValue(10, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.cc_last_4</code>.
	 */
	public String getCcLast_4() {
		return (String) getValue(10);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.address</code>.
	 */
	public void setAddress(String value) {
		setValue(11, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.address</code>.
	 */
	public String getAddress() {
		return (String) getValue(11);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.city</code>.
	 */
	public void setCity(String value) {
		setValue(12, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.city</code>.
	 */
	public String getCity() {
		return (String) getValue(12);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.state</code>.
	 */
	public void setState(String value) {
		setValue(13, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.state</code>.
	 */
	public String getState() {
		return (String) getValue(13);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.zip</code>.
	 */
	public void setZip(String value) {
		setValue(14, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.zip</code>.
	 */
	public String getZip() {
		return (String) getValue(14);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.country</code>.
	 */
	public void setCountry(String value) {
		setValue(15, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.country</code>.
	 */
	public String getCountry() {
		return (String) getValue(15);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.created_at</code>.
	 */
	public void setCreatedAt(Timestamp value) {
		setValue(16, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.created_at</code>.
	 */
	public Timestamp getCreatedAt() {
		return (Timestamp) getValue(16);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.updated_at</code>.
	 */
	public void setUpdatedAt(Timestamp value) {
		setValue(17, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.updated_at</code>.
	 */
	public Timestamp getUpdatedAt() {
		return (Timestamp) getValue(17);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.kb_tenant_id</code>.
	 */
	public void setKbTenantId(String value) {
		setValue(18, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.kb_tenant_id</code>.
	 */
	public String getKbTenantId() {
		return (String) getValue(18);
	}

	/**
	 * Setter for <code>authorize_net_payment_methods.status</code>.
	 */
	public void setStatus(String value) {
		setValue(19, value);
	}

	/**
	 * Getter for <code>authorize_net_payment_methods.status</code>.
	 */
	public String getStatus() {
		return (String) getValue(19);
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
	// Record20 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row20<ULong, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, Timestamp, Timestamp, String, String> fieldsRow() {
		return (Row20) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row20<ULong, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, Timestamp, Timestamp, String, String> valuesRow() {
		return (Row20) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<ULong> field1() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.RECORD_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.KB_ACCOUNT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_CUSTOMER_PROFILE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.AUTHORIZE_NET_PAYMENT_PROFILE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.CC_FIRST_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field7() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.CC_LAST_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field8() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.CC_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field9() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.CC_EXP_MONTH;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field10() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.CC_EXP_YEAR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field11() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.CC_LAST_4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field12() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.ADDRESS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field13() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.CITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field14() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.STATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field15() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.ZIP;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field16() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.COUNTRY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field17() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.CREATED_AT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field18() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.UPDATED_AT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field19() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.KB_TENANT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field20() {
		return AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS.STATUS;
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
		return getKbAccountId();
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
		return getAuthorizeNetCustomerProfileId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getAuthorizeNetPaymentProfileId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getCcFirstName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value7() {
		return getCcLastName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value8() {
		return getCcType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value9() {
		return getCcExpMonth();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value10() {
		return getCcExpYear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value11() {
		return getCcLast_4();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value12() {
		return getAddress();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value13() {
		return getCity();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value14() {
		return getState();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value15() {
		return getZip();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value16() {
		return getCountry();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value17() {
		return getCreatedAt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value18() {
		return getUpdatedAt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value19() {
		return getKbTenantId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value20() {
		return getStatus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value1(ULong value) {
		setRecordId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value2(String value) {
		setKbAccountId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value3(String value) {
		setKbPaymentMethodId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value4(String value) {
		setAuthorizeNetCustomerProfileId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value5(String value) {
		setAuthorizeNetPaymentProfileId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value6(String value) {
		setCcFirstName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value7(String value) {
		setCcLastName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value8(String value) {
		setCcType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value9(String value) {
		setCcExpMonth(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value10(String value) {
		setCcExpYear(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value11(String value) {
		setCcLast_4(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value12(String value) {
		setAddress(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value13(String value) {
		setCity(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value14(String value) {
		setState(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value15(String value) {
		setZip(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value16(String value) {
		setCountry(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value17(Timestamp value) {
		setCreatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value18(Timestamp value) {
		setUpdatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value19(String value) {
		setKbTenantId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord value20(String value) {
		setStatus(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetPaymentMethodsRecord values(ULong value1, String value2, String value3, String value4, String value5, String value6, String value7, String value8, String value9, String value10, String value11, String value12, String value13, String value14, String value15, String value16, Timestamp value17, Timestamp value18, String value19, String value20) {
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
		value16(value16);
		value17(value17);
		value18(value18);
		value19(value19);
		value20(value20);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached AuthorizeNetPaymentMethodsRecord
	 */
	public AuthorizeNetPaymentMethodsRecord() {
		super(AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS);
	}

	/**
	 * Create a detached, initialised AuthorizeNetPaymentMethodsRecord
	 */
	public AuthorizeNetPaymentMethodsRecord(ULong recordId, String kbAccountId, String kbPaymentMethodId, String authorizeNetCustomerProfileId, String authorizeNetPaymentProfileId, String ccFirstName, String ccLastName, String ccType, String ccExpMonth, String ccExpYear, String ccLast_4, String address, String city, String state, String zip, String country, Timestamp createdAt, Timestamp updatedAt, String kbTenantId, String status) {
		super(AuthorizeNetPaymentMethods.AUTHORIZE_NET_PAYMENT_METHODS);

		setValue(0, recordId);
		setValue(1, kbAccountId);
		setValue(2, kbPaymentMethodId);
		setValue(3, authorizeNetCustomerProfileId);
		setValue(4, authorizeNetPaymentProfileId);
		setValue(5, ccFirstName);
		setValue(6, ccLastName);
		setValue(7, ccType);
		setValue(8, ccExpMonth);
		setValue(9, ccExpYear);
		setValue(10, ccLast_4);
		setValue(11, address);
		setValue(12, city);
		setValue(13, state);
		setValue(14, zip);
		setValue(15, country);
		setValue(16, createdAt);
		setValue(17, updatedAt);
		setValue(18, kbTenantId);
		setValue(19, status);
	}
}