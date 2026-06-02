-- create "billings" table
CREATE TABLE "billings" (
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
  CONSTRAINT "fk4eip6q0an8hjsblnlmwnf993h" FOREIGN KEY ("subscription_period_id") REFERENCES "subscription_periods" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "fk8cq3bwt04rk4snkaup5yl85ow" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
-- drop "billing" table
DROP TABLE "billing";
