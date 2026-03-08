-- create "refresh_token" table
CREATE TABLE "refresh_token" (
  "id" character varying(255) NOT NULL,
  "expired_at" timestamptz NULL,
  "hash_token" character varying(255) NULL,
  "revoked" boolean NOT NULL,
  "user_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fk84fbivrkjeeot65m8b63nlxcm" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
