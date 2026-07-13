# 🗄️ Database Documentation — Dungcony E-Commerce

> Database: PostgreSQL
> 
> Migration tool: Flyway

---

## 📊 Entity Relationship Overview

```
tbl_accounts (1) ──── (1) tbl_users (1) ──── (N) tbl_address
                                │
                          (không liên kết trực tiếp với sản phẩm)

tbl_categories (N) ──── (N) tbl_categories  [self-reference: parent_id]
tbl_categories (1) ──── (N) tbl_products
tbl_providers  (1) ──── (N) tbl_products

tbl_promotions (N) ──── (N) tbl_products    [via tbl_promotion_product]
tbl_promotions (N) ──── (N) tbl_categories  [via tbl_promotion_category]
```

---

## 📋 Bảng chi tiết

### `tbl_accounts` — Tài khoản đăng nhập

| Column | Type | Nullable | Default | Mô tả |
|--------|------|----------|---------|-------|
| id | SERIAL | ❌ | auto | Primary key |
| username | VARCHAR(50) | ❌ | - | Tên đăng nhập, unique |
| password | VARCHAR(255) | ❌ | - | BCrypt hash |
| role | VARCHAR(10) | ❌ | `CUSTOMER` | `CUSTOMER` / `ADMIN` / `EMPLOYEE` |
| email | VARCHAR(100) | ❌ | - | Email, unique |
| verify | BOOLEAN | ❌ | `false` | Đã xác thực email chưa |
| status | VARCHAR(10) | ❌ | `ACTIVE` | `ACTIVE` / `INACTIVE` / `BANNED` |
| created_at | TIMESTAMP(3) | ✅ | now() | |
| updated_at | TIMESTAMP(3) | ✅ | now() | |

**Lưu ý:**
- Tài khoản `verify = false` không thể đăng nhập
- Tài khoản `status != ACTIVE` không thể đăng nhập

---

### `tbl_users` — Thông tin cá nhân

| Column | Type | Nullable | Default | Mô tả |
|--------|------|----------|---------|-------|
| id | CHAR(36) | ❌ | - | UUID, Primary key |
| f_name | VARCHAR(255) | ❌ | - | Tên |
| l_name | VARCHAR(255) | ❌ | - | Họ |
| img | VARCHAR(255) | ✅ | - | URL ảnh đại diện |
| phone | VARCHAR(15) | ✅ | - | Số điện thoại, unique |
| acc_id | INT | ❌ | - | FK → `tbl_accounts.id` (unique, 1-1) |
| created_at | TIMESTAMP(3) | ✅ | now() | |
| updated_at | TIMESTAMP(3) | ✅ | now() | |

**Quan hệ:** 1 account ↔ 1 user profile (xóa account → xóa user theo)

**Index:** `idx_users_acc_id` trên `acc_id`

---

### `tbl_address` — Địa chỉ

| Column | Type | Nullable | Default | Mô tả |
|--------|------|----------|---------|-------|
| id | SERIAL | ❌ | auto | Primary key |
| user_id | CHAR(36) | ✅ | - | FK → `tbl_users.id` |
| country | VARCHAR(20) | ❌ | - | Quốc gia |
| province | VARCHAR(20) | ❌ | - | Tỉnh/Thành phố |
| district | VARCHAR(20) | ❌ | - | Quận/Huyện |
| street | VARCHAR(100) | ❌ | - | Đường/Phường |
| detail | VARCHAR(255) | ✅ | - | Chi tiết thêm |

**Quan hệ:** 1 user → N địa chỉ (xóa user → xóa địa chỉ theo)

**Index:** `idx_address_user` trên `user_id`

---

### `tbl_categories` — Danh mục sản phẩm

