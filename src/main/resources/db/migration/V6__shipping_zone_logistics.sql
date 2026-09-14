ALTER TABLE warehouses
  ADD COLUMN priority_area VARCHAR(120) NOT NULL DEFAULT 'Chưa phân vùng';

UPDATE warehouses
SET priority_area = 'Hà Nội'
WHERE code = 'WH-HN-01';

UPDATE warehouses
SET priority_area = 'TP.HCM'
WHERE code = 'WH-HCM-01';

UPDATE warehouses
SET priority_area = 'Đà Nẵng'
WHERE code = 'WH-DN-01';

CREATE TABLE shipping_zones (
  id UUID PRIMARY KEY,
  code VARCHAR(40) NOT NULL UNIQUE,
  name VARCHAR(160) NOT NULL,
  matched_cities VARCHAR(1000) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_shipping_zone_active
  ON shipping_zones(active, is_deleted);

CREATE TABLE warehouse_shipping_zones (
  id UUID PRIMARY KEY,
  warehouse_id UUID NOT NULL,
  shipping_zone_id UUID NOT NULL,
  priority INT NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_warehouse_zone_warehouse FOREIGN KEY(warehouse_id) REFERENCES warehouses(id),
  CONSTRAINT fk_warehouse_zone_shipping_zone FOREIGN KEY(shipping_zone_id) REFERENCES shipping_zones(id),
  CONSTRAINT uk_warehouse_shipping_zone UNIQUE(warehouse_id, shipping_zone_id),
  CONSTRAINT chk_warehouse_zone_priority CHECK(priority > 0)
);

CREATE INDEX idx_warehouse_zone_zone_priority
  ON warehouse_shipping_zones(shipping_zone_id, active, is_deleted, priority);

CREATE INDEX idx_warehouse_zone_warehouse
  ON warehouse_shipping_zones(warehouse_id, is_deleted);

INSERT INTO shipping_zones
  (id, code, name, matched_cities, active, created_at, updated_at, is_deleted)
VALUES
  ('21000000-0000-0000-0000-000000000001', 'NORTH', 'Miền Bắc', 'Hà Nội,Ha Noi,Hanoi', true, TIMESTAMP '2026-09-18 00:00:00.000000', TIMESTAMP '2026-09-18 00:00:00.000000', false),
  ('21000000-0000-0000-0000-000000000002', 'CENTRAL', 'Miền Trung', 'Đà Nẵng,Da Nang,Danang', true, TIMESTAMP '2026-09-18 00:00:00.000000', TIMESTAMP '2026-09-18 00:00:00.000000', false),
  ('21000000-0000-0000-0000-000000000003', 'SOUTH', 'Miền Nam', 'TP. Hồ Chí Minh,TP.HCM,Hồ Chí Minh,Ho Chi Minh,HCM', true, TIMESTAMP '2026-09-18 00:00:00.000000', TIMESTAMP '2026-09-18 00:00:00.000000', false);

INSERT INTO warehouse_shipping_zones
  (id, warehouse_id, shipping_zone_id, priority, active, created_at, updated_at, is_deleted)
VALUES
  (
    '22000000-0000-0000-0000-000000000001',
    '20000000-0000-0000-0000-000000000001',
    '21000000-0000-0000-0000-000000000001',
    1,
    true,
    TIMESTAMP '2026-09-18 00:00:00.000000',
    TIMESTAMP '2026-09-18 00:00:00.000000',
    false
  ),
  (
    '22000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000003',
    '21000000-0000-0000-0000-000000000002',
    1,
    true,
    TIMESTAMP '2026-09-18 00:00:00.000000',
    TIMESTAMP '2026-09-18 00:00:00.000000',
    false
  ),
  (
    '22000000-0000-0000-0000-000000000003',
    '20000000-0000-0000-0000-000000000002',
    '21000000-0000-0000-0000-000000000003',
    1,
    true,
    TIMESTAMP '2026-09-18 00:00:00.000000',
    TIMESTAMP '2026-09-18 00:00:00.000000',
    false
  );
