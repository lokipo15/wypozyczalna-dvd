alter table user_account
    add column if not exists customer_id bigint;

alter table user_account
    add constraint fk_user_account_customer foreign key (customer_id) references customer(id) on delete set null;

create index if not exists idx_user_account_customer on user_account(customer_id);

