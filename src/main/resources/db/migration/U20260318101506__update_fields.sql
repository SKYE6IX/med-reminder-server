-- reverse: modify "medication_schedules" table
ALTER TABLE "medication_schedules" DROP COLUMN "start_date";
-- reverse: rename a column from "start_at" to "start_time"
ALTER TABLE "medication_schedules" RENAME COLUMN "start_time" TO "start_at";
-- reverse: modify "medication_profiles" table
ALTER TABLE "medication_profiles" ADD COLUMN "start_at" timestamp NULL;
