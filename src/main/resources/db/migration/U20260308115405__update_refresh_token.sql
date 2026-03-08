-- reverse: modify "refresh_token" table
ALTER TABLE "refresh_token" DROP CONSTRAINT "uk_6mg43wbxe9ohu2esqmp3yx7j6", ALTER COLUMN "hash_token" DROP NOT NULL;
