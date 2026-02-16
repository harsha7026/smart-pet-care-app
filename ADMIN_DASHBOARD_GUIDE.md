# Admin Dashboard Implementation Guide

## Overview

A complete Admin Dashboard built with real database data for the Smart Pet Care Application. This dashboard provides system-level monitoring and management capabilities with role-based access control.

## Technology Stack

- **Frontend**: React JS
- **Backend**: Spring Boot 3.3.5
- **Database**: MySQL 8.0
- **Authentication**: Session-based (no JWT)
- **Authorization**: Role-based access control (RBAC)

## Architecture

### Backend API Endpoints

All endpoints are protected with admin-only role verification.

**Base URL**: `/api/admin`

#### 1. Dashboard Overview
```
GET /api/admin/dashboard/overview
```
Returns summary statistics:
- `totalUsers` - Total count of all users
- `totalPetOwners` - Count of users with PET_OWNER role
- `totalVeterinaryDoctors` - Count of users with VETERINARY_DOCTOR role
- `totalPets` - Total count of all pets
- `totalAppointments` - Total count of appointments

#### 2. User Management
```
GET /api/admin/users
```
Returns array of all users with:
- id, name, email, phone, role, status, createdAt

```
PUT /api/admin/users/{id}/status
Body: { "status": "ACTIVE" | "INACTIVE" }
```
Updates user status to activate or deactivate.

#### 3. Doctor Management
```
GET /api/admin/doctors
```
Returns array of all veterinary doctors with:
- id, name, email, phone, status, createdAt

#### 4. Pet Management
```
GET /api/admin/pets
```
Returns array of all pets with:
- id, name, species, breed, age, ownerName, ownerEmail, createdAt

#### 5. Statistics
```
GET /api/admin/statistics
```
Returns detailed statistics:
- **User Statistics**: totalUsers, activeUsers, inactiveUsers
- **Role Statistics**: petOwners, veterinaryDoctors, admins
- **Pet Statistics**: totalPets, bySpecies (breakdown by species)

### Security Implementation

1. **Admin Role Verification**
   - Every endpoint checks if current user has ADMIN role
   - Uses SecurityContextHolder to get authenticated user
   - Returns 403 Forbidden if user lacks permission

2. **Session-Based Auth**
   - Existing authentication system (no changes)
   - Admin users created manually in database
   - No self-registration for admins

3. **Data Isolation**
   - Admins can view all data (no restrictions)
   - APIs only accessible with ADMIN role
   - Other roles receive 403 Forbidden responses

## Frontend Components

### 1. AdminDashboard.js
Main dashboard page showing overview cards and quick action links.

**Features**:
- Real-time summary statistics fetched from `/api/admin/dashboard/overview`
- Quick action cards linking to management pages
- Loading and error states
- Responsive grid layout

### 2. AdminUsers.js
User management page with status activation/deactivation.

**Features**:
- Table of all users with columns: Name, Email, Phone, Role, Status, Created At
- Activate/Deactivate buttons for each user
- Real-time status updates via PUT endpoint
- Role badges (color-coded by role)
- Loading and empty states

### 3. AdminDoctors.js
Veterinary doctor management page.

**Features**:
- Table of all doctors with columns: Name, Email, Phone, Status, Created At
- Doctor approval buttons (feature-ready)
- Status badges
- Loading and empty states

### 4. AdminPets.js
Pet registry overview page (read-only).

**Features**:
- Table of all registered pets
- Shows pet details: Name, Species, Breed, Age
- Shows owner information: Name, Email
- Sortable data
- No edit/delete (read-only)

### 5. AdminReports.js
System statistics and reporting page.

**Features**:
- User statistics (Total, Active, Inactive)
- Role distribution (Pet Owners, Vets, Admins)
- Pet statistics (Total count by species)
- Visual stat cards
- Species breakdown list

### 6. AdminAppointments.js & AdminSettings.js
Stub pages ready for future implementation.

## Styling

### AdminPages.css
Comprehensive styles for all admin pages:

- **Tables**: Professional table styling with hover effects
- **Badges**: Status and role badges with color coding
- **Buttons**: Action buttons with states (activate, deactivate, approve)
- **Cards**: Stat cards with gradients
- **Responsive**: Mobile-friendly grid and flex layouts
- **States**: Loading, empty, and error state styling

