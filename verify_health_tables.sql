-- Health Records Database Schema for Smart Pet Care Application
-- This script shows the expected table structure that will be auto-created by Hibernate

-- Note: These tables will be automatically created by Spring Boot JPA
-- when the application starts with spring.jpa.hibernate.ddl-auto=update

-- Verify existing pets table (should already exist)
DESCRIBE pets;

-- Check if health_metrics table was created
DESCRIBE health_metrics;

-- Check if vaccination_records table was created
DESCRIBE vaccination_records;

-- Check if medical_history table was created
DESCRIBE medical_history;

-- Check if health_reminders table was created
DESCRIBE health_reminders;

-- Show all tables in the database
SHOW TABLES;

-- Query to check foreign key constraints
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM
    INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE
    REFERENCED_TABLE_SCHEMA = 'petcare'
    AND REFERENCED_TABLE_NAME = 'pets';

-- Query to verify indexes on health tables
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    NON_UNIQUE
FROM
    INFORMATION_SCHEMA.STATISTICS
WHERE
    TABLE_SCHEMA = 'petcare'
    AND TABLE_NAME IN ('health_metrics', 'vaccination_records', 'medical_history', 'health_reminders')
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- Sample queries to verify empty tables (no demo data)
SELECT COUNT(*) as health_metrics_count FROM health_metrics;
SELECT COUNT(*) as vaccination_records_count FROM vaccination_records;
SELECT COUNT(*) as medical_history_count FROM medical_history;
SELECT COUNT(*) as health_reminders_count FROM health_reminders;

-- Query to show table relationships
SELECT 
    CONCAT(TABLE_NAME, '.', COLUMN_NAME) AS 'Foreign Key',
    CONCAT(REFERENCED_TABLE_NAME, '.', REFERENCED_COLUMN_NAME) AS 'References'
FROM
    INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE
    REFERENCED_TABLE_SCHEMA = 'petcare'
    AND TABLE_NAME IN ('health_metrics', 'vaccination_records', 'medical_history', 'health_reminders');
