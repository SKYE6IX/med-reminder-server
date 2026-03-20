-- reverse: modify "schedule_events" table
ALTER TABLE "schedule_events" ADD COLUMN "last_expanded_until" timestamp NULL;
-- reverse: modify "medication_schedules" table
ALTER TABLE "medication_schedules" DROP COLUMN "last_expanded_until";
