-- reverse: modify "schedule_events" table
ALTER TABLE "schedule_events" ALTER COLUMN "dosage" TYPE double precision, ALTER COLUMN "dosage" SET NOT NULL;
-- reverse: modify "medication_schedules" table
ALTER TABLE "medication_schedules" ALTER COLUMN "taken_quantity" TYPE double precision, ALTER COLUMN "dose_quantity" TYPE double precision;
-- reverse: modify "medication_packs" table
ALTER TABLE "medication_packs" ALTER COLUMN "total_quantity" TYPE double precision, ALTER COLUMN "current_quantity" TYPE double precision;
