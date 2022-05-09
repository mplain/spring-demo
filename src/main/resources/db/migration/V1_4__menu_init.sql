insert into storage.ingredient
values ('dough', 20),
       ('eggs', 20),
       ('cheese', 20),
       ('tomato', 20),
       ('mushroom', 20),
       ('olive', 20),
       ('arugula', 20),
       ('asparagus', 20),
       ('bacon', 20),
       ('beef jerky', 20),
       ('meatball', 20),
       ('salami', 20);

insert into menu.coffee (name, brew_time)
values ('espresso', 5),
       ('americano', 5),
       ('cappuccino', 6),
       ('latte', 6);

insert into menu.pizza (name)
values ('carbonara'),
       ('marinara'),
       ('sardinia'),
       ('valtellina'),
       ('rustica');

insert into menu.pizza_ingredients (pizza, ingredient, amount)
values ('carbonara', 'dough', 1),
       ('carbonara', 'eggs', 1),
       ('carbonara', 'cheese', 2),
       ('carbonara', 'bacon', 2),
       ('marinara', 'dough', 1),
       ('marinara', 'tomato', 2),
       ('marinara', 'olive', 3),
       ('sardinia', 'dough', 1),
       ('sardinia', 'cheese', 3),
       ('sardinia', 'olive', 1),
       ('sardinia', 'salami', 3),
       ('valtellina', 'dough', 1),
       ('valtellina', 'cheese', 2),
       ('valtellina', 'arugula', 1),
       ('valtellina', 'beef jerky', 1),
       ('rustica', 'dough', 1),
       ('rustica', 'tomato', 1),
       ('rustica', 'mushroom', 3),
       ('rustica', 'asparagus', 1),
       ('rustica', 'meatball', 1);
