# 🧪 MILESTONE 3 - QUICK TEST GUIDE

## Phase 1: Dashboard Statistics

### Backend Testing (Postman/cURL)

#### 1. Pet Owner Stats
```bash
GET http://localhost:8080/api/stats/owner
Authorization: Session Cookie (login as pet owner)
```

**Expected Response:**
```json
{
  "totalPets": 2,
  "upcomingAppointments": 1,
  "pendingPrescriptions": 0,
  "healthRecords": 3
}
```

---

#### 2. Doctor Stats
```bash
GET http://localhost:8080/api/stats/doctor
Authorization: Session Cookie (login as doctor)
```

**Expected Response:**
```json
{
  "totalAppointments": 15,
  "pendingAppointmentsToday": 2,
  "uniquePetsTreated": 8,
  "appointmentsThisMonth": 5
}
```

---

#### 3. Admin Stats
```bash
GET http://localhost:8080/api/stats/admin
Authorization: Session Cookie (login as admin)
```

**Expected Response:**
```json
{
  "totalOwners": 10,
  "totalDoctors": 5,
  "totalPets": 18,
  "totalAppointmentsThisMonth": 25,
  "pendingDoctorApprovals": 2
}
```

---

### Frontend Testing (Browser)

#### Pet Owner Dashboard
1. Login as pet owner
2. Navigate to dashboard
3. **Check Quick Overview panel (right sidebar):**
   - Total Pets: Shows count of your pets
   - Upcoming Appointments: Shows appointments in next 7 days
   - Prescriptions: Shows prescription count
   - Health Records: Shows medical history count
4. **Verify:**
   - Stats load (not showing "–")
   - Numbers match your data
   - Quick action buttons work

#### Doctor Dashboard
1. Login as doctor
2. Navigate to dashboard
3. **Check stat cards:**
   - Total Appointments: All your appointments
   - Pending Today: Today's pending appointments
   - Pets Treated: Unique pets you've seen
   - This Month: Appointments this month
4. **Verify:**
   - All numbers are valid (not NaN or undefined)
   - Loading state shows briefly
   - Quick links work

#### Admin Dashboard
1. Login as admin
2. Navigate to dashboard
3. **Check stat cards:**
   - Pet Owners: Count of owners
   - Vet Doctors: Count of doctors
   - Total Pets: All pets in system
   - Appointments This Month: Current month appointments
   - Pending Approvals: Doctors awaiting approval
   - Total Users: Sum of owners + doctors
4. **Verify:**
   - All stats displayed
   - Quick access links work

---

## 🔍 Debugging Tips

### If stats show "–" or don't load:

1. **Check browser console:**
   ```javascript
   // Look for errors like:
   Failed to fetch stats: [error details]
   ```

2. **Verify backend is running:**
   ```bash
   # Check if Spring Boot is running on port 8080
   curl http://localhost:8080/api/health
   ```

3. **Check authentication:**
   - Make sure you're logged in
   - Check browser cookies for JSESSIONID
   - Try logging out and back in

4. **Check browser network tab:**
   - Look for `/api/stats/*` requests
   - Check response status (should be 200)
   - Check response body

### Common Issues:

| Issue | Cause | Solution |
|-------|-------|----------|
| Stats show 0 | No data in database | Add test data |
| 401 Unauthorized | Not logged in | Login again |
| 403 Forbidden | Wrong role | Login with correct role |
| 500 Server Error | Backend issue | Check backend logs |
| Infinite loading | Network issue | Check backend connection |

---

## ✅ Success Criteria

### All tests pass if:
- ✅ All three stat endpoints return 200 OK
- ✅ Response data types are correct (numbers, not strings)
- ✅ Frontend displays stats without errors
- ✅ Stats update when you perform actions (e.g., add pet)
- ✅ Loading states work properly
- ✅ No console errors in browser

---

## 🎯 Quick Manual Test Flow

### Complete End-to-End Test (5 minutes):

1. **Pet Owner Test:**
   - Login as owner → Check stats
   - Add a pet → Refresh → Stats updated?
   - Book appointment → Refresh → Upcoming count increased?

2. **Doctor Test:**
   - Login as doctor → Check stats
   - Approve an appointment → Stats updated?

3. **Admin Test:**
   - Login as admin → Check stats
   - Approve a doctor → Pending approvals decreased?

---

**Test Status:** Ready to test Phase 1 ✅  
**Next Phase:** Notification System 🔔
