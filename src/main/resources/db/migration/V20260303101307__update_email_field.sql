-- modify "users" table
ALTER TABLE "users" ALTER COLUMN "email" SET NOT NULL, ADD CONSTRAINT "uk_avh1b2ec82audum2lyjx2p1ws" UNIQUE ("email");
