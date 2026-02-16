# MILESTONE 2 - QUICK START GUIDE

## 🎬 Quick Test Instructions

### 1️⃣ Start the Application
```bash
# Backend (Spring Boot)
cd petcare
./mvnw spring-boot:run

# Frontend (React)
cd petcare-frontend
npm start
```

Access at: http://localhost:3000

---

## 2️⃣ Login Test Accounts

Create test accounts or use existing ones:

**Pet Owner Account:**
- Email: `owner@test.com`
- Password: `password123`
- Role: PET_OWNER

**Doctor Account:**
- Email: `doctor@test.com`
- Password: `password123`
- Role: VETERINARY_DOCTOR

---

## 3️⃣ Test Appointment Booking

### As Pet Owner:
1. Login with pet owner credentials
2. Go to Dashboard
3. Click "📋 Book Appointment" button in right sidebar
4. Fill the form:
   - Select a doctor from dropdown
   - Choose future date (e.g., tomorrow)
   - Pick a time (e.g., 10:00 AM)
   - Enter reason: "General Checkup"
5. Click "Book & Pay"
6. Razorpay payment window opens

### Razorpay Test Payment:
- Card: `4111 1111 1111 1111`
- CVV: `123`
- Expiry: `12/25`
- Click Pay

✅ Success message appears → Appointment booked as PENDING

---

## 4️⃣ View Appointments

### As Pet Owner:
1. Dashboard → Click "Appointments" in Quick Overview
2. See your appointment in PENDING status
3. Can filter by status using tabs

### As Doctor:
1. Go to Doctor Dashboard
2. Click "View Appointments" card
3. See pet owner's appointment in PENDING status
4. Can filter by status

---

## 5️⃣ Test Appointment Actions

### Doctor Approving Appointment:
1. View appointments as doctor
2. Filter to show PENDING appointments
3. Click "✓ Approve" button
4. Status changes to APPROVED
5. Refresh to confirm

### Doctor Marking Complete:
1. View appointments with APPROVED status
2. Click "✅ Mark Complete" button
3. Status changes to COMPLETED
4. Appointment history is permanent

### Doctor Rejecting:
1. View PENDING appointments
2. Click "✗ Reject" button
3. Status changes to REJECTED

---

## 📊 Status Indicators

| Status | Icon | Color | Meaning |
|--------|------|-------|---------|
| PENDING | ⏳ | Yellow | Awaiting doctor approval |
| APPROVED | ✓ | Green | Doctor approved |
| COMPLETED | ✅ | Blue | Consultation done |
| REJECTED | ✗ | Red | Doctor rejected |

**Payment Status:**
| Status | Meaning |
|--------|---------|
| SUCCESS | Payment successful, appointment active |
| PENDING | Awaiting payment |
| FAILED | Payment failed |

---

## 🔌 API Endpoints Reference

### Doctors
```
GET /api/appointments/doctors
GET /api/appointments/doctors/{doctorId}
```

### Appointments
```
POST /api/appointments
GET /api/appointments/user/{userId}
GET /api/appointments/doctor/{doctorId}
GET /api/appointments/{id}

PUT /api/appointments/{id}/order/{orderId}
POST /api/appointments/verify-payment

PUT /api/appointments/{id}/approve
PUT /api/appointments/{id}/reject
PUT /api/appointments/{id}/complete
```

---

## 🐛 Troubleshooting

### Issue: "Payment gateway not loaded"
**Solution:** Refresh the page, ensure internet connection

### Issue: "Appointment not saved after payment"
**Solution:** Check backend is running, payment status should be SUCCESS

### Issue: "Doctor can't see appointments"
**Solution:** Ensure pet owner's appointment date is >= today, check doctor ID matches

### Issue: Razorpay popup not opening
**Solution:** Check browser console, may need to disable popup blockers

### Issue: "Unauthorized access" error
**Solution:** Verify you're logged in, session may have expired. Log out and login again

---

## 📱 Responsive Design

✅ Works on Desktop (1920px+)
✅ Works on Tablet (768px - 1024px)
✅ Works on Mobile (< 768px)

Tested card layouts adjust automatically.

---

## ✨ Features Included

### Pet Owner:
- ✅ Book appointments with doctors
- ✅ View all appointments
- ✅ Filter by appointment status
- ✅ See consultation fee
- ✅ Track payment status
- ✅ View appointment history

### Doctor:
- ✅ View assigned appointments
- ✅ Approve/Reject appointments
- ✅ Mark appointments complete
- ✅ Filter appointments by status
- ✅ See pet owner details
- ✅ Manage appointment workflow

### System:
- ✅ Razorpay payment integration
- ✅ Status workflow (PENDING → APPROVED/REJECTED → COMPLETED)
- ✅ Payment verification
- ✅ Session-based authentication
- ✅ Authorization checks
- ✅ Appointment database persistence

---

## 🎯 Expected Test Results

### ✅ Booking Flow Works
- Form submits successfully
- Razorpay payment processes
- Appointment saves in database

### ✅ Doctor Actions Work
- Can approve appointments
- Can reject appointments
- Can mark complete appointments
- Status changes immediately

### ✅ Filtering Works
- Appointment list filters by status
- Shows correct count per status
- Tab switching works smooth

### ✅ Authorization Works
- Pet owner sees only own appointments
- Doctor sees only assigned appointments
- Unauthorized access returns 401

### ✅ UI/UX Works
- Modals open/close smoothly
- Buttons are responsive
- Status badges show correctly
- Mobile layout adjusts properly

---

## 📞 Contact

For issues or questions:
1. Check server logs: Terminal running `mvn spring-boot:run`
2. Check browser console: F12 in browser
3. Verify credentials: Check login works first
4. Test Razorpay: Use provided test cards

---

**Ready to test! Happy coding! 🚀**
