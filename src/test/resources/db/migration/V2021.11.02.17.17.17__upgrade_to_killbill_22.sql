-- V20161220000000__unit_price_override
DROP TABLE IF EXISTS catalog_override_usage_definition;
create table catalog_override_usage_definition
(
record_id serial unique,
parent_usage_name varchar(255) NOT NULL,
type varchar(255) NOT NULL,
fixed_price decimal(15,9) NULL,
recurring_price decimal(15,9) NULL,
currency varchar(3) NOT NULL,
effective_date datetime NOT NULL,
created_date datetime NOT NULL,
created_by varchar(50) NOT NULL,
tenant_record_id bigint /*! unsigned */ not null default 0,
PRIMARY KEY(record_id)
);
CREATE INDEX catalog_override_usage_definition_idx ON catalog_override_usage_definition(tenant_record_id, parent_usage_name, currency);


DROP TABLE IF EXISTS catalog_override_tier_definition;
create table catalog_override_tier_definition
(
record_id serial unique,
fixed_price decimal(15,9) NULL,
recurring_price decimal(15,9) NULL,
currency varchar(3) NOT NULL,
effective_date datetime NOT NULL,
created_date datetime NOT NULL,
created_by varchar(50) NOT NULL,
tenant_record_id bigint /*! unsigned */ not null default 0,
PRIMARY KEY(record_id)
);
CREATE INDEX catalog_override_tier_definition_idx ON catalog_override_usage_definition(tenant_record_id, currency);

DROP TABLE IF EXISTS catalog_override_block_definition;
create table catalog_override_block_definition
(
record_id serial unique,
parent_unit_name varchar(255) NOT NULL,
size decimal(15,9) NOT NULL,
max decimal(15,9) NULL,
currency varchar(3) NOT NULL,
price decimal(15,9) NOT NULL,
effective_date datetime NOT NULL,
created_date datetime NOT NULL,
created_by varchar(50) NOT NULL,
tenant_record_id bigint /*! unsigned */ not null default 0,
PRIMARY KEY(record_id)
);
CREATE INDEX catalog_override_block_definition_idx ON catalog_override_block_definition(tenant_record_id, parent_unit_name, currency);


DROP TABLE IF EXISTS catalog_override_phase_usage;
create table catalog_override_phase_usage
(
record_id serial unique,
usage_number int /*! unsigned */,
usage_def_record_id  bigint /*! unsigned */ not null,
target_phase_def_record_id bigint /*! unsigned */ not null,
created_date datetime NOT NULL,
created_by varchar(50) NOT NULL,
tenant_record_id bigint /*! unsigned */ not null default 0,
PRIMARY KEY(record_id)
);
CREATE INDEX catalog_override_phase_usage_idx ON catalog_override_phase_usage(tenant_record_id, usage_number, usage_def_record_id);

DROP TABLE IF EXISTS catalog_override_usage_tier;
create table catalog_override_usage_tier
(
record_id serial unique,
tier_number int /*! unsigned */,
tier_def_record_id bigint /*! unsigned */ not null,
target_usage_def_record_id bigint /*! unsigned */ not null,
created_date datetime NOT NULL,
created_by varchar(50) NOT NULL,
tenant_record_id bigint /*! unsigned */ not null default 0,
PRIMARY KEY(record_id)
);
CREATE INDEX catalog_override_usage_tier_idx ON catalog_override_usage_tier(tenant_record_id, tier_number, tier_def_record_id);


DROP TABLE IF EXISTS catalog_override_tier_block;
create table catalog_override_tier_block
(
record_id serial unique,
block_number int /*! unsigned */,
block_def_record_id bigint /*! unsigned */ not null,
target_tier_def_record_id bigint /*! unsigned */ not null,
created_date datetime NOT NULL,
created_by varchar(50) NOT NULL,
tenant_record_id bigint /*! unsigned */ NOT NULL default 0,
PRIMARY KEY(record_id)
);
CREATE INDEX catalog_override_tier_block_idx ON catalog_override_tier_block(tenant_record_id, block_number, block_def_record_id);

-- V20170915165117__external_key_not_null
alter table accounts modify external_key varchar(255) NOT NULL;
alter table account_history modify external_key varchar(255) NOT NULL;

-- V20170920200757__bundle_external_key
drop index bundles_key on bundles;
create unique index bundles_external_key on bundles(external_key, tenant_record_id);

-- V20171011170256__tag_definition_object_types
alter table tag_definitions add column applicable_object_types varchar(500) after name;
alter table tag_definition_history add column applicable_object_types varchar(500) after name;

