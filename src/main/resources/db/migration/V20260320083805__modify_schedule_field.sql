-- modify "medication_schedules" table
ALTER TABLE "medication_schedules" ADD COLUMN "last_expanded_until" timestamp NULL;
-- modify "schedule_events" table
ALTER TABLE "schedule_events" DROP COLUMN "last_expanded_until";
