create schema accounting;

create table if not exists accounting.orders
(
    id      identity,
    type    varchar not null check (type in ('COFFEE', 'PIZZA')),
    name    varchar not null,
    cash    numeric not null,
    price   numeric not null,
    status  varchar not null check (status in ('IN_PROGRESS', 'READY', 'DECLINED')),
    comment varchar,
    created timestamp default current_timestamp,
    updated timestamp default current_timestamp
);