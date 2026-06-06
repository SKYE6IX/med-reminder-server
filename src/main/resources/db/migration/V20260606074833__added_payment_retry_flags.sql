-- rename a column from "auto_renewal" to "is_billing_retry"
ALTER TABLE "subscriptions" RENAME COLUMN "auto_renewal" TO "is_billing_retry";
-- modify "subscriptions" table
ALTER TABLE "subscriptions" ADD COLUMN "next_retry_billing_at" timestamp NULL;
