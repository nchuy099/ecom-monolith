ALTER TABLE orders ADD COLUMN completed_at TIMESTAMP(6);

UPDATE orders o
SET completed_at = delivered.completed_at
FROM (
  SELECT s.order_id, MAX(te.occurred_at) AS completed_at
  FROM shipments s
  JOIN tracking_events te ON te.shipment_id = s.id
  WHERE te.status = 'DELIVERED'
  GROUP BY s.order_id
) delivered
WHERE o.id = delivered.order_id AND o.status = 'COMPLETED';

UPDATE orders SET completed_at = updated_at
WHERE status = 'COMPLETED' AND completed_at IS NULL;

ALTER TABLE returns ADD COLUMN decision_note VARCHAR(1000);
ALTER TABLE return_items ADD COLUMN restocked_quantity INT NOT NULL DEFAULT 0;

ALTER TABLE shipments ADD COLUMN shipment_type VARCHAR(16) NOT NULL DEFAULT 'OUTBOUND';
ALTER TABLE shipments ADD COLUMN return_id UUID;
ALTER TABLE shipments ADD COLUMN warehouse_received_at TIMESTAMP(6);
ALTER TABLE shipments ADD CONSTRAINT fk_shipment_return FOREIGN KEY(return_id) REFERENCES returns(id);
CREATE INDEX idx_shipments_return ON shipments(return_id);

ALTER TABLE shipment_items ADD COLUMN return_item_id UUID;
ALTER TABLE shipment_items ADD CONSTRAINT fk_shipment_item_return_item FOREIGN KEY(return_item_id) REFERENCES return_items(id);

CREATE TABLE payment_refunds (
  id UUID PRIMARY KEY,
  payment_id UUID NOT NULL,
  return_id UUID NOT NULL UNIQUE,
  amount NUMERIC(19, 2) NOT NULL,
  provider_reference VARCHAR(255) NOT NULL,
  refunded_at TIMESTAMP(6) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  created_by UUID,
  updated_at TIMESTAMP(6) NOT NULL,
  updated_by UUID,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_payment_refund_payment FOREIGN KEY(payment_id) REFERENCES payments(id),
  CONSTRAINT fk_payment_refund_return FOREIGN KEY(return_id) REFERENCES returns(id),
  CONSTRAINT chk_payment_refund_amount CHECK(amount > 0)
);
