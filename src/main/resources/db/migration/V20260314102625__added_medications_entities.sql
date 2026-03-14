-- create "medication_profiles" table
CREATE TABLE "medication_profiles" (
  "id" character varying(255) NOT NULL,
  "created_at" timestamp NULL,
  "is_active" boolean NULL,
  "note" character varying(255) NULL,
  "start_at" timestamp NULL,
  "updated_at" timestamp NULL,
  "profile_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fkn6xjo4dg2cbnx2ph5s3aryt5d" FOREIGN KEY ("profile_id") REFERENCES "profiles" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
-- create "medications" table
CREATE TABLE "medications" (
  "id" character varying(255) NOT NULL,
  "created_at" timestamp NULL,
  "name" character varying(255) NULL,
  "unit_type" character varying(255) NULL,
  "updated_at" timestamp NULL,
  "medication_profile_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fk22v1go6ndjso0k7b441i2u9q4" FOREIGN KEY ("medication_profile_id") REFERENCES "medication_profiles" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
-- create "measurement_units" table
CREATE TABLE "measurement_units" (
  "id" character varying(255) NOT NULL,
  "created_at" timestamp NULL,
  "is_liquid" boolean NULL,
  "name" character varying(255) NULL,
  "symbol" character varying(255) NULL,
  "updated_at" timestamp NULL,
  "medication_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fkctjg8vp2gdfbcx5t3xu0dt6df" FOREIGN KEY ("medication_id") REFERENCES "medications" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
-- create "medication_packs" table
CREATE TABLE "medication_packs" (
  "id" character varying(255) NOT NULL,
  "added_at" timestamp NULL,
  "created_at" timestamp NULL,
  "current_quantity" double precision NULL,
  "total_amount" double precision NULL,
  "total_quantity" double precision NULL,
  "updated_at" timestamp NULL,
  "medication_profile_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fkn3tqesn3jtu0njrbd0pmmw1jj" FOREIGN KEY ("medication_profile_id") REFERENCES "medication_profiles" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
-- create "medication_schedules" table
CREATE TABLE "medication_schedules" (
  "id" character varying(255) NOT NULL,
  "created_at" timestamp NULL,
  "dose_quantity" double precision NULL,
  "recurrence_rule" character varying(255) NULL,
  "start_at" timestamp NULL,
  "updated_at" timestamp NULL,
  "medication_profile_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fk4tvgtcx50ueppislk70gaei5e" FOREIGN KEY ("medication_profile_id") REFERENCES "medication_profiles" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
