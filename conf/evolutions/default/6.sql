# Accounts schema

# --- !Ups

ALTER TABLE accounts
    ADD monthly_income double;

# --- !Downs

ALTER TABLE accounts
    DROP COLUMN monthly_income;
