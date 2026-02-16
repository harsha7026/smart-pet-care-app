-- Add Sample Health Records for Pet Care Application
-- This script adds medical history, vaccination records, health metrics, and reminders

USE petcare;

-- Get pet IDs (assuming pets exist from previous data)
-- We'll add health records for the first few pets

-- =======================
-- MEDICAL HISTORY RECORDS
-- =======================

-- Medical History for Pet ID 1 (assuming it exists)
INSERT INTO medical_history (pet_id, visit_date, visit_type, veterinarian_name, diagnosis, treatment, prescription, notes, follow_up_date, cost, created_at)
VALUES 
(1, '2025-11-15', 'CHECKUP', 'Dr. Sarah Johnson', 'Routine checkup - Healthy', 'Regular wellness examination', 'Vitamin supplements', 'Pet is in excellent health. Continue current diet.', '2026-05-15', 150.00, NOW()),
(1, '2025-12-20', 'VACCINATION', 'Dr. Sarah Johnson', 'Rabies vaccination due', 'Administered rabies vaccine', 'No prescription needed', 'Annual rabies vaccination completed successfully', '2026-12-20', 75.00, NOW()),
(1, '2026-01-10', 'FOLLOW_UP', 'Dr. Sarah Johnson', 'Mild skin irritation', 'Topical ointment application', 'Hydrocortisone cream 1%, apply twice daily', 'Condition improving. Continue treatment for 7 days.', '2026-01-20', 85.00, NOW());

-- Medical History for Pet ID 2
INSERT INTO medical_history (pet_id, visit_date, visit_type, veterinarian_name, diagnosis, treatment, prescription, notes, follow_up_date, cost, created_at)
VALUES 
(2, '2025-10-05', 'EMERGENCY', 'Dr. Michael Chen', 'Gastroenteritis', 'Fluid therapy and medication', 'Metronidazole 250mg, twice daily for 5 days', 'Pet responded well to treatment. Monitor food intake.', '2025-10-12', 320.00, NOW()),
(2, '2025-11-18', 'CHECKUP', 'Dr. Michael Chen', 'Post-treatment checkup - Recovered', 'General examination', 'Probiotic supplements', 'Full recovery confirmed. Continue probiotics for gut health.', '2026-05-18', 120.00, NOW()),
(2, '2025-12-28', 'VACCINATION', 'Dr. Michael Chen', 'Distemper vaccination', 'Administered DHPP vaccine', 'No prescription needed', 'Vaccination completed. Monitor for any reactions.', NULL, 80.00, NOW());

-- Medical History for Pet ID 5 (MAX - Golden Retriever)
INSERT INTO medical_history (pet_id, visit_date, visit_type, veterinarian_name, diagnosis, treatment, prescription, notes, follow_up_date, cost, created_at)
VALUES 
(5, '2025-09-10', 'CHECKUP', 'Dr. Emily Rodriguez', 'Routine wellness exam', 'Physical examination and blood work', 'Multivitamin supplement', 'Healthy active dog. Continue regular exercise.', '2026-03-10', 150.00, NOW()),
(5, '2025-10-20', 'EMERGENCY', 'Dr. Emily Rodriguez', 'Minor laceration on paw', 'Wound cleaning and bandaging', 'Antibiotic ointment - apply 2x daily', 'Keep wound clean. Return if signs of infection.', '2025-10-27', 180.00, NOW()),
(5, '2025-12-05', 'VACCINATION', 'Dr. Emily Rodriguez', 'Annual vaccinations', 'DHPP vaccine administered', 'No prescription needed', 'All vaccinations up to date. No reactions observed.', NULL, 95.00, NOW());

-- ========================
-- VACCINATION RECORDS
-- ========================

-- Vaccinations for Pet ID 1
INSERT INTO vaccination_records (pet_id, vaccine_name, administered_date, due_date, next_due_date, status, veterinarian_name, batch_number, notes, created_at)
VALUES 
(1, 'Rabies', '2025-12-20', '2025-12-15', '2026-12-20', 'COMPLETED', 'Dr. Sarah Johnson', 'RAB-2025-1234', 'Annual rabies vaccination - no adverse reactions', NOW()),
(1, 'DHPP (Distemper, Hepatitis, Parvo, Parainfluenza)', '2025-11-15', '2025-11-10', '2026-11-15', 'COMPLETED', 'Dr. Sarah Johnson', 'DHPP-2025-5678', 'Core vaccine administered successfully', NOW()),
(1, 'Bordetella', '2026-06-15', '2026-06-15', '2027-06-15', 'SCHEDULED', 'Dr. Sarah Johnson', NULL, 'Due for kennel cough vaccine', NOW());

