-- modify "medications" table
ALTER TABLE "medications" ADD COLUMN "measurement" character varying(255) NULL;
-- drop "measurement_units" table
DROP TABLE "measurement_units";
