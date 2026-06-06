-- reverse: modify "subscriptions" table
ALTER TABLE "subscriptions" DROP COLUMN "next_retry_billing_at";
-- reverse: rename a column from "auto_renewal" to "is_billing_retry"
ALTER TABLE "subscriptions" RENAME COLUMN "is_billing_retry" TO "auto_renewal";
