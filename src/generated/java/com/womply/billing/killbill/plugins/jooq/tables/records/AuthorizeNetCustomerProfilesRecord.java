/**
 * This class is generated by jOOQ
 */
package com.womply.billing.killbill.plugins.jooq.tables.records;


import com.womply.billing.killbill.plugins.jooq.tables.AuthorizeNetCustomerProfiles;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
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
public class AuthorizeNetCustomerProfilesRecord extends UpdatableRecordImpl<AuthorizeNetCustomerProfilesRecord> implements Record6<ULong, String, String, Timestamp, Timestamp, String> {

	private static final long serialVersionUID = 1721167570;

	/**
	 * Setter for <code>authorize_net_customer_profiles.record_id</code>.
	 */
	public void setRecordId(ULong value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>authorize_net_customer_profiles.record_id</code>.
	 */
	public ULong getRecordId() {
		return (ULong) getValue(0);
	}

	/**
	 * Setter for <code>authorize_net_customer_profiles.customer_id</code>.
	 */
	public void setCustomerId(String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>authorize_net_customer_profiles.customer_id</code>.
	 */
	public String getCustomerId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>authorize_net_customer_profiles.customer_profile_id</code>.
	 */
	public void setCustomerProfileId(String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>authorize_net_customer_profiles.customer_profile_id</code>.
	 */
	public String getCustomerProfileId() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>authorize_net_customer_profiles.created_at</code>.
	 */
	public void setCreatedAt(Timestamp value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>authorize_net_customer_profiles.created_at</code>.
	 */
	public Timestamp getCreatedAt() {
		return (Timestamp) getValue(3);
	}

	/**
	 * Setter for <code>authorize_net_customer_profiles.updated_at</code>.
	 */
	public void setUpdatedAt(Timestamp value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>authorize_net_customer_profiles.updated_at</code>.
	 */
	public Timestamp getUpdatedAt() {
		return (Timestamp) getValue(4);
	}

	/**
	 * Setter for <code>authorize_net_customer_profiles.kb_tenant_id</code>.
	 */
	public void setKbTenantId(String value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>authorize_net_customer_profiles.kb_tenant_id</code>.
	 */
	public String getKbTenantId() {
		return (String) getValue(5);
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
	// Record6 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<ULong, String, String, Timestamp, Timestamp, String> fieldsRow() {
		return (Row6) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<ULong, String, String, Timestamp, Timestamp, String> valuesRow() {
		return (Row6) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<ULong> field1() {
		return AuthorizeNetCustomerProfiles.AUTHORIZE_NET_CUSTOMER_PROFILES.RECORD_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return AuthorizeNetCustomerProfiles.AUTHORIZE_NET_CUSTOMER_PROFILES.CUSTOMER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return AuthorizeNetCustomerProfiles.AUTHORIZE_NET_CUSTOMER_PROFILES.CUSTOMER_PROFILE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field4() {
		return AuthorizeNetCustomerProfiles.AUTHORIZE_NET_CUSTOMER_PROFILES.CREATED_AT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field5() {
		return AuthorizeNetCustomerProfiles.AUTHORIZE_NET_CUSTOMER_PROFILES.UPDATED_AT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return AuthorizeNetCustomerProfiles.AUTHORIZE_NET_CUSTOMER_PROFILES.KB_TENANT_ID;
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
		return getCustomerId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getCustomerProfileId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value4() {
		return getCreatedAt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value5() {
		return getUpdatedAt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getKbTenantId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetCustomerProfilesRecord value1(ULong value) {
		setRecordId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetCustomerProfilesRecord value2(String value) {
		setCustomerId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetCustomerProfilesRecord value3(String value) {
		setCustomerProfileId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetCustomerProfilesRecord value4(Timestamp value) {
		setCreatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetCustomerProfilesRecord value5(Timestamp value) {
		setUpdatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetCustomerProfilesRecord value6(String value) {
		setKbTenantId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorizeNetCustomerProfilesRecord values(ULong value1, String value2, String value3, Timestamp value4, Timestamp value5, String value6) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		return this;
	}

	@Override
	public ULong component1() {
		return getRecordId();
	}

	@Override
	public String component2() {
		return getCustomerId();
	}

	@Override
	public String component3() {
		return getCustomerProfileId();
	}

	@Override
	public Timestamp component4() {
		return getCreatedAt();
	}

	@Override
	public Timestamp component5() {
		return getUpdatedAt();
	}

	@Override
	public String component6() {
		return getKbTenantId();
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached AuthorizeNetCustomerProfilesRecord
	 */
	public AuthorizeNetCustomerProfilesRecord() {
		super(AuthorizeNetCustomerProfiles.AUTHORIZE_NET_CUSTOMER_PROFILES);
	}

	/**
	 * Create a detached, initialised AuthorizeNetCustomerProfilesRecord
	 */
	public AuthorizeNetCustomerProfilesRecord(ULong recordId, String customerId, String customerProfileId, Timestamp createdAt, Timestamp updatedAt, String kbTenantId) {
		super(AuthorizeNetCustomerProfiles.AUTHORIZE_NET_CUSTOMER_PROFILES);

		setValue(0, recordId);
		setValue(1, customerId);
		setValue(2, customerProfileId);
		setValue(3, createdAt);
		setValue(4, updatedAt);
		setValue(5, kbTenantId);
	}
}
