create schema storage;

create table if not exists storage.ingredient
(
    name   varchar primary key,
    amount int not null check (amount >= 0)
);