-- modify "medication_schedules" table
ALTER TABLE "medication_schedules" DROP COLUMN "time_zone";
-- rename a column from "end_time" to "end_at"
ALTER TABLE "subscription_periods" RENAME COLUMN "end_time" TO "end_at";
-- rename a column from "start_time" to "start_at"
ALTER TABLE "subscription_periods" RENAME COLUMN "start_time" TO "start_at";
-- modify "subscription_periods" table
ALTER TABLE "subscription_periods" DROP COLUMN "payment_status";
-- rename a column from "billing_cycle" to "store"
ALTER TABLE "subscriptions" RENAME COLUMN "billing_cycle" TO "store";
-- modify "subscriptions" table
ALTER TABLE "subscriptions" DROP COLUMN "is_billing_retry", DROP COLUMN "time_zone", DROP COLUMN "next_retry_billing_at";
-- modify "users" table
ALTER TABLE "users" DROP COLUMN "payment_method_id";
-- drop "billings" table
DROP TABLE "billings";
