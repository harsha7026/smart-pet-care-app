-- Clean up the database while preserving users, doctors, and admins
-- This script removes all pets and their related health records, and all appointments

SET FOREIGN_KEY_CHECKS=0;

-- Delete all health-related records
DELETE FROM health_reminders;
DELETE FROM vaccination_records;
DELETE FROM medical_history;
DELETE FROM health_metrics;
DELETE FROM prescriptions;
DELETE FROM appointments;
DELETE FROM pets;

-- Reset auto-increment counters
ALTER TABLE health_reminders AUTO_INCREMENT=1;
ALTER TABLE vaccination_records AUTO_INCREMENT=1;
ALTER TABLE medical_history AUTO_INCREMENT=1;
ALTER TABLE health_metrics AUTO_INCREMENT=1;
ALTER TABLE prescriptions AUTO_INCREMENT=1;
ALTER TABLE appointments AUTO_INCREMENT=1;
ALTER TABLE pets AUTO_INCREMENT=1;

SET FOREIGN_KEY_CHECKS=1;

-- Verify users, doctors, and admins still exist
SELECT 'Users Count:' AS info, COUNT(*) AS count FROM users;
SELECT 'Pets Count:' AS info, COUNT(*) AS count FROM pets;
SELECT 'Appointments Count:' AS info, COUNT(*) AS count FROM appointments;
SELECT 'Health Metrics Count:' AS info, COUNT(*) AS count FROM health_metrics;
SELECT 'Vaccination Records Count:' AS info, COUNT(*) AS count FROM vaccination_records;
SELECT 'Medical History Count:' AS info, COUNT(*) AS count FROM medical_history;
