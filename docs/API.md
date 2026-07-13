# 📘 API Documentation — Dungcony E-Commerce

> Base URL: `http://localhost:8080`
>
> Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## 🔐 Authentication

Tất cả API có ký hiệu 🔒 đều yêu cầu **Bearer Token** trong header:

```
Authorization: Bearer <access_token>
```

---

## 📦 Response Format

Tất cả API đều trả về cấu trúc chung:

```json
{
  "success": true
  |
  false,
  "message": "mô tả kết quả",
  "data": {
    ...
  }
  |
  null
}
```

Phân trang (`PageRes`):

```json
{
  "success": true,
  "message": "...",
  "data": {
    "content": [
      ...
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10,
    "last": false
  }
}
```

---

## 🔑 Module: Auth

### Đăng ký tài khoản

#### `POST /v1/api/public/auth/regis/`

Tạo tài khoản mới. Hệ thống gửi OTP 6 số về email.

**Request Body:**

```json
{
  "email": "user@example.com",
  "username": "dungcony",
  "password": "mypassword123"
}
```

| Field    | Type   | Bắt buộc | Mô tả                                |
|----------|--------|----------|--------------------------------------|
| email    | string | ✅        | Định dạng email hợp lệ, chưa tồn tại |
| username | string | ✅        | 3–50 ký tự, chưa tồn tại             |
| password | string | ✅        | 8–50 ký tự                           |

**Response 200:**

```json
{
  "success": true,
  "message": "register success",
  "data": null
}
```

**Lỗi:**
| Code | Mô tả |
|------|-------|
| 400 | Dữ liệu không hợp lệ |
| 409 | Email hoặc username đã tồn tại |

---

#### `POST /v1/api/public/auth/regis/verify`

Xác thực OTP để kích hoạt tài khoản (Bước 2).

**Request Body:**

```json
{
  "email": "user@example.com",
  "otp": "123456"
}
```

**Response 200:**

```json
{
  "success": true,
  "message": "register success",
  "data": null
}
```

**Lỗi:**
| Code | Mô tả |
|------|-------|
| 401 | OTP sai |
| 401 | OTP đã hết hạn (5 phút) |

---

### Đăng nhập

#### `POST /v1/api/public/auth/login`

Đăng nhập, nhận access token + cookie refresh token.

**Headers:**
| Header | Bắt buộc | Mô tả |
|--------|----------|-------|
| X-Device-Id | ✅ | UUID thiết bị, FE tự sinh và lưu lại |

**Request Body:**

```json
{
  "username": "dungcony",
  "password": "mypassword123"
}
```

**Response 200:**

```json
{
  "success": true,
  "message": "login success",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "header": "Bearer",
    "expiration": 3600
  }
}
```

- Cookie `refresh_token` được set tự động (httpOnly, Secure)
- Dùng `token` cho header `Authorization: Bearer <token>`

**Lỗi:**
| Code | Mô tả |
|------|-------|
| 401 | Sai username/password |
| 401 | Tài khoản chưa verify email |

---

#### `POST /v1/api/public/auth/refresh`

Lấy access token mới khi hết hạn.

**Yêu cầu:** Cookie `refresh_token` gửi kèm tự động.

**Response 200:**

```json
{
  "success": true,
  "message": "refresh_success",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "header": "Bearer",
    "expiration": 3600
  }
}
```

---

#### `POST /v1/api/public/auth/logout`

Đăng xuất, thu hồi refresh token của thiết bị.

**Headers:**
| Header | Bắt buộc | Mô tả |
|--------|----------|-------|
| X-Device-Id | ✅ | Phải khớp với lúc đăng nhập |

**Yêu cầu:** Cookie `refresh_token`.

**Response 200:**

```json
{
  "success": true,
  "message": "logout success",
  "data": null
}
```

---

### Quên mật khẩu

#### `POST /v1/api/public/auth/forgot-password?email=user@example.com`

Gửi mật khẩu mới về email đã đăng ký.

**Query Param:**
| Param | Bắt buộc | Mô tả |
|-------|----------|-------|
| email | ✅ | Email đã đăng ký |

**Response 200:**

```json
{
  "success": true,
  "message": "New password sent to your email",
  "data": null
}
```

---

### Kiểm tra tài khoản

#### `GET /v1/api/account/check/exists-email?email=user@example.com`

Kiểm tra email đã tồn tại chưa (dùng realtime khi điền form).

#### `GET /v1/api/account/check/exists-username?username=dungcony`

Kiểm tra username đã tồn tại chưa.

**Response 200:** `{ "success": true, "message": "check email/username", "data": null }`

---

#### `POST /v1/api/account/check/password` 🔒

Kiểm tra mật khẩu hiện tại có đúng không.

**Request Body:** `"mypassword123"` (plain string)

**Response 200:** `{ "success": true, "message": "password correct", "data": null }`

---

### Cập nhật tài khoản 🔒

#### `POST /v1/api/account/update/email/send-otp?oldEmail=old@example.com` 🔒

Gửi OTP về email hiện tại để xác nhận đổi email (Bước 1).

**Query Param:**
| Param | Bắt buộc | Mô tả |
|-------|----------|-------|
| oldEmail | ✅ | Email hiện tại của tài khoản |

**Response 200:** `{ "success": true, "message": "send otp req update email successfully", "data": null }`

---

#### `POST /v1/api/account/update/email/verify-otp?newEmail=new@example.com&otp=123456` 🔒

Xác nhận OTP và đổi sang email mới (Bước 2).

**Query Params:**
| Param | Bắt buộc | Mô tả |
|-------|----------|-------|
| newEmail | ✅ | Email mới muốn đổi sang |
| otp | ✅ | Mã OTP 6 số nhận qua email cũ |

**Response 200:** `{ "success": true, "message": "successfully", "data": null }`

---

#### `POST /v1/api/account/update/password` 🔒

Đổi mật khẩu.

**Request Body:**

```json
{
  "oldPass": "currentpassword123",
  "newPass": "newpassword456"
}
```

**Response 200:** `{ "success": true, "message": "successfully", "data": null }`

**Lỗi:**
| Code | Mô tả |
|------|-------|
| 401 | Mật khẩu cũ không đúng |

---

## 🛍️ Module: Products

### Sản phẩm (Public)

#### `GET /v1/api/public/product/get-all`

Lấy danh sách sản phẩm, hỗ trợ phân trang và sort.

**Query Params:**
| Param | Mặc định | Mô tả |
|-------|----------|-------|
| page | 0 | Trang hiện tại |
| size | 10 | Số lượng mỗi trang |
| sort | - | Ví dụ: `price,asc` hoặc `rated,desc` |

**Response 200:** `PageRes<ProductSummaryRes>`

---

#### `GET /v1/api/public/product/get-by-category?category_code=ELEC`

Lấy sản phẩm theo danh mục (bao gồm cả sub-categories).

#### `GET /v1/api/public/product/get-by-code?code=PROD001`

Xem chi tiết sản phẩm.

#### `GET /v1/api/public/product/search?keyword=iphone`

Tìm kiếm sản phẩm theo tên hoặc mô tả. Hỗ trợ phân trang.

---

### Sản phẩm (Admin) 🔒

#### `POST /v1/api/admin/product/product/add-new`

Thêm sản phẩm mới.

#### `PUT /v1/api/admin/product/product/update`

Cập nhật sản phẩm.

#### `DELETE /v1/api/admin/product/product/{code}`

Xóa sản phẩm theo code.

---

### Danh mục (Public)

#### `GET /v1/api/public/category/get-all`

Lấy toàn bộ danh mục.

#### `GET /v1/api/public/category/get-children/{code}`

Lấy danh sách danh mục con của danh mục có `code`.

#### `GET /v1/api/public/category/get/{code}`

Lấy thông tin chi tiết danh mục.

---

### Danh mục (Admin) 🔒

#### `POST /v1/api/admin/category/category/add-new`

Thêm danh mục mới.

#### `DELETE /v1/api/admin/category/category/{code}`

