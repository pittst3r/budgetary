# Budgets schema

# --- !Ups

CREATE SEQUENCE budget_id_seq;
CREATE TABLE budgets (
    id integer NOT NULL DEFAULT nextval('budget_id_seq'),
    updated_at TIMESTAMP,
    name varchar(255),
    amount double
);

# --- !Downs

DROP TABLE budgets;
DROP SEQUENCE budget_id_seq;
