-- modify "users" table
ALTER TABLE "users" ADD COLUMN "last_login_at" timestamp NULL, ADD COLUMN "provider_id" character varying(255) NULL, ADD COLUMN "provider" character varying(255) NULL;