-- V20171108184350__reference_time
alter table accounts add column reference_time datetime NOT NULL DEFAULT '1970-01-01 00:00:00' after payment_method_id;
alter table account_history add column reference_time datetime NOT NULL DEFAULT '1970-01-01 00:00:00' after payment_method_id;
update accounts set reference_time = created_date;
update account_history set reference_time = created_date;

-- V20180123114605__invoice_item_quantity_item_details
alter table invoice_items add column quantity int after linked_item_id;
alter table invoice_items add column item_details text after quantity;

-- V20180202093043__increase_field_event_json
alter table bus_events modify event_json text not null;
alter table bus_events_history modify event_json text not null;
alter table notifications modify event_json text not null;
alter table notifications_history modify event_json text not null;

-- V20180202093543__increase_field_event_json
alter table bus_ext_events modify event_json text not null;
alter table bus_ext_events_history modify event_json text not null;

-- V20180501155616__invoice_item_product_name
alter table invoice_items add column product_name varchar(255) after description;

-- V20180625172110__account_is_notified_for_invoices
alter table accounts drop column is_notified_for_invoices;
alter table account_history drop column is_notified_for_invoices;

-- V20181129164135__tracking_ids
DROP TABLE IF EXISTS invoice_tracking_ids;
CREATE TABLE invoice_tracking_ids (
    record_id serial unique,
    id varchar(36) NOT NULL,
    tracking_id varchar(128) NOT NULL,
    invoice_id varchar(36) NOT NULL,
    subscription_id varchar(36),
    unit_type varchar(255) NOT NULL,
    record_date date NOT NULL,
    created_by varchar(50) NOT NULL,
    created_date datetime NOT NULL,
    account_record_id bigint /*! unsigned */ not null,
    tenant_record_id bigint /*! unsigned */ not null default 0,
    PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE INDEX invoice_tracking_tenant_account_date_idx ON invoice_tracking_ids(tenant_record_id, account_record_id, record_date);

-- V20181205101746__tenant_tenant_kvs_trid_key_idx
CREATE INDEX tenant_kvs_trid_key ON tenant_kvs(tenant_record_id, tenant_key);
DROP INDEX tenant_kvs_key ON tenant_kvs;

-- V20190121141325__tracking_ids_is_active
alter table invoice_tracking_ids add column is_active boolean default true after record_date;
alter table invoice_tracking_ids add column updated_by varchar(50) NOT NULL after created_date;
alter table invoice_tracking_ids add column updated_date datetime NOT NULL DEFAULT '1970-01-01 00:00:00' after updated_by;
create index invoice_tracking_invoice_id_idx on invoice_tracking_ids(invoice_id);

-- V20190220133701__subscription_history_tables
DROP TABLE IF EXISTS subscription_event_history;
CREATE TABLE subscription_event_history (
    record_id serial unique,
    id varchar(36) NOT NULL,
    target_record_id bigint /*! unsigned */ not null,
    event_type varchar(15) NOT NULL,
    user_type varchar(25) DEFAULT NULL,
    effective_date datetime NOT NULL,
    subscription_id varchar(36) NOT NULL,
    plan_name varchar(255) DEFAULT NULL,
    phase_name varchar(255) DEFAULT NULL,
    price_list_name varchar(64) DEFAULT NULL,
    billing_cycle_day_local int DEFAULT NULL,
    is_active boolean default true,
    change_type varchar(6) NOT NULL,
    created_by varchar(50) NOT NULL,
    created_date datetime NOT NULL,
    updated_by varchar(50) NOT NULL,
    updated_date datetime NOT NULL,
    account_record_id bigint /*! unsigned */ not null,
    tenant_record_id bigint /*! unsigned */ not null default 0,
    PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE INDEX subscription_event_history_target_record_id ON subscription_event_history(target_record_id);
CREATE INDEX subscription_event_history_tenant_record_id ON subscription_event_history(tenant_record_id);


DROP TABLE IF EXISTS subscription_history;
CREATE TABLE subscription_history (
    record_id serial unique,
    id varchar(36) NOT NULL,
    target_record_id bigint /*! unsigned */ not null,
    bundle_id varchar(36) NOT NULL,
    category varchar(32) NOT NULL,
    start_date datetime NOT NULL,
    bundle_start_date datetime NOT NULL,
    charged_through_date datetime DEFAULT NULL,
    migrated bool NOT NULL default FALSE,
    change_type varchar(6) NOT NULL,
    created_by varchar(50) NOT NULL,
    created_date datetime NOT NULL,
    updated_by varchar(50) NOT NULL,
    updated_date datetime NOT NULL,
    account_record_id bigint /*! unsigned */ not null,
    tenant_record_id bigint /*! unsigned */ not null default 0,
    PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE INDEX subscription_history_target_record_id ON subscription_history(target_record_id);
CREATE INDEX subscription_history_tenant_record_id ON subscription_history(tenant_record_id);

DROP TABLE IF EXISTS bundle_history;
CREATE TABLE bundle_history (
    record_id serial unique,
    id varchar(36) NOT NULL,
    target_record_id bigint /*! unsigned */ not null,
    external_key varchar(255) NOT NULL,
    account_id varchar(36) NOT NULL,
    last_sys_update_date datetime,
    original_created_date datetime NOT NULL,
    change_type varchar(6) NOT NULL,
    created_by varchar(50) NOT NULL,
    created_date datetime NOT NULL,
    updated_by varchar(50) NOT NULL,
    updated_date datetime NOT NULL,
    account_record_id bigint /*! unsigned */ not null,
    tenant_record_id bigint /*! unsigned */ not null default 0,
    PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE INDEX bundle_history_target_record_id ON bundle_history(target_record_id);
CREATE INDEX bundle_history_tenant_record_id ON bundle_history(tenant_record_id);

-- V20190221163050__blocking_state_history_tables
DROP TABLE IF EXISTS blocking_state_history;
CREATE TABLE blocking_state_history (
    record_id serial unique,
    id varchar(36) NOT NULL,
    target_record_id bigint /*! unsigned */ not null,
    blockable_id varchar(36) NOT NULL,
    type varchar(20) NOT NULL,
    state varchar(50) NOT NULL,
    service varchar(20) NOT NULL,
    block_change bool NOT NULL,
    block_entitlement bool NOT NULL,
    block_billing bool NOT NULL,
    effective_date datetime NOT NULL,
    is_active boolean default true,
    change_type varchar(6) NOT NULL,
    created_date datetime NOT NULL,
    created_by varchar(50) NOT NULL,
    updated_date datetime DEFAULT NULL,
    updated_by varchar(50) DEFAULT NULL,
    account_record_id bigint /*! unsigned */ not null,
    tenant_record_id bigint /*! unsigned */ not null default 0,
    PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE INDEX blocking_state_history_target_record_id ON blocking_state_history(target_record_id);
CREATE INDEX blocking_state_history_tenant_record_id ON blocking_state_history(tenant_record_id);

-- V20190222142211__invoice_history_tables
DROP TABLE IF EXISTS invoice_tracking_id_history;
CREATE TABLE invoice_tracking_id_history (
    record_id serial unique,
    id varchar(36) NOT NULL,
    target_record_id bigint /*! unsigned */ not null,
    tracking_id varchar(128) NOT NULL,
    invoice_id varchar(36) NOT NULL,
    subscription_id varchar(36),
    unit_type varchar(255) NOT NULL,
    record_date date NOT NULL,
    is_active boolean default true,
    change_type varchar(6) NOT NULL,
    created_by varchar(50) NOT NULL,
    created_date datetime NOT NULL,
    updated_by varchar(50) NOT NULL,
    updated_date datetime NOT NULL,
    account_record_id bigint /*! unsigned */ not null,
    tenant_record_id bigint /*! unsigned */ not null default 0,
    PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE INDEX invoice_tracking_id_history_target_record_id ON invoice_tracking_id_history(target_record_id);
CREATE INDEX invoice_tracking_id_history_tenant_record_id ON invoice_tracking_id_history(tenant_record_id);

DROP TABLE IF EXISTS invoice_item_history;
CREATE TABLE invoice_item_history (
    record_id serial unique,
    id varchar(36) NOT NULL,
    target_record_id bigint /*! unsigned */ not null,
    type varchar(24) NOT NULL,
    invoice_id varchar(36) NOT NULL,
    account_id varchar(36) NOT NULL,
    child_account_id varchar(36),
    bundle_id varchar(36),
    subscription_id varchar(36),
    description varchar(255),
    product_name varchar(255),
    plan_name varchar(255),
    phase_name varchar(255),
    usage_name varchar(255),
    start_date date,
    end_date date,
    amount numeric(15,9) NOT NULL,
    rate numeric(15,9) NULL,
    currency varchar(3) NOT NULL,
    linked_item_id varchar(36),
    quantity int,
    item_details text,
    change_type varchar(6) NOT NULL,
    created_by varchar(50) NOT NULL,
    created_date datetime NOT NULL,
    account_record_id bigint /*! unsigned */ not null,
    tenant_record_id bigint /*! unsigned */ not null default 0,
    PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE INDEX invoice_item_history_target_record_id ON invoice_item_history(target_record_id);
CREATE INDEX invoice_item_history_tenant_record_id ON invoice_item_history(tenant_record_id);


DROP TABLE IF EXISTS invoice_history;
CREATE TABLE invoice_history (
    record_id serial unique,
    id varchar(36) NOT NULL,
    target_record_id bigint /*! unsigned */ not null,
    account_id varchar(36) NOT NULL,
    invoice_date date NOT NULL,
    target_date date,
    currency varchar(3) NOT NULL,
    status varchar(15) NOT NULL DEFAULT 'COMMITTED',
    migrated bool NOT NULL,
    parent_invoice bool NOT NULL DEFAULT FALSE,
    change_type varchar(6) NOT NULL,
    created_by varchar(50) NOT NULL,
    created_date datetime NOT NULL,
    account_record_id bigint /*! unsigned */ not null,
    tenant_record_id bigint /*! unsigned */ not null default 0,
    PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE INDEX invoice_history_target_record_id ON invoice_history(target_record_id);
CREATE INDEX invoice_history_tenant_record_id ON invoice_history(tenant_record_id);

DROP TABLE IF EXISTS invoice_payment_history;
CREATE TABLE invoice_payment_history (
    record_id serial unique,
    id varchar(36) NOT NULL,
    target_record_id bigint /*! unsigned */ not null,
    type varchar(24) NOT NULL,
    invoice_id varchar(36) NOT NULL,
    payment_id varchar(36),
    payment_date datetime NOT NULL,
    amount numeric(15,9) NOT NULL,
    currency varchar(3) NOT NULL,
    processed_currency varchar(3) NOT NULL,
    payment_cookie_id varchar(255) DEFAULT NULL,
    linked_invoice_payment_id varchar(36) DEFAULT NULL,
    success bool DEFAULT true,
    change_type varchar(6) NOT NULL,
    created_by varchar(50) NOT NULL,
    created_date datetime NOT NULL,
    account_record_id bigint /*! unsigned */ not null,
    tenant_record_id bigint /*! unsigned */ not null default 0,
    PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE INDEX invoice_payment_history_target_record_id ON invoice_payment_history(target_record_id);
CREATE INDEX invoice_payment_history_tenant_record_id ON invoice_payment_history(tenant_record_id);

-- V20190712115638__subscription_external_key
alter table subscriptions add column external_key varchar(255) after bundle_id;
update subscriptions set external_key=id;
alter table subscriptions modify external_key varchar(255) not null;
create unique index subscriptions_external_key on subscriptions(external_key, tenant_record_id);

alter table subscription_history add column external_key varchar(255) after bundle_id;
update subscription_history set external_key=id;
alter table subscription_history modify external_key varchar(255) not null;

-- V20190717122114__rename_table_invoice_payment_control_plugin_auto_pay_off
drop index _invoice_payment_control_plugin_auto_pay_off_account on _invoice_payment_control_plugin_auto_pay_off;
alter table _invoice_payment_control_plugin_auto_pay_off rename invoice_payment_control_plugin_auto_pay_off;
create INDEX invoice_payment_control_plugin_auto_pay_off_account on invoice_payment_control_plugin_auto_pay_off(account_id);

-- V20190717131708__rename_size_field
alter table catalog_override_block_definition change size bsize decimal(15,9) NOT NULL;

-- V20190717161524__recreate_index_tier_definition
drop index catalog_override_tier_definition_idx on catalog_override_usage_definition;
create index catalog_override_tier_definition_idx on catalog_override_tier_definition(tenant_record_id, currency);

-- V20190717161645__recreate_index_tag_history
drop index tag_history_by_object on tags;
create index tag_history_by_object on tag_history(object_id);

-- V20190816155743__invoice_tracking_id_index
CREATE INDEX invoice_tracking_id_idx ON invoice_tracking_ids(id);

-- V20190821144001__custom_field_name_value_index
alter table custom_fields add index custom_fields_name_value(field_name, field_value);

-- V20190904150944__invoice_item_catalog_effective_date
alter table invoice_items add column catalog_effective_date datetime after usage_name;
alter table invoice_item_history add column catalog_effective_date datetime after usage_name;

-- V20190924111732__invoice_billing_events
CREATE TABLE invoice_billing_events (
    record_id serial unique,
    id varchar(36) NOT NULL,
    invoice_id varchar(36) NOT NULL,
	billing_events blob NOT NULL,
    created_by varchar(50) NOT NULL,
    created_date datetime NOT NULL,
    account_record_id bigint /*! unsigned */ not null,
    tenant_record_id bigint /*! unsigned */ not null default 0,
    PRIMARY KEY(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE UNIQUE INDEX invoice_billing_events_invoice_id ON invoice_billing_events(invoice_id);

-- V20191001160927__remove_invoice_payment_control_plugin_auto_pay_off_payment_method_id
alter table invoice_payment_control_plugin_auto_pay_off drop column payment_method_id;

-- V20200916171757__recreate_users_index_as_unique
drop index users_username on users;
create unique index users_username ON users(username);