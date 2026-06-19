-- reverse: drop "refresh_token" table
CREATE TABLE "refresh_token" (
  "id" character varying(255) NOT NULL,
  "expired_at" timestamptz NULL,
  "hash_token" character varying(255) NOT NULL,
  "revoked" boolean NOT NULL,
  "user_id" character varying(255) NULL,
  "created_at" timestamp NULL,
  "updated_at" timestamp NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "uk_6mg43wbxe9ohu2esqmp3yx7j6" UNIQUE ("hash_token"),
  CONSTRAINT "fk84fbivrkjeeot65m8b63nlxcm" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
-- reverse: create "refresh_tokens" table
DROP TABLE "refresh_tokens";
