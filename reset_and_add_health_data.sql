-- Reset and Add Real Health Records Based on Existing Database Data
-- This script clears old health data and adds new records for existing pets

USE petcare;

-- First, clear existing health records
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE health_reminders;
TRUNCATE TABLE health_metrics;
TRUNCATE TABLE vaccination_records;
TRUNCATE TABLE medical_history;
SET FOREIGN_KEY_CHECKS = 1;

-- Based on typical database setup, we expect:
-- Users: Pet owners (role='PET_OWNER') and Doctors (role='VETERINARY_DOCTOR')
-- Pets: Owned by pet owners
-- The script will add health records for existing pets

-- =======================
-- MEDICAL HISTORY RECORDS
-- =======================

-- For Pet ID 1 (Don - Labrador Retriever)
INSERT INTO medical_history (pet_id, visit_date, visit_type, veterinarian_name, diagnosis, treatment, prescription, notes, follow_up_date, cost, created_at)
VALUES 
(1, '2025-10-15', 'CHECKUP', 'Dr. Sarah Johnson', 'Annual wellness examination', 'Complete physical exam, blood work panel', 'Multivitamin supplement (daily)', 'Healthy active dog. All vitals normal. Continue current diet and exercise routine.', '2026-04-15', 180.00, '2025-10-15 10:30:00'),
(1, '2025-11-28', 'VACCINATION', 'Dr. Sarah Johnson', 'Annual vaccination update', 'DHPP vaccine administered', 'No prescription required', 'Vaccination completed successfully. No adverse reactions observed. Next due in 12 months.', '2026-11-28', 85.00, '2025-11-28 14:00:00'),
(1, '2026-01-08', 'EMERGENCY', 'Dr. Michael Chen', 'Acute gastritis', 'IV fluids, antiemetic medication', 'Metronidazole 500mg (twice daily for 5 days), Bland diet for 3 days', 'Dog ate something inappropriate. Responding well to treatment. Monitor appetite and stool.', '2026-01-15', 320.00, '2026-01-08 18:45:00'),
(1, '2026-01-15', 'FOLLOW_UP', 'Dr. Michael Chen', 'Gastritis recovery checkup', 'Physical examination', 'Continue probiotics for 2 weeks', 'Full recovery confirmed. Appetite normal. Energy levels back to baseline.', NULL, 75.00, '2026-01-15 11:00:00');

-- For Pet ID 2 (asvd - Cat)
INSERT INTO medical_history (pet_id, visit_date, visit_type, veterinarian_name, diagnosis, treatment, prescription, notes, follow_up_date, cost, created_at)
VALUES 
(2, '2025-09-20', 'CHECKUP', 'Dr. Emily Rodriguez', 'Routine wellness exam', 'Physical examination, dental check', 'Dental treats for tartar control', 'Cat is healthy. Mild tartar buildup noted. Schedule dental cleaning in 6 months.', '2026-03-20', 120.00, '2025-09-20 09:15:00'),
(2, '2025-11-10', 'VACCINATION', 'Dr. Emily Rodriguez', 'Core vaccine booster', 'FVRCP vaccine administered', 'No prescription required', 'Annual vaccination completed. Cat tolerated well. Next due November 2026.', '2026-11-10', 75.00, '2025-11-10 10:30:00'),
(2, '2025-12-18', 'EMERGENCY', 'Dr. Michael Chen', 'Upper respiratory infection', 'Antibiotic injection, supportive care', 'Doxycycline 50mg (once daily for 10 days), L-lysine supplement', 'URI common in cats. Keep isolated if other pets present. Follow up if not improving.', '2025-12-28', 180.00, '2025-12-18 16:20:00'),
(2, '2025-12-28', 'FOLLOW_UP', 'Dr. Michael Chen', 'URI recovery check', 'Physical examination', 'Continue L-lysine supplement', 'Significant improvement. Breathing normal. Continue medication as prescribed.', NULL, 60.00, '2025-12-28 14:00:00');

