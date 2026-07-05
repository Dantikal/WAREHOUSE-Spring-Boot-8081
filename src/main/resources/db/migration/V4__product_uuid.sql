-- Добавляем UUID колонку в таблицу product
ALTER TABLE product ADD COLUMN IF NOT EXISTS uuid UUID;

-- Заполняем UUID для существующих записей (генерируем случайные UUID)
UPDATE product SET uuid = gen_random_uuid() WHERE uuid IS NULL;

-- Делаем UUID NOT NULL и уникальным
ALTER TABLE product ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE product ADD CONSTRAINT product_uuid_key UNIQUE (uuid);

-- Добавляем индекс на UUID для быстрого поиска
CREATE INDEX IF NOT EXISTS idx_product_uuid ON product(uuid);

-- Добавляем колонку product_uuid в dispatch_item (временно сосуществует с product_id)
ALTER TABLE dispatch_item ADD COLUMN IF NOT EXISTS product_uuid UUID;

-- Заполняем product_uuid на основе существующего product_id
UPDATE dispatch_item di 
SET product_uuid = p.uuid 
FROM product p 
WHERE di.product_id = p.id AND di.product_uuid IS NULL;

-- Создаем внешний ключ на product_uuid
ALTER TABLE dispatch_item ADD CONSTRAINT dispatch_item_product_uuid_fkey 
    FOREIGN KEY (product_uuid) REFERENCES product(uuid);

-- Создаем индекс на product_uuid в dispatch_item
CREATE INDEX IF NOT EXISTS idx_dispatch_item_product_uuid ON dispatch_item(product_uuid);
