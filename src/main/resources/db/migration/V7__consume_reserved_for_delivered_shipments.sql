UPDATE inventories inventory
SET reserved_quantity =
  GREATEST(inventory.reserved_quantity - delivered_stock.delivered_quantity, 0)
FROM (
  SELECT
    shipment_item.inventory_id,
    SUM(shipment_item.quantity) AS delivered_quantity
  FROM shipment_items shipment_item
  JOIN shipments shipment ON shipment.id = shipment_item.shipment_id
  WHERE shipment.status = 'DELIVERED'
    AND shipment.is_deleted = false
    AND shipment_item.is_deleted = false
  GROUP BY shipment_item.inventory_id
) delivered_stock
WHERE delivered_stock.inventory_id = inventory.id;