-- For Pet ID 5 (MAX - Golden Retriever)  
INSERT INTO medical_history (pet_id, visit_date, visit_type, veterinarian_name, diagnosis, treatment, prescription, notes, follow_up_date, cost, created_at)
VALUES 
(5, '2025-08-15', 'CHECKUP', 'Dr. Sarah Johnson', 'Annual health screening', 'Complete physical, heartworm test, blood panel', 'Heartworm prevention (monthly)', 'Excellent health. All tests negative. Maintain current preventive care regimen.', '2026-08-15', 250.00, '2025-08-15 09:00:00'),
(5, '2025-10-05', 'SURGERY', 'Dr. Emily Rodriguez', 'Elective neutering procedure', 'Castration surgery performed', 'Carprofen 75mg (twice daily for 7 days), Elizabethan collar', 'Surgery successful. Keep incision clean and dry. Restrict activity for 10-14 days.', '2025-10-15', 450.00, '2025-10-05 08:00:00'),
(5, '2025-10-15', 'FOLLOW_UP', 'Dr. Emily Rodriguez', 'Post-surgical checkup', 'Suture removal, wound check', 'No additional medication needed', 'Healing excellently. Incision site clean. Can resume normal activity gradually.', NULL, 50.00, '2025-10-15 15:30:00'),
(5, '2025-12-12', 'VACCINATION', 'Dr. Sarah Johnson', 'Rabies vaccination', 'Rabies vaccine administered (3-year)', 'No prescription required', 'Rabies vaccination completed. Valid until December 2028. Certificate provided.', NULL, 45.00, '2025-12-12 11:00:00'),
(5, '2026-01-18', 'CHECKUP', 'Dr. Sarah Johnson', '6-month wellness check', 'Physical examination, weight check', 'Joint supplement recommended', 'Thriving post-neutering. Weight stable. Consider joint support for large breed.', '2026-07-18', 95.00, '2026-01-18 10:00:00');

-- For Pet ID 6 (asdf - Rabbit)
INSERT INTO medical_history (pet_id, visit_date, visit_type, veterinarian_name, diagnosis, treatment, prescription, notes, follow_up_date, cost, created_at)
VALUES 
(6, '2025-11-05', 'CHECKUP', 'Dr. Emily Rodriguez', 'New patient examination', 'Complete physical, fecal exam', 'Vitamin C supplement, hay recommendation', 'Healthy rabbit. Educate owner on proper diet. Increase hay intake, limit pellets.', '2026-05-05', 110.00, '2025-11-05 13:00:00'),
(6, '2025-12-20', 'EMERGENCY', 'Dr. Michael Chen', 'GI stasis (suspected)', 'Subcutaneous fluids, gut motility drugs', 'Metoclopramide, Critical Care formula', 'Common rabbit emergency. Hand-feeding required. Monitor closely for 48 hours.', '2025-12-22', 280.00, '2025-12-20 19:30:00'),
(6, '2025-12-22', 'FOLLOW_UP', 'Dr. Michael Chen', 'GI stasis follow-up', 'Physical examination, hydration check', 'Continue Critical Care for 3 more days', 'Good progress. Eating some hay voluntarily. Continue supportive care.', NULL, 65.00, '2025-12-22 10:00:00');

-- ========================
-- VACCINATION RECORDS
-- ========================

