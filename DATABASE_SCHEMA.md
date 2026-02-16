# Health Records Database Schema

## Overview
This document describes the health records database schema for the Smart Pet Care Application.

## Tables Created

### 1. health_metrics
Tracks daily health parameters for each pet.

**Columns:**
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `pet_id` (BIGINT, FK → pets.id, NOT NULL)
- `record_date` (DATE, NOT NULL) - Date of the health record
- `weight` (DOUBLE) - Pet weight in kg
- `calorie_intake` (INT) - Daily calorie intake
- `activity_level` (INT) - Activity percentage (0-100)
- `stress_level` (VARCHAR) - LOW, MEDIUM, HIGH
- `blood_pressure` (VARCHAR) - Format: "120/80" (vet-entered)
- `notes` (VARCHAR) - Additional notes
- `vet_entered` (BOOLEAN) - True if entered by vet
- `created_at` (TIMESTAMP, NOT NULL)

**Indexes:**
- Primary key on `id`
- Foreign key `fk_health_metric_pet` on `pet_id`
- Index on `pet_id` for fast pet-based queries
- Index on `record_date` for time-series analytics

**Use Cases:**
- Track weight trends over time
- Monitor activity levels
- Track calorie intake
- Store vet-measured blood pressure
- Analytics and visualization

---

### 2. vaccination_records
Stores vaccination history and schedules.

**Columns:**
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `pet_id` (BIGINT, FK → pets.id, NOT NULL)
- `vaccine_name` (VARCHAR, NOT NULL) - e.g., "Rabies", "Distemper"
- `administered_date` (DATE) - When vaccine was given
- `due_date` (DATE) - Original due date
- `next_due_date` (DATE) - Next vaccination due date
- `status` (VARCHAR, NOT NULL) - COMPLETED, DUE, OVERDUE, SCHEDULED
- `veterinarian_name` (VARCHAR)
- `batch_number` (VARCHAR) - Vaccine batch/lot number
- `notes` (TEXT)
- `created_at` (TIMESTAMP, NOT NULL)

**Indexes:**
- Primary key on `id`
- Foreign key `fk_vaccination_pet` on `pet_id`
- Index on `next_due_date` for reminder queries

**Use Cases:**
- Track vaccination history
- Generate vaccination reminders
- Monitor overdue vaccinations
- Display vaccination schedules

---

### 3. medical_history
Records vet visits, diagnoses, and treatments.

**Columns:**
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `pet_id` (BIGINT, FK → pets.id, NOT NULL)
- `visit_date` (DATE, NOT NULL)
- `visit_type` (VARCHAR, NOT NULL) - CHECKUP, EMERGENCY, VACCINATION, SURGERY, FOLLOW_UP
- `veterinarian_name` (VARCHAR)
- `diagnosis` (VARCHAR)
- `treatment` (TEXT) - Treatment details
- `prescription` (TEXT) - Prescribed medications
- `notes` (TEXT) - Medical notes
- `follow_up_date` (DATE) - Next follow-up date
- `cost` (DOUBLE) - Visit cost
- `created_at` (TIMESTAMP, NOT NULL)

**Indexes:**
- Primary key on `id`
- Foreign key `fk_medical_history_pet` on `pet_id`
- Index on `visit_date` for chronological queries

**Use Cases:**
- Complete medical history
- Track vet visits
- Monitor treatment plans
- Financial tracking
- Follow-up scheduling

---

### 4. health_reminders
Manages health-related reminders.

**Columns:**
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `pet_id` (BIGINT, FK → pets.id, NOT NULL)
- `reminder_type` (VARCHAR, NOT NULL) - VACCINATION, CHECKUP, MEDICATION, CUSTOM
- `title` (VARCHAR, NOT NULL) - Reminder title
- `description` (TEXT) - Detailed description
- `reminder_date` (DATE, NOT NULL) - When reminder is due
- `status` (VARCHAR, NOT NULL) - PENDING, COMPLETED, DISMISSED
- `notification_sent` (BOOLEAN) - Whether notification was sent
- `created_at` (TIMESTAMP, NOT NULL)
- `completed_at` (TIMESTAMP) - When reminder was completed

**Indexes:**
- Primary key on `id`
- Foreign key `fk_reminder_pet` on `pet_id`
- Index on `reminder_date` and `status` for active reminders

**Use Cases:**
- Vaccination reminders
- Regular checkup reminders
- Medication reminders
- Custom owner-created reminders
- Dashboard notifications

---

## Foreign Key Relationships

```
pets (existing table)
  ↓ (1:N)
  ├── health_metrics
  ├── vaccination_records
  ├── medical_history
  └── health_reminders
```

All health tables reference `pets.id` via `pet_id` foreign key with CASCADE constraints.

## Data Characteristics

- **Normalized:** Each table serves a distinct purpose
- **Time-series ready:** Date columns for analytics
- **Flexible:** NULL allowed for optional fields
- **Analytics-friendly:** Indexed for performance
- **No demo data:** Tables created empty, ready for real data

## Auto-Creation

Tables are automatically created by Hibernate when the application starts:
- `spring.jpa.hibernate.ddl-auto=update`
- Foreign key constraints enforced by MySQL
- Indexes created automatically via JPA annotations

## Usage Notes

1. **Pet Owner Access:**
   - Can view all health records for their pets
   - Can add health metrics (weight, calories, activity, stress)
   - Cannot edit vet-entered data (blood_pressure, medical_history)
   - Can create custom reminders

2. **Veterinarian Access:**
   - Can add/edit all health records
   - Can enter medical history
   - Can update vaccination records
   - Can set blood pressure and other medical data

3. **Data Integrity:**
   - Foreign key constraints ensure referential integrity
   - Deletion of pet cascades to all health records (configured in JPA)
   - Date-based queries optimized with indexes
