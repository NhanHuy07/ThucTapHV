# Implementation Plan: Payment & Promotions

This document outlines the systematic plan to implement Payment Integration and Advertisement/Promotion features for the E-Fashion web application.

## 1. Feature: Payment Integration & Management

### Overview
Enable secure online payments using Stripe and Razorpay, manage payment status, and allow users to view their payment history.

### Backend Implementation (`/backend`)
*Current Status*: Basic `placeOrder` (COD) and `placeOrderStripe` (Stripe Session) exist.

1.  **Enhance Stripe Integration**:
    *   **Verify Webhooks**: Implement a webhook endpoint to listen for Stripe events (e.g., `checkout.session.completed`) to update the order's `payment` status to `true` automatically.
    *   **Endpoint**: `POST /api/order/stripe-webhook`

2.  **Implement Razorpay Integration**:
    *   **SDK Setup**: Initialize Razorpay instance in `orderController.js`.
    *   **Create Order**: Implement `placeOrderRazorpay` to generate a Razorpay order ID.
    *   **Verification**: Create an endpoint `verifyRazorpay` to validate the signature sent from the frontend after successful payment.

3.  **Order Management**:
    *   **Update Model**: Ensure `orderModel.js` supports storing transaction IDs (e.g., `paymentId`).
    *   **History**: `userOrders` controller is already present; ensure it returns payment status details.

### Frontend Implementation (`/frontend`)

1.  **Install Dependencies**:
    *   `@stripe/stripe-js`: For Stripe frontend logic.
    *   `react-razorpay` (or script load): For Razorpay.

2.  **Update `PlaceOrder.tsx`**:
    *   **Stripe Flow**:
        *   On "Place Order", call backend `api/order/stripe`.
        *   Receive `session_url`.
        *   Redirect user: `window.location.replace(url)`.
    *   **Razorpay Flow**:
        *   Call backend `api/order/razorpay` to get order ID.
        *   Open Razorpay modal using the key and order ID.
        *   On success, call backend `api/order/verifyRazorpay`.

3.  **Payment Verification Pages**:
    *   Create a `Verify.tsx` page to handle redirects from payment gateways.
    *   Route: `/verify?success=true&orderId=...`
    *   Logic: API call to confirm backend status, then redirect to Orders.

4.  **Orders Page**:
    *   Update `Orders.tsx` to show "Paid" vs "Pending" status and "Method" (Stripe/Razorpay/COD).

---

## 2. Feature: Advertisements & Promotions

### Overview
Create a system to manage and display marketing offers (banners) and discount codes (promocodes).

### Backend Implementation

1.  **New Model: `PromoCode`**:
    *   Fields: `code` (string), `discountType` (percentage/fixed), `value` (number), `minOrderAmount` (number), `isActive` (boolean), `expiryDate` (Date).

2.  **API Endpoints (`promoRoutes.js`)**:
    *   `POST /api/promo/create`: Admin creates a code.
    *   `POST /api/promo/validate`: Frontend checks if a code is valid for the current cart total. Returns final discount amount.

3.  **New Model: `AdBanner`** (Optional for dynamic ads):
    *   Fields: `imageUrl`, `linkTo`, `isActive`, `position` (top-bar, hero-slide).

### Frontend Implementation

1.  **Components**:
    *   **`AnnouncementBar.tsx`**: Top of page ("Free shipping on orders over $50", "Use code SALE20").
    *   **`PromoInput`**: In `Cart.tsx`, add an input field "Enter Discount Code".

2.  **Logic**:
    *   **State**: Add `discount` and `appliedCode` to `ShopContext`.
    *   **Validation**: When user hits "Apply Code", call `api/promo/validate`.
    *   **Calculation**: Update `getCartAmount` to subtract the discount (ensure total >= 0).

3.  **UI Updates**:
    *   **Cart Page**: Show "Subtotal", "Discount", "Total".
    *   **Home Page**: Integrate `AdBanner` carousel or static banners between sections.

---

## 3. Execution Roadmap

### Phase 1: Payment Foundations (Days 1-2)
1.  Setup Backend Environment Variables (`STRIPE_SECRET`, `RAZORPAY_KEY`).
2.  Complete `placeOrderRazorpay` in backend.
3.  Connect Frontend `PlaceOrder` to these API endpoints.

### Phase 2: Webhooks & Verification (Day 3)
1.  Implement backend webhooks for secure status updates.
2.  Create Frontend `Verify` page to handle redirects.

### Phase 3: Promotions System (Days 4-5)
1.  Create `PromoCode` backend model and APIs.
2.  Update `ShopContext` to handle discounts.
3.  Add Promo Input to `Cart` and `PlaceOrder`.
