# 🚀 MILESTONE 3 - ADVANCED FEATURES & ENHANCEMENTS

**Status:** 🟡 IN PROGRESS  
**Start Date:** February 2, 2026  
**Target Completion:** TBD

---

## 📋 OVERVIEW

Milestone 3 builds upon the solid foundation of Milestones 1 & 2 by adding advanced features that enhance user experience, improve engagement, and add critical healthcare management capabilities.

### What We're Building:
1. **Dashboard Analytics** - Real-time statistics and insights
2. **Notification System** - In-app notifications for appointments, approvals, reminders
3. **Pet Profile Enhancements** - Photo uploads and detailed profiles
4. **Prescription Management** - Digital prescriptions from doctors
5. **Calendar View** - Visual appointment calendar
6. **Bug Fixes** - Address pending compilation warnings

---

## 🎯 FEATURE BREAKDOWN

### 1️⃣ DASHBOARD STATISTICS API ✨

**Current State:** Stats sections are commented out with placeholder data  
**Goal:** Real-time data fetched from backend

#### Backend Implementation:
- **New Service:** `DashboardStatsService.java`
- **New Controller:** `DashboardStatsController.java`
- **Endpoints:**
  - `GET /api/stats/owner` - Pet owner statistics
  - `GET /api/stats/doctor` - Doctor statistics
  - `GET /api/stats/admin` - Admin statistics

#### Stats to Track:

**Pet Owner:**
- Total pets registered
- Upcoming appointments (next 7 days)
- Pending prescriptions
- Health records count

**Doctor:**
- Total appointments handled
- Pending appointments today
- Patients treated (unique pets)
- Appointments this month

**Admin:**
- Total users (owners, doctors)
- Total pets in system
- Appointments this month
- Pending doctor approvals

---

### 2️⃣ NOTIFICATION SYSTEM 🔔

**Goal:** Real-time in-app notifications for important events

#### Backend Implementation:
- **New Entity:** `Notification.java`
  - Fields: id, userId, type, title, message, isRead, createdAt, relatedEntityId
  - Types: APPOINTMENT_BOOKED, APPOINTMENT_APPROVED, APPOINTMENT_REJECTED, APPOINTMENT_REMINDER, PRESCRIPTION_READY, DOCTOR_APPROVED
- **Repository:** `NotificationRepository.java`
- **Service:** `NotificationService.java`
  - createNotification()
  - getUserNotifications()
  - markAsRead()
  - markAllAsRead()
  - deleteNotification()
  - getUnreadCount()
- **Controller:** `NotificationController.java`
  - GET /api/notifications
  - GET /api/notifications/unread-count
  - PUT /api/notifications/{id}/read
  - PUT /api/notifications/read-all
  - DELETE /api/notifications/{id}

#### Frontend Implementation:
- **Component:** `NotificationBell.jsx`
  - Bell icon in header with unread count badge
  - Dropdown panel with notification list
  - Click to mark as read
  - Link to related item (appointment, prescription)
- **Integration:** Add to DashboardHeader.js
- **Auto-refresh:** Poll every 30 seconds for new notifications

#### Notification Triggers:
- Pet owner books appointment → Notify doctor
- Doctor approves/rejects → Notify pet owner
- Appointment reminder (1 day before) → Notify both
- Prescription created → Notify pet owner
- Admin approves doctor → Notify doctor

---

### 3️⃣ PET PROFILE ENHANCEMENTS 📸

**Goal:** Rich pet profiles with photos and detailed information

#### Backend Implementation:
- **File Upload:** Add photo upload endpoint
  - POST /api/pets/{id}/photo
  - File storage in `uploads/pet-photos/`
  - Image validation (size, format)
  - Generate thumbnail
- **Update Pet Entity:** Add photoUrl field
- **Service Methods:**
  - uploadPetPhoto()
  - deletePetPhoto()
  - getPetPhotoUrl()

#### Frontend Implementation:
- **Photo Upload in AddPet.js:**
  - File input with preview
  - Drag-and-drop support
  - Image cropping/resizing
- **Display in PetList.js:**
  - Show pet photo in cards
  - Fallback to default pet icon
- **Enhanced Pet Detail View:**
  - Larger photo display
  - Edit photo button
  - Medical history timeline
  - Vaccination status

---

### 4️⃣ PRESCRIPTION MANAGEMENT 💊

**Goal:** Digital prescription creation and management

#### Backend Implementation:
- **New Entity:** `Prescription.java`
  - Fields: id, appointmentId, doctorId, petId, petOwnerId
  - medicationName, dosage, frequency, duration
  - instructions, notes, prescribedDate, startDate, endDate
  - status (ACTIVE, COMPLETED, CANCELLED)
- **Repository:** `PrescriptionRepository.java`
- **Service:** `PrescriptionService.java`
  - createPrescription()
  - getPrescriptionsByPet()
  - getPrescriptionsByAppointment()
  - updatePrescription()
  - cancelPrescription()
- **Controller:** `PrescriptionController.java`
  - POST /api/prescriptions
  - GET /api/prescriptions/pet/{petId}
  - GET /api/prescriptions/appointment/{appointmentId}
  - GET /api/prescriptions/{id}
  - PUT /api/prescriptions/{id}
  - DELETE /api/prescriptions/{id}

