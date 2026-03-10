-- create "profiles" table
CREATE TABLE "profiles" (
  "id" character varying(255) NOT NULL,
  "created_at" timestamp NULL,
  "is_self" boolean NULL,
  "name" character varying(255) NULL,
  "relation" character varying(255) NULL,
  "updated_at" timestamp NULL,
  "user_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fki9awkvq3hwxmr454cwdep3t0b" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
