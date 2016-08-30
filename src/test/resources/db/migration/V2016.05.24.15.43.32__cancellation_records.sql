DROP TABLE IF EXISTS subscription_cancellations;

CREATE TABLE subscription_cancellations (
  record_id BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT
, kb_account_id CHAR(36) NOT NULL
, kb_tenant_id CHAR(36) NOT NULL
, kb_external_id VARCHAR(255) NOT NULL
, kb_subscription_id CHAR(36) NOT NULL
, product_name VARCHAR(255) DEFAULT NULL
, entitlement_state VARCHAR(127) DEFAULT NULL
, entitlement_end_date DATETIME DEFAULT NULL
, created_at DATETIME NOT NULL
, PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;