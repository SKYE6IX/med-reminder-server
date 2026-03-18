-- modify "medication_profiles" table
ALTER TABLE "medication_profiles" DROP COLUMN "start_at";
-- rename a column from "start_at" to "start_time"
ALTER TABLE "medication_schedules" RENAME COLUMN "start_at" TO "start_time";
-- modify "medication_schedules" table
ALTER TABLE "medication_schedules" ADD COLUMN "start_date" date NULL;
