-- modify "refresh_token" table
ALTER TABLE "refresh_token" DROP CONSTRAINT "fk84fbivrkjeeot65m8b63nlxcm", ADD COLUMN "created_at" timestamp NULL, ADD COLUMN "updated_at" timestamp NULL, ADD CONSTRAINT "fk84fbivrkjeeot65m8b63nlxcm" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE CASCADE;
