# Implementation Plan: Tich hop Voucher cho Checkout va kiem tra UI Admin

## Pham vi

- Chi thuc hien tren Frontend truoc khi duoc phe duyet.
- Khong sua Backend.
- Muc tieu: user co the nhap/chon voucher khi dat hang, FE tinh dung `voucherCode`, `voucherDiscount`, `finalPrice` va gui vao API tao don.

## Ket qua kiem tra hien tai

### 1. API admin tao voucher

Backend dang co:

- `POST /v1/api/admin/voucher/create`
- Payload theo `CreateVoucherReq`:

```json
{
  "code": "string",
  "discountType": "PERCENT",
  "voucherType": "NEWBIE",
  "value": 1,
  "minOrderAmount": 0,
  "startAt": "2026-05-31T09:48:05.034Z",
  "endAt": "2026-05-31T09:48:05.034Z"
}
```

Frontend `Admin.tsx` hien tai da co form tao voucher va dang gui dung cac field:

- `code`
- `discountType`
- `voucherType`
- `value`
- `minOrderAmount`
- `startAt`
- `endAt`

Diem can chinh o FE:

- Form hien tai chi validate `code`, `value`, `minOrderAmount`.
- Nen bat buoc nhap `startAt`, `endAt` tren UI vi Backend dang goi `req.startAt().isAfter(...)`; neu FE khong gui `startAt`, BE co nguy co loi null.
- Nen hien thi ro option `PERCENT/FIXED` va `GLOBAL/NEWBIE`.

### 2. API voucher cua user

Backend dang co:

- `GET /v1/api/user/vouchers`
- Tra ve danh sach voucher AVAILABLE cua user:

```json
[
  {
    "code": "SALE30",
    "type": "PERCENT",
    "value": 30,
    "minOrderAmount": 200000,
    "status": "AVAILABLE",
    "endAt": "2026-06-30T00:00:00Z"
  }
]
```

Frontend hien chua tich hop API nay trong checkout.

### 3. API ap dung voucher cho don hang

Khong thay API apply voucher rieng.

Backend ap dung voucher trong API:

- `POST /v1/api/user/order/create`

`CreateOrderReq` da co cac field:

- `voucherCode`
- `voucherDiscount`
- `finalPrice`

Backend se:

- Neu `voucherCode != null`, goi `userVoucherService.applyVoucher(...)`.
- Danh dau voucher thanh `USED`.
- Validate lai `totalPrice`, `voucherDiscount`, `finalPrice` FE gui len.

Frontend `PlaceOrder.tsx` hien tai dang gui:

```ts
voucherDiscount: 0,
finalPrice: subtotal,
```

Va chua gui `voucherCode`, nen user khong co cach ap dung voucher.

## De xuat UI/UX

### Tai trang dat hang `PlaceOrder.tsx`

Them section "Voucher" trong cot tong ket don hang:

- Input nhap ma voucher.
- Nut "Ap dung".
- Danh sach voucher cua toi lay tu `GET /v1/api/user/vouchers`.
- Nut chon nhanh voucher tu danh sach.
- Hien thi:
  - Tam tinh
  - Ma voucher dang ap dung
  - So tien giam
  - Tong thanh toan sau giam

### Logic tinh tien tren FE

Voi voucher user response:

- Neu `type === 'PERCENT'`: `discount = subtotal * value / 100`
- Neu `type === 'FIXED'`: `discount = value`
- Chi cho ap dung neu `subtotal >= minOrderAmount`
- `finalPrice = max(subtotal - discount, 0)`

Khi tao don:

```ts
voucherCode: appliedVoucher?.code,
voucherDiscount,
finalPrice,
```

Neu user nhap ma GLOBAL khong nam trong danh sach `/user/vouchers`, co 2 cach:

1. Chi cho chon voucher trong danh sach user dang co.
2. Cho nhap ma thu cong, nhung FE khong co API validate/tinh truoc. Cach nay de phat sinh conflict vi BE se validate `voucherDiscount/finalPrice`.

De an toan, de xuat giai doan 1 chi ap dung voucher co trong `GET /v1/api/user/vouchers`.

## Diem can bao BE kiem tra

Khong sua BE trong task nay, nhung co 2 diem dang nghi van:

1. `VoucherImpl.createVoucher` dang goi `req.startAt().isAfter(...)`. DTO cho phep `startAt` null, nen neu request khong co `startAt` co the loi null.
2. `UserVoucherImpl.applyVoucher` khong dung enum `discountType` de tinh giam gia. Logic hien tai dua vao `value < 100`; voi voucher FIXED, code dang tru `minOrderAmount` thay vi tru `value`.

## Proposed Changes FE

### 1. Them type voucher trong `src/types/shop.ts`

- `UserVoucher`
- `VoucherDiscountType`

### 2. Them API helper moi `src/lib/voucherApi.ts`

- `getMyVouchers(token)`

### 3. Cap nhat `src/pages/PlaceOrder.tsx`

- Fetch voucher cua user khi vao trang dat hang.
- Them state:
  - `voucherInput`
  - `myVouchers`
  - `appliedVoucher`
  - `voucherDiscount`
  - `orderFinalPrice`
- Them UI voucher trong summary.
- Khi submit order, gui `voucherCode`, `voucherDiscount`, `finalPrice`.

### 4. Cap nhat nhe UI admin voucher trong `src/pages/Admin.tsx`

- Bat buoc nhap `startAt`, `endAt`.
- Them validate `endAt > startAt`.
- Khong thay doi endpoint admin.

## Verification Plan

- Chay `npx tsc --noEmit`.
- Manual:
  - Tao voucher GLOBAL tu admin.
  - Login user, vao checkout, thay voucher trong danh sach neu available.
  - Chon voucher, tong thanh toan thay doi.
  - Dat hang, request `/v1/api/user/order/create` co `voucherCode`, `voucherDiscount`, `finalPrice`.
  - Kiem tra order detail hien dung voucher va giam gia.
