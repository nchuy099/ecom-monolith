CREATE INDEX idx_products_category_active_deleted
    ON products(category_id, active, is_deleted);

CREATE INDEX idx_product_variants_product_price_id
    ON product_variants(product_id, price, id);

CREATE INDEX idx_product_variants_price_id
    ON product_variants(price, id);
