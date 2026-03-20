-- rename a column from "medication_id" to "medication_schedule_id"
ALTER TABLE "schedule_events" RENAME COLUMN "medication_id" TO "medication_schedule_id";
-- modify "schedule_events" table
ALTER TABLE "schedule_events" DROP CONSTRAINT "fkjmphnk2nqp7m027lkchxokuwk", ADD CONSTRAINT "fk8f3881rav9om6owbkn08hfkpd" FOREIGN KEY ("medication_schedule_id") REFERENCES "medication_schedules" ("id") ON UPDATE NO ACTION ON DELETE CASCADE;
