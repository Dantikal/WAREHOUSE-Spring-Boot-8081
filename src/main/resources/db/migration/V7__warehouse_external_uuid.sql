ALTER TABLE warehouse ADD COLUMN IF NOT EXISTS external_uuid UUID;

UPDATE warehouse SET external_uuid = gen_random_uuid() WHERE external_uuid IS NULL;

ALTER TABLE warehouse ALTER COLUMN external_uuid SET NOT NULL;
ALTER TABLE warehouse ADD CONSTRAINT warehouse_external_uuid_key UNIQUE (external_uuid);

CREATE INDEX IF NOT EXISTS idx_warehouse_external_uuid ON warehouse(external_uuid);
