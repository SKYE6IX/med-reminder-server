-- reverse: modify "medication_packs" table
ALTER TABLE "medication_packs" DROP COLUMN "status", DROP COLUMN "started_at";
-- reverse: rename a column from "added_at" to "ended_at"
ALTER TABLE "medication_packs" RENAME COLUMN "ended_at" TO "added_at";
