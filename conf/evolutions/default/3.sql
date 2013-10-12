# Budgets schema

# --- !Ups

ALTER TABLE budgets
    ADD category_id integer;

# --- !Downs

ALTER TABLE budgets
    DROP COLUMN category_id;
