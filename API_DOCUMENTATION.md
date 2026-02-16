# Pet Health Records Backend API Documentation

## Overview
Complete backend implementation for Pet Health Monitoring with role-based access control.

## Base URL
```
http://localhost:8080
```

## Authentication
- Session-based authentication (cookies)
- User must be logged in for all health endpoints
- BCrypt password encryption

## API Endpoints

### 1. Health Metrics

#### List Health Metrics
```http
GET /api/pets/{petId}/health/metrics?from=2024-01-01&to=2024-12-31
```
- **Access:** Pet Owner (own pets only) | Veterinary Doctor | Admin
- **Query Params:**
  - `from` (optional): Start date (ISO format)
  - `to` (optional): End date (ISO format)
- **Returns:** Array of `HealthMetric` objects

#### Add Owner Health Metric
```http
POST /api/owner/pets/{petId}/health/metrics
Content-Type: application/json

{
  "recordDate": "2024-01-15",
  "weight": 25.5,
  "calorieIntake": 1200,
  "activityLevel": 78,
  "stressLevel": "LOW",
  "notes": "Active day, walked 5km"
}
```
- **Access:** Pet Owner (own pets only)
- **Restrictions:**
  - Cannot set `bloodPressure` (vet-only field)
  - `vetEntered` automatically set to `false`

#### Add/Update Vet Health Metric
```http
POST /api/vet/pets/{petId}/health/metrics
Content-Type: application/json

{
  "recordDate": "2024-01-15",
  "weight": 25.5,
  "calorieIntake": 1200,
  "activityLevel": 78,
  "stressLevel": "MEDIUM",
  "bloodPressure": "120/80",
  "notes": "Regular checkup, all vitals normal"
}
```
- **Access:** Veterinary Doctor | Admin
- **Can set all fields including `bloodPressure`**
- `vetEntered` automatically set to `true`

#### Update Vet Health Metric
```http
PUT /api/vet/health/metrics/{id}
Content-Type: application/json

{
  "weight": 26.0,
  "bloodPressure": "118/78"
}
```
- **Access:** Veterinary Doctor | Admin

---

### 2. Vaccination Records

#### List Vaccination Records
```http
GET /api/pets/{petId}/vaccinations?from=2024-01-01&to=2024-12-31
```
- **Access:** Pet Owner (own pets only) | Veterinary Doctor | Admin
- **Query Params:**
  - `from` (optional): Start date
  - `to` (optional): End date
- **Returns:** Array of `VaccinationRecord` objects

#### Create Vaccination Record
```http
POST /api/vet/pets/{petId}/vaccinations
Content-Type: application/json

{
  "vaccineName": "Rabies",
  "administeredDate": "2024-01-10",
  "dueDate": "2024-01-10",
  "nextDueDate": "2025-01-10",
  "veterinarianName": "Dr. Smith",
  "batchNumber": "RAB-2024-001",
  "notes": "Annual rabies vaccination"
}
```
- **Access:** Veterinary Doctor | Admin
- **Status auto-computed** based on dates:
  - `COMPLETED` if `administeredDate` is set
  - `OVERDUE` if `dueDate` < today
  - `DUE` if `dueDate` = today or near future
  - `SCHEDULED` otherwise

#### Update Vaccination Record
```http
PUT /api/vet/vaccinations/{id}
Content-Type: application/json

{
  "administeredDate": "2024-01-15",
  "notes": "Completed successfully"
}
```
- **Access:** Veterinary Doctor | Admin
- Status auto-updated

---

### 3. Medical History

#### List Medical History
```http
GET /api/pets/{petId}/medical-history?from=2024-01-01&to=2024-12-31
```
- **Access:** Pet Owner (own pets only) | Veterinary Doctor | Admin
- **Query Params:**
  - `from` (optional): Start date
  - `to` (optional): End date
- **Returns:** Array of `MedicalHistory` objects ordered by visit date (desc)

#### Create Medical History Entry
```http
POST /api/vet/pets/{petId}/medical-history
Content-Type: application/json

{
  "visitDate": "2024-01-20",
  "visitType": "CHECKUP",
  "veterinarianName": "Dr. Johnson",
  "diagnosis": "Mild ear infection",
  "treatment": "Antibiotic ear drops, 2x daily for 7 days",
  "prescription": "Otomax 0.5ml drops",
  "notes": "Follow-up in 2 weeks",
  "followUpDate": "2024-02-03",
  "cost": 85.50
}
```
- **Access:** Veterinary Doctor | Admin only
- **Visit Types:** `CHECKUP`, `EMERGENCY`, `VACCINATION`, `SURGERY`, `FOLLOW_UP`

#### Update Medical History Entry
```http
PUT /api/vet/medical-history/{id}
Content-Type: application/json

{
  "diagnosis": "Ear infection resolved",
  "notes": "Recovery complete"
}
```
- **Access:** Veterinary Doctor | Admin only

---

### 4. Health Reminders

#### List Health Reminders
```http
GET /api/pets/{petId}/reminders?from=2024-01-01&to=2024-12-31
```
- **Access:** Pet Owner (own pets only) | Veterinary Doctor | Admin
- **Query Params:**
  - `from` (optional): Start date
  - `to` (optional): End date
- **Returns:** Array of `HealthReminder` objects ordered by reminder date (asc)

