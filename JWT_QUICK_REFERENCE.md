# JWT Security - Quick Reference

## 🚀 Quick Start

### 1. Start Backend
```powershell
cd "c:\Users\Nikhil s\Desktop\cgpt\petcare"
.\mvnw.cmd spring-boot:run
```

### 2. Start Frontend
```powershell
cd "c:\Users\Nikhil s\Desktop\cgpt\petcare-frontend"
npm start
```

### 3. Login
- Navigate to `http://localhost:3000/login`
- Enter credentials
- JWT token stored in localStorage automatically

---

## 📝 Key Changes from Session-Based Auth

| Aspect | Before (Session) | After (JWT) |
|--------|-----------------|-------------|
| **Authentication** | Cookie (JSESSIONID) | Bearer Token in Header |
| **Storage** | Server-side session | Client-side (localStorage) |
| **State** | Stateful | Stateless |
| **Login Request** | form-urlencoded | JSON |
| **Login Response** | Success message | Token + User data |
| **Logout** | Server invalidates session | Client removes token |

---

## 🔐 Token Format

**Request Header:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Token Contains:**
- Email (subject)
- Role (PET_OWNER, VETERINARY_DOCTOR, ADMIN)
- User ID
- Expiration time (24 hours)

---

## 🧪 Testing

### Test Login
```bash
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "test@example.com",
  "password": "password123"
}
```

**Expected Response:**
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "test@example.com",
  "name": "Test User",
  "role": "PET_OWNER"
}
```

### Test Authenticated Request
```bash
GET http://localhost:8080/api/user/me
Authorization: Bearer YOUR_TOKEN_HERE
```

**Expected Response:**
```json
{
  "id": 1,
  "name": "Test User",
  "email": "test@example.com",
  "phone": "1234567890",
  "role": "PET_OWNER",
  "status": "ACTIVE"
}
```

---

## 🛠️ Configuration

**Backend** - `application.properties`:
```properties
jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongForHS256Algorithm
jwt.expiration=86400000  # 24 hours
```

**Frontend** - Token stored in:
```javascript
localStorage.getItem('token')
localStorage.getItem('user')
```

---

## 🔧 Troubleshooting

### ❌ 401 Unauthorized
- Check token exists: `localStorage.getItem('token')`
- Check token hasn't expired (24 hours)
- Verify Authorization header format: `Bearer <token>`
- Re-login to get fresh token

### ❌ Token not saved after login
- Check browser console for errors
- Verify localStorage is enabled
- Check login API returns `token` field

### ❌ Redirected to login after page refresh
- Check token in localStorage
- Verify token hasn't expired
- Check `/api/user/me` endpoint works
- Check browser console for errors

---

## 📊 Implementation Status

### ✅ Backend
- [x] JWT dependencies added
- [x] JwtUtil.java created
- [x] JwtAuthenticationFilter.java created
- [x] SecurityConfig updated (stateless)
- [x] AuthController updated (returns JWT)
- [x] UserController created (/api/user/me)
- [x] Compilation successful

### ✅ Frontend
- [x] axios.js updated (Bearer token)
- [x] Login.js updated (stores token)
- [x] AuthContext.js updated (manages token)
- [x] localStorage integration

---

## 🎯 Files Modified

**Backend (8 files):**
1. `pom.xml` - JWT dependencies
2. `application.properties` - JWT config
3. `security/JwtUtil.java` - NEW
4. `security/JwtAuthenticationFilter.java` - NEW
5. `config/SecurityConfig.java` - Updated
6. `controller/AuthController.java` - Updated
7. `controller/UserController.java` - NEW
8. `service/UserService.java` - Added method

**Frontend (3 files):**
1. `api/axios.js` - JWT interceptors
2. `pages/Login.js` - Token storage
3. `context/AuthContext.js` - Token management

---

## 📚 Documentation

- **Detailed Guide:** [JWT_IMPLEMENTATION_GUIDE.md](JWT_IMPLEMENTATION_GUIDE.md)
- **Main README:** [README.md](README.md)

---

## ✅ Status

**Implementation:** ✅ Complete  
**Compilation:** ✅ Success  
**Testing:** ⏳ Ready to test  
**Production:** ⚠️ Change jwt.secret before deploying

---

**Last Updated:** February 5, 2026
