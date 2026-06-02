-- reverse: drop "payments" table
CREATE TABLE "payments" (
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
  CONSTRAINT "fk3k9kd8hs4880y3lyr06u6snjh" FOREIGN KEY ("subscription_period_id") REFERENCES "subscription_periods" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "fkqmtjlhwfcjuqh5sny7uw3cjvg" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
-- reverse: create "billing" table
DROP TABLE "billing";
-- reverse: modify "users" table
ALTER TABLE "users" DROP COLUMN "payment_method_id";