#### Create Health Reminder
```http
POST /api/pets/{petId}/reminders
Content-Type: application/json

{
  "reminderType": "CHECKUP",
  "title": "Annual Checkup",
  "description": "Yearly health examination",
  "reminderDate": "2024-06-15"
}
```
- **Access:** Pet Owner (own pets only) | Veterinary Doctor | Admin
- **Owner Restrictions:** Can only create `CHECKUP` or `CUSTOM` reminders
- **Vet Access:** Can create any type including `VACCINATION` and `MEDICATION`
- **Reminder Types:** `VACCINATION`, `CHECKUP`, `MEDICATION`, `CUSTOM`
- **Status:** `PENDING` (default), `COMPLETED`, `DISMISSED`

#### Update Reminder Status
```http
PUT /api/reminders/{id}
Content-Type: application/json

{
  "status": "COMPLETED"
}
```
- **Access:** Pet Owner (own pets only) | Veterinary Doctor | Admin

---

## Data Models

### HealthMetric
```json
{
  "id": 1,
  "recordDate": "2024-01-15",
  "weight": 25.5,
  "calorieIntake": 1200,
  "activityLevel": 78,
  "stressLevel": "LOW",
  "bloodPressure": "120/80",
  "notes": "Active day",
  "vetEntered": false,
  "createdAt": "2024-01-15T10:30:00"
}
```

### VaccinationRecord
```json
{
  "id": 1,
  "vaccineName": "Rabies",
  "administeredDate": "2024-01-10",
  "dueDate": "2024-01-10",
  "nextDueDate": "2025-01-10",
  "status": "COMPLETED",
  "veterinarianName": "Dr. Smith",
  "batchNumber": "RAB-2024-001",
  "notes": "Annual vaccination",
  "createdAt": "2024-01-10T14:00:00"
}
```

### MedicalHistory
```json
{
  "id": 1,
  "visitDate": "2024-01-20",
  "visitType": "CHECKUP",
  "veterinarianName": "Dr. Johnson",
  "diagnosis": "Mild ear infection",
  "treatment": "Antibiotic ear drops, 2x daily for 7 days",
  "prescription": "Otomax 0.5ml drops",
  "notes": "Follow-up in 2 weeks",
  "followUpDate": "2024-02-03",
  "cost": 85.50,
  "createdAt": "2024-01-20T11:15:00"
}
```

### HealthReminder
```json
{
  "id": 1,
  "reminderType": "VACCINATION",
  "title": "Rabies Booster",
  "description": "Annual rabies vaccination due",
  "reminderDate": "2025-01-10",
  "status": "PENDING",
  "notificationSent": false,
  "createdAt": "2024-12-01T09:00:00"
}
```

---

## Role-Based Access Control

### Pet Owner
- ✅ View all health records for own pets
- ✅ Add health metrics (weight, calories, activity, stress)
- ❌ Cannot set blood pressure (vet-only)
- ❌ Cannot edit medical history
- ❌ Cannot create/edit vaccination records
- ✅ Can create CHECKUP or CUSTOM reminders only

### Veterinary Doctor
- ✅ View health records for any pet
- ✅ Add/update full health metrics (including blood pressure)
- ✅ Create/update vaccination records
- ✅ Create/update medical history
- ✅ Create any type of reminder

### Admin
- Full access to all endpoints

---

## Security Features

1. **Session-based Authentication**
   - User must be logged in
   - Session validated on every request

2. **Pet Ownership Validation**
   - Pet Owner can only access their own pets
   - Enforced at repository level via `pet.user.id` check

3. **Field-Level Security**
   - `bloodPressure`: Vet-only write access
   - `vetEntered`: Auto-set, cannot be overridden
   - Medical history: Vet-only write

4. **Reminder Type Restrictions**
   - Owners limited to CHECKUP and CUSTOM
   - Vets can create VACCINATION and MEDICATION reminders

---

## Database Tables

All tables automatically created by Hibernate on application startup:

1. **health_metrics**
   - Foreign key to `pets(id)`
   - Indexed on `pet_id` and `record_date`

2. **vaccination_records**
   - Foreign key to `pets(id)`
   - Indexed on `pet_id` and `due_date`

3. **medical_history**
   - Foreign key to `pets(id)`
   - Indexed on `pet_id` and `visit_date`

4. **health_reminders**
   - Foreign key to `pets(id)`
   - Indexed on `pet_id`, `reminder_date`, and `status`

---

## Error Responses

### 401 Unauthorized
```json
{}
```
User not logged in or session expired.

### 403 Forbidden
```json
{
  "message": "Forbidden"
}
```
User doesn't have permission (e.g., accessing another owner's pet).

### 404 Not Found
```json
{
  "message": "Pet not found"
}
```
Resource doesn't exist.

### 400 Bad Request
```json
{
  "message": "Owners can create only CHECKUP or CUSTOM reminders"
}
```
Validation error or business rule violation.

---

## Testing with cURL

### Login First
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=owner@example.com&password=password123" \
  -c cookies.txt
```

### Add Health Metric (Owner)
```bash
curl -X POST http://localhost:8080/api/owner/pets/1/health/metrics \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "recordDate": "2024-01-15",
    "weight": 25.5,
    "calorieIntake": 1200,
    "activityLevel": 78,
    "stressLevel": "LOW"
  }'
```

### List Health Metrics
```bash
curl -X GET "http://localhost:8080/api/pets/1/health/metrics?from=2024-01-01&to=2024-12-31" \
  -b cookies.txt
```

---

## Status Codes

- `200 OK` - Success
- `201 Created` - Resource created
- `400 Bad Request` - Validation error
- `401 Unauthorized` - Not logged in
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Next Steps for Frontend Integration

1. Create React components for health tracking
2. Add date range pickers for filtering
3. Implement charts using the health metrics data
4. Add reminder notifications in dashboard
5. Create vet-specific UI for medical history entry
