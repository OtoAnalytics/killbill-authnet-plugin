CREATE TABLE authorize_net_responses (
  record_id BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT
, request_id BIGINT(20) UNSIGNED DEFAULT NULL  -- FK into the authorize_net_requests table
, kb_payment_id CHAR(36) DEFAULT NULL
, kb_payment_method_id CHAR(36) DEFAULT NULL
, kb_payment_transaction_id CHAR(36) DEFAULT NULL
, kb_transaction_type varchar(50) NOT NULL -- KB's transaction type
, transaction_type VARCHAR(255) DEFAULT NULL -- Auth.Net transaction type
, authorize_net_customer_profile_id VARCHAR(255) DEFAULT NULL -- Authorize.Net Customer Profile Id
, authorize_net_payment_profile_id VARCHAR(255) DEFAULT NULL -- Authorize.Net Payment Profile Id
, authorize_net_transaction_id VARCHAR(255) DEFAULT NULL -- authorize.Net transaction id
, amount DECIMAL(8,2) DEFAULT NULL -- DECIMAL(8,2): 8 - the number of significant digits stored, 2 - the number of digits that can be stored after the decimal point
, currency VARCHAR(36) DEFAULT NULL
, auth_code VARCHAR(255) DEFAULT NULL
, avs_result_code VARCHAR(255) DEFAULT NULL
, cvv_result_code VARCHAR(255) DEFAULT NULL
, cavv_result_code VARCHAR(255) DEFAULT NULL
, account_type VARCHAR(255) DEFAULT NULL -- e.g. Visa
, response_status VARCHAR(32) DEFAULT NULL
, response_message VARCHAR(255) DEFAULT NULL
, transaction_status VARCHAR(32) DEFAULT NULL
, transaction_message VARCHAR(255) DEFAULT NULL
, transaction_error VARCHAR(255) DEFAULT NULL
, test_request VARCHAR(16) DEFAULT NULL -- seems to only be "0". Seems to be missing from Auth.Net docs?
, success BOOLEAN DEFAULT NULL
, created_at DATETIME NOT NULL
, updated_at DATETIME NOT NULL
, kb_account_id CHAR(36) DEFAULT NULL
, kb_tenant_id CHAR(36) DEFAULT NULL
, PRIMARY KEY (record_id)
, INDEX index_authorize_net_responses_kb_account_id_kb_payment_id (kb_account_id, kb_payment_id)
) /*! ENGINE=InnoDB CHARACTER SET utf8 COLLATE utf8_bin */;