### Color Scheme
- Primary: #667eea (Purple)
- Secondary: #764ba2 (Dark Purple)
- Status colors:
  - Active: Green (#d1fae5)
  - Inactive: Red (#fee2e2)
  - Pending: Yellow (#fef3c7)

## Data Flow

1. **Page Load**
   ```
   Component Mount
   ↓
   fetchData() → GET /api/admin/{endpoint}
   ↓
   Response received
   ↓
   Set state with real data
   ↓
   Render with data
   ```

2. **User Action (e.g., status update)**
   ```
   User clicks "Activate" button
   ↓
   PUT /api/admin/users/{id}/status
   ↓
   Update local state optimistically
   ↓
   Re-fetch if needed
   ```

## Error Handling

1. **API Errors**
   - Caught in try-catch blocks
   - Display user-friendly error messages
   - Log to console for debugging

2. **Authorization Errors**
   - 403 responses redirect to dashboard
   - PrivateRoute component prevents unauthorized access
   - Session expiry handled by AuthContext

3. **Data Validation**
   - Array.isArray() checks for responses
   - Fallback to empty arrays if invalid
   - Loading states prevent UI jumps

## Database Schema Integration

### Users Table
```sql
- id (PK)
- name
- email (unique)
- phone
- password (hashed)
- role (enum: PET_OWNER, VETERINARY_DOCTOR, ADMIN)
- status (enum: ACTIVE, INACTIVE)
- created_at
- updated_at
```

### Pets Table
```sql
- id (PK)
- user_id (FK to users)
- name
- species
- breed
- age
- gender
- color
- weight
- medical_notes
- created_at
- updated_at
```

## Usage Instructions

### For Admins

1. **Login**: Use admin credentials to log in
2. **Access Dashboard**: Navigate to `/admin/dashboard`
3. **View Statistics**: See real-time overview of users, pets, doctors
4. **Manage Users**: Activate/Deactivate users as needed
5. **View Doctors**: Monitor veterinary doctor profiles
6. **Review Pets**: Browse all registered pets
7. **Check Reports**: View system statistics and breakdowns

### For Developers

1. **Add New Admin Feature**:
   - Create new page in `/pages/Admin[Feature].js`
   - Add route in `App.js` with `allowedRoles={['ADMIN']}`
   - Create API endpoint in `AdminController.java`
   - Add navigation link in sidebar

2. **Modify API Response**:
   - Edit `AdminController.java`
   - Ensure admin role check is present
   - Test with non-admin user (should return 403)

3. **Extend Admin Features**:
   - Add new columns to tables (update both backend and frontend)
   - Add new stat cards (update `AdminReports.js` and `/api/admin/statistics`)
   - Create new admin pages following existing patterns

## Testing

### Manual Testing Checklist

- [ ] Admin can view dashboard with real data
- [ ] Non-admin users cannot access admin routes
- [ ] User activation/deactivation works
- [ ] Data updates reflect immediately in UI
- [ ] All error states display correctly
- [ ] Responsive design works on mobile
- [ ] Loading states appear while fetching data
- [ ] Empty states display when no data exists

### Example Test Credentials

Admin account should be created manually in the database:
```sql
INSERT INTO users (name, email, phone, password, role, status, created_at, updated_at)
VALUES ('Admin User', 'admin@petcare.com', '1234567890', 'hashed_password', 'ADMIN', 'ACTIVE', NOW(), NOW());
```

## Future Enhancements

1. **Appointments Management**
   - Create/view/edit appointments
   - Reschedule functionality
   - Cancellation with notifications

2. **Advanced Reporting**
   - Export reports to CSV/PDF
   - Date range filtering
   - Custom report builder

3. **Platform Settings**
   - Configure app-wide settings
   - Email templates
   - Notification preferences
   - Feature flags

4. **Doctor Approval System**
   - Accept/reject doctor registrations
   - Request additional documents
   - Email notifications

5. **Product Management**
   - Add/edit/delete pet products
   - Inventory tracking
   - Order management

6. **Analytics Dashboard**
   - Revenue tracking
   - User growth charts
   - Appointment analytics
   - Peak usage times

## Troubleshooting

### Common Issues

1. **403 Forbidden on admin endpoints**
   - Verify user has ADMIN role in database
   - Check session is valid
   - Confirm correct user logged in

2. **No data displaying**
   - Check browser console for API errors
   - Verify backend is running
   - Confirm database has data
   - Check network tab for failed requests

3. **Responsive layout broken**
   - Clear browser cache
   - Check if CSS file is loaded
   - Verify media queries in AdminPages.css

## API Reference

All API endpoints require:
- Valid session cookie
- ADMIN role
- Content-Type: application/json

### Response Format

Success (200):
```json
{
  "totalUsers": 150,
  "totalPets": 450,
  ...
}
```

Error (403):
```json
{
  "message": "Access Denied: Admin role required"
}
```

Error (500):
```json
{
  "message": "Failed to fetch [resource]"
}
```

## Support

For issues or questions:
1. Check logs: `tail -f application.log`
2. Verify database connection
3. Review error messages in browser console
4. Check Spring Boot startup logs
5. Ensure admin user exists and is active
