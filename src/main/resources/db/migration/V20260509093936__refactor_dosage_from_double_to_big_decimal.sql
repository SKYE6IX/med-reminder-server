-- modify "medication_packs" table
ALTER TABLE "medication_packs" ALTER COLUMN "current_quantity" TYPE numeric(38,2), ALTER COLUMN "total_quantity" TYPE numeric(38,2);
-- modify "medication_schedules" table
ALTER TABLE "medication_schedules" ALTER COLUMN "dose_quantity" TYPE numeric(38,2), ALTER COLUMN "taken_quantity" TYPE numeric(38,2);
-- modify "schedule_events" table
ALTER TABLE "schedule_events" ALTER COLUMN "dosage" TYPE numeric(38,2), ALTER COLUMN "dosage" DROP NOT NULL;
