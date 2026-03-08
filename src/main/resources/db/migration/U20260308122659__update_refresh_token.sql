-- reverse: modify "refresh_token" table
ALTER TABLE "refresh_token" DROP CONSTRAINT "fk84fbivrkjeeot65m8b63nlxcm", DROP COLUMN "updated_at", DROP COLUMN "created_at", ADD CONSTRAINT "fk84fbivrkjeeot65m8b63nlxcm" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;