-- Vaccinations for Pet ID 1 (Don - Dog)
INSERT INTO vaccination_records (pet_id, vaccine_name, administered_date, due_date, next_due_date, status, veterinarian_name, batch_number, notes, created_at)
VALUES 
(1, 'Rabies', '2025-01-20', '2025-01-15', '2028-01-20', 'COMPLETED', 'Dr. Sarah Johnson', 'RAB-2025-0145', '3-year rabies vaccine. Certificate on file. Next due January 2028.', '2025-01-20 14:00:00'),
(1, 'DHPP (Distemper, Hepatitis, Parvo, Parainfluenza)', '2025-11-28', '2025-11-20', '2026-11-28', 'COMPLETED', 'Dr. Sarah Johnson', 'DHPP-2025-2389', 'Annual core vaccine booster. No adverse reactions.', '2025-11-28 14:00:00'),
(1, 'Bordetella (Kennel Cough)', '2025-06-15', '2025-06-10', '2026-06-15', 'COMPLETED', 'Dr. Michael Chen', 'BOR-2025-0876', 'Intranasal administration. For boarding/daycare requirement.', '2025-06-15 10:30:00'),
(1, 'Leptospirosis', '2026-03-15', '2026-03-15', '2027-03-15', 'DUE', 'Dr. Sarah Johnson', NULL, 'Annual lepto vaccine due. Schedule appointment soon.', '2025-11-28 14:00:00'),
(1, 'Bordetella Booster', '2026-06-15', '2026-06-15', '2027-06-15', 'SCHEDULED', NULL, NULL, 'Annual kennel cough booster scheduled for June.', '2025-06-15 10:30:00');

-- Vaccinations for Pet ID 2 (asvd - Cat)
INSERT INTO vaccination_records (pet_id, vaccine_name, administered_date, due_date, next_due_date, status, veterinarian_name, batch_number, notes, created_at)
VALUES 
(2, 'FVRCP (Feline Viral Rhinotracheitis, Calicivirus, Panleukopenia)', '2025-11-10', '2025-11-01', '2026-11-10', 'COMPLETED', 'Dr. Emily Rodriguez', 'FVRCP-2025-1456', 'Core feline vaccine. Annual booster administered.', '2025-11-10 10:30:00'),
(2, 'Rabies', '2024-12-08', '2024-12-01', '2027-12-08', 'COMPLETED', 'Dr. Emily Rodriguez', 'RAB-2024-3421', '3-year rabies vaccine. Valid until December 2027.', '2024-12-08 11:00:00'),
(2, 'FeLV (Feline Leukemia)', '2026-02-20', '2026-02-20', '2027-02-20', 'DUE', NULL, NULL, 'FeLV booster due for outdoor cat. Schedule appointment.', '2025-11-10 10:30:00');

-- Vaccinations for Pet ID 5 (MAX - Dog)
INSERT INTO vaccination_records (pet_id, vaccine_name, administered_date, due_date, next_due_date, status, veterinarian_name, batch_number, notes, created_at)
VALUES 
(5, 'Rabies', '2025-12-12', '2025-12-10', '2028-12-12', 'COMPLETED', 'Dr. Sarah Johnson', 'RAB-2025-3890', '3-year rabies vaccine administered. Certificate issued.', '2025-12-12 11:00:00'),
(5, 'DHPP', '2025-08-15', '2025-08-10', '2026-08-15', 'COMPLETED', 'Dr. Sarah Johnson', 'DHPP-2025-2145', 'Annual core vaccine series. Well tolerated.', '2025-08-15 09:00:00'),
(5, 'Lyme Disease', '2025-05-20', '2025-05-15', '2026-05-20', 'COMPLETED', 'Dr. Michael Chen', 'LYME-2025-0678', 'Recommended for dogs in endemic areas. Booster in 12 months.', '2025-05-20 14:30:00'),
(5, 'DHPP Booster', '2026-08-15', '2026-08-15', '2027-08-15', 'SCHEDULED', NULL, NULL, 'Annual booster scheduled for August 2026.', '2025-08-15 09:00:00'),
(5, 'Lyme Booster', '2026-05-20', '2026-05-20', '2027-05-20', 'DUE', NULL, NULL, 'Lyme disease booster due in May 2026.', '2025-05-20 14:30:00');

