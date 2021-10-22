-- Adds index on auth_net_transactions table

CREATE INDEX auth_net_transactions_comp ON authorize_net_transactions (kb_payment_id, created_at)