ALTER TABLE driver ADD COLUMN IF NOT EXISTS uuid UUID;

UPDATE driver SET uuid = gen_random_uuid() WHERE uuid IS NULL;

ALTER TABLE driver ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE driver ADD CONSTRAINT driver_uuid_key UNIQUE (uuid);

CREATE INDEX IF NOT EXISTS idx_driver_uuid ON driver(uuid);
