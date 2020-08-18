DROP TABLE IF EXISTS authorize_net_payment_methods;

CREATE TABLE authorize_net_payment_methods (
  record_id BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT
, kb_account_id CHAR(36) NOT NULL
, kb_payment_method_id CHAR(36) NOT NULL
, authorize_net_customer_profile_id VARCHAR(255) DEFAULT NULL -- Authorize.Net Customer Profile Id
, authorize_net_payment_profile_id VARCHAR(255) DEFAULT NULL -- Authorize.Net Payment Profile Id
, cc_first_name VARCHAR(255) DEFAULT NULL
, cc_last_name VARCHAR(255) DEFAULT NULL
, cc_type VARCHAR(255) DEFAULT NULL
, cc_exp_month VARCHAR(255) DEFAULT NULL
, cc_exp_year VARCHAR(255) DEFAULT NULL
, cc_last_4 VARCHAR(255) DEFAULT NULL
, address VARCHAR(255) DEFAULT NULL
, city VARCHAR(255) DEFAULT NULL
, state VARCHAR(255) DEFAULT NULL
, zip VARCHAR(255) DEFAULT NULL
, country VARCHAR(255) DEFAULT NULL
, is_default BOOLEAN NOT NULL DEFAULT FALSE
, is_deleted BOOLEAN NOT NULL DEFAULT FALSE
, created_at DATETIME NOT NULL
, updated_at DATETIME NOT NULL
, kb_tenant_id CHAR(36) NOT NULL
, PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;

CREATE UNIQUE INDEX unique_authorize_net_payment_methods_payment_profile_id ON authorize_net_payment_methods(authorize_net_customer_profile_id, authorize_net_payment_profile_id, kb_account_id);

CREATE INDEX index_authorize_net_payment_methods_kb_account_id ON authorize_net_payment_methods(kb_account_id);

CREATE UNIQUE INDEX authorize_net_payment_methods_kb_payment_id ON authorize_net_payment_methods(kb_payment_method_id);

DROP TABLE IF EXISTS authorize_net_customer_profiles;

CREATE TABLE authorize_net_customer_profiles (
  record_id BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT
, customer_id VARCHAR(255) NOT NULL -- has values of the form W_{merchant_location_id}
, customer_profile_id VARCHAR(255) NOT NULL
, created_at DATETIME NOT NULL
, updated_at DATETIME NOT NULL
, kb_tenant_id CHAR(36) NOT NULL
, PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;

CREATE UNIQUE INDEX unique_authorize_net_customer_profiles ON authorize_net_customer_profiles(customer_id, customer_profile_id);