-- Vaccinations for Pet ID 2
INSERT INTO vaccination_records (pet_id, vaccine_name, administered_date, due_date, next_due_date, status, veterinarian_name, batch_number, notes, created_at)
VALUES 
(2, 'Rabies', '2025-11-18', '2025-11-15', '2026-11-18', 'COMPLETED', 'Dr. Michael Chen', 'RAB-2025-1256', 'Vaccination completed during checkup', NOW()),
(2, 'DHPP', '2025-12-28', '2025-12-20', '2026-12-28', 'COMPLETED', 'Dr. Michael Chen', 'DHPP-2025-7890', 'No adverse reactions observed', NOW()),
(2, 'Leptospirosis', '2026-02-01', '2026-02-01', '2027-02-01', 'DUE', 'Dr. Michael Chen', NULL, 'Vaccine due soon - schedule appointment', NOW());

-- Vaccinations for Pet ID 5 (MAX)
INSERT INTO vaccination_records (pet_id, vaccine_name, administered_date, due_date, next_due_date, status, veterinarian_name, batch_number, notes, created_at)
VALUES 
(5, 'Rabies', '2025-09-10', '2025-09-01', '2026-09-10', 'COMPLETED', 'Dr. Emily Rodriguez', 'RAB-2025-0987', 'Annual rabies vaccine - no reactions', NOW()),
(5, 'DHPP', '2025-12-05', '2025-12-01', '2026-12-05', 'COMPLETED', 'Dr. Emily Rodriguez', 'DHPP-2025-4321', 'Core vaccine series completed', NOW()),
(5, 'Bordetella', '2026-03-01', '2026-03-01', '2027-03-01', 'SCHEDULED', 'Dr. Emily Rodriguez', NULL, 'Kennel cough vaccine scheduled', NOW());

-- ====================
-- HEALTH METRICS
-- ====================

-- Health metrics for Pet ID 1 (last 30 days of data)
INSERT INTO health_metrics (pet_id, record_date, weight, calorie_intake, activity_level, stress_level, blood_pressure, notes, vet_entered, created_at)
VALUES 
(1, '2025-12-20', 15.5, 850, 75, 'LOW', '120/80', 'Regular checkup - all vitals normal', TRUE, NOW()),
(1, '2025-12-27', 15.6, 900, 80, 'LOW', NULL, 'Active day with good appetite', FALSE, NOW()),
(1, '2026-01-03', 15.7, 820, 70, 'MEDIUM', NULL, 'Slightly less active due to weather', FALSE, NOW()),
(1, '2026-01-10', 15.8, 880, 78, 'LOW', '118/78', 'Follow-up visit - healthy weight gain', TRUE, NOW()),
(1, '2026-01-17', 15.9, 900, 82, 'LOW', NULL, 'Very active today, good energy levels', FALSE, NOW());

-- Health metrics for Pet ID 2
INSERT INTO health_metrics (pet_id, record_date, weight, calorie_intake, activity_level, stress_level, blood_pressure, notes, vet_entered, created_at)
VALUES 
(2, '2025-11-18', 28.3, 1200, 65, 'MEDIUM', '125/85', 'Post-treatment checkup - recovering well', TRUE, NOW()),
(2, '2025-12-01', 28.8, 1300, 70, 'LOW', NULL, 'Weight gain positive sign of recovery', FALSE, NOW()),
(2, '2025-12-15', 29.2, 1250, 72, 'LOW', NULL, 'Appetite excellent, activity normal', FALSE, NOW()),
(2, '2025-12-28', 29.5, 1280, 75, 'LOW', '122/82', 'Vaccination visit - all vitals good', TRUE, NOW()),
(2, '2026-01-10', 29.7, 1300, 78, 'LOW', NULL, 'Maintaining healthy weight and activity', FALSE, NOW());

