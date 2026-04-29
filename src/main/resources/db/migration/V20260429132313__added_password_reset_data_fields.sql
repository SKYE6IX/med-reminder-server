-- modify "users" table
ALTER TABLE "users" ADD COLUMN "password_reset_issued_at" timestamp NULL, ADD COLUMN "password_reset_redeem_at" timestamp NULL, ADD COLUMN "password_reset_token" character varying(255) NULL;
