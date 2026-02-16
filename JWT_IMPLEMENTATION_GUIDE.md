# JWT Security Implementation Guide

## 🎯 Overview

Your Smart Pet Care application has been successfully upgraded from **session-based authentication** to **JWT (JSON Web Token) authentication**. This provides better scalability, stateless authentication, and improved security for your API.

---

## 📋 What Changed

### Backend Changes

#### 1. **Dependencies Added** ([pom.xml](petcare/pom.xml))
- Added `io.jsonwebtoken:jjwt-api:0.12.3`
- Added `io.jsonwebtoken:jjwt-impl:0.12.3`
- Added `io.jsonwebtoken:jjwt-jackson:0.12.3`

#### 2. **New Security Components**

**JwtUtil.java** - JWT Token Management
- Location: `petcare/src/main/java/com/petcare/security/JwtUtil.java`
- Features:
  - Generate JWT tokens with user info (email, role, userId)
  - Validate tokens
  - Extract claims (username, role, userId)
  - 24-hour token expiration (configurable)

**JwtAuthenticationFilter.java** - Request Interceptor
- Location: `petcare/src/main/java/com/petcare/security/JwtAuthenticationFilter.java`
- Features:
  - Intercepts all HTTP requests
  - Extracts JWT from `Authorization: Bearer <token>` header
  - Validates token and sets Spring Security context
  - Adds `userId` to request attributes

#### 3. **Updated Components**

**SecurityConfig.java**
- Removed session management (now stateless)
- Removed form-login configuration
- Added JWT filter before UsernamePasswordAuthenticationFilter
- Added AuthenticationManager bean
- Added PasswordEncoder bean
- Session policy: `STATELESS`

**AuthController.java**
- Updated `/auth/login` endpoint:
  - Now accepts JSON instead of form data
  - Authenticates using AuthenticationManager
  - Returns JWT token on successful login
  - Returns user info (id, email, name, role)

**UserService.java**
- Added `getUserByEmail()` method for authentication

**UserController.java** (NEW)
- Created `/api/user/me` endpoint
- Returns current authenticated user information
- Extracts user from SecurityContext

#### 4. **Configuration** ([application.properties](petcare/src/main/resources/application.properties))
```properties
# JWT Configuration
jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongForHS256Algorithm
jwt.expiration=86400000  # 24 hours in milliseconds
```

---

### Frontend Changes

#### 1. **axios.js** - API Client
- Location: `petcare-frontend/src/api/axios.js`
- Changes:
  - Request interceptor: Adds `Authorization: Bearer <token>` header
  - Response interceptor: Handles 401 errors, clears token, redirects to login
  - Reads token from `localStorage`

#### 2. **Login.js** - Login Page
- Location: `petcare-frontend/src/pages/Login.js`
- Changes:
  - Sends JSON instead of form-urlencoded data
  - Stores JWT token in `localStorage` on successful login
  - Stores user data in `localStorage`
  - Navigates based on role from login response

#### 3. **AuthContext.js** - Authentication State Management
- Location: `petcare-frontend/src/context/AuthContext.js`
- Changes:
  - Checks for token in `localStorage` before API calls
  - Loads user from `localStorage` for faster initial load
  - Validates token by calling `/api/user/me`
  - Clears token and user data on logout
  - Handles token expiration gracefully

---

## 🔐 How JWT Authentication Works

### Login Flow
```
1. User enters email/password
   ↓
2. Frontend sends POST /auth/login with credentials
   ↓
3. Backend validates credentials
   ↓
4. Backend generates JWT token with user info
   ↓
5. Backend returns token + user data
   ↓
6. Frontend stores token in localStorage
   ↓
7. Frontend redirects to dashboard based on role
```

### Authenticated Request Flow
```
1. Frontend makes API request (e.g., GET /api/pets)
   ↓
2. axios interceptor adds "Authorization: Bearer <token>" header
   ↓
3. JwtAuthenticationFilter extracts token from header
   ↓
4. JwtAuthenticationFilter validates token
   ↓
5. If valid: Sets SecurityContext with user authentication
   ↓
6. Request proceeds to controller
   ↓
7. Controller has access to authenticated user
```

### Logout Flow
```
1. User clicks logout
   ↓
2. Frontend removes token from localStorage
   ↓
3. Frontend removes user data from localStorage
   ↓
4. Frontend redirects to login page
```

---

## 🧪 Testing the JWT Implementation

### 1. **Build the Backend**
```powershell
cd "c:\Users\Nikhil s\Desktop\cgpt\petcare"
mvn clean package -DskipTests
```

### 2. **Start the Backend**
```powershell
mvn spring-boot:run
```

### 3. **Install Frontend Dependencies** (if needed)
```powershell
cd "c:\Users\Nikhil s\Desktop\cgpt\petcare-frontend"
npm install
```

### 4. **Start the Frontend**
```powershell
npm start
```

### 5. **Test Login**
1. Navigate to `http://localhost:3000/login`
2. Enter credentials (e.g., existing user from database)
3. Check Browser DevTools → Application → Local Storage
4. You should see:
   - `token`: JWT token (long string)
   - `user`: User data object

### 6. **Test Authenticated Requests**
1. After login, navigate to any protected page
2. Open Browser DevTools → Network tab
3. Click on any API request
4. Check Request Headers → You should see:
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

### 7. **Test Token Expiration**
1. Login successfully
2. Manually edit the token in localStorage (corrupt it)
3. Refresh the page or make any API request
4. You should be redirected to login page

### 8. **Test Logout**
1. Login successfully
2. Click logout button
3. Check localStorage → token and user should be removed
4. Try accessing protected page → should redirect to login

---

## 🔍 API Testing with Postman/cURL

### Login Request
```bash
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "role": "PET_OWNER"
}
```

### Authenticated Request
```bash
GET http://localhost:8080/api/user/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "user@example.com",
  "phone": "1234567890",
  "role": "PET_OWNER",
  "status": "ACTIVE"
}
```

---

## 🛡️ Security Features

### ✅ What's Secure
- **Stateless Authentication**: No server-side session storage
- **Token-based**: Tokens contain user info (signed, not encrypted)
- **HMAC-SHA256 Signature**: Prevents token tampering
- **Expiration**: Tokens expire after 24 hours
- **HTTPS Ready**: Works with HTTPS for production
- **CORS Configured**: Only localhost:3000 allowed in development

### ⚠️ Important Security Notes
1. **Secret Key**: Change `jwt.secret` in production to a long, random string
2. **HTTPS**: Always use HTTPS in production
3. **Token Storage**: Tokens in localStorage are vulnerable to XSS
4. **Token Expiration**: 24 hours is reasonable; adjust based on your needs
5. **Refresh Tokens**: Consider implementing refresh tokens for better UX

---

## 📝 JWT Token Structure

A JWT token has 3 parts (separated by `.`):

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiUEVUX09XTkVSIiwidXNlcklkIjoxLCJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzA5NjQwMDAwLCJleHAiOjE3MDk3MjY0MDB9.signature
```

**Header** (Base64):
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload** (Base64):
```json
{
  "role": "PET_OWNER",
  "userId": 1,
  "sub": "user@example.com",
  "iat": 1709640000,
  "exp": 1709726400
}
```

**Signature**: HMAC-SHA256(header + payload, secret)

---

## 🔧 Configuration Options

### Token Expiration
Edit `application.properties`:
```properties
jwt.expiration=86400000  # 24 hours
jwt.expiration=3600000   # 1 hour
jwt.expiration=604800000 # 7 days
```

### Secret Key (IMPORTANT for Production)
```properties
# Development (current)
jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongForHS256Algorithm

# Production (CHANGE THIS!)
jwt.secret=YOUR_SUPER_SECRET_KEY_MINIMUM_256_BITS_USE_RANDOM_GENERATOR
```

**Generate a secure secret:**
```bash
# Linux/Mac
openssl rand -base64 64

# Windows PowerShell
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

---

## 🚨 Troubleshooting

### Issue: "401 Unauthorized" on all requests
**Solution:**
- Check if token is in localStorage
- Verify Authorization header is being sent
- Check token hasn't expired
- Ensure backend is running

### Issue: Token not being saved
**Solution:**
- Check login response contains `token` field
- Verify localStorage is enabled in browser
- Check browser console for errors

### Issue: "Invalid JWT signature"
**Solution:**
- Ensure `jwt.secret` hasn't changed
- Token may be corrupted
- Re-login to get new token

### Issue: Redirected to login after refresh
**Solution:**
- Check token expiration time
- Verify token is in localStorage
- Check `/api/user/me` endpoint works

---

## 📊 Migration from Session to JWT

### What Was Removed
- ❌ Spring Security form-login
- ❌ Session management (JSESSIONID cookies)
- ❌ Session storage on server
- ❌ form-urlencoded login request

### What Was Added
- ✅ JWT token generation and validation
- ✅ Bearer token authentication
- ✅ Stateless session policy
- ✅ localStorage token storage
- ✅ JSON login request/response

### Backward Compatibility
- ⚠️ **NOT backward compatible** with old session-based clients
- All clients must update to use JWT tokens
- Old session cookies will not work

---

## 🎯 Next Steps (Optional Enhancements)

### 1. **Refresh Tokens**
- Implement long-lived refresh tokens
- Short-lived access tokens (15 min)
- Refresh endpoint to get new access token

### 2. **Remember Me**
- Longer expiration for "remember me" option
- Separate token types

### 3. **Token Blacklist**
- Redis-based token blacklist for logout
- Revoke tokens before expiration

### 4. **Role-based Token Claims**
- Add more claims to JWT (permissions, features)
- Fine-grained access control

### 5. **Multi-device Management**
- Track active sessions per user
- Logout from specific devices

---

## 📚 Files Modified Summary

### Backend (7 files)
1. ✅ `petcare/pom.xml` - Added JWT dependencies
2. ✅ `petcare/src/main/resources/application.properties` - JWT config
3. ✅ `petcare/src/main/java/com/petcare/security/JwtUtil.java` - NEW
4. ✅ `petcare/src/main/java/com/petcare/security/JwtAuthenticationFilter.java` - NEW
5. ✅ `petcare/src/main/java/com/petcare/config/SecurityConfig.java` - Updated
6. ✅ `petcare/src/main/java/com/petcare/controller/AuthController.java` - Updated
7. ✅ `petcare/src/main/java/com/petcare/controller/UserController.java` - NEW
8. ✅ `petcare/src/main/java/com/petcare/service/UserService.java` - Added method

### Frontend (3 files)
1. ✅ `petcare-frontend/src/api/axios.js` - JWT interceptors
2. ✅ `petcare-frontend/src/pages/Login.js` - Token storage
3. ✅ `petcare-frontend/src/context/AuthContext.js` - Token management

---

## ✅ Implementation Complete

Your application now uses **JWT-based authentication**!

- **Token expiration**: 24 hours
- **Storage**: localStorage (client-side)
- **Format**: Bearer token in Authorization header
- **Security**: HMAC-SHA256 signature

**Status:** ✅ Ready for Testing

---

**Last Updated:** February 5, 2026  
**Implementation:** Complete  
**Status:** Production Ready (after security review)
