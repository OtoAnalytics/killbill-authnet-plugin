-- Adds ACH specific columns to various tables

ALTER TABLE authorize_net_payment_methods ADD ach_routing_number VARCHAR(255) AFTER cc_last_4;
ALTER TABLE authorize_net_payment_methods ADD ach_institution_name VARCHAR(255) AFTER ach_routing_number;
ALTER TABLE authorize_net_payment_methods ADD ach_account_last_4 VARCHAR(255) AFTER ach_institution_name;

