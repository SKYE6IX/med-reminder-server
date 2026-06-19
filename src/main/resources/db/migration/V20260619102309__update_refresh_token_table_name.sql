-- create "refresh_tokens" table
CREATE TABLE "refresh_tokens" (
  "id" character varying(255) NOT NULL,
  "created_at" timestamp NULL,
  "expired_at" timestamptz NULL,
  "hash_token" character varying(255) NOT NULL,
  "revoked" boolean NOT NULL,
  "updated_at" timestamp NULL,
  "user_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "uk_19wuovt3jld65o1qxph8p3sph" UNIQUE ("hash_token"),
  CONSTRAINT "fkhspjwa36lvj54jpx0kuyx4b33" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
-- drop "refresh_token" table
DROP TABLE "refresh_token";
