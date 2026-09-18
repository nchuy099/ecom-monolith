UPDATE inventories inventory
SET
  available_quantity =
    inventory.available_quantity
    + GREATEST(inventory.reserved_quantity - COALESCE(active_stock.active_reserved_quantity, 0), 0),
  reserved_quantity = COALESCE(active_stock.active_reserved_quantity, 0)
FROM (
  SELECT
    shipment_item.inventory_id,
    SUM(shipment_item.quantity) AS active_reserved_quantity
  FROM shipment_items shipment_item
  JOIN shipments shipment ON shipment.id = shipment_item.shipment_id
  WHERE shipment.status NOT IN ('DELIVERED', 'CANCELLED')
    AND shipment.is_deleted = false
    AND shipment_item.is_deleted = false
  GROUP BY shipment_item.inventory_id
) active_stock
WHERE active_stock.inventory_id = inventory.id
  AND inventory.is_deleted = false;

UPDATE inventories inventory
SET
  available_quantity = inventory.available_quantity + inventory.reserved_quantity,
  reserved_quantity = 0
WHERE inventory.is_deleted = false
  AND inventory.reserved_quantity > 0
  AND NOT EXISTS (
    SELECT 1
    FROM shipment_items shipment_item
    JOIN shipments shipment ON shipment.id = shipment_item.shipment_id
    WHERE shipment_item.inventory_id = inventory.id
      AND shipment.status NOT IN ('DELIVERED', 'CANCELLED')
      AND shipment.is_deleted = false
      AND shipment_item.is_deleted = false
  );