-- Vaccinations for Pet ID 6 (asdf - Rabbit)
INSERT INTO vaccination_records (pet_id, vaccine_name, administered_date, due_date, next_due_date, status, veterinarian_name, batch_number, notes, created_at)
VALUES 
(6, 'RHDV2 (Rabbit Hemorrhagic Disease)', '2025-11-05', '2025-11-01', '2026-11-05', 'COMPLETED', 'Dr. Emily Rodriguez', 'RHDV-2025-0234', 'Critical vaccine for rabbits. Annual booster required.', '2025-11-05 13:00:00'),
(6, 'RHDV2 Booster', '2026-11-05', '2026-11-05', '2027-11-05', 'SCHEDULED', NULL, NULL, 'Annual RHDV2 booster scheduled for November 2026.', '2025-11-05 13:00:00');

-- ====================
-- HEALTH METRICS
-- ====================

-- Health metrics for Pet ID 1 (Don) - Last 60 days
INSERT INTO health_metrics (pet_id, record_date, weight, calorie_intake, activity_level, stress_level, blood_pressure, notes, vet_entered, created_at)
VALUES 
(1, '2025-11-20', 32.5, 1450, 85, 'LOW', NULL, 'Very active day, great appetite', FALSE, '2025-11-20 20:00:00'),
(1, '2025-11-28', 32.4, 1400, 82, 'MEDIUM', '128/82', 'Vaccination visit - all vitals normal', TRUE, '2025-11-28 14:00:00'),
(1, '2025-12-05', 32.6, 1480, 88, 'LOW', NULL, 'High energy, excellent activity levels', FALSE, '2025-12-05 19:30:00'),
(1, '2025-12-15', 32.8, 1500, 90, 'LOW', NULL, 'Peak fitness, very playful', FALSE, '2025-12-15 20:00:00'),
(1, '2025-12-25', 33.0, 1600, 75, 'MEDIUM', NULL, 'Holiday treats - slightly overindulged', FALSE, '2025-12-25 21:00:00'),
(1, '2026-01-01', 33.2, 1550, 80, 'MEDIUM', NULL, 'New Year - back to regular routine', FALSE, '2026-01-01 19:00:00'),
(1, '2026-01-08', 32.9, 800, 30, 'HIGH', '135/88', 'Emergency visit - gastritis, dehydrated', TRUE, '2026-01-08 18:45:00'),
(1, '2026-01-10', 32.5, 1000, 45, 'MEDIUM', NULL, 'Recovery phase - appetite improving', FALSE, '2026-01-10 20:00:00'),
(1, '2026-01-15', 32.6, 1350, 70, 'LOW', '126/80', 'Follow-up visit - full recovery confirmed', TRUE, '2026-01-15 11:00:00'),
(1, '2026-01-18', 32.7, 1420, 85, 'LOW', NULL, 'Back to normal - energetic and happy', FALSE, '2026-01-18 19:30:00');

-- Health metrics for Pet ID 2 (asvd - Cat)
INSERT INTO health_metrics (pet_id, record_date, weight, calorie_intake, activity_level, stress_level, blood_pressure, notes, vet_entered, created_at)
VALUES 
(2, '2025-11-10', 4.8, 280, 70, 'LOW', '115/75', 'Vaccination visit - healthy cat', TRUE, '2025-11-10 10:30:00'),
(2, '2025-11-25', 4.9, 290, 75, 'LOW', NULL, 'Active and playful, good appetite', FALSE, '2025-11-25 21:00:00'),
(2, '2025-12-05', 5.0, 300, 78, 'LOW', NULL, 'Healthy weight gain, very content', FALSE, '2025-12-05 20:30:00'),
(2, '2025-12-18', 4.7, 150, 30, 'HIGH', '120/78', 'URI emergency - lethargic, not eating', TRUE, '2025-12-18 16:20:00'),
(2, '2025-12-22', 4.6, 180, 40, 'MEDIUM', NULL, 'Still recovering, slight improvement', FALSE, '2025-12-22 19:00:00'),
(2, '2025-12-28', 4.7, 240, 55, 'MEDIUM', '117/76', 'Follow-up - significant improvement', TRUE, '2025-12-28 14:00:00'),
(2, '2026-01-05', 4.8, 275, 68, 'LOW', NULL, 'Nearly back to normal activity', FALSE, '2026-01-05 20:00:00'),
(2, '2026-01-15', 4.9, 290, 75, 'LOW', NULL, 'Full recovery - playful and energetic', FALSE, '2026-01-15 21:00:00'),
(2, '2026-01-19', 5.0, 295, 80, 'LOW', NULL, 'Thriving - excellent health', FALSE, '2026-01-19 20:30:00');

