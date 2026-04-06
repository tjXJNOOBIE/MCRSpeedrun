alter table checkout_session
    add column if not exists order_number varchar(64);

update checkout_session
set order_number = concat('LEGACY-', id)
where order_number is null;

alter table checkout_session
    alter column order_number set not null;

create unique index if not exists idx_checkout_session_order_number
    on checkout_session(order_number);
