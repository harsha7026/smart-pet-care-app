-- Migration: add order status column and tracking events table

ALTER TABLE orders
  ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'PLACED',
  ADD COLUMN last_updated_at TIMESTAMP NULL;

CREATE TABLE order_tracking_events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL,
  note TEXT,
  created_by BIGINT,
  created_by_role VARCHAR(32),
  metadata JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_order_tracking_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Optional index
CREATE INDEX idx_order_tracking_order_id ON order_tracking_events(order_id);
