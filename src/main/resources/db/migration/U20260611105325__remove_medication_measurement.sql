-- reverse: drop "measurement_units" table
CREATE TABLE "measurement_units" (
  "id" character varying(255) NOT NULL,
  "created_at" timestamp NULL,
  "name" character varying(255) NULL,
  "symbol" character varying(255) NULL,
  "updated_at" timestamp NULL,
  "medication_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fkctjg8vp2gdfbcx5t3xu0dt6df" FOREIGN KEY ("medication_id") REFERENCES "medications" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
-- reverse: modify "medications" table
ALTER TABLE "medications" DROP COLUMN "measurement";