-- Health metrics for Pet ID 5 (MAX)
INSERT INTO health_metrics (pet_id, record_date, weight, calorie_intake, activity_level, stress_level, blood_pressure, notes, vet_entered, created_at)
VALUES 
(5, '2025-10-05', 28.5, 1300, 40, 'HIGH', '130/85', 'Pre-surgery - slightly anxious', TRUE, '2025-10-05 08:00:00'),
(5, '2025-10-15', 27.8, 1100, 35, 'MEDIUM', '125/82', 'Post-surgery checkup - healing well', TRUE, '2025-10-15 15:30:00'),
(5, '2025-10-25', 28.0, 1250, 50, 'LOW', NULL, 'Activity gradually increasing', FALSE, '2025-10-25 19:00:00'),
(5, '2025-11-10', 28.5, 1400, 70, 'LOW', NULL, 'Back to normal energy levels', FALSE, '2025-11-10 20:00:00'),
(5, '2025-11-28', 29.0, 1480, 82, 'LOW', NULL, 'Very active, excellent recovery', FALSE, '2025-11-28 19:30:00'),
(5, '2025-12-12', 29.3, 1500, 85, 'LOW', '124/80', 'Vaccination visit - thriving', TRUE, '2025-12-12 11:00:00'),
(5, '2025-12-25', 29.8, 1650, 88, 'LOW', NULL, 'Holiday season - extra treats', FALSE, '2025-12-25 20:00:00'),
(5, '2026-01-05', 30.0, 1520, 90, 'LOW', NULL, 'Peak condition - very athletic', FALSE, '2026-01-05 19:00:00'),
(5, '2026-01-18', 30.2, 1550, 92, 'LOW', '122/78', 'Wellness check - excellent health', TRUE, '2026-01-18 10:00:00'),
(5, '2026-01-19', 30.2, 1540, 94, 'LOW', NULL, 'Extremely active today', FALSE, '2026-01-19 20:00:00');

-- Health metrics for Pet ID 6 (asdf - Rabbit)
INSERT INTO health_metrics (pet_id, record_date, weight, calorie_intake, activity_level, stress_level, blood_pressure, notes, vet_entered, created_at)
VALUES 
(6, '2025-11-05', 5.2, 180, 65, 'MEDIUM', NULL, 'New patient exam - healthy rabbit', TRUE, '2025-11-05 13:00:00'),
(6, '2025-11-20', 5.3, 190, 70, 'LOW', NULL, 'Good appetite, active hopping', FALSE, '2025-11-20 19:00:00'),
(6, '2025-12-05', 5.4, 195, 75, 'LOW', NULL, 'Healthy weight gain, very social', FALSE, '2025-12-05 20:00:00'),
(6, '2025-12-20', 5.0, 50, 15, 'HIGH', NULL, 'GI stasis emergency - critical condition', TRUE, '2025-12-20 19:30:00'),
(6, '2025-12-22', 5.0, 80, 25, 'HIGH', NULL, 'Follow-up - slight improvement', TRUE, '2025-12-22 10:00:00'),
(6, '2025-12-28', 5.1, 140, 45, 'MEDIUM', NULL, 'Recovery progressing, eating hay', FALSE, '2025-12-28 19:00:00'),
(6, '2026-01-05', 5.2, 175, 60, 'LOW', NULL, 'Much better - appetite nearly normal', FALSE, '2026-01-05 20:00:00'),
(6, '2026-01-15', 5.3, 185, 68, 'LOW', NULL, 'Fully recovered - back to baseline', FALSE, '2026-01-15 19:30:00'),
(6, '2026-01-19', 5.4, 190, 72, 'LOW', NULL, 'Thriving - healthy and active', FALSE, '2026-01-19 20:00:00');

