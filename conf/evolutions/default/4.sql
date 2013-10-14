# Accounts schema

# --- !Ups

CREATE SEQUENCE account_id_seq;
CREATE TABLE accounts (
    id integer NOT NULL DEFAULT nextval('account_id_seq'),
    updated_at TIMESTAMP,
    token varchar(255)
);

# --- !Downs

DROP TABLE accounts;
DROP SEQUENCE account_id_seq;
