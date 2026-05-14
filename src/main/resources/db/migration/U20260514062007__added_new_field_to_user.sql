-- reverse: modify "users" table
ALTER TABLE "users" DROP COLUMN "provider", DROP COLUMN "provider_id", DROP COLUMN "last_login_at";
