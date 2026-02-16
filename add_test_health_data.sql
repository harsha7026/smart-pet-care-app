-- Add sample health records for testing
-- Assuming you have a pet with ID 1 and doctor with ID 2

-- Add health metrics for pet 1
INSERT INTO health_metrics (pet_id, record_date, weight, calorie_intake, activity_level, stress_level, vet_entered, created_at)
VALUES 
(1, CURDATE(), 5.5, 450, 75, 'LOW', false, NOW()),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 5.4, 480, 80, 'LOW', false, NOW()),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 5.3, 460, 70, 'MEDIUM', false, NOW());

-- Add vaccination records for pet 1
INSERT INTO vaccination_records (pet_id, vaccine_name, administered_date, due_date, next_due_date, status, veterinarian_name, created_at)
VALUES 
(1, 'Rabies', DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 330 DAY), 'COMPLETED', 'Dr. Smith', NOW()),
(1, 'Distemper', DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_ADD(CURDATE(), INTERVAL 300 DAY), 'COMPLETED', 'Dr. Smith', NOW()),
(1, 'Heartworm', NULL, DATE_ADD(CURDATE(), INTERVAL 15 DAY), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'DUE', 'Dr. Smith', NOW());

-- Add medical history for pet 1
INSERT INTO medical_history (pet_id, visit_date, visit_type, veterinarian_name, diagnosis, treatment, prescription, notes, cost, created_at)
VALUES 
(1, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 'CHECKUP', 'Dr. Smith', 'Healthy checkup', 'Regular examination', 'None', 'Pet is in good health', 500, NOW()),
(1, DATE_SUB(CURDATE(), INTERVAL 45 DAY), 'VACCINATION', 'Dr. Smith', NULL, 'Rabies vaccination administered', 'Post-vaccination care', 'Vaccination completed successfully', 800, NOW());

-- Add health reminders for pet 1
INSERT INTO health_reminders (pet_id, reminder_type, title, description, reminder_date, status, notification_sent, created_at)
VALUES 
(1, 'VACCINATION', 'Heartworm Vaccine Due', 'Schedule appointment for heartworm vaccination', DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'PENDING', false, NOW()),
(1, 'CHECKUP', 'Regular Checkup', 'Time for regular health checkup', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'PENDING', false, NOW()),
(1, 'CUSTOM', 'Flea Treatment', 'Apply flea treatment as per schedule', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'PENDING', false, NOW());

-- Verify data
SELECT 'Health Metrics' as 'Data Type', COUNT(*) as 'Count' FROM health_metrics WHERE pet_id = 1
UNION ALL
SELECT 'Vaccination Records', COUNT(*) FROM vaccination_records WHERE pet_id = 1
UNION ALL
SELECT 'Medical History', COUNT(*) FROM medical_history WHERE pet_id = 1
UNION ALL
SELECT 'Health Reminders', COUNT(*) FROM health_reminders WHERE pet_id = 1;
