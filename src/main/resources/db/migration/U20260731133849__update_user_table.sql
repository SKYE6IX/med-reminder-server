-- reverse: modify "users" table
ALTER TABLE "users" DROP CONSTRAINT "uk_hb562dwvv5g7dwrn81nvrdp79", DROP COLUMN "time_zone";
-- reverse: drop index "idx_user_provider_id" from table: "users"
CREATE INDEX "idx_user_provider_id" ON "users" ("provider_id");
-- reverse: drop index "idx_user_email" from table: "users"
CREATE INDEX "idx_user_email" ON "users" ("email");
