-- modify "payments" table
ALTER TABLE "payments" DROP CONSTRAINT "fkqmtjlhwfcjuqh5sny7uw3cjvg", ADD CONSTRAINT "fkqmtjlhwfcjuqh5sny7uw3cjvg" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE CASCADE;
-- modify "plans" table
ALTER TABLE "plans" DROP CONSTRAINT "fk6jxa7tcfdwq7ovcrr3i6jlwby", ADD CONSTRAINT "fk6jxa7tcfdwq7ovcrr3i6jlwby" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE CASCADE;
-- modify "subscriptions" table
ALTER TABLE "subscriptions" DROP CONSTRAINT "fkgb4j0qpwv6hdgy7aotoobd4ty", ADD CONSTRAINT "fkgb4j0qpwv6hdgy7aotoobd4ty" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE NO ACTION ON DELETE CASCADE;
-- modify "subscription_periods" table
ALTER TABLE "subscription_periods" DROP CONSTRAINT "fkrl51oqgb1736jt09x1wrr9h4p", ADD CONSTRAINT "fkrl51oqgb1736jt09x1wrr9h4p" FOREIGN KEY ("subscription_id") REFERENCES "subscriptions" ("id") ON UPDATE NO ACTION ON DELETE CASCADE;
