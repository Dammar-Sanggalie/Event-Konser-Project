# Event Konser Project - Remaining Tasks Checklist

**Status:** Frontend 100% Complete ✅ | Backend 70% Complete ⏳

---

## 📋 SUMMARY YANG SUDAH SELESAI

### ✅ Frontend (100% Complete)
- **33 HTML Pages** - All user-facing features (booking, dashboard, admin, management)
- **4 CSS Modules** - components.css, admin.css, responsive.css, main.css
- **7 JS Modules** - api.js, auth.js, app.js, events.js, cart.js, payment.js, validation.js
- **Total Frontend Files:** 44 files, 5000+ lines of code

### ✅ Backend Architecture (70% Complete)
- **14 Controllers** - API endpoints for all entities
- **13 Services** - Business logic implementation
- **15 Models** - Database entities with relationships
- **12 Repositories** - Data access layer
- **Exception Handling** - Custom exceptions
- **Database** - MySQL with all tables created

---

## 📝 YANG MASIH HARUS DIKERJAKAN (BACKEND)

### ⏳ PRIORITY 1: CRITICAL FEATURES (WAJIB BUAT)

#### 1. **Authentication & Authorization** 
**Status:** Partial (Auth page ada, API belum lengkap)
- [ ] Implement JWT token generation in AuthController
- [ ] Add JWT filter for request validation
- [ ] Add role-based access control (RBAC) - Admin vs User
- [ ] Implement password encryption (BCrypt)
- [ ] Create `/api/auth/login` endpoint with token return
- [ ] Create `/api/auth/register` endpoint with validation
- [ ] Create `/api/auth/refresh` endpoint for token refresh
- [ ] Add @Secured/@PreAuthorize annotations to admin endpoints
- [ ] Test authentication flow end-to-end

**Files to create/modify:**
- `config/SecurityConfig.java` - Spring Security configuration
- `config/JwtTokenProvider.java` - JWT token generation/validation
- `controller/AuthController.java` - NEW (authentication endpoints)
- `service/AuthService.java` - NEW (authentication business logic)

**Expected Time:** 2-3 hours

---

