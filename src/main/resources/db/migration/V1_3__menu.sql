create schema menu;

create table if not exists menu.coffee
(
    name varchar primary key,
    brew_time int not null
);

create table if not exists menu.pizza
(
    name varchar primary key
);

create table if not exists menu.pizza_ingredients
(
    pizza varchar not null,
    ingredient varchar not null,
    amount int not null check (amount > 0),
    foreign key (pizza) references menu.pizza (name),
    foreign key (ingredient) references storage.ingredient (name),
    constraint unique_pizza_ingredient unique (pizza, ingredient)
);
