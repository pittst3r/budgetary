# Categories schema

# --- !Ups

ALTER TABLE categories
    ADD account_id integer;

# --- !Downs

ALTER TABLE categories
    DROP COLUMN account_id;