Xóa danh mục.

---

### Nhà cung cấp (Public)

#### `GET /v1/api/public/provider/get-by-code/{code}`

Lấy thông tin nhà cung cấp theo code.

#### `GET /v1/api/public/provider/get-by-name/{name}`

Lấy thông tin nhà cung cấp theo tên.

#### `GET /v1/api/public/provider/gets`

Lấy toàn bộ nhà cung cấp.

---

### Nhà cung cấp (Admin) 🔒

#### `POST /v1/api/admin/provider/add-new`

Thêm nhà cung cấp mới.

#### `PUT /v1/api/admin/provider/update/{code}`

Cập nhật nhà cung cấp.

#### `DELETE /v1/api/admin/provider/delete/{code}`

Xóa nhà cung cấp.

---

## 🎁 Module: Promotions

### Public

#### `GET /v1/api/promotions/product/{productCode}`

Lấy danh sách khuyến mãi áp dụng cho sản phẩm.

#### `GET /v1/api/promotions/category/{categoryCode}`

Lấy danh sách khuyến mãi áp dụng cho danh mục.

#### `GET /v1/api/promotions/{code}`

Lấy chi tiết khuyến mãi theo code.

---

### Admin 🔒

#### `GET /v1/api/admin/promotions/get-all`

Lấy tất cả khuyến mãi, hỗ trợ phân trang.

#### `POST /v1/api/admin/promotions/add-new`

Tạo khuyến mãi mới. Trả về `Location` header chứa URL của khuyến mãi vừa tạo.

#### `PUT /v1/api/admin/promotions/update`

Cập nhật khuyến mãi.

#### `DELETE /v1/api/admin/promotions/{code}`

Xóa hẳn khuyến mãi.

#### `PATCH /v1/api/admin/promotions/{code}/soft-delete`

Vô hiệu hóa khuyến mãi (soft delete, không xóa khỏi DB).

---

## 👤 Module: Users 🔒

### `GET /v1/api/user/me` 🔒

Lấy thông tin profile của user đang đăng nhập.

**Response 200:**

```json
{
  "success": true,
  "message": "profile",
  "data": {
    "fName": "Dung",
    "lName": "Cony",
    "img": "https://...",
    "phone": "0901234567"
  }
}
```

---

### `POST /v1/api/user/create-user` 🔒

Tạo profile user (sau khi đăng ký tài khoản lần đầu).

**Request Body:** `UserDto`

---

### `PUT /v1/api/user/update-profile` 🔒

Cập nhật thông tin profile.

**Request Body:** `UserDto`

---

### `POST /v1/api/address/add` 🔒

Thêm địa chỉ mới.

### `PUT /v1/api/address/update-by-id` 🔒

Cập nhật địa chỉ theo id.

### `DELETE /v1/api/address/delete-by-id?id=1` 🔒

Xóa địa chỉ theo id.

---

## ❌ Error Codes

| HTTP Code | Mô tả                                            |
|-----------|--------------------------------------------------|
| 400       | Dữ liệu request không hợp lệ (validation failed) |
| 401       | Chưa xác thực hoặc sai credentials               |
| 403       | Không có quyền truy cập                          |
| 404       | Không tìm thấy resource                          |
| 409       | Conflict (email/username đã tồn tại)             |
| 500       | Lỗi server                                       |

---

## 🔄 Flow sử dụng điển hình

### Flow đăng ký:

```
1. POST /regis/          → tạo tài khoản, nhận OTP qua email
2. POST /regis/verify    → nhập OTP → tài khoản kích hoạt
3. POST /auth/login      → đăng nhập → nhận access token
```

### Flow sử dụng API có auth:

```
1. Lưu access_token từ login response
2. Thêm header: Authorization: Bearer <access_token>
3. Khi nhận 401 → gọi POST /auth/refresh → nhận access_token mới
```

### Flow đổi email:

```
1. POST /update/email/send-otp?oldEmail=...   → nhận OTP về email cũ
2. POST /update/email/verify-otp?newEmail=...&otp=...  → đổi email
```

