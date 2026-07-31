-- drop index "idx_user_email" from table: "users"
DROP INDEX "idx_user_email";
-- drop index "idx_user_provider_id" from table: "users"
DROP INDEX "idx_user_provider_id";
-- modify "users" table
ALTER TABLE "users" ADD COLUMN "time_zone" character varying(255) NULL, ADD CONSTRAINT "uk_hb562dwvv5g7dwrn81nvrdp79" UNIQUE ("provider_id");
