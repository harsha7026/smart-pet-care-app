# Milestone 3: Complete API Documentation

## Base URL
```
http://localhost:8080
```

## Authentication
All endpoints (except Login/Register) require:
- Session cookie: `JSESSIONID` (set automatically by Spring)
- OR Authentication header with valid Bearer token

---

## 🛒 CART API ENDPOINTS

### 1. Get User's Cart
```http
GET /api/cart
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 5,
  "items": [
    {
      "id": 10,
      "cartId": 1,
      "productId": 2,
      "quantity": 3,
      "product": {
        "id": 2,
        "categoryId": 1,
        "name": "Dog Food Premium",
        "description": "High quality dog food",
        "price": 500.00,
        "stockQuantity": 50,
        "imageUrl": "...",
        "isActive": true
      },
      "subtotal": 1500.00
    }
  ],
  "totalAmount": 1500.00,
  "itemCount": 1
}
```

**Error (401 Unauthorized):**
```json
{
  "error": "User not authenticated"
}
```

---

### 2. Add Product to Cart
```http
POST /api/cart/add
Content-Type: application/json

{
  "productId": 2,
  "quantity": 1
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 5,
  "items": [...],
  "totalAmount": 1500.00,
  "itemCount": 1
}
```

**Error (400 Bad Request - Out of stock):**
```json
{
  "error": "Insufficient stock. Available: 5, Requested: 10"
}
```

**Error (404 Not Found):**
```json
{
  "error": "Product not found or is inactive"
}
```

---

### 3. Update Cart Item Quantity
```http
PUT /api/cart/update
Content-Type: application/json

{
  "cartItemId": 10,
  "quantity": 5
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 5,
  "items": [...],
  "totalAmount": 2500.00,
  "itemCount": 1
}
```

**Error (400 Bad Request):**
```json
{
  "error": "Unauthorized: You don't have permission to update this item"
}
```

---

### 4. Remove Item from Cart
```http
DELETE /api/cart/remove/10
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 5,
  "items": [],
  "totalAmount": 0.00,
  "itemCount": 0
}
```

---

### 5. Clear Cart
```http
DELETE /api/cart/clear
```

**Response (200 OK):**
```json
{
  "message": "Cart cleared successfully"
}
```

---

## 📦 PRODUCT API ENDPOINTS

### PUBLIC ENDPOINTS

#### 1. List Active Products
```http
GET /api/products
GET /api/products?search=dog&category=1
GET /api/products?search=food
```

**Query Parameters:**
- `search` (optional): Search by product name
- `category` (optional): Filter by category ID

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "categoryId": 1,
    "name": "Dog Food",
    "description": "Nutritious dog food",
    "price": 500.00,
    "stockQuantity": 100,
    "imageUrl": "http://...",
    "isActive": true,
    "createdAt": "2024-01-10T10:00:00",
    "updatedAt": "2024-01-10T10:00:00"
  },
  {
    "id": 2,
    "categoryId": 1,
    "name": "Cat Food",
    "description": "Premium cat food",
    "price": 400.00,
    "stockQuantity": 75,
    "imageUrl": "http://...",
    "isActive": true,
    "createdAt": "2024-01-10T10:00:00",
    "updatedAt": "2024-01-10T10:00:00"
  }
]
```

#### 2. Get Product Details
```http
GET /api/products/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "categoryId": 1,
  "name": "Dog Food",
  "description": "Nutritious dog food",
  "price": 500.00,
  "stockQuantity": 100,
  "imageUrl": "http://...",
  "isActive": true,
  "createdAt": "2024-01-10T10:00:00",
  "updatedAt": "2024-01-10T10:00:00"
}
```

---

### ADMIN ENDPOINTS

#### 1. List All Products (including inactive)
```http
GET /api/admin/products
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "categoryId": 1,
    "name": "Dog Food",
    "price": 500.00,
    "stockQuantity": 100,
    "isActive": true,
    ...
  },
  {
    "id": 99,
    "categoryId": 2,
    "name": "Old Product",
    "price": 250.00,
    "stockQuantity": 5,
    "isActive": false,  // Soft deleted product
    ...
  }
]
```

#### 2. Create Product
```http
POST /api/admin/products
Content-Type: application/json

{
  "categoryId": 1,
  "name": "New Dog Food",
  "description": "Premium quality",
  "price": 550.00,
  "stockQuantity": 200,
  "imageUrl": "http://example.com/image.jpg",
  "isActive": true
}
```

**Response (201 Created):**
```json
{
  "id": 100,
  "categoryId": 1,
  "name": "New Dog Food",
  "description": "Premium quality",
  "price": 550.00,
  "stockQuantity": 200,
  "imageUrl": "http://example.com/image.jpg",
  "isActive": true,
  "createdAt": "2024-01-15T14:30:00",
  "updatedAt": "2024-01-15T14:30:00"
}
```

**Error (400 Bad Request):**
```json
{
  "error": "Price must be greater than 0"
}
```

#### 3. Update Product
```http
PUT /api/admin/products/1
Content-Type: application/json