-- Health metrics for Pet ID 5 (MAX)
INSERT INTO health_metrics (pet_id, record_date, weight, calorie_intake, activity_level, stress_level, blood_pressure, notes, vet_entered, created_at)
VALUES 
(5, '2025-09-10', 28.5, 1400, 85, 'LOW', '125/82', 'Annual checkup - excellent health', TRUE, NOW()),
(5, '2025-10-20', 28.8, 1450, 75, 'MEDIUM', '128/85', 'Emergency visit - minor injury treated', TRUE, NOW()),
(5, '2025-11-15', 29.0, 1500, 88, 'LOW', NULL, 'Very active and playful, eating well', FALSE, NOW()),
(5, '2025-12-05', 29.2, 1480, 90, 'LOW', '124/80', 'Vaccination visit - healthy and energetic', TRUE, NOW()),
(5, '2026-01-10', 29.5, 1520, 92, 'LOW', NULL, 'Peak fitness, excellent activity levels', FALSE, NOW());

-- ====================
-- HEALTH REMINDERS
-- ====================

-- Reminders for Pet ID 1
INSERT INTO health_reminders (pet_id, reminder_type, title, description, reminder_date, status, notification_sent, created_at)
VALUES 
(1, 'VACCINATION', 'Bordetella Vaccine Due', 'Annual kennel cough vaccination is due. Please schedule an appointment.', '2026-06-15', 'PENDING', FALSE, NOW()),
(1, 'CHECKUP', 'Annual Wellness Exam', '6-month checkup due. Regular health monitoring recommended.', '2026-05-15', 'PENDING', FALSE, NOW()),
(1, 'MEDICATION', 'Flea/Tick Prevention', 'Monthly flea and tick preventive medication due.', '2026-02-01', 'PENDING', FALSE, NOW());

-- Reminders for Pet ID 2
INSERT INTO health_reminders (pet_id, reminder_type, title, description, reminder_date, status, notification_sent, created_at)
VALUES 
(2, 'VACCINATION', 'Leptospirosis Vaccine Due', 'Leptospirosis vaccination is due. Schedule appointment soon.', '2026-02-01', 'PENDING', TRUE, NOW()),
(2, 'CHECKUP', '6-Month Wellness Check', 'Semi-annual wellness exam recommended.', '2026-05-18', 'PENDING', FALSE, NOW()),
(2, 'MEDICATION', 'Heartworm Prevention', 'Monthly heartworm preventive medication reminder.', '2026-02-01', 'PENDING', FALSE, NOW()),
(2, 'CUSTOM', 'Dental Cleaning', 'Annual dental cleaning recommended by vet.', '2026-03-15', 'PENDING', FALSE, NOW());

-- Reminders for Pet ID 5 (MAX)
INSERT INTO health_reminders (pet_id, reminder_type, title, description, reminder_date, status, notification_sent, created_at)
VALUES 
(5, 'VACCINATION', 'Bordetella Vaccine Scheduled', 'Kennel cough vaccination scheduled. Confirm appointment.', '2026-03-01', 'PENDING', FALSE, NOW()),
(5, 'CHECKUP', '6-Month Wellness Exam', 'Semi-annual health checkup recommended.', '2026-03-10', 'PENDING', FALSE, NOW()),
(5, 'MEDICATION', 'Heartworm Prevention', 'Monthly heartworm preventive medication due.', '2026-02-10', 'PENDING', FALSE, NOW());

-- Verify the inserted records
SELECT 'Medical History Records:' AS Info;
SELECT COUNT(*) as total_records FROM medical_history;

SELECT 'Vaccination Records:' AS Info;
SELECT COUNT(*) as total_records FROM vaccination_records;

SELECT 'Health Metrics:' AS Info;
SELECT COUNT(*) as total_records FROM health_metrics;

SELECT 'Health Reminders:' AS Info;
SELECT COUNT(*) as total_records FROM health_reminders;

-- Show sample data
SELECT 'Sample Medical History:' AS Info;
SELECT mh.id, p.name as pet_name, mh.visit_date, mh.visit_type, mh.diagnosis, mh.veterinarian_name
FROM medical_history mh
JOIN pets p ON mh.pet_id = p.id
ORDER BY mh.visit_date DESC
LIMIT 5;

SELECT 'Sample Vaccination Records:' AS Info;
SELECT vr.id, p.name as pet_name, vr.vaccine_name, vr.status, vr.next_due_date
FROM vaccination_records vr
JOIN pets p ON vr.pet_id = p.id
ORDER BY vr.next_due_date
LIMIT 5;

SELECT 'Sample Health Reminders:' AS Info;
SELECT hr.id, p.name as pet_name, hr.reminder_type, hr.title, hr.reminder_date, hr.status
FROM health_reminders hr
JOIN pets p ON hr.pet_id = p.id
WHERE hr.status = 'PENDING'
ORDER BY hr.reminder_date
LIMIT 5;
