-- create "schedule_events" table
CREATE TABLE "schedule_events" (
  "id" character varying(255) NOT NULL,
  "created_at" timestamp NULL,
  "dosage" double precision NOT NULL,
  "last_expanded_until" timestamp NULL,
  "schedule_at" timestamp NULL,
  "status" character varying(255) NULL,
  "taken_at" timestamp NULL,
  "updated_at" timestamp NULL,
  "medication_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fkjmphnk2nqp7m027lkchxokuwk" FOREIGN KEY ("medication_id") REFERENCES "medication_schedules" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
