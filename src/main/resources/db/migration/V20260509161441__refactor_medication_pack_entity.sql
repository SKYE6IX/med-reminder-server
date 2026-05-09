-- rename a column from "added_at" to "ended_at"
ALTER TABLE "medication_packs" RENAME COLUMN "added_at" TO "ended_at";
-- modify "medication_packs" table
ALTER TABLE "medication_packs" ADD COLUMN "started_at" timestamp NULL, ADD COLUMN "status" character varying(255) NULL;
