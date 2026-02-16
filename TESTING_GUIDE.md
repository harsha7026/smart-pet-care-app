# Quick Start Guide - Testing Doctor Approval Workflow

## Prerequisites
- Backend running on `localhost:8080`
- Frontend running on `localhost:3000`
- MySQL database running
- Test Razorpay credentials configured

## Step-by-Step Testing

### 1. Setup Verification (1 minute)

**Check Backend**:
```powershell
# Navigate to backend
cd "c:\Users\Nikhil s\Desktop\cgpt\petcare"

# Start backend
mvnw.cmd spring-boot:run
```

Wait for: `Started PetcareApplication in X seconds`

**Check Frontend** (in new terminal):
```powershell
# Navigate to frontend
cd "c:\Users\Nikhil s\Desktop\cgpt\petcare-frontend"

# Start frontend
npm start
```

Wait for: `Compiled successfully!` and browser opens to `localhost:3000`

---

### 2. Create Test Users (if not already created) (2 minutes)

**Register Pet Owner**:
1. Open `http://localhost:3000/register`
2. Fill form:
   - Name: Test Owner
   - Email: owner@test.com
   - Password: password123
   - Phone: 9876543210
   - Role: PET_OWNER
3. Click "Register"

**Register Doctor**:
1. Open `http://localhost:3000/register`
2. Fill form:
   - Name: Dr. Test
   - Email: doctor@test.com
   - Password: password123
   - Phone: 9876543211
   - Role: DOCTOR
   - Specialization: General Veterinarian
   - Consultation Fee: 500
3. Click "Register"

---

### 3. Pet Owner: Create Pet with Health Records (3 minutes)

**Login as Pet Owner**:
1. Open `http://localhost:3000/login`
2. Email: owner@test.com, Password: password123
3. Click "Login"

**Create Pet**:
1. Navigate to "My Pets" or "Add Pet"
2. Fill form:
   - Name: Fluffy
   - Species: Dog
   - Breed: Labrador Retriever
   - Age: 3
   - Weight: 32
   - Medical Notes: Generally healthy. Prone to ear infections.
3. Click "Save"

**Add Medical History** (optional but recommended for testing):
1. Go to pet profile
2. Add Medical Record:
   - Visit Date: Today's date
   - Diagnosis: Routine Checkup
   - Treatment: Preventive Care
   - Notes: All vitals normal
3. Save

**Add Vaccination Record** (optional but recommended):
1. Go to pet profile
2. Add Vaccination:
   - Vaccine Name: Rabies Vaccine
   - Administered Date: Today
   - Due Date: Next year
   - Status: Up-to-date
3. Save

---

### 4. Pet Owner: Book Appointment (3 minutes)

**Book Appointment**:
1. Navigate to "Book Appointment" or Dashboard → Book
2. Select Doctor: Dr. Test (fee auto-loads: ₹500)
3. Select Pet: Fluffy
4. Select Date: Tomorrow's date (or any future date)
5. ❌ **NO TIME FIELD** - This is correct!
6. Reason: Vaccination Check
7. Click "Book Appointment"

**Complete Payment** (Razorpay Test):
1. Razorpay Checkout popup appears
2. Card Number: `4111 1111 1111 1111`
3. Expiry: Any future date (e.g., 12/25)
4. CVV: Any 3 digits (e.g., 123)
5. Name: Test User
6. Click "Pay"
7. Wait for success message: "Payment successful!"

**Verify in MyAppointments**:
1. Navigate to "My Appointments"
2. You should see:
   ```
   Dr. Test
   Pet: Fluffy
   Status: ⏳ PENDING
   Payment: SUCCESS ✓
   Date: [Tomorrow's date]
   Time: ⏳ Time: Pending Doctor Assignment
   ```
3. ✅ **If you see "Pending Doctor Assignment", perfect!**

---

### 5. Doctor: Review & Approve Appointment (5 minutes)

**Login as Doctor** (in incognito/private window or different browser):
1. Open `http://localhost:3000/login`
2. Email: doctor@test.com, Password: password123
3. Click "Login"

**View Appointments**:
1. Navigate to "Appointments" or Doctor Dashboard
2. Click on "Pending" filter tab (should show "Pending (1)")
3. You should see appointment card:
   ```
   Test Owner
   Pet: Fluffy
   Status: ⏳ PENDING
   Payment: SUCCESS
   Date: [Tomorrow's date]
   Time: ⏳ Assign time during approval
   Reason: Vaccination Check
   Fee: ₹500.00
   ```

**Open Review Modal**:
1. Click "📋 Review & Approve" button
2. Modal should open showing:
   - ✅ Appointment details (owner, date, reason, fee)
   - ✅ Pet info (Fluffy, Labrador Retriever, 3 years, 32 kg)
   - ✅ Medical notes section
   - ✅ Medical History (if you added any)
   - ✅ Vaccination Records (if you added any)
3. **Review all health information**

**Assign Time**:
1. Click "✓ Approve & Assign Time" button
2. Time picker modal opens
3. Select time: `14:30` (2:30 PM) or any time
4. Click "Confirm"
5. Wait for success message: "Appointment approved with time slot assigned!"
6. Modal closes automatically
7. Appointment list refreshes

**Verify Approval**:
1. Click "Approved" filter tab
2. Appointment should now show:
   ```
   Test Owner
   Pet: Fluffy
   Status: ✓ APPROVED
   Payment: SUCCESS
   Date: [Tomorrow's date] at 14:30  ← TIME NOW SHOWN!
   ```

---

### 6. Pet Owner: Verify Time Assignment (1 minute)

**Check MyAppointments** (switch back to pet owner browser):
1. Refresh page or navigate to "My Appointments"
2. Appointment should now show:
   ```
   Dr. Test
   Pet: Fluffy
   Status: ✓ APPROVED
   Date: [Tomorrow's date] at 14:30  ← TIME CONFIRMED!
   ```
3. ✅ **If owner sees the time, workflow complete!**

---

## Expected Results Checklist

### ✅ Pet Owner Experience
- [ ] Can book appointment without selecting time
- [ ] Razorpay payment popup appears (real, not simulated)
- [ ] Payment succeeds with test card
- [ ] MyAppointments shows "⏳ Time: Pending Doctor Assignment"
- [ ] After doctor approval, time appears in appointment card
- [ ] Status changes from PENDING to APPROVED

### ✅ Doctor Experience
- [ ] PENDING appointments show "⏳ Assign time during approval"
- [ ] "📋 Review & Approve" button opens modal
- [ ] Modal displays complete pet health records
- [ ] Medical history displays (if exists)
- [ ] Vaccination records display (if exists)
- [ ] Time picker modal opens when clicking "Approve & Assign Time"
- [ ] Can select time from time input
- [ ] Appointment updates to APPROVED with time after confirmation
- [ ] Appointment moves to "Approved" filter tab

### ✅ Technical Verification
- [ ] Backend logs show Razorpay order creation
- [ ] Payment signature verification succeeds
- [ ] GET /api/appointments/{id}/pet-health returns health data
- [ ] PUT /api/appointments/{id}/approve accepts time parameter
- [ ] Database: appointment.appointment_time column updates
- [ ] Database: appointment.status changes to APPROVED
- [ ] No console errors in browser (check DevTools)
- [ ] No backend exceptions (check terminal logs)

---

## Troubleshooting

### Issue: "Payment verification failed"
**Cause**: Razorpay signature validation failing
**Fix**: 
1. Check application.properties has correct keys
2. Verify signature verification uses JSONObject (not Map)
3. Check backend logs for exact error

### Issue: "Time still shows as pending after approval"
**Cause**: Frontend not refreshing or backend not updating time
**Fix**:
1. Manually refresh browser (F5)
2. Check backend logs for approval success
3. Check database: `SELECT * FROM appointments WHERE id = X`
4. Verify appointmentTime column has value

### Issue: "Modal not opening"
**Cause**: Component not imported or state not updating
**Fix**:
1. Check browser console for errors
2. Verify DoctorAppointmentDetail.jsx exists
3. Check DoctorAppointments.jsx imports

### Issue: "Health records not loading"
**Cause**: No health records exist or API error
**Fix**:
1. Add at least one medical history or vaccination record
2. Check backend endpoint: GET /api/appointments/{id}/pet-health
3. Verify pet has health data in database

### Issue: "Cannot select time"
**Cause**: Time input not working or modal not opening
**Fix**:
1. Check time picker modal rendering
2. Try different browser (Chrome, Firefox, Edge)
3. Check CSS for time-picker-modal class

---

## Advanced Testing Scenarios

### Scenario 1: Multiple Pending Appointments
1. Create 3 different appointments with different dates
2. Verify all show in PENDING filter
3. Approve them one by one
4. Verify each moves to APPROVED with unique times

### Scenario 2: Reject Appointment
1. Create appointment
2. Doctor clicks "✗ Reject" instead of "Review & Approve"
3. Verify status changes to REJECTED
4. Verify owner sees REJECTED status
5. Verify no time assignment needed

### Scenario 3: Empty Health Records
1. Create pet with NO medical history or vaccinations
2. Book appointment
3. Verify modal still opens
4. Verify sections show "No records" or are empty
5. Verify doctor can still approve with time

### Scenario 4: Concurrent Approvals
1. Create 2 appointments
2. Open review modal for first
3. Open review modal for second (different tab)
4. Approve both with different times
5. Verify both update correctly

---

## Database Verification Queries

**Check Appointments**:
```sql
SELECT 
  id, 
  appointment_date, 
  appointment_time, 
  status, 
  payment_status,
  razorpay_payment_id
FROM appointments
ORDER BY created_at DESC
LIMIT 5;
```

**Check Pet Health Records**:
```sql
-- Medical History
SELECT * FROM medical_history WHERE pet_id = [your_pet_id];

-- Vaccinations
SELECT * FROM vaccination_records WHERE pet_id = [your_pet_id];
```

**Check User Roles**:
```sql
SELECT id, name, email, role FROM users;
```

---

## API Endpoint Manual Testing (via Postman/cURL)

**Get Pet Health Summary**:
```bash
curl -X GET http://localhost:8080/api/appointments/{appointmentId}/pet-health \
  -H "Cookie: JSESSIONID=your_session_id"
```

**Approve with Time**:
```bash
curl -X PUT http://localhost:8080/api/appointments/{appointmentId}/approve \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=your_session_id" \
  -d '{"appointmentTime": "14:30"}'
```

---

## Browser DevTools Checks

**Network Tab**:
1. Open DevTools (F12) → Network
2. Book appointment → Check for:
   - POST /api/appointments/create (200 OK)
   - POST /api/appointments/{id}/create-order (200 OK)
   - POST /api/appointments/verify-payment (200 OK)
3. Doctor approves → Check for:
   - GET /api/appointments/{id}/pet-health (200 OK)
   - PUT /api/appointments/{id}/approve (200 OK)

**Console Tab**:
- Should have NO red errors
- May have warnings (acceptable)
- Check for successful API response logs

**Application Tab** (Storage):
- Check Cookies → Should see JSESSIONID
- Verify session cookie exists

---

## Success Criteria

### ✅ Workflow Complete When:
1. Pet owner books with DATE only (no time)
2. Payment succeeds with real Razorpay
3. Appointment shows "Pending Doctor Assignment"
4. Doctor sees PENDING appointment
5. Doctor clicks "Review & Approve"
6. Modal shows complete health records
7. Doctor assigns time (e.g., 14:30)
8. Appointment updates to APPROVED
9. Both owner and doctor see confirmed time
10. No errors in console or backend logs

---

## Estimated Testing Time
- **Quick Test** (happy path only): 10-15 minutes
- **Comprehensive Test** (all scenarios): 30-45 minutes
- **Database Verification**: +5 minutes
- **API Testing**: +10 minutes

---

## Next Steps After Testing

### If All Tests Pass ✅:
- Consider UAT (User Acceptance Testing)
- Prepare for production deployment
- Update documentation
- Create user training materials

### If Issues Found ❌:
1. Note exact error messages
2. Check browser console logs
3. Check backend terminal logs
4. Check database state
5. Refer to Troubleshooting section above
6. Create issue ticket with details

---

## Contact & Support
If you encounter issues during testing:
1. Check logs (backend terminal + browser console)
2. Review documentation files
3. Verify database schema
4. Check configuration (application.properties)
5. Test with different test data

Happy Testing! 🎉
