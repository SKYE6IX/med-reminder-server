-- reverse: modify "users" table
ALTER TABLE "users" DROP COLUMN "password_reset_token", DROP COLUMN "password_reset_redeem_at", DROP COLUMN "password_reset_issued_at";
