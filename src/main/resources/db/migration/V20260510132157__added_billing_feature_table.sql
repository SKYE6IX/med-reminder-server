-- create "plans" table
CREATE TABLE "plans" (
  "id" character varying(255) NOT NULL,
  "created_at" timestamp NULL,
  "managed_relation" boolean NULL,
  "max_medications" integer NULL,
  "plan_type" character varying(255) NULL,
  "refill_reminders" boolean NULL,
  "reminder_preference" boolean NULL,
  "updated_at" timestamp NULL,
  "user_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fk6jxa7tcfdwq7ovcrr3i6jlwby" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- create "subscriptions" table
CREATE TABLE "subscriptions" (
  "id" character varying(255) NOT NULL,
  "auto_renewal" boolean NULL,
  "billing_cycle" character varying(255) NULL,
  "canceled_at" timestamp NULL,
  "created_at" timestamp NULL,
  "started_at" timestamp NULL,
  "status" character varying(255) NULL,
  "updated_at" timestamp NULL,
  "plan_id" character varying(255) NULL,
  "user_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fkgb4j0qpwv6hdgy7aotoobd4ty" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "fkl1fu0etqyy5sd9nbfqdjrn4wu" FOREIGN KEY ("plan_id") REFERENCES "plans" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- create "subscription_periods" table
CREATE TABLE "subscription_periods" (
  "id" character varying(255) NOT NULL,
  "created_at" timestamp NULL,
  "end_time" timestamp NULL,
  "payment_status" character varying(255) NULL,
  "start_time" timestamp NULL,
  "updated_at" timestamp NULL,
  "subscription_id" character varying(255) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fkrl51oqgb1736jt09x1wrr9h4p" FOREIGN KEY ("subscription_id") REFERENCES "subscriptions" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- create "payments" table
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
  CONSTRAINT "fkqmtjlhwfcjuqh5sny7uw3cjvg" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
