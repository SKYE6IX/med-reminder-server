-- create "users" table
CREATE TABLE "users" (
  "id" bytea NOT NULL,
  "date_of_birth" date NULL,
  "email" character varying(255) NULL,
  "gender" character varying(255) NULL,
  "hash_password" character varying(255) NULL,
  "name" character varying(255) NULL,
  PRIMARY KEY ("id")
);