-- ====================
-- HEALTH REMINDERS
-- ====================

-- Reminders for Pet ID 1 (Don)
INSERT INTO health_reminders (pet_id, reminder_type, title, description, reminder_date, status, notification_sent, created_at, completed_at)
VALUES 
(1, 'VACCINATION', 'Leptospirosis Vaccine Due', 'Annual Leptospirosis vaccination is due in March 2026. Please schedule an appointment with Dr. Sarah Johnson.', '2026-03-15', 'PENDING', TRUE, '2026-01-15 08:00:00', NULL),
(1, 'VACCINATION', 'Bordetella Booster Scheduled', 'Kennel cough vaccine booster scheduled for June 2026. Appointment confirmation pending.', '2026-06-15', 'PENDING', FALSE, '2026-01-18 09:00:00', NULL),
(1, 'MEDICATION', 'Heartworm Prevention', 'Monthly heartworm preventive medication due. Ensure continuous protection year-round.', '2026-02-01', 'PENDING', FALSE, '2026-01-15 10:00:00', NULL),
(1, 'MEDICATION', 'Flea & Tick Treatment', 'Monthly flea and tick preventive treatment due. Apply topical medication.', '2026-02-01', 'PENDING', FALSE, '2026-01-18 10:00:00', NULL),
(1, 'CHECKUP', '6-Month Wellness Exam', 'Semi-annual health checkup recommended in April 2026. General physical and blood work.', '2026-04-15', 'PENDING', FALSE, '2026-01-19 08:00:00', NULL);

-- Reminders for Pet ID 2 (asvd - Cat)
INSERT INTO health_reminders (pet_id, reminder_type, title, description, reminder_date, status, notification_sent, created_at, completed_at)
VALUES 
(2, 'VACCINATION', 'FeLV Vaccine Due', 'Feline Leukemia virus vaccine booster is due. Important for outdoor cats. Schedule with Dr. Emily Rodriguez.', '2026-02-20', 'PENDING', TRUE, '2026-01-15 09:00:00', NULL),
(2, 'CHECKUP', 'Dental Cleaning Recommended', 'Professional dental cleaning recommended. Mild tartar buildup noted during last exam.', '2026-03-20', 'PENDING', TRUE, '2026-01-10 10:00:00', NULL),
(2, 'MEDICATION', 'Flea Prevention', 'Monthly flea preventive treatment due. Indoor cats still need protection.', '2026-02-01', 'PENDING', FALSE, '2026-01-18 11:00:00', NULL),
(2, 'CUSTOM', 'L-Lysine Supplement', 'Continue L-lysine supplement for immune support. Helps prevent URI recurrence.', '2026-02-15', 'PENDING', FALSE, '2026-01-05 14:00:00', NULL),
(2, 'VACCINATION', 'FVRCP Annual Booster', 'Core feline vaccine (FVRCP) booster due in November 2026.', '2026-11-10', 'PENDING', FALSE, '2026-01-19 09:00:00', NULL);

