-- reverse: modify "users" table
ALTER TABLE "users" DROP CONSTRAINT "uk_avh1b2ec82audum2lyjx2p1ws", ALTER COLUMN "email" DROP NOT NULL;
