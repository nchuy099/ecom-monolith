UPDATE product_variants
SET
  image_url = CONCAT(
    'https://picsum.photos/seed/ecom-',
    LOWER(REGEXP_REPLACE(sku, '[^a-zA-Z0-9]+', '-', 'g')),
    '/900/700'
  ),
  updated_at = TIMESTAMP '2026-09-19 00:00:00.000000'
WHERE is_deleted = false;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM product_variants
    WHERE is_deleted = false
      AND image_url IS NOT NULL
    GROUP BY image_url
    HAVING COUNT(*) > 1
  ) THEN
    RAISE EXCEPTION 'Duplicate product variant image_url exists after catalog reseed';
  END IF;
END $$;
