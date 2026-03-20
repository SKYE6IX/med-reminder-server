-- reverse: modify "schedule_events" table
ALTER TABLE "schedule_events" DROP CONSTRAINT "fk8f3881rav9om6owbkn08hfkpd", ADD CONSTRAINT "fkjmphnk2nqp7m027lkchxokuwk" FOREIGN KEY ("medication_id") REFERENCES "medication_schedules" ("id") ON UPDATE NO ACTION ON DELETE CASCADE;
-- reverse: rename a column from "medication_id" to "medication_schedule_id"
ALTER TABLE "schedule_events" RENAME COLUMN "medication_schedule_id" TO "medication_id";