#### 2. **Complete Order/Payment Flow**
**Status:** Partial (Controllers exist but logic incomplete)
- [ ] Implement `OrderService.createOrder()` - full ticket booking logic with validation
- [ ] Implement `OrderService.cancelOrder()` - refund logic
- [ ] Implement `PaymentService.processPayment()` - payment integration
- [ ] Implement `PaymentService.verifyPayment()- payment status verification
- [ ] Add transaction management (@Transactional) for order-payment sync
- [ ] Implement QR code generation for tickets
- [ ] Implement order status workflow (PENDING → PAID → CONFIRMED)
- [ ] Test cart checkout flow: book → payment → order creation

**Files to modify:**
- `service/OrderService.java` - Complete implementation
- `service/PaymentService.java` - Complete implementation
- `service/TicketService.java` - Validate ticket availability

**Expected Time:** 3-4 hours

---

#### 3. **Admin Endpoints Implementation**
**Status:** Partial (Dashboard endpoint exists, others missing)
- [ ] Complete AdminController endpoints:
  - [ ] Dashboard statistics (revenue, orders, users, events)
  - [ ] Event analytics (views, bookings, revenue)
  - [ ] User management endpoints
  - [ ] Order management endpoints
- [ ] Add filtering and pagination to all endpoints
- [ ] Implement role validation (only admin can access)

**Files to modify:**
- `controller/AdminController.java` - Complete all endpoints
- `service/AdminService.java` - NEW (admin business logic)

**Expected Time:** 2-3 hours

---

#### 4. **User Profile Management**
**Status:** Missing (UserController skeleton exists, no logic)
- [ ] Implement `UserService.updateProfile()` - update user data
- [ ] Implement `UserService.changePassword()` - password change with validation
- [ ] Add user profile endpoint: GET `/api/users/me`
- [ ] Implement email verification
- [ ] Add profile picture upload endpoint (file handling)

**Files to modify:**
- `service/UserService.java` - Complete implementation
- `controller/UserController.java` - Add endpoints

**Expected Time:** 2-3 hours

---

### ⏳ PRIORITY 2: IMPORTANT FEATURES (SANGAT PENTING)

#### 5. **Wishlist Full Implementation**
**Status:** Partial (Endpoints exist, some methods incomplete)
- [ ] Ensure `WishlistService.addToWishlist()` works properly
- [ ] Ensure `WishlistService.removeFromWishlist()` works properly
- [ ] Verify `WishlistService.getWishlist()` returns complete data
- [ ] Test wishlist persistence

**Files to check:**
- `service/WishlistService.java` - Verify all methods

**Expected Time:** 1 hour

---

#### 6. **Event Management Endpoints**
**Status:** Partial (List endpoints exist, create/update/delete partial)
- [ ] Complete `EventService.createEvent()` - with validation
- [ ] Complete `EventService.updateEvent()` - with validation
- [ ] Implement `EventService.publishEvent()` - change status
- [ ] Add event filtering by category, date, price
- [ ] Implement event search functionality
- [ ] Add pagination to event listing

**Files to modify:**
- `service/EventService.java` - Complete implementation
- `controller/EventController.java` - Ensure all endpoints working

**Expected Time:** 2-3 hours

---

#### 7. **Ticket Management**
**Status:** Partial (Service exists, availability checking incomplete)
- [ ] Implement ticket availability checking in `TicketService`
- [ ] Implement ticket reservation logic
- [ ] Implement "hold" mechanism (temporary reservation before payment)
- [ ] Add validation: enough quantity, not expired event
- [ ] Implement ticket release on order cancellation

**Files to modify:**
- `service/TicketService.java` - Complete availability logic

**Expected Time:** 2 hours

---

#### 8. **Promo Code Validation**
**Status:** Partial (Service exists, validation incomplete)
- [ ] Implement `PromoCodeService.validatePromoCode()` - check expiry, usage limits
- [ ] Implement `PromoCodeService.calculateDiscount()` - percentage vs fixed amount
- [ ] Add promo code filtering by category/event
- [ ] Implement usage limit checking

**Files to modify:**
- `service/PromoCodeService.java` - Complete validation logic

**Expected Time:** 2 hours

---

### ⏳ PRIORITY 3: NICE-TO-HAVE FEATURES (OPTIONAL)

#### 9. **Analytics & Reporting**
- [ ] Event performance metrics (views, bookings, revenue)
- [ ] User behavior tracking
- [ ] Payment success rate analytics
- [ ] Generate sales reports

**Files to create:**
- `service/AnalyticsService.java` - NEW
- `controller/AnalyticsController.java` - NEW

**Expected Time:** 2-3 hours (optional)

---

#### 10. **File Upload Handling**
- [ ] Implement file upload for event posters
- [ ] Implement file upload for user profile pictures
- [ ] Add file validation (type, size)
- [ ] Store files in `/uploads` directory

**Files to create:**
- `service/FileUploadService.java` - NEW
- `controller/FileUploadController.java` - NEW

**Expected Time:** 1-2 hours (optional)

---

#### 11. **Email Notifications** (Optional)
- [ ] Send confirmation email after booking
- [ ] Send payment confirmation email
- [ ] Send reminder email before event
- [ ] Setup email templates

**Files to create:**
- `service/EmailService.java` - NEW
- `service/EmailTemplate.java` - NEW

**Expected Time:** 1-2 hours (optional)

---

#### 12. **Logging & Error Handling**
- [ ] Add comprehensive logging (SLF4J)
- [ ] Implement centralized error handling
- [ ] Add request/response logging interceptor
- [ ] Better error messages for frontend

**Files to create:**
- `config/LoggingConfig.java` - NEW
- `exception/GlobalExceptionHandler.java` - NEW

**Expected Time:** 1-2 hours (optional)

---

## 🚀 RECOMMENDED WORK ORDER

### **Phase 1: Core Functionality (MUST DO - 8-10 hours)**
1. Authentication & JWT (2-3 hours)
2. Complete Order/Payment Flow (3-4 hours)
3. Admin Endpoints (2-3 hours)

**After Phase 1:** Basic application will be functional (login, browse events, book tickets, make payments)

---

### **Phase 2: Polish & Complete (SHOULD DO - 8-10 hours)**
4. User Profile Management (2-3 hours)
5. Wishlist Verification (1 hour)
6. Event Management (2-3 hours)
7. Ticket Management (2 hours)
8. Promo Code Validation (2 hours)

**After Phase 2:** All core features fully working and polished

---

### **Phase 3: Enhancement (NICE-TO-HAVE - 4-7 hours)**
9. Analytics & Reporting (2-3 hours)
10. File Upload (1-2 hours)
11. Email Notifications (1-2 hours)
12. Logging & Error Handling (1-2 hours)

**After Phase 3:** Production-ready application

---

## ✅ TESTING CHECKLIST

After implementation, test these flows:

- [ ] User Registration → Login → Browse Events
- [ ] Add to Wishlist → Remove from Wishlist
- [ ] Add to Cart → Checkout → Payment
- [ ] Order Confirmation → Email Notification
- [ ] View Order History → Order Details
- [ ] Admin: View Dashboard → Create Event → Manage Tickets
- [ ] Admin: View Orders → View Payments → View Users
- [ ] Promo Code: Valid → Invalid → Expired
- [ ] Error Handling: Missing data → Invalid payment → Server errors

---

## 📊 ESTIMATED TOTAL TIME

- **Phase 1 (Core):** 8-10 hours → Functional MVP
- **Phase 2 (Complete):** 8-10 hours → Polished Application
- **Phase 3 (Enhancement):** 4-7 hours → Production Ready

**Total:** 20-27 hours of backend development

---

## 🎯 NEXT IMMEDIATE STEPS

1. **Start with Authentication (Phase 1.1)**
   - Create `config/SecurityConfig.java` with Spring Security setup
   - Create `config/JwtTokenProvider.java` for token generation
   - Create `AuthController.java` and `AuthService.java`
   - Implement login/register endpoints
   - Test with Postman

2. **Then Order/Payment (Phase 1.2)**
   - Complete `OrderService` booking logic
   - Complete `PaymentService` payment processing
   - Test cart → checkout → payment flow
   - Generate QR codes for tickets

3. **Then Admin Endpoints (Phase 1.3)**
   - Complete `AdminController` with all endpoints
   - Add role-based access control
   - Test admin dashboard data retrieval

**After Phase 1 complete:** You'll have a working application that can handle the full user journey!

---

## 📝 NOTES

- Frontend is **100% ready** and can already call these endpoints
- Database schema is **complete** with proper relationships
- You have **69 Java files** - good foundation to build on
- Each priority 1 task is **independent** - can work on in any order
- **Recommendation:** Follow the suggested order for best workflow

---

**Good luck! 💪 You're at 70% done! Final push needed for backend! 🚀**
