-- modify "refresh_token" table
ALTER TABLE "refresh_token" ALTER COLUMN "hash_token" SET NOT NULL, ADD CONSTRAINT "uk_6mg43wbxe9ohu2esqmp3yx7j6" UNIQUE ("hash_token");
