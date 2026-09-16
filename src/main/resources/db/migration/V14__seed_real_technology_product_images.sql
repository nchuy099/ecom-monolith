-- Replace placeholder catalog images with real technology product photos from Unsplash.
WITH images AS (
  SELECT image_url, ordinality AS sort_order
  FROM unnest(ARRAY[
    'https://plus.unsplash.com/premium_photo-1761494495055-6077392f40a4?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1579586337278-3befd40fd17a?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1711051475117-f3a4d3ff6778?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1681302427948-2fd0eca629b1?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1531297484001-80022131f5a1?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1681302547899-9339f12aca53?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1484788984921-03950022c9ef?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1681702156223-ea59bfbf1065?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1680985551009-05107cd2752c?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1523206489230-c012c64b2b48?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1580910051074-3eb694886505?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1660844817855-3ecc7ef21f12?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1546868871-7041f2a55e12?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1637160151663-a410315e4e75?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1679513691474-73102089c117?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1726769123522-81fb47e9758d?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1756575959912-9da92ce35af3?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1527814050087-3793815479db?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1683543124615-fb42e42c6201?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1574012716378-0ca6f4c18c08?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1607853202273-797f1c22a38e?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1665041982909-8a86864a1e49?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1551645120-d70bfe84c826?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1614624532983-4ce03382d63d?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1570485071395-29b575ea3b4e?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1672192166833-c8ae84e5e127?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1553787499-4036afbbcd8d?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1729006559482-d289e4385b1e?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1653990480360-31a12ce9723e?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1516044734145-07ca8eef8731?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1606904825846-647eb07f5be2?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1764113105038-6ed85522fbf3?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1761033366858-d6ec8aca56e3?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1703319952940-ec62d23def39?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1761494494603-ba8c748100ed?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1661481079679-04e8a19e258c?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1502920917128-1aa500764cbd?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1495707902641-75cac588d2e9?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1490117874548-e35a2286fd89?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1502982720700-bfff97f2ecac?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1703332795377-65ccc6818232?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1681139760927-4c510ce6d8f0?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1561154464-82e9adf32764?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1588131153911-a4ea5189fe19?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1676810460522-bc963e5554d8?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1527515673510-8aa78ce21f9b?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1764687895590-42927b92efeb?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1762859731349-c9ff2808b672?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1681233751666-612c7bc77485?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1623126908029-58cb08a2b272?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1687892170417-f9a11a402ef7?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1593640408182-31c70c8268f5?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1613141412501-9012977f1969?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1561930661-20c9650e3e25?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1618424181497-157f25b6ddd5?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1661662850226-83c981ed4eba?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1687854992749-e15cba89631d?auto=format&fit=crop&w=900&q=85',
    'https://plus.unsplash.com/premium_photo-1681666713680-fb39c13070f3?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1585060544812-6b45742d762f?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1545127398-14699f92334b?auto=format&fit=crop&w=900&q=85'
  ]::text[]) WITH ORDINALITY AS seeded(image_url, ordinality)
), ranked_variants AS (
  SELECT id, ROW_NUMBER() OVER (ORDER BY sku) AS sort_order
  FROM product_variants
  WHERE is_deleted = false
)
UPDATE product_variants pv
SET image_url = images.image_url,
    updated_at = TIMESTAMP '2026-09-19 00:00:00.000000'
FROM ranked_variants ranked
JOIN images ON images.sort_order = ranked.sort_order
WHERE pv.id = ranked.id;

DO $$
DECLARE
  active_variants INTEGER;
  seeded_images INTEGER;
BEGIN
  SELECT COUNT(*) INTO active_variants
  FROM product_variants
  WHERE is_deleted = false;

  SELECT COUNT(*) INTO seeded_images
  FROM product_variants
  WHERE is_deleted = false
    AND image_url LIKE 'https://%unsplash.com/%';

  IF active_variants <> 68 OR seeded_images <> active_variants THEN
    RAISE EXCEPTION 'Expected % unique technology images for % active variants, found %', 68, active_variants, seeded_images;
  END IF;

  IF EXISTS (
    SELECT 1
    FROM product_variants
    WHERE is_deleted = false
      AND image_url IS NOT NULL
    GROUP BY image_url
    HAVING COUNT(*) > 1
  ) THEN
    RAISE EXCEPTION 'Duplicate product variant image_url exists after technology catalog reseed';
  END IF;
END $$;
