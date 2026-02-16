# Appointment Feedback System

## Overview
A complete feedback and rating system that allows pet owners to rate and review their appointment experience with doctors. The ratings are aggregated and displayed in the doctor's dashboard.

## Features Implemented

### 1. Backend (Java Spring Boot)

#### New Entities Created:
- **Feedback.java** - Entity to store appointment feedback
  - Fields: id, appointment, doctor, petOwner, rating (1-5), comments, createdAt
  - One-to-One relationship with Appointment (one feedback per appointment)
  - Many-to-One relationships with User (doctor and petOwner)

#### New Repository:
- **FeedbackRepository.java**
  - `findByAppointmentId()` - Get feedback for specific appointment
  - `findByDoctorId()` - Get all feedback for a doctor
  - `findByPetOwnerId()` - Get all feedback submitted by a user
  - `getAverageRatingForDoctor()` - Calculate average rating (with query)
  - `getFeedbackCountForDoctor()` - Count total feedback for doctor

#### New Service:
- **FeedbackService.java**
  - `submitFeedback()` - Submit feedback for completed appointment
  - `getFeedbackByAppointmentId()` - Retrieve feedback by appointment
  - `getFeedbackForDoctor()` - Get all doctor feedback
  - `getFeedbackFromUser()` - Get user's submitted feedback
  - `getAverageRatingForDoctor()` - Get rating summary with average
  - `getFeedbackCountForDoctor()` - Count doctor's feedback
  - `updateFeedback()` - Update submitted feedback
  - `deleteFeedback()` - Delete feedback with authorization checks

#### New Controller:
- **FeedbackController.java**
  - `POST /api/feedback/submit` - Submit feedback for appointment
  - `GET /api/feedback/appointment/{appointmentId}` - Get feedback by appointment
  - `GET /api/feedback/doctor/{doctorId}` - Get all feedback for doctor
  - `GET /api/feedback/doctor/{doctorId}/rating-summary` - Get doctor rating summary
  - `GET /api/feedback/my-feedback` - Get current user's feedback
  - `PUT /api/feedback/{feedbackId}` - Update feedback
  - `DELETE /api/feedback/{feedbackId}` - Delete feedback

#### New DTO:
- **FeedbackRequest.java**
  - appointmentId (Long)
  - rating (Integer 1-5)
  - comments (String, optional)

### 2. Frontend (React)

#### New Components:

1. **AppointmentFeedback.jsx** - Modal component for feedback submission
   - Star rating selector (1-5 stars) with hover effect
   - Text area for optional comments (max 1000 chars)
   - Form validation
   - Success/error handling
   - Features:
     - Interactive star rating with visual feedback
     - Character counter for comments
     - Beautiful modal with animation
     - Loading and error states

2. **DoctorRatingCard.jsx** - Display doctor ratings
   - Shows average rating (e.g., 4.5 stars)
   - Displays feedback count
   - Renders star visualization
   - Auto-fetches rating summary from backend

#### Updated Components:

1. **MyAppointments.jsx** - Enhanced with feedback integration
   - Added feedback modal state management
   - Added "Rate Appointment" button for COMPLETED appointments
   - Shows confirmation message after feedback submission
   - Displays "Thank you for feedback" after rating

#### New Styles:

1. **AppointmentFeedback.css**
   - Modal overlay with dark background
   - Smooth animations (slide-up effect)
   - Star rating styling with hover effects
   - Star colors: gold (#ffc107) when selected
   - Responsive design for mobile
   - Form validation styling

2. **DoctorRatingCard.css**
   - Rating card with shadow and hover effects
   - Star display with filled/empty states
   - Gradient styling with animations
   - Responsive grid layout

3. **MyAppointments.css** (updated)
   - Card footer with flex layout for multiple buttons
   - Orange gradient for "Rate Appointment" button
   - Green background for "Feedback submitted" message
   - Responsive button layout

## User Flow

### For Pet Owners (Submitting Feedback):
1. Navigate to "My Appointments" page
2. View completed appointments
3. Click "⭐ Rate Appointment" button
4. Select rating (1-5 stars) by clicking
5. Optionally add comments
6. Click "Submit Feedback"
7. See confirmation message

### For Doctors (Viewing Ratings):
1. View own dashboard
2. See DoctorRatingCard component
3. View average rating (e.g., 4.5/5)
4. See total feedback count
5. Click to view detailed feedback list

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/feedback/submit` | Submit feedback for appointment |
| GET | `/api/feedback/appointment/{appointmentId}` | Get feedback for appointment |
| GET | `/api/feedback/doctor/{doctorId}` | Get all feedback for doctor |
| GET | `/api/feedback/doctor/{doctorId}/rating-summary` | Get average rating & count |
| GET | `/api/feedback/my-feedback` | Get user's submitted feedback |
| PUT | `/api/feedback/{feedbackId}` | Update feedback |
| DELETE | `/api/feedback/{feedbackId}` | Delete feedback |

## Validation & Security

### Backend:
- Rating validation: Must be between 1-5
- Authorization checks: Users can only submit feedback for their own appointments
- One feedback per appointment (unique constraint)
- Only pet owner can submit/update/delete their feedback

### Frontend:
- Rating is required (cannot submit without selecting stars)
- Comments limited to 1000 characters
- Form validation before submission
- Error messages for validation failures

## Database Schema

### feedbacks Table:
```sql
CREATE TABLE feedbacks (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  appointment_id BIGINT NOT NULL UNIQUE,
  doctor_id BIGINT NOT NULL,
  pet_owner_id BIGINT NOT NULL,
  rating INT NOT NULL,
  comments VARCHAR(1000),
  created_at DATETIME NOT NULL,
  FOREIGN KEY (appointment_id) REFERENCES appointments(id),
  FOREIGN KEY (doctor_id) REFERENCES users(id),
  FOREIGN KEY (pet_owner_id) REFERENCES users(id)
);
```

## Integration Points

1. **With Appointments**:
   - Feedback linked to completed appointments
   - Shows after appointment status is COMPLETED
   - One-to-One relationship (one feedback per appointment)

2. **With Doctor Dashboard**:
   - Display average rating in doctor's profile
   - Show feedback count
   - Display recent feedback reviews

3. **With User Profiles**:
   - Can view their submitted feedback
   - Can update/delete their feedback

## Future Enhancements

1. Display recent feedback reviews in doctor dashboard
2. Sort feedback by helpfulness/rating
3. Add photo/image support in feedback
4. Email notifications to doctors for new feedback
5. Feedback moderation system
6. Reply to feedback from doctors
7. Analytics dashboard for ratings trends
8. Certification badges for high-rated doctors

## Files Modified/Created

### New Files (Backend):
- `Feedback.java` - Entity
- `FeedbackRepository.java` - Repository
- `FeedbackService.java` - Service
- `FeedbackController.java` - REST Controller
- `FeedbackRequest.java` - DTO

### New Files (Frontend):
- `AppointmentFeedback.jsx` - Feedback modal component
- `DoctorRatingCard.jsx` - Rating display component
- `AppointmentFeedback.css` - Feedback modal styles
- `DoctorRatingCard.css` - Rating card styles

### Modified Files:
- `MyAppointments.jsx` - Added feedback integration
- `MyAppointments.css` - Added button and feedback styles
- `pom.xml` - No changes (all dependencies already present)
- `package.json` - No changes (all dependencies already present)

## Build Status

✅ **Backend**: Compiled successfully (83 source files)
✅ **Frontend**: No compilation errors
✅ **Servers**: Both running on ports 8080 (backend) and 3000 (frontend)

## Next Steps for User

1. **Test the feedback submission**:
   - Navigate to completed appointments
   - Click "Rate Appointment"
   - Submit a rating with optional comments
   - Verify confirmation message appears

2. **View doctor ratings**:
   - Navigate to doctor's profile/dashboard
   - See DoctorRatingCard with average rating
   - See feedback count

3. **Update feedback** (optional):
   - Click on submitted feedback
   - Modify rating/comments
   - Submit update

4. **Future integration**:
   - Add DoctorRatingCard to doctor dashboard
   - Display recent feedback reviews
   - Show ratings in doctor search/directory
