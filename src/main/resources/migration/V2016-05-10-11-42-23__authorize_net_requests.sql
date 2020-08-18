CREATE TABLE authorize_net_requests (
  record_id BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT
, kb_payment_id CHAR(36) NOT NULL
, kb_payment_method_id CHAR(36) DEFAULT NULL
, kb_payment_transaction_id CHAR(36) NOT NULL
, kb_transaction_type varchar(50) NOT NULL -- KB's transaction type
, transaction_type varchar(50) NOT NULL -- Auth.Net transaction type
, authorize_net_customer_profile_id VARCHAR(255) DEFAULT NULL -- Authorize.Net Customer Profile Id
, authorize_net_payment_profile_id VARCHAR(255) DEFAULT NULL -- Authorize.Net Payment Profile Id
, amount DECIMAL(8,2) DEFAULT NULL -- DECIMAL(8,2): 8 - the number of significant digits stored, 2 - the number of digits that can be stored after the decimal point
, currency VARCHAR(36) DEFAULT NULL
, created_at DATETIME NOT NULL
, updated_at DATETIME NOT NULL
, kb_account_id CHAR(36) NOT NULL
, kb_tenant_id CHAR(36) NOT NULL
, PRIMARY KEY (record_id)
, INDEX index_authorize_net_requests_kb_payment_id (kb_payment_id)
) /*! ENGINE=InnoDB CHARACTER SET utf8 COLLATE utf8_bin */;

