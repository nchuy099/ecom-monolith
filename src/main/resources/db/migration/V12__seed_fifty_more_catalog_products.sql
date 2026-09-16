WITH seed AS (
  SELECT
    product_number,
    (ARRAY[
      'MacBook Air M3 13 inch', 'Lenovo ThinkPad X1 Carbon Gen 12', 'HP Spectre x360 14',
      'Acer Swift Go 14 OLED', 'Microsoft Surface Laptop 6', 'Samsung Galaxy Z Fold6',
      'Google Pixel 9 Pro', 'Xiaomi 14 Ultra', 'OPPO Find X8 Pro', 'Nothing Phone 2a Plus',
      'Apple Watch Series 10', 'Samsung Galaxy Watch7', 'Garmin Forerunner 965',
      'Bose QuietComfort Ultra Headphones', 'Apple AirPods Pro 2 USB-C', 'JBL Charge 5',
      'Sony SRS-XG300', 'FiiO K7 Desktop DAC', 'Logitech G Pro X Superlight 2',
      'Razer BlackWidow V4 Pro', 'Keychron K2 Pro', 'Elgato Stream Deck MK.2',
      'Nintendo Switch OLED', 'Meta Quest 3 512GB', 'Steam Deck OLED 512GB',
      'Samsung Odyssey G8 OLED 34 inch', 'LG UltraGear 27GS95QE', 'Dell UltraSharp U2723QE',
      'Xiaomi Smart Air Fryer 6.5L', 'Philips LatteGo 5400', 'Roborock Q Revo',
      'Ecovacs X2 Omni', 'TP-Link Deco BE65 Wi-Fi 7', 'ASUS RT-AX86U Pro',
      'Synology DS224 Plus NAS', 'SanDisk Extreme Pro 2TB SSD', 'Samsung T9 2TB SSD',
      'Anker 737 Power Bank 24000mAh', 'Belkin BoostCharge Pro 3-in-1',
      'DJI Osmo Pocket 3', 'GoPro HERO12 Black', 'Canon EOS R50 Kit',
      'Fujifilm Instax Mini Evo', 'Kindle Paperwhite Signature Edition',
      'Xiaomi Pad 6S Pro', 'Huawei MatePad Pro 13.2', 'Sennheiser Momentum 4',
      'Marshall Emberton III', 'Dyson V12 Detect Slim', 'Samsung Bespoke Jet AI'
    ])[product_number - 100] AS name,
    CASE (product_number - 101) % 5
      WHEN 0 THEN '10000000-0000-0000-0000-000000000001'::uuid
      WHEN 1 THEN '10000000-0000-0000-0000-000000000002'::uuid
      WHEN 2 THEN '10000000-0000-0000-0000-000000000003'::uuid
      WHEN 3 THEN '10000000-0000-0000-0000-000000000004'::uuid
      ELSE '10000000-0000-0000-0000-000000000005'::uuid
    END AS category_id,
    CASE (product_number - 101) % 5
      WHEN 0 THEN 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=800&q=80'
      WHEN 1 THEN 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=800&q=80'
      WHEN 2 THEN 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=800&q=80'
      WHEN 3 THEN 'https://images.unsplash.com/photo-1592840496694-26d035b52b48?auto=format&fit=crop&w=800&q=80'
      ELSE 'https://images.unsplash.com/photo-1556228578-0d85b1a4d571?auto=format&fit=crop&w=800&q=80'
    END AS image_url,
    (4.50 + ((product_number % 45)::numeric / 100))::numeric(3, 2) AS rating,
    25 + ((product_number - 100) * 7) AS review_count,
    990000 + ((product_number - 100) * 490000) AS price,
    8 + (product_number % 33) AS available_quantity
  FROM generate_series(101, 150) AS product_number
)
INSERT INTO products (id, category_id, name, description, rating, review_count, active, created_at, updated_at, is_deleted)
SELECT
  CONCAT('30000000-0000-0000-0000-', LPAD(product_number::text, 12, '0'))::uuid,
  category_id,
  name,
  'Sản phẩm công nghệ chính hãng, đầy đủ thông tin cấu hình và hỗ trợ bảo hành tiêu chuẩn.',
  rating,
  review_count,
  true,
  TIMESTAMP '2026-09-19 00:00:00.000000',
  TIMESTAMP '2026-09-19 00:00:00.000000',
  false
FROM seed;

WITH seed AS (
  SELECT
    product_number,
    CASE (product_number - 101) % 5
      WHEN 0 THEN 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=800&q=80'
      WHEN 1 THEN 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=800&q=80'
      WHEN 2 THEN 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=800&q=80'
      WHEN 3 THEN 'https://images.unsplash.com/photo-1592840496694-26d035b52b48?auto=format&fit=crop&w=800&q=80'
      ELSE 'https://images.unsplash.com/photo-1556228578-0d85b1a4d571?auto=format&fit=crop&w=800&q=80'
    END AS image_url,
    990000 + ((product_number - 100) * 490000) AS price
  FROM generate_series(101, 150) AS product_number
)
INSERT INTO product_variants (id, product_id, sku, name, price, image_url, attributes_json, active, created_at, updated_at, is_deleted)
SELECT
  CONCAT('40000000-0000-0000-0000-', LPAD(product_number::text, 12, '0'))::uuid,
  CONCAT('30000000-0000-0000-0000-', LPAD(product_number::text, 12, '0'))::uuid,
  'DEMO-' || product_number,
  'Phiên bản tiêu chuẩn',
  price,
  image_url,
  jsonb_build_object('Phiên bản', 'Tiêu chuẩn', 'Mã sản phẩm', 'DEMO-' || product_number)::text,
  true,
  TIMESTAMP '2026-09-19 00:00:00.000000',
  TIMESTAMP '2026-09-19 00:00:00.000000',
  false
FROM seed;

INSERT INTO inventories (id, warehouse_id, product_variant_id, available_quantity, reserved_quantity, version, created_at, updated_at, is_deleted)
SELECT
  CONCAT('50000000-0000-0000-0000-', LPAD(product_number::text, 12, '0'))::uuid,
  CASE (product_number - 101) % 3
    WHEN 0 THEN '20000000-0000-0000-0000-000000000001'::uuid
    WHEN 1 THEN '20000000-0000-0000-0000-000000000002'::uuid
    ELSE '20000000-0000-0000-0000-000000000003'::uuid
  END,
  CONCAT('40000000-0000-0000-0000-', LPAD(product_number::text, 12, '0'))::uuid,
  8 + (product_number % 33),
  0,
  0,
  TIMESTAMP '2026-09-19 00:00:00.000000',
  TIMESTAMP '2026-09-19 00:00:00.000000',
  false
FROM generate_series(101, 150) AS product_number;
