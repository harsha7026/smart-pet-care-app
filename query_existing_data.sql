USE petcare;

-- Query all users
SELECT '=== USERS ===' as Section;
SELECT id, name, email, role, phone FROM users ORDER BY id;

-- Query all pets with owner info
SELECT '=== PETS ===' as Section;
SELECT p.id, p.name, p.species, p.breed, p.age, p.gender, u.name as owner_name, u.id as owner_id FROM pets p JOIN users u ON p.owner_id = u.id ORDER BY p.id;

-- Query all appointments
SELECT '=== APPOINTMENTS ===' as Section;
SELECT a.id, p.name as pet_name, d.name as doctor_name, a.appointment_date, a.status, a.payment_status FROM appointments a JOIN pets p ON a.pet_id = p.id JOIN users d ON a.doctor_id = d.id ORDER BY a.id;

-- Query prescriptions
SELECT '=== PRESCRIPTIONS ===' as Section;
SELECT pr.id, p.name as pet_name, d.name as doctor_name, pr.diagnosis, a.appointment_date FROM prescriptions pr JOIN pets p ON pr.pet_id = p.id JOIN users d ON pr.doctor_id = d.id JOIN appointments a ON pr.appointment_id = a.id ORDER BY pr.id;
