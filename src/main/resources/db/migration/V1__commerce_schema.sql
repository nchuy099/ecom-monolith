CREATE TABLE users (
  id UUID PRIMARY KEY,
  email VARCHAR(160) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  display_name VARCHAR(120) NOT NULL,
  role VARCHAR(32) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE user_addresses (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  recipient_name VARCHAR(120) NOT NULL,
  phone VARCHAR(32) NOT NULL,
  address_line VARCHAR(255) NOT NULL,
  city VARCHAR(120) NOT NULL,
  latitude DECIMAL(9, 6) NOT NULL,
  longitude DECIMAL(9, 6) NOT NULL,
  is_default BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_address_user ON user_addresses(user_id);

CREATE TABLE refresh_tokens (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  token_hash CHAR(64) NOT NULL UNIQUE,
  family_id UUID NOT NULL,
  expires_at TIMESTAMP(6) NOT NULL,
  revoked_at TIMESTAMP(6),
  replaced_by_token_id UUID,
  device_name VARCHAR(120),
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_refresh_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_family ON refresh_tokens(family_id);

CREATE TABLE categories (
  id UUID PRIMARY KEY,
  parent_id UUID,
  name VARCHAR(160) NOT NULL,
  description VARCHAR(1000),
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_category_parent FOREIGN KEY(parent_id) REFERENCES categories(id)
);

CREATE INDEX idx_category_parent ON categories(parent_id);

CREATE TABLE products (
  id UUID PRIMARY KEY,
  category_id UUID NOT NULL,
  name VARCHAR(160) NOT NULL,
  description TEXT,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_product_category FOREIGN KEY(category_id) REFERENCES categories(id)
);

CREATE INDEX idx_product_category ON products(category_id);

CREATE TABLE product_variants (
  id UUID PRIMARY KEY,
  product_id UUID NOT NULL,
  sku VARCHAR(80) NOT NULL UNIQUE,
  name VARCHAR(160) NOT NULL,
  price DECIMAL(19, 2) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_variant_product FOREIGN KEY(product_id) REFERENCES products(id),
  CONSTRAINT chk_variant_price CHECK(price >= 0)
);

CREATE INDEX idx_variant_product ON product_variants(product_id);

CREATE TABLE warehouses (
  id UUID PRIMARY KEY,
  code VARCHAR(40) NOT NULL UNIQUE,
  name VARCHAR(160) NOT NULL,
  address_line VARCHAR(255) NOT NULL,
  latitude DECIMAL(9, 6) NOT NULL,
  longitude DECIMAL(9, 6) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE inventories (
  id UUID PRIMARY KEY,
  warehouse_id UUID NOT NULL,
  product_variant_id UUID NOT NULL,
  available_quantity INT NOT NULL,
  reserved_quantity INT NOT NULL DEFAULT 0,
  version BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_inventory_warehouse FOREIGN KEY(warehouse_id) REFERENCES warehouses(id),
  CONSTRAINT fk_inventory_variant FOREIGN KEY(product_variant_id) REFERENCES product_variants(id),
  CONSTRAINT uk_inventory_warehouse_variant UNIQUE(warehouse_id, product_variant_id),
  CONSTRAINT chk_inventory_available_quantity CHECK(available_quantity >= 0),
  CONSTRAINT chk_inventory_reserved_quantity CHECK(reserved_quantity >= 0)
);

CREATE INDEX idx_inventory_variant_warehouse
  ON inventories(product_variant_id, warehouse_id);

CREATE TABLE carts (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL UNIQUE,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_cart_user FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE cart_items (
  id UUID PRIMARY KEY,
  cart_id UUID NOT NULL,
  product_variant_id UUID NOT NULL,
  quantity INT NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_cart_item_cart FOREIGN KEY(cart_id) REFERENCES carts(id),
  CONSTRAINT fk_cart_item_variant FOREIGN KEY(product_variant_id) REFERENCES product_variants(id),
  CONSTRAINT uk_cart_variant UNIQUE(cart_id, product_variant_id),
  CONSTRAINT chk_cart_item_quantity CHECK(quantity > 0)
);

CREATE TABLE orders (
  id UUID PRIMARY KEY,
  order_number VARCHAR(64) NOT NULL UNIQUE,
  user_id UUID NOT NULL,
  status VARCHAR(32) NOT NULL,
  recipient_name VARCHAR(120) NOT NULL,
  phone VARCHAR(32) NOT NULL,
  address_line VARCHAR(255) NOT NULL,
  city VARCHAR(120) NOT NULL,
  latitude DECIMAL(9, 6) NOT NULL,
  longitude DECIMAL(9, 6) NOT NULL,
  total_amount DECIMAL(19, 2) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_order_user FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE INDEX idx_order_user_created ON orders(user_id, created_at);

CREATE TABLE order_items (
  id UUID PRIMARY KEY,
  order_id UUID NOT NULL,
  product_variant_id UUID NOT NULL,
  sku_snapshot VARCHAR(80) NOT NULL,
  name_snapshot VARCHAR(160) NOT NULL,
  unit_price DECIMAL(19, 2) NOT NULL,
  quantity INT NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_order_item_order FOREIGN KEY(order_id) REFERENCES orders(id),
  CONSTRAINT fk_order_item_variant FOREIGN KEY(product_variant_id) REFERENCES product_variants(id),
  CONSTRAINT chk_order_item_quantity CHECK(quantity > 0)
);

CREATE TABLE payments (
  id UUID PRIMARY KEY,
  order_id UUID NOT NULL,
  provider VARCHAR(40) NOT NULL,
  provider_reference VARCHAR(120),
  idempotency_key VARCHAR(120) NOT NULL UNIQUE,
  amount DECIMAL(19, 2) NOT NULL,
  status VARCHAR(32) NOT NULL,
  paid_at TIMESTAMP(6),
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_payment_order FOREIGN KEY(order_id) REFERENCES orders(id)
);

CREATE INDEX idx_payment_order ON payments(order_id);

CREATE TABLE shipments (
  id UUID PRIMARY KEY,
  order_id UUID NOT NULL,
  warehouse_id UUID NOT NULL,
  shipper_id UUID,
  status VARCHAR(32) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_shipment_order FOREIGN KEY(order_id) REFERENCES orders(id),
  CONSTRAINT fk_shipment_warehouse FOREIGN KEY(warehouse_id) REFERENCES warehouses(id),
  CONSTRAINT fk_shipment_shipper FOREIGN KEY(shipper_id) REFERENCES users(id)
);

CREATE INDEX idx_shipment_shipper_status ON shipments(shipper_id, status);

CREATE TABLE shipment_items (
  id UUID PRIMARY KEY,
  shipment_id UUID NOT NULL,
  order_item_id UUID NOT NULL,
  inventory_id UUID NOT NULL,
  quantity INT NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_shipment_item_shipment FOREIGN KEY(shipment_id) REFERENCES shipments(id),
  CONSTRAINT fk_shipment_item_order_item FOREIGN KEY(order_item_id) REFERENCES order_items(id),
  CONSTRAINT fk_shipment_item_inventory FOREIGN KEY(inventory_id) REFERENCES inventories(id),
  CONSTRAINT chk_shipment_item_quantity CHECK(quantity > 0)
);

CREATE TABLE tracking_events (
  id UUID PRIMARY KEY,
  shipment_id UUID NOT NULL,
  actor_id UUID,
  status VARCHAR(32) NOT NULL,
  note VARCHAR(1000),
  latitude DECIMAL(9, 6),
  longitude DECIMAL(9, 6),
  proof_url VARCHAR(500),
  occurred_at TIMESTAMP(6) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_tracking_shipment FOREIGN KEY(shipment_id) REFERENCES shipments(id),
  CONSTRAINT fk_tracking_actor FOREIGN KEY(actor_id) REFERENCES users(id)
);

CREATE INDEX idx_tracking_shipment_time
  ON tracking_events(shipment_id, occurred_at);

CREATE TABLE returns (
  id UUID PRIMARY KEY,
  order_id UUID NOT NULL,
  status VARCHAR(32) NOT NULL,
  reason VARCHAR(1000) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_return_order FOREIGN KEY(order_id) REFERENCES orders(id)
);

CREATE TABLE return_items (
  id UUID PRIMARY KEY,
  return_id UUID NOT NULL,
  order_item_id UUID NOT NULL,
  quantity INT NOT NULL,
  received_quantity INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_return_item_return FOREIGN KEY(return_id) REFERENCES returns(id),
  CONSTRAINT fk_return_item_order_item FOREIGN KEY(order_item_id) REFERENCES order_items(id),
  CONSTRAINT chk_return_item_quantity CHECK(quantity > 0)
);

CREATE TABLE notifications (
  id UUID PRIMARY KEY,
  user_id UUID,
  order_id UUID,
  recipient_email VARCHAR(160) NOT NULL,
  subject VARCHAR(255) NOT NULL,
  body TEXT NOT NULL,
  status VARCHAR(32) NOT NULL,
  attempt_count INT NOT NULL DEFAULT 0,
  next_attempt_at TIMESTAMP(6) NOT NULL,
  claimed_at TIMESTAMP(6),
  last_error VARCHAR(1000),
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_notification_due ON notifications(status, next_attempt_at);
