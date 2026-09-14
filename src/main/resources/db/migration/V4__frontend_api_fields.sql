ALTER TABLE product_variants
  ADD COLUMN image_url VARCHAR(500),
  ADD COLUMN attributes_json TEXT;

ALTER TABLE categories
  ADD COLUMN slug VARCHAR(120),
  ADD COLUMN icon VARCHAR(80);

ALTER TABLE products
  ADD COLUMN rating DECIMAL(3,2),
  ADD COLUMN review_count INT,
  ADD COLUMN badge VARCHAR(80);

ALTER TABLE shipments
  ADD COLUMN tracking_number VARCHAR(80);

UPDATE shipments
SET tracking_number = CONCAT('LEGACY-', REPLACE(id::text, '-', ''))
WHERE tracking_number IS NULL;

ALTER TABLE shipments
  ALTER COLUMN tracking_number SET NOT NULL;

ALTER TABLE shipments
  ADD CONSTRAINT uk_shipment_tracking_number UNIQUE(tracking_number);

ALTER TABLE notifications
  ADD COLUMN read_at TIMESTAMP(6);