#### Frontend Implementation:
- **Component:** `AddPrescription.jsx`
  - Form for doctors to create prescriptions
  - Auto-fill from appointment
  - Medication name, dosage, frequency
  - Duration and instructions
- **Component:** `ViewPrescription.jsx`
  - List prescriptions for pet owner
  - Filter by status (active/completed)
  - Print/download prescription
  - Medication tracker
- **Integration:**
  - Add "Create Prescription" in DoctorAppointmentDetail
  - Add "Prescriptions" tab in MyAppointments
  - Show prescription icon if prescription exists

---

### 5️⃣ CALENDAR VIEW 📅

**Goal:** Visual calendar for appointment management

#### Frontend Implementation:
- **Library:** React Big Calendar or FullCalendar
- **Component:** `AppointmentCalendar.jsx`
  - Month, week, day views
  - Color-coded by status
  - Click to view details
  - Drag to reschedule (doctor only)
- **Features:**
  - Pet owner sees their appointments
  - Doctor sees all their appointments
  - Admin sees all appointments
  - Filters by status, doctor, pet
  - Export to Google Calendar / iCal

---

### 6️⃣ BUG FIXES & IMPROVEMENTS 🐛

#### Issues to Fix:
1. **User.java Lombok Warning**
   - Add `@Builder.Default` to `isVerified` field
   
2. **Dashboard Stats Integration**
   - Remove commented placeholder code
   - Connect to real APIs

#### Code Quality Improvements:
- Add error logging
- Improve error messages
- Add input validation
- Add loading states
- Add error boundaries

---

## 📦 IMPLEMENTATION ORDER

### Phase 1: Foundation (Days 1-2)
1. ✅ Fix Lombok warning
2. Implement Dashboard Stats API
3. Connect Frontend to Stats API

### Phase 2: Notifications (Days 3-4)
4. Create Notification Entity & Backend
5. Build Notification UI Component
6. Integrate with existing features

### Phase 3: Pet Enhancements (Days 5-6)
7. Implement Pet Photo Upload Backend
8. Add Photo Upload UI
9. Enhance Pet Profile Display

### Phase 4: Prescriptions (Days 7-8)
10. Create Prescription Backend
11. Build Prescription Creation UI (Doctor)
12. Build Prescription View UI (Owner)

### Phase 5: Calendar & Polish (Days 9-10)
13. Implement Calendar View
14. Testing & Bug Fixes
15. Documentation Updates

---

## 🛠️ TECHNICAL REQUIREMENTS

### Backend:
- File upload handling (Multipart)
- Image processing (Thumbnails)
- Scheduled tasks (Notification reminders)
- Additional database tables (Notification, Prescription)

### Frontend:
- File upload with preview
- Image handling
- Calendar library integration
- Real-time polling for notifications

### Database Schema Changes:
```sql
-- Notification Table
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    related_entity_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Prescription Table
CREATE TABLE prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    pet_owner_id BIGINT NOT NULL,
    medication_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100) NOT NULL,
    instructions TEXT,
    notes TEXT,
    prescribed_date DATE NOT NULL,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (doctor_id) REFERENCES users(id),
    FOREIGN KEY (pet_id) REFERENCES pets(id),
    FOREIGN KEY (pet_owner_id) REFERENCES users(id)
);

-- Add photo_url to pets table
ALTER TABLE pets ADD COLUMN photo_url VARCHAR(500);
```

---

## 📊 SUCCESS CRITERIA

### Dashboard Stats:
- ✅ Real-time data displayed
- ✅ Fast loading (<500ms)
- ✅ Accurate counts
- ✅ Responsive design

### Notification System:
- ✅ Notifications created on events
- ✅ Real-time badge updates
- ✅ Mark as read functionality
- ✅ Click to navigate
- ✅ Delete functionality

### Pet Photos:
- ✅ Upload images (JPG, PNG)
- ✅ Max size: 5MB
- ✅ Preview before upload
- ✅ Display in profiles
- ✅ Delete/replace photos

### Prescriptions:
- ✅ Doctor can create
- ✅ Owner can view
- ✅ Print/download
- ✅ Track status
- ✅ Linked to appointments

### Calendar:
- ✅ Visual appointment display
- ✅ Multiple views (month/week/day)
- ✅ Color-coded by status
- ✅ Click for details
- ✅ Responsive on mobile

---

## 🧪 TESTING PLAN

### Unit Tests:
- Service layer methods
- Repository queries
- Validation logic

### Integration Tests:
- API endpoints
- File upload/download
- Notification triggers

### E2E Tests:
- Complete user workflows
- Cross-role interactions
- Mobile responsiveness

---

## 📝 DOCUMENTATION TO UPDATE

1. **API_DOCUMENTATION.md** - Add new endpoints
2. **DATABASE_SCHEMA.md** - Add new tables
3. **TESTING_GUIDE.md** - Add Milestone 3 tests
4. **README.md** - Update features list
5. **MILESTONE_3_COMPLETION_CHECKLIST.md** - Track progress

---

## 🚀 GETTING STARTED

Ready to begin implementation! Starting with:
1. Fix the Lombok warning in User.java
2. Implement Dashboard Statistics backend
3. Connect frontend to real stats

Let's build amazing features! 💪
