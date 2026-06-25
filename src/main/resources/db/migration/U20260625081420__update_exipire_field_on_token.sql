-- reverse: modify "refresh_tokens" table
ALTER TABLE "refresh_tokens" ALTER COLUMN "expired_at" TYPE timestamptz;