| Column | Type | Nullable | Default | Mô tả |
|--------|------|----------|---------|-------|
| id | SERIAL | ❌ | auto | Primary key |
| name | VARCHAR(50) | ❌ | - | Tên danh mục |
| code | VARCHAR(10) | ❌ | - | Mã danh mục, unique |
| img_url | VARCHAR(255) | ✅ | - | URL ảnh |
| description | VARCHAR(255) | ✅ | - | Mô tả |
| status | VARCHAR(20) | ❌ | `ACTIVE` | Trạng thái |
| parent_id | INT | ✅ | - | FK → `tbl_categories.id` (self-reference) |
| is_leaf | BOOLEAN | ❌ | `true` | Có phải danh mục lá không |
| level | INT | ❌ | `0` | Cấp độ trong cây (0 = root) |
| path | VARCHAR(255) | ✅ | - | Đường dẫn từ root, ví dụ: `/1/3/7` |
| version | BIGINT | ❌ | `0` | Optimistic locking |
| created_at | TIMESTAMP(3) | ✅ | now() | |
| updated_at | TIMESTAMP(3) | ✅ | now() | |

**Lưu ý:** Cấu trúc cây danh mục dùng `parent_id` + `path` để query nhanh toàn bộ sub-categories.

**Index:** `idx_category_status`, `idx_category_parent`, `idx_category_path`

---

### `tbl_providers` — Nhà cung cấp

| Column | Type | Nullable | Default | Mô tả |
|--------|------|----------|---------|-------|
| id | SERIAL | ❌ | auto | Primary key |
| name | VARCHAR(50) | ❌ | - | Tên nhà cung cấp |
| code | VARCHAR(10) | ❌ | - | Mã, unique |
| description | VARCHAR(255) | ✅ | - | Mô tả |
| email | VARCHAR(100) | ✅ | - | Email liên hệ |
| phone | VARCHAR(15) | ✅ | - | SĐT liên hệ |
| logo | VARCHAR(255) | ✅ | - | URL logo |
| status | VARCHAR(20) | ❌ | `ACTIVE` | Trạng thái |
| version | BIGINT | ❌ | `0` | Optimistic locking |
| created_at | TIMESTAMP(3) | ✅ | now() | |
| updated_at | TIMESTAMP(3) | ✅ | now() | |

---

### `tbl_products` — Sản phẩm

| Column | Type | Nullable | Default | Mô tả |
|--------|------|----------|---------|-------|
| id | SERIAL | ❌ | auto | Primary key |
| name | VARCHAR(50) | ❌ | - | Tên sản phẩm |
| code | VARCHAR(10) | ❌ | - | Mã sản phẩm, unique |
| description | VARCHAR(255) | ✅ | - | Mô tả |
| quantity | INT | ✅ | `0` | Số lượng tồn kho |
| quantity_sold | INT | ✅ | `0` | Số lượng đã bán |
| price | DECIMAL(19,2) | ✅ | `0` | Giá bán |
| rated | DECIMAL(2,1) | ✅ | - | Điểm đánh giá (0.0 – 5.0) |
| img | VARCHAR(255) | ✅ | - | URL ảnh đại diện |
| status | VARCHAR(20) | ❌ | `ACTIVE` | Trạng thái |
| category_id | INT | ✅ | - | FK → `tbl_categories.id` |
| provider_id | INT | ✅ | - | FK → `tbl_providers.id` |
| version | BIGINT | ❌ | `0` | Optimistic locking |
| created_at | TIMESTAMP(3) | ✅ | now() | |
| updated_at | TIMESTAMP(3) | ✅ | now() | |

**Index:** `idx_product_status`, `idx_product_category`, `idx_product_provider`, `idx_product_status_price`, `idx_product_status_rated`, `idx_product_sold`

---

### `tbl_promotions` — Khuyến mãi

