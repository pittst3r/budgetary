# Categories schema

# --- !Ups

CREATE SEQUENCE category_id_seq;
CREATE TABLE categories (
    id integer NOT NULL DEFAULT nextval('category_id_seq'),
    updated_at TIMESTAMP,
    name varchar(255)
);

# --- !Downs

DROP TABLE categories;
DROP SEQUENCE category_id_seq;
