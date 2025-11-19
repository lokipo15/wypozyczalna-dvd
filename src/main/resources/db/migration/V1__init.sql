create type copy_status as enum ('AVAILABLE', 'RENTED', 'DAMAGED');
create type user_role as enum ('ADMIN', 'CLERK');

create table title (
    id              bigserial primary key,
    name            varchar(255) not null,
    year            integer,
    genre           varchar(100),
    description     text,
    tvdb_id         varchar(100),
    created_at      timestamptz not null default timezone('utc', now()),
    updated_at      timestamptz not null default timezone('utc', now())
);
create index idx_title_name on title(lower(name));

create table dvd_copy (
    id              bigserial primary key,
    title_id        bigint not null references title(id) on delete restrict,
    inventory_code  varchar(100) not null,
    status          copy_status not null default 'AVAILABLE',
    version         bigint not null default 0,
    created_at      timestamptz not null default timezone('utc', now()),
    updated_at      timestamptz not null default timezone('utc', now()),
    unique (inventory_code)
);
create index idx_dvd_copy_title_status on dvd_copy(title_id, status);

create table customer (
    id              bigserial primary key,
    first_name      varchar(100) not null,
    last_name       varchar(100) not null,
    email           varchar(255) not null,
    phone           varchar(50),
    active          boolean not null default true,
    created_at      timestamptz not null default timezone('utc', now()),
    updated_at      timestamptz not null default timezone('utc', now())
);
create unique index ux_customer_email on customer(lower(email));

create table rental (
    id              bigserial primary key,
    customer_id     bigint not null references customer(id) on delete restrict,
    copy_id         bigint not null references dvd_copy(id) on delete restrict,
    rented_at       timestamptz not null,
    due_at          timestamptz,
    returned_at     timestamptz
);
create index idx_rental_customer_rented_at on rental(customer_id, rented_at desc);
create index idx_rental_returned_null on rental(returned_at) where returned_at is null;
create unique index ux_rental_copy_active on rental(copy_id) where returned_at is null;

create table user_account (
    id              bigserial primary key,
    username        varchar(100) not null,
    display_name    varchar(255) not null,
    role            user_role not null,
    active          boolean not null default true,
    created_at      timestamptz not null default timezone('utc', now()),
    updated_at      timestamptz not null default timezone('utc', now())
);
create unique index ux_user_account_username on user_account(lower(username));
