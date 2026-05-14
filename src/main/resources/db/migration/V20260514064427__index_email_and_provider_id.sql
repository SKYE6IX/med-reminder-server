-- create index "idx_user_email" to table: "users"
CREATE INDEX "idx_user_email" ON "users" ("email");
-- create index "idx_user_provider_id" to table: "users"
CREATE INDEX "idx_user_provider_id" ON "users" ("provider_id");
