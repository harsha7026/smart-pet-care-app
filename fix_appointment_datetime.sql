-- Fix appointment_date_time column to allow NULL values
USE petcare_db;

ALTER TABLE appointments MODIFY COLUMN appointment_date_time DATETIME NULL;

-- Verify the change
DESCRIBE appointments;
