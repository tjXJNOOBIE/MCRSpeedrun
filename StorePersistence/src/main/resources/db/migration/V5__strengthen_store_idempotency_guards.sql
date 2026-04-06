create unique index if not exists idx_payment_transaction_provider_reference_unique
    on payment_transaction(provider, provider_reference)
    where provider_reference is not null;

create unique index if not exists idx_entitlement_order_item_benefit_unique
    on entitlement(order_item_id, benefit_type, target_system, target_key, target_value);

create index if not exists idx_fulfillment_job_entitlement_created
    on fulfillment_job(entitlement_id, created_at desc);