{
  "categoryId": 1,
  "name": "Premium Dog Food",
  "description": "Updated description",
  "price": 600.00,
  "stockQuantity": 150,
  "imageUrl": "http://example.com/updated.jpg",
  "isActive": true
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "categoryId": 1,
  "name": "Premium Dog Food",
  "description": "Updated description",
  "price": 600.00,
  "stockQuantity": 150,
  "imageUrl": "http://example.com/updated.jpg",
  "isActive": true,
  "createdAt": "2024-01-10T10:00:00",
  "updatedAt": "2024-01-15T15:00:00"
}
```

#### 4. Delete Product (Soft Delete)
```http
DELETE /api/admin/products/1
```

**Response (204 No Content):**
```
(Empty body - product marked as inactive)
```

---

## 📋 ORDER API ENDPOINTS

### PET OWNER ENDPOINTS

#### 1. Get My Orders
```http
GET /api/orders/my
```

**Response (200 OK):**
```json
[
  {
    "id": 15,
    "userId": 5,
    "status": "DELIVERED",
    "paymentStatus": "SUCCESS",
    "totalAmount": 1500.00,
    "shippingAddress": "123 Main St, City, State 12345, Phone: 9876543210",
    "items": [
      {
        "id": 51,
        "orderId": 15,
        "productId": 2,
        "quantity": 3,
        "unitPrice": 500.00,
        "subtotal": 1500.00,
        "product": {
          "id": 2,
          "name": "Dog Food Premium",
          "price": 500.00
        }
      }
    ],
    "createdAt": "2024-01-10T10:00:00",
    "expectedDeliveryDate": "2024-01-15T00:00:00",
    "razorpayOrderId": "order_KqJzHvj8Whsek9",
    "razorpayPaymentId": "pay_KqJzHvj8Whsek9"
  }
]
```

#### 2. Get Order Details
```http
GET /api/orders/15
```

**Response (200 OK):**
```json
{
  "id": 15,
  "userId": 5,
  "status": "DELIVERED",
  "paymentStatus": "SUCCESS",
  "totalAmount": 1500.00,
  "shippingAddress": "123 Main St, City, State 12345, Phone: 9876543210",
  "items": [...],
  "createdAt": "2024-01-10T10:00:00",
  "expectedDeliveryDate": "2024-01-15T00:00:00",
  "razorpayOrderId": "order_...",
  "razorpayPaymentId": "pay_..."
}
```

**Error (403 Forbidden):**
```json
{
  "error": "Unauthorized: This order belongs to another user"
}
```

#### 3. Create Order (Checkout)
```http
POST /api/orders/checkout
Content-Type: application/json

{
  "fullAddress": "123 Main St, City, State 12345, Phone: 9876543210",
  "items": [
    {
      "productId": 2,
      "quantity": 3,
      "unitPrice": 500.00
    }
  ],
  "totalAmount": 1500.00
}
```

**Response (201 Created):**
```json
{
  "orderId": 15,
  "razorpayOrderId": "order_KqJzHvj8Whsek9",
  "razorpayKeyId": "rzp_test_1234567890",
  "amount": 150000
}
```

**Error (400 Bad Request - Empty cart):**
```json
{
  "error": "Cart is empty. Add products before checkout"
}
```

#### 4. Confirm Payment
```http
POST /api/orders/15/confirm-payment
Content-Type: application/json

