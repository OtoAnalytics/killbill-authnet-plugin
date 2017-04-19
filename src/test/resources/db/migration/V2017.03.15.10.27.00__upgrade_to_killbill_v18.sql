alter table accounts add column notes varchar(4096) DEFAULT NULL after phone;
alter table account_history add column notes varchar(4096) DEFAULT NULL after phone;
drop index idx_invoice_payments on invoice_payments;
alter table subscriptions drop column active_version;
alter table subscriptions add column migrated bool NOT NULL default FALSE after charged_through_date;
alter table subscription_events drop column requested_date;
alter table subscription_events drop column current_version;
alter table subscription_events change event_type event_type varchar(15) NOT NULL;
alter table subscription_events add column billing_cycle_day_local int DEFAULT NULL after price_list_name;
alter table rolled_up_usage add column tracking_id varchar(128) NOT NULL after amount;
CREATE INDEX rolled_up_usage_tracking_id_subscription_id_tenant_record_id ON rolled_up_usage(tracking_id, subscription_id, tenant_record_id);
alter table rolled_up_usage change subscription_id subscription_id varchar(36) not null;
alter table rolled_up_usage change unit_type unit_type varchar(255) not null;
alter table accounts modify external_key varchar(255);
alter table account_history modify external_key varchar(255);
alter table tenants modify external_key varchar(255);
alter table bundles modify external_key varchar(255) not null;
alter table payment_attempts modify payment_external_key varchar(255) not null;
alter table payment_attempts modify transaction_external_key varchar(255) not null;
alter table payment_attempt_history modify payment_external_key varchar(255) not null;
alter table payment_attempt_history modify transaction_external_key varchar(255) not null;
alter table invoice_items modify plan_name varchar(255) COLLATE utf8_bin DEFAULT NULL;
alter table invoice_items modify phase_name varchar(255) COLLATE utf8_bin DEFAULT NULL;
alter table invoice_items modify usage_name varchar(255) COLLATE utf8_bin DEFAULT NULL;
alter table sessions add column id varchar(36) not null after record_id;
update sessions set id = record_id;
create unique index sessions_id on sessions(id);
alter table subscription_events modify plan_name varchar(255) COLLATE utf8_bin DEFAULT NULL;
alter table subscription_events modify phase_name varchar(255) COLLATE utf8_bin DEFAULT NULL;
update invoice_items set type = 'CREDIT_ADJ' where type = 'REFUND_ADJ';
CREATE TABLE invoice_parent_children (
    record_id serial unique,
    id varchar(36) NOT NULL,
    parent_invoice_id varchar(36) NOT NULL,
    child_invoice_id varchar(36) NOT NULL,
    child_account_id varchar(36) NOT NULL,
    created_by varchar(50) NOT NULL,
    created_date datetime NOT NULL,
    account_record_id bigint /*! unsigned */ not null,
    tenant_record_id bigint /*! unsigned */ not null default 0,
    PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE UNIQUE INDEX invoice_parent_children_id ON invoice_parent_children(id);
CREATE INDEX invoice_parent_children_invoice_id ON invoice_parent_children(parent_invoice_id);
CREATE INDEX invoice_parent_children_tenant_account_record_id ON invoice_parent_children(tenant_record_id, account_record_id);


alter table invoice_items add column child_account_id varchar(36) after account_id;
alter table invoice_items modify start_date date;

alter table invoices add column  parent_invoice bool NOT NULL DEFAULT FALSE after migrated;
alter table invoices modify target_date date;

drop index invoices_account_target on invoices;
create index invoices_account on invoices(account_id asc);
alter table accounts add column parent_account_id varchar(36) DEFAULT NULL after billing_cycle_day_local;
alter table accounts add column is_payment_delegated_to_parent boolean DEFAULT FALSE after parent_account_id;

alter table account_history add column parent_account_id varchar(36) DEFAULT NULL after billing_cycle_day_local;
alter table account_history add column is_payment_delegated_to_parent boolean DEFAULT FALSE after parent_account_id;
alter table invoices add column status varchar(15) NOT NULL DEFAULT 'COMMITTED' after currency;
update invoices set status = 'COMMITTED';