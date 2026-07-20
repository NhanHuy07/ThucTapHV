# Implementation Plan: User Order Tracking & Admin Order Status Flow

## 1. Current State Analysis

### Frontend user tracking

- `TrackOrder.tsx` currently renders a fixed status timeline:
  - `UNPAID -> PAID -> PENDING -> CONFIRMED -> SHIPPING -> DELIVERED -> COMPLETED`
- This timeline is not suitable for COD orders (`PAYMENT_UPON_DELIVER`) because COD orders are created directly as `PENDING`.
- For COD, user should not see payment-oriented steps like `UNPAID` and `PAID`.
- The desired UX should behave like an e-commerce marketplace tracking flow: user sees operational fulfillment progress, not internal payment state.

### Backend order creation

- `OrderMapper.toEnity(...)` sets initial status:
  - `ONLINE` -> `UNPAID`
  - `PAYMENT_UPON_DELIVER` -> `PENDING`
- This part is reasonable.

### Backend update flow issues

Current `OrderUpdateImpl` has several inconsistent methods:

- `userCompleteOrder(...)`
  - Currently only allows `PENDING`.
  - Then sets status to `CONFIRMED`.
  - This is incorrect because user "complete" should mean user confirms received goods, so it should only allow `DELIVERED` and then set `COMPLETED`.

- `adminConfirmOrder(...)`
  - Method name says confirm order.
  - But currently requires `SHIPPING` and sets `DELIVERED`.
  - This is incorrect. It should confirm `PENDING` or `PAID` orders and set `CONFIRMED`.

- `adminShippingOrder(...)`
  - Currently requires `CONFIRMED`.
  - But sets `DELIVERED`.
  - This is incorrect. It should set `SHIPPING`.

- `deliveredOrder(...)`
  - Correct target is `DELIVERED`, but should be clearly treated as admin marks delivery completed.

- `adminUpdateOrderStatus(...)`
  - Has a generic transition validator, but the admin frontend allows choosing any status from a dropdown.
  - This causes a poor admin UX because invalid actions are visible and only fail after submit.

## 2. Target User Tracking Flow

### COD / direct payment: `PAYMENT_UPON_DELIVER`

Display timeline:

```text
Chờ xác nhận -> Đơn hàng được xác nhận -> Đang giao -> Đã giao -> Hoàn tất
```

Status mapping:

| Backend status | User label | Timeline step |
| --- | --- | --- |
| `PENDING` | Chờ xác nhận | 1 |
| `CONFIRMED` | Đơn hàng được xác nhận | 2 |
| `SHIPPING` | Đang giao | 3 |
| `DELIVERED` | Đã giao | 4 |
| `COMPLETED` | Hoàn tất | 5 |
| `CANCELLED` | Đã hủy | Terminal state |
| `RETURNED` | Đã hoàn trả | Terminal state |

### Online payment: `ONLINE`

Recommended timeline:

```text
Chờ thanh toán -> Đã thanh toán -> Chờ xác nhận -> Đơn hàng được xác nhận -> Đang giao -> Đã giao -> Hoàn tất
```

Status mapping:

| Backend status | User label | Timeline step |
| --- | --- | --- |
| `UNPAID` | Chờ thanh toán | 1 |
| `PAID` | Đã thanh toán | 2 |
| `PENDING` | Chờ xác nhận | 3 |
| `CONFIRMED` | Đơn hàng được xác nhận | 4 |
| `SHIPPING` | Đang giao | 5 |
| `DELIVERED` | Đã giao | 6 |
| `COMPLETED` | Hoàn tất | 7 |
| `CANCELLED` | Đã hủy | Terminal state |
| `RETURNED` | Đã hoàn trả | Terminal state |

### User actions

- User can cancel:
  - `UNPAID`
  - `PENDING`
- User can repay:
  - `UNPAID` and `paymentType === ONLINE`
- User can confirm received:
  - only `DELIVERED`
  - backend changes `DELIVERED -> COMPLETED`

## 3. Target Admin Status Flow

### Recommended state machine

```text
ONLINE:
UNPAID -> PAID -> PENDING -> CONFIRMED -> SHIPPING -> DELIVERED -> COMPLETED

COD:
PENDING -> CONFIRMED -> SHIPPING -> DELIVERED -> COMPLETED
```

Terminal/cross-flow transitions:

```text
UNPAID -> CANCELLED
PENDING -> CANCELLED
SHIPPING -> RETURNED
DELIVERED -> RETURNED
COMPLETED -> RETURNED
```

### Role responsibility

- Payment gateway:
  - `UNPAID -> PAID`
- Backend after successful online payment:
  - Option A: `UNPAID -> PAID`, admin later moves `PAID -> PENDING`
  - Option B, recommended for smoother ops: `UNPAID -> PENDING` after payment succeeds, while payment metadata records paid state separately.
- Admin:
  - `PENDING -> CONFIRMED`
  - `CONFIRMED -> SHIPPING`
  - `SHIPPING -> DELIVERED`
  - `SHIPPING/DELIVERED/COMPLETED -> RETURNED`
- User:
  - `DELIVERED -> COMPLETED`
  - `UNPAID/PENDING -> CANCELLED`

### Recommendation about `PAID`

The current enum mixes payment state and fulfillment state. For long-term clarity, payment status should be a separate field, for example:

```text
paymentStatus: UNPAID | PAID | REFUNDED | FAILED
orderStatus: PENDING | CONFIRMED | SHIPPING | DELIVERED | COMPLETED | CANCELLED | RETURNED
```

However, this requires database/entity/API changes. For this iteration, keep the enum and make the transition rules consistent.

## 4. Backend Implementation Plan

### 4.1 Fix `OrderUpdateImpl`

Update methods:

- `userCompleteOrder`
  - Allow only `DELIVERED`.
  - Set `COMPLETED`.

- `adminConfirmOrder`
  - Allow `PENDING` and optionally `PAID`.
  - Set `CONFIRMED`.

- `adminShippingOrder`
  - Allow only `CONFIRMED`.
  - Set `SHIPPING`.

- `deliveredOrder`
  - Allow only `SHIPPING`.
  - Set `DELIVERED`.

- `adminReturnOrder`
  - Allow `SHIPPING`, `DELIVERED`, `COMPLETED`.
  - Set `RETURNED`.
  - Decide whether stock should be restored for returned orders. Recommended: not automatic unless return is accepted and inventory is inspected.

### 4.2 Fix `validateStatusTransition`

Recommended valid transitions:

| Current | Next statuses |
| --- | --- |
| `UNPAID` | `PAID`, `CANCELLED` |
| `PAID` | `PENDING`, `CANCELLED` |
| `PENDING` | `CONFIRMED`, `CANCELLED` |
| `CONFIRMED` | `SHIPPING`, `CANCELLED` if not shipped yet |
| `SHIPPING` | `DELIVERED`, `RETURNED` |
| `DELIVERED` | `COMPLETED`, `RETURNED` |
| `COMPLETED` | `RETURNED` |
| `CANCELLED` | none |
| `RETURNED` | none |

### 4.3 Optional backend improvement

Add an endpoint to expose allowed next statuses:

```http
GET /v1/api/admin/order/{orderCode}/allowed-statuses
```

Response:

```json
{
  "success": true,
  "data": ["CONFIRMED", "CANCELLED"]
}
```

This lets the admin UI render only valid actions without duplicating business rules.

For a quick iteration, duplicate a small `getAllowedNextStatuses(status, paymentType)` helper in FE and keep backend validation as the source of truth.

## 5. Frontend Implementation Plan

### 5.1 User order detail tracking

Update `TrackOrder.tsx`:

- Replace fixed `trackingSteps` with a function:

```ts
getTrackingSteps(paymentType, status)
```

- If `paymentType === PAYMENT_UPON_DELIVER`, render COD timeline:
  - `PENDING`
  - `CONFIRMED`
  - `SHIPPING`
  - `DELIVERED`
  - `COMPLETED`

- If `paymentType === ONLINE`, render online timeline:
  - `UNPAID`
  - `PAID`
  - `PENDING`
  - `CONFIRMED`
  - `SHIPPING`
  - `DELIVERED`
  - `COMPLETED`

- If order is `CANCELLED` or `RETURNED`, do not render progress circles. Render a terminal status panel.

### 5.2 User order list

Update `Orders.tsx` status groups:

- `UNPAID`: Chờ thanh toán
- `PROCESSING`: `PAID`, `PENDING`, `CONFIRMED`
- `SHIPPING`: `SHIPPING`
- `DELIVERED`: `DELIVERED`, `COMPLETED`
- `CANCELLED`: `CANCELLED`
- `RETURNED`: `RETURNED`

Review labels:

- `PENDING`: Chờ xác nhận
- `CONFIRMED`: Đơn hàng được xác nhận
- `SHIPPING`: Đang giao
- `DELIVERED`: Đã giao
- `COMPLETED`: Hoàn tất

### 5.3 Admin order UI

Replace generic status dropdown with valid action buttons:

- For `UNPAID`:
  - Cancel
  - Mark paid, only if admin needs manual payment correction
- For `PAID`:
  - Move to waiting confirmation
  - Cancel
- For `PENDING`:
  - Confirm order
  - Cancel order
- For `CONFIRMED`:
  - Start shipping
  - Cancel order, only if shipment has not started
- For `SHIPPING`:
  - Mark delivered
  - Mark returned
- For `DELIVERED`:
  - Mark returned
  - Admin should not usually mark completed unless business requires auto-complete.
- For `COMPLETED`:
  - Mark returned only if return is accepted.
- For `CANCELLED` / `RETURNED`:
  - No further action.

Recommended UX:

- Show current status.
- Show next valid actions as buttons.
- Keep a fallback advanced dropdown only if needed, but hide it by default.
- After action success, reload order detail and list.

## 6. Testing Plan

### Frontend

- Build check:

```bash
npm run build
```

- Manual test cases:
  - COD order starts at `Chờ xác nhận`.
  - Online unpaid order starts at `Chờ thanh toán`.
  - Cancelled order shows terminal cancelled panel.
  - Returned order shows terminal returned panel.
  - Delivered order shows user confirm received button.

### Backend

Run with Java 21:

```bash
cd backend-e
./mvnw test
```

If using Docker:

```bash
docker compose -f docker-compose.local.yaml up --build
```

Recommended service tests:

- `PENDING -> CONFIRMED`
- `CONFIRMED -> SHIPPING`
- `SHIPPING -> DELIVERED`
- `DELIVERED -> COMPLETED`
- invalid `PENDING -> COMPLETED` must fail
- invalid `SHIPPING -> CONFIRMED` must fail

## 7. Execution Order

1. Fix backend transition methods and validator.
2. Update user `TrackOrder.tsx` timeline based on payment type.
3. Update user-facing labels in `Orders.tsx` and `TrackOrder.tsx`.
4. Update admin UI to show valid action buttons instead of all statuses.
5. Run frontend build.
6. Rebuild backend with Docker or Java 21.
7. Manual QA with one COD order and one ONLINE order.