{
  "razorpayPaymentId": "pay_KqJzHvj8Whsek9",
  "razorpayOrderId": "order_KqJzHvj8Whsek9",
  "razorpaySignature": "signature_generated_by_razorpay"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Payment confirmed successfully",
  "order": {
    "id": 15,
    "status": "PAYMENT_CONFIRMED",
    "paymentStatus": "SUCCESS",
    ...
  }
}
```

**Error (400 Bad Request - Invalid signature):**
```json
{
  "error": "Payment signature verification failed"
}
```

---

### ADMIN ENDPOINTS

#### 1. Get All Orders
```http
GET /api/orders/admin/all
```

**Response (200 OK):**
```json
[
  {
    "id": 15,
    "userId": 5,
    "status": "DELIVERED",
    "paymentStatus": "SUCCESS",
    "totalAmount": 1500.00,
    "shippingAddress": "...",
    "items": [...],
    "createdAt": "2024-01-10T10:00:00",
    "user": {
      "id": 5,
      "name": "John Doe",
      "email": "john@example.com",
      "phone": "9876543210"
    }
  },
  {
    "id": 16,
    "userId": 6,
    "status": "SHIPPED",
    "paymentStatus": "SUCCESS",
    ...
  }
]
```

#### 2. Update Order Status
```http
PATCH /api/orders/admin/15/status?status=SHIPPED&expectedDeliveryDate=2024-01-15
```

**Query Parameters:**
- `status` (required): New order status (PLACED, PAYMENT_CONFIRMED, PACKED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED)
- `expectedDeliveryDate` (optional): Expected delivery date in format YYYY-MM-DD

**Response (200 OK):**
```json
{
  "id": 15,
  "userId": 5,
  "status": "SHIPPED",
  "paymentStatus": "SUCCESS",
  "totalAmount": 1500.00,
  "expectedDeliveryDate": "2024-01-15T00:00:00",
  ...
}
```

**Side Effect:** Email sent to customer
```
Subject: Your Order #15 has been shipped!
Body: Your order is on its way. Expected delivery: 2024-01-15
```

**Error (404 Not Found):**
```json
{
  "error": "Order not found"
}
```

---

## 📧 EMAIL NOTIFICATIONS

### On Status Update
Each status change triggers an email with contextual message:

**PLACED:**
```
Subject: Order #15 Placed Successfully
Body: Your order has been placed successfully. We will prepare it for shipment soon.
```

**PAYMENT_CONFIRMED:**
```
Subject: Payment Confirmed for Order #15
Body: Your payment has been confirmed. We are now preparing your order.
```

**PACKED:**
```
Subject: Order #15 Packed and Ready
Body: Your order has been packed and is ready for shipment.
```

**SHIPPED:**
```
Subject: Order #15 Shipped!
Body: Your order is on its way. Expected delivery: 2024-01-15
```

**OUT_FOR_DELIVERY:**
```
Subject: Order #15 Out for Delivery
Body: Your order is out for delivery today. Please be available to receive it.
```

**DELIVERED:**
```
Subject: Order #15 Delivered!
Body: Your order has been delivered successfully. Thank you for shopping with us!
```

**CANCELLED:**
```
Subject: Order #15 Cancelled
Body: Your order has been cancelled. A refund will be processed within 5-7 business days.
```

---

## 🔄 Error Status Codes

| Code | Message | Meaning |
|------|---------|---------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 204 | No Content | Successful but no content to return |
| 400 | Bad Request | Invalid request (validation failed) |
| 401 | Unauthorized | Authentication required |
| 403 | Forbidden | Permission denied |
| 404 | Not Found | Resource not found |
| 500 | Internal Server Error | Server error |

---

## 🧪 Testing with Curl

### Get Cart
```bash
curl -X GET http://localhost:8080/api/cart \
  -H "Content-Type: application/json" \
  --cookie "JSESSIONID=<session-id>"
```

### Add to Cart
```bash
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{"productId":2,"quantity":1}' \
  --cookie "JSESSIONID=<session-id>"
```

### Create Order
```bash
curl -X POST http://localhost:8080/api/orders/checkout \
  -H "Content-Type: application/json" \
  -d '{
    "fullAddress":"123 Main St, City, State 12345",
    "items":[{"productId":2,"quantity":3,"unitPrice":500.00}],
    "totalAmount":1500.00
  }' \
  --cookie "JSESSIONID=<session-id>"
```

### Update Order Status (Admin)
```bash
curl -X PATCH "http://localhost:8080/api/orders/admin/15/status?status=SHIPPED&expectedDeliveryDate=2024-01-15" \
  -H "Content-Type: application/json" \
  --cookie "JSESSIONID=<session-id>"
```

---

## 🔑 Test Card for Razorpay

**Card Number:** `4111 1111 1111 1111`  
**Expiry:** Any future date (e.g., 12/25)  
**CVV:** Any 3 digits (e.g., 123)  
**OTP:** 123456

---

## 📊 Response Time Goals

| Endpoint | Target |
|----------|--------|
| Get Cart | < 200ms |
| Add to Cart | < 500ms |
| List Products | < 300ms |
| Create Order | < 1000ms |
| Get Orders | < 400ms |
| Update Status | < 800ms |

---

## 🔐 Security Notes

1. **All endpoints** (except public product list) require authentication
2. **User isolation** - Users can only see their own cart and orders
3. **Admin-only** endpoints - Product management and order status updates
4. **Payment verification** - Razorpay signature validated before order confirmation
5. **SQL injection** - JPA queries prevent SQL injection
6. **XSS protection** - React escapes all user input

---

**API Documentation Complete ✅**

For implementation details, see MILESTONE_3_IMPLEMENTATION_COMPLETE.md
