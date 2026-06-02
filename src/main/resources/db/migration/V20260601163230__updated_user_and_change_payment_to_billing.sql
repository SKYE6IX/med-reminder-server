-- modify "users" table
ALTER TABLE "users" ADD COLUMN "payment_method_id" character varying(255) NULL;
-- create "billing" table
CREATE TABLE "billing" (
  "id" character varying(255) NOT NULL,
  "amount" numeric(38,2) NULL,
  "created_at" timestamp NULL,
  "method" character varying(255) NULL,
  "paid_at" timestamp NULL,
  "status" character varying(255) NULL,
  "updated_at" timestamp NULL,
  "subscription_period_id" character varying(255) NULL,
  "user_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fk1y5e5m9h2qhfmra0en3dirgk5" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT "fkjk4v1art4tno4uve823ull5ua" FOREIGN KEY ("subscription_period_id") REFERENCES "subscription_periods" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- drop "payments" table
DROP TABLE "payments";
