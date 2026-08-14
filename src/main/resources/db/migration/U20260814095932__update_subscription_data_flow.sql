-- reverse: drop "billings" table
CREATE TABLE "billings" (
  "id" character varying(255) NOT NULL,
  "amount" numeric(38,2) NULL,
  "created_at" timestamp NULL,
  "method" character varying(255) NULL,
  "paid_at" timestamp NULL,
  "status" character varying(255) NULL,
  "updated_at" timestamp NULL,
  "subscription_period_id" character varying(255) NULL,
  "user_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fk4eip6q0an8hjsblnlmwnf993h" FOREIGN KEY ("subscription_period_id") REFERENCES "subscription_periods" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "fk8cq3bwt04rk4snkaup5yl85ow" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
-- reverse: modify "users" table
ALTER TABLE "users" ADD COLUMN "payment_method_id" character varying(255) NULL;
-- reverse: modify "subscriptions" table
ALTER TABLE "subscriptions" ADD COLUMN "next_retry_billing_at" timestamp NULL, ADD COLUMN "time_zone" character varying(255) NULL, ADD COLUMN "is_billing_retry" boolean NULL;
-- reverse: rename a column from "billing_cycle" to "store"
ALTER TABLE "subscriptions" RENAME COLUMN "store" TO "billing_cycle";
-- reverse: modify "subscription_periods" table
ALTER TABLE "subscription_periods" ADD COLUMN "payment_status" character varying(255) NULL;
-- reverse: rename a column from "start_time" to "start_at"
ALTER TABLE "subscription_periods" RENAME COLUMN "start_at" TO "start_time";
-- reverse: rename a column from "end_time" to "end_at"
ALTER TABLE "subscription_periods" RENAME COLUMN "end_at" TO "end_time";
-- reverse: modify "medication_schedules" table
ALTER TABLE "medication_schedules" ADD COLUMN "time_zone" character varying(255) NULL;