| Column | Type | Nullable | Default | Mô tả |
|--------|------|----------|---------|-------|
| id | SERIAL | ❌ | auto | Primary key |
| code | VARCHAR(20) | ❌ | - | Mã khuyến mãi, unique |
| type | VARCHAR(20) | ❌ | - | Loại: `PERCENT` / `FIXED` |
| value | INT | ❌ | - | Giá trị giảm (% hoặc số tiền) |
| min_price_apply | DECIMAL(19,2) | ❌ | `0` | Giá trị đơn hàng tối thiểu để áp dụng |
| scope | VARCHAR(20) | ❌ | `GLOBAL` | Phạm vi: `GLOBAL` / `PRODUCT` / `CATEGORY` |
| status | VARCHAR(20) | ❌ | `SCHEDULED` | `SCHEDULED` / `ACTIVE` / `EXPIRED` / `DISABLED` |
| priority | INT | ✅ | `1` | Độ ưu tiên khi có nhiều khuyến mãi áp dụng |
| start_at | TIMESTAMP(3) | ✅ | now() | Thời gian bắt đầu |
| end_at | TIMESTAMP(3) | ✅ | - | Thời gian kết thúc |
| version | BIGINT | ❌ | `0` | Optimistic locking |

**Index:** `idx_promotion_status`

---

### `tbl_promotion_product` — Khuyến mãi ↔ Sản phẩm

Bảng trung gian N-N giữa `tbl_promotions` và `tbl_products`.

| Column | Type | Mô tả |
|--------|------|-------|
| product_id | INT | FK → `tbl_products.id` |
| promotion_id | INT | FK → `tbl_promotions.id` |

**PK:** `(product_id, promotion_id)`

**Index:** `idx_promotion_product_promotion` trên `promotion_id`

---

### `tbl_promotion_category` — Khuyến mãi ↔ Danh mục

Bảng trung gian N-N giữa `tbl_promotions` và `tbl_categories`.

| Column | Type | Mô tả |
|--------|------|-------|
| promotion_id | INT | FK → `tbl_promotions.id` |
| category_id | INT | FK → `tbl_categories.id` |

**PK:** `(promotion_id, category_id)`

**Index:** `idx_promotion_category_category` trên `category_id`

---

## 🔗 Foreign Keys Summary

| Bảng | Column | Tham chiếu | On Delete |
|------|--------|-----------|-----------|
| tbl_users | acc_id | tbl_accounts.id | CASCADE |
| tbl_address | user_id | tbl_users.id | CASCADE |
| tbl_categories | parent_id | tbl_categories.id | SET NULL |
| tbl_products | category_id | tbl_categories.id | - |
| tbl_products | provider_id | tbl_providers.id | - |
| tbl_promotion_product | product_id | tbl_products.id | - |
| tbl_promotion_product | promotion_id | tbl_promotions.id | - |
| tbl_promotion_category | promotion_id | tbl_promotions.id | CASCADE |
| tbl_promotion_category | category_id | tbl_categories.id | CASCADE |

---

## 📝 Enums

### Role (`tbl_accounts.role`)
| Value | Mô tả |
|-------|-------|
| `CUSTOMER` | Khách hàng thông thường (default) |
| `ADMIN` | Quản trị viên |
| `EMPLOYEE` | Nhân viên |

### Account Status (`tbl_accounts.status`)
| Value | Mô tả |
|-------|-------|
| `ACTIVE` | Hoạt động bình thường |
| `INACTIVE` | Tạm ngừng hoạt động |
| `BANNED` | Bị cấm |

### Promotion Type (`tbl_promotions.type`)
| Value | Mô tả |
|-------|-------|
| `PERCENT` | Giảm theo % |
| `FIXED` | Giảm số tiền cố định |

### Promotion Status (`tbl_promotions.status`)
| Value | Mô tả |
|-------|-------|
| `SCHEDULED` | Chưa bắt đầu |
| `ACTIVE` | Đang chạy |
| `EXPIRED` | Đã hết hạn |
| `DISABLED` | Bị vô hiệu hóa thủ công |

### Promotion Scope (`tbl_promotions.scope`)
| Value | Mô tả |
|-------|-------|
| `GLOBAL` | Áp dụng cho tất cả |
| `PRODUCT` | Chỉ áp dụng cho sản phẩm trong `tbl_promotion_product` |
| `CATEGORY` | Chỉ áp dụng cho danh mục trong `tbl_promotion_category` |

