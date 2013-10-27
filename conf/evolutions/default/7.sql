# Allocations schema

# --- !Ups

CREATE SEQUENCE allocation_id_seq;
CREATE TABLE allocations (
    id integer NOT NULL DEFAULT nextval('allocation_id_seq'),
    updated_at TIMESTAMP,
    name varchar(255),
    amount double,
    account_id integer
);

# --- !Downs

DROP TABLE allocations;
DROP SEQUENCE allocation_id_seq;