-- Reminders for Pet ID 5 (MAX)
INSERT INTO health_reminders (pet_id, reminder_type, title, description, reminder_date, status, notification_sent, created_at, completed_at)
VALUES 
(5, 'VACCINATION', 'Lyme Disease Booster', 'Lyme disease vaccine booster due in May 2026. Important for outdoor activities.', '2026-05-20', 'PENDING', FALSE, '2026-01-15 08:30:00', NULL),
(5, 'CHECKUP', '6-Month Wellness Check', 'Semi-annual health examination scheduled for July 2026. Weight and joint health assessment.', '2026-07-18', 'PENDING', FALSE, '2026-01-18 09:00:00', NULL),
(5, 'VACCINATION', 'DHPP Annual Booster', 'Core vaccine booster (DHPP) due in August 2026. Schedule appointment.', '2026-08-15', 'PENDING', FALSE, '2026-01-19 08:00:00', NULL),
(5, 'MEDICATION', 'Heartworm Prevention', 'Monthly heartworm preventive medication reminder. Continuous year-round protection required.', '2026-02-01', 'PENDING', TRUE, '2026-01-15 10:00:00', NULL),
(5, 'MEDICATION', 'Joint Supplement', 'Start daily joint supplement for large breed dog. Recommended during last checkup.', '2026-02-01', 'PENDING', FALSE, '2026-01-18 15:00:00', NULL),
(5, 'CUSTOM', 'Exercise & Weight Monitoring', 'Monitor weight monthly. Large breed prone to joint issues. Maintain healthy weight through diet and exercise.', '2026-02-15', 'PENDING', FALSE, '2026-01-19 10:00:00', NULL);

-- Reminders for Pet ID 6 (asdf - Rabbit)
INSERT INTO health_reminders (pet_id, reminder_type, title, description, reminder_date, status, notification_sent, created_at, completed_at)
VALUES 
(6, 'CHECKUP', '6-Month Wellness Exam', 'Routine rabbit health checkup due. Physical exam and dental assessment recommended.', '2026-05-05', 'PENDING', FALSE, '2026-01-10 11:00:00', NULL),
(6, 'VACCINATION', 'RHDV2 Annual Booster', 'Critical RHDV2 vaccine booster due in November 2026. Essential for rabbit health.', '2026-11-05', 'PENDING', FALSE, '2026-01-15 09:00:00', NULL),
(6, 'CUSTOM', 'Diet Monitoring', 'Ensure unlimited hay available daily. Limit pellets to 1/4 cup. Monitor for GI stasis signs.', '2026-02-01', 'PENDING', FALSE, '2026-01-05 14:00:00', NULL),
(6, 'CUSTOM', 'Nail Trim', 'Rabbit nail trimming due. Schedule appointment or learn proper home technique.', '2026-02-15', 'PENDING', FALSE, '2026-01-18 12:00:00', NULL);

-- Display summary
SELECT '=== DATA INSERTION COMPLETE ===' as Status;
SELECT 
    'Medical History' as Record_Type,
    COUNT(*) as Total_Records,
    COUNT(DISTINCT pet_id) as Pets_Covered
FROM medical_history
UNION ALL
SELECT 
    'Vaccinations',
    COUNT(*),
    COUNT(DISTINCT pet_id)
FROM vaccination_records
UNION ALL
SELECT 
    'Health Metrics',
    COUNT(*),
    COUNT(DISTINCT pet_id)
FROM health_metrics
UNION ALL
SELECT 
    'Health Reminders',
    COUNT(*),
    COUNT(DISTINCT pet_id)
FROM health_reminders;

SELECT '=== SUMMARY BY PET ===' as Info;
SELECT 
    p.id,
    p.name as Pet_Name,
    p.species,
    u.name as Owner_Name,
    (SELECT COUNT(*) FROM medical_history WHERE pet_id = p.id) as Medical_Records,
    (SELECT COUNT(*) FROM vaccination_records WHERE pet_id = p.id) as Vaccinations,
    (SELECT COUNT(*) FROM health_metrics WHERE pet_id = p.id) as Health_Metrics,
    (SELECT COUNT(*) FROM health_reminders WHERE pet_id = p.id) as Reminders
FROM pets p
JOIN users u ON p.owner_id = u.id
WHERE p.id IN (1, 2, 5, 6)
ORDER BY p.id;
