-- Performance indexes
CREATE INDEX IF NOT EXISTS idx_inventory_warehouse ON inventory(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_inventory_product  ON inventory(product_id);
CREATE INDEX IF NOT EXISTS idx_dispatch_warehouse ON dispatch(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_dispatch_driver    ON dispatch(driver_id);
CREATE INDEX IF NOT EXISTS idx_dispatch_status    ON dispatch(status);
CREATE INDEX IF NOT EXISTS idx_cashbox_tx_warehouse ON cashbox_transaction(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_movement_warehouse ON inventory_movement(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_debt_warehouse     ON debt(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_debt_type          ON debt(debt_type);
