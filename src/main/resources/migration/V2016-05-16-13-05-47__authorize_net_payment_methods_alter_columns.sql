ALTER TABLE authorize_net_payment_methods
    DROP COLUMN is_default,
    DROP COLUMN is_deleted,
    ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE';
