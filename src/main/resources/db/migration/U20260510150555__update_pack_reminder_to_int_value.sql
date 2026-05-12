-- reverse: modify "medication_packs" table
ALTER TABLE "medication_packs" DROP COLUMN "reminder_days", ADD COLUMN "notify_rule" character varying(255) NULL;
