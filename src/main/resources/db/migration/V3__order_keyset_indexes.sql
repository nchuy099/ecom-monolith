CREATE INDEX idx_orders_user_status_created_id
    ON orders(user_id, status, created_at, id);
