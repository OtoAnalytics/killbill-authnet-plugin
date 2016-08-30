ALTER TABLE authorize_net_transactions ADD kb_ref_transaction_record_id BIGINT(20) UNSIGNED NULL; -- referenced transaction record_id, used in refund transactions
