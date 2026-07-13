***

- customer:
    + đăng nhập đăng ký
    + quản lý tài khoản
    + xem chi tiết sản phẩm
    + tìm kiếm
    + thêm vào giỏ hàng
    + mua hàng
    + quản lý đơn hàng
    + thanh toán
    + đánh giá
    + điểm mua hàng (thêm, sử dụng)
    + giảm giá

- nhân viên:
    + thêm hóa đơn
    + thêm sản phẩm (thêm NCC)
    + tìm kiếm, xem chi tiết
    + quản lý đơn hàng
    + gửi thông báo

- admin
    + quản lý user (thêm, sửa, xóa, khóa)
    + quản lý sản phẩm (xem, thêm, sửa, xóa)


- ADVANCED:
    + hệ thống gợi ý sản phẩm
    + hệ thống quảng cáo sản phẩm
    + các chế độ khuyến mãi

***

# 📦 Danh sách lớp thực thể (Entities)

> Tài liệu mô tả các lớp thực thể trong hệ thống bán hàng

---

## 🧑‍💼 Account

| Thuộc tính | Kiểu dữ liệu |
|------------|--------------|
| id         | int          |
| username   | String       |
| password   | String       |
| role       | String       |
| email      | String       |
| phone      | String       |
| status     | String       |
| create_at  | long         |
| update_at  | long         |

---

## 👤 User

| Thuộc tính | Kiểu dữ liệu       |
|------------|--------------------|
| id         | UUID               |
| firstName  | String             | 
| lastName   | String             |
| addr       | List\<Address>     |
| point      | Point              |
| acc        | Account            |
| cart       | Cart               |
| vouchers   | List\<UserVoucher> |
| comments   | List\<Comment>     |

---

---

## Address

| Thuộc tính | Kiểu dữ liệu |
|------------|--------------|
| id         | int          |
| country    | String       | 
| province   | String       |
| district   | String       |
| street     | String       |
| detail     | String       |

---

## Point

| Thuộc tính  | Kiểu dữ liệu |
|-------------|--------------|
| id          | int          |
| current     | Integer      | 
| used        | Integer      | // điểm đã sử dụng
| expira      | Integer      | // điểm đã hết hạn
| expira_date | Long         | // thời điểm hết hạn

---

## Point_History

| Thuộc tính | Kiểu dữ liệu |
|------------|--------------|
| act        | String       | // kiểu hành động (mua, điểm danh, hoàn tiền)
| add        | Integer      | // điểm cộng
| minus      | Integer      | // điểm trừ
| created_at | Long         | // thời điểm 

---

## 🗂️ Category

| Thuộc tính | Kiểu dữ liệu    |
|------------|-----------------|
| id         | int             |
| name       | String          |
| desc       | String          |
| categories | List\<Category> |
| products   | List\<Product>  |
| created_at | long            |
| updated_at | long            |

---

## 💬 Comment

| Thuộc tính    | Kiểu dữ liệu |
|---------------|--------------|
| id            | int          |
| img           | String       | // cmt bằng hình ảnh
| data          | String       | // có thể chỉ vote không cmt
| rating        | Float        | // có thể không vote chỉ cmt
| create_at     | long         |
| create_update | long         |

---

## 🏭 Provider (Nhà cung cấp)

| Thuộc tính | Kiểu dữ liệu   |
|------------|----------------|
| id         | int            |
| name       | String         |
| addr       | Address        |
| email      | String         |
| phone      | String         |
| create_at  | long           |
| update_at  | long           |
| products   | List\<Product> |

---

## 📦 Product

| Thuộc tính | Kiểu dữ liệu   |
|------------|----------------|
| id         | int            |
| name       | String         |
| img        | String         |
| desc       | String         |
| price      | int            |
| rated      | float          |
| comments   | List\<Comment> |
| cur_quan   | int            |
| sold       | int            |
| create_at  | long           |
| update_at  | long           |

---

## 🛒 Cart (Giỏ hàng)

| Thuộc tính | Kiểu dữ liệu    |
|------------|-----------------|
| id         | int             |
| items      | List\<ItemCart> |

---

## 🧾 ItemCart

| Thuộc tính | Kiểu dữ liệu |
|------------|--------------|
| product    | Product      |
| quantity   | int          |

---

## 🎟️ Voucher (Mã giảm giá)

| Thuộc tính   | Kiểu dữ liệu |
|--------------|--------------|
| id           | int          |
| code         | varchar(30)  |
| percent      | Integer      |
| value        | Integer      |
| max_discount | int          |
| require      | int          |

---

## 🎫 UserVoucher

| Thuộc tính | Kiểu dữ liệu |
|------------|--------------|
| id         | int          |
| vouchers   | Voucher      |
| quantity   | int          |
| status     | String       |
| start_at   | timestamp    |
| end_at     | timestamp    |

---

## 🧾 Invoice (Hóa đơn)

| Thuộc tính  | Kiểu dữ liệu          |
|-------------|-----------------------|
| id          | int                   |
| user        | User                  |
| type        | String                |
| status      | String                | // đang chờ thanh toán , đã thanh toán , lỗi
| details     | List\<InvoiceDetails> |
| total_money | int                   |
| create_at   | long                  |

---

## 📄 InvoiceDetails

| Thuộc tính | Kiểu dữ liệu |
|------------|--------------|
| id_invoice | int          |
| id_product | int          |
| price      | int          |
| quantity   | int          |
| voucher    | Voucher      |
| total      | int          |

---

# 🏗️ Thiết kế hệ thống bán hàng

---

## I. Lớp thiết kế (Database / Entities)

### 1. tbl_account

| Trường    | Kiểu dữ liệu       | Ràng buộc                 |
|-----------|--------------------|---------------------------|
| id        | INT                | PK, NOT NULL              |
| username  | VARCHAR(50)        | UNIQUE, NOT NULL          |
| password  | VARCHAR(30)        | NOT NULL                  |
| role      | VARCHAR(10)        |                           |
| email     | VARCHAR(50)        |                           |
| phone     | VARCHAR(10)        |                           |
| status    | ENUM(ACTIVE, LOCK) | DEFAULT ACTIVE            |
| create_at | TIMESTAMP          | DEFAULT CURRENT_TIMESTAMP |
| update_at | TIMESTAMP          | DEFAULT CURRENT_TIMESTAMP |

---

### 2. tbl_users

| Trường     | Kiểu dữ liệu | Ràng buộc                 |
|------------|--------------|---------------------------|
| id         | char(36)     | PK, NOT NULL              |
| f_name     | NVARCHAR(20) | NOT NULL                  |
| l_name     | nvarchar(20) | not null                  |
| img_id     | int          |                           |
| created-at | timestamp    | default current_timestamp |
| update_at  | timestamp    | default current_timestamp |
| acc_id     | INT          | FK → account(id)          |

---

### 3. tbl_address

| Trường   | Kiểu dữ liệu  | Ràng buộc    |
|----------|---------------|--------------|
| id       | INT           | PK, NOT NULL |
| country  | NVARCHAR(20)  | NOT NULL     |
| province | nvarchar(20)  | not null     |
| district | nvarchar(20)  | not null     |
| street   | nvarchar(100) |              |
| detail   | nvarchar(255) |              |
| u_id     | char(36)      | fk on casade |

---

**### 4. tbl_point

| Trường      | Kiểu dữ liệu | Ràng buộc |
|-------------|--------------|-----------|
| id          | INT          | PK        |
| current     | INT          | DEFAULT 0 |
| time_expira | TIMESTAMP    |           |
| user_id     | char(36)     | fk        |

---

### 5. tbl_point_history

| Thuộc tính | Kiểu dữ liệu | Ràng buộc                 |
|------------|--------------|---------------------------|
| id         | int          | pk                        |
| user_id    | char(36)     | fk                        |
| action     | ENUM         | EARN, USE, REFUND, EXPIRE | 
| point      | Integer      | số điểm tác động          | // điểm 
| created_at | Long         |                           | // thời điểm 
| invoice_id | int          | fk invoice                |

### 6. tbl_categories

| Trường    | Kiểu dữ liệu | Ràng buộc                 |
|-----------|--------------|---------------------------|
| id        | INT          | PK, NOT NULL              |
| name      | NVARCHAR(50) | NOT NULL                  |
| img_url   | varchar(255) |                           |
| desc      | Nvarchar(50) |                           |
| status    | varchar(20)  | default "active"          |
| parent_id | INT          | FK → category(id)         |
| create_at | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP |
| update_at | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP |

---

### 7. tbl_products

| Trường         | Kiểu dữ liệu  | Ràng buộc                 |
|----------------|---------------|---------------------------|
| id             | INT           | PK, NOT NULL              |
| name           | NVARCHAR(50)  | NOT NULL                  |
| desc           | NVARCHAR(255) |                           |
| quantity       | int           | default 0                 |
| quantity_slold | int           | default 0                 |
| price          | INT           |                           |
| status         | varchar(20)   | default 'active'          |
| rated          | FLOAT         |                           |
| category_id    | int           | fk -> tbl_categories(id)  |
| provider_id    | INT           | FK → provider(id)         |
| create_at      | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP |
| update_at      | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP |

---

### 8. tbl_providers

| Trường    | Kiểu dữ liệu  | Ràng buộc                 |
|-----------|---------------|---------------------------|
| id        | INT           | PK, NOT NULL              |
| name      | NVARCHAR(50)  | NOT NULL                  |
| desc      | NVARCHAR(255) |                           |
| email     | varchar(100)  |                           |
| phone     | VARCHAR(15)   |                           |
| status    | VARCHAR(20)   | DEFAULT 'ACTIVE'          |
| create_at | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP |
| update_at | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP |

---

### 9. tbl_comments

| Trường     | Kiểu dữ liệu  | Ràng buộc                 |
|------------|---------------|---------------------------|
| id         | INT           | PK, NOT NULL              |
| desc       | NVARCHAR(255) |                           |
| rate       | float         | tiny int                  |
| user_id    | char(36)      | FK → user(id)             |
| product_id | INT           | FK → product(id)          |
| create_at  | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP |
| update_at  | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP |

---

### 10. tbl_carts

| Trường  | Kiểu dữ liệu | Ràng buộc     |
|---------|--------------|---------------|
| id      | INT          | PK, NOT NULL  |
| user_id | char(36)     | FK → user(id) |

---

### 11. tbl_item_carts

| Trường     | Kiểu dữ liệu | Ràng buộc        |
|------------|--------------|------------------|
| id         | int          | pk               |
| cart_id    | INT          | FK → cart(id)    |
| product_id | INT          | FK → product(id) |
| quantity   | INT          |                  |

---

### 12. tbl_vouchers

| Trường       | Kiểu dữ liệu | Ràng buộc    |
|--------------|--------------|--------------|
| id           | INT          | PK, NOT NULL |
| code         | varchar(30)  | unique       |
| type         | VARCHAR(20)  |              |
| percent      | INT          |              |
| value        | INT          |              |
| require      | int          |              |
| max_discount | int          |              |
| url_img      | VARCHAR(255) |              |

---

### 13. tbl_user_voucher

| Trường     | Kiểu dữ liệu | Ràng buộc        |
|------------|--------------|------------------|
| user_id    | char(36)     | FK → user(id)    |
| voucher_id | INT          | FK → voucher(id) |
| status     | varchar(20)  | default 'active' |
| quantity   | int          | default 1        |
| start_at   | timestamp    |                  |
| end_at     | TIMESTAMP    |                  |

---

### 14. tbl_invoices

| Trường         | Kiểu dữ liệu | Ràng buộc     |
|----------------|--------------|---------------|
| id             | INT          | PK, NOT NULL  |
| code           | varchar(30)  | unique        |
| type           | VARCHAR(10)  |               |
| status         | VARCHAR(20)  |               |
| total_money    | INT          |               |
| total_discount | int          |               |
| create_at      | TIMESTAMP    |               |
| user_id        | char(36)     | FK → user(id) |

---

### 15. tbl_invoice_detail

| Trường     | Kiểu dữ liệu | Ràng buộc        |
|------------|--------------|------------------|
| id         | int          | pk               |
| invoice_id | INT          | FK → invoice(id) |
| product_id | INT          | FK → product(id) |
| quantity   | INT          |                  |
| voucher_id | INT          | FK → voucher(id) |
| discount   | int          |                  |
| total      | INT          |                  |

---

### 16. tbl_product_img

| Trường     | Kiểu dữ liệu | Ràng buộc             |
|------------|--------------|-----------------------|
| id         | INT          | PK                    |
| product_id | INT          | FK → tbl_products(id) |
| image_url  | VARCHAR(255) | NOT NULL              |
| is_main    | BOOLEAN      | DEFAULT FALSE         |
| created_at | TIMESTAMP    |                       |

---

### 16. tbl_avata_img

| Trường     | Kiểu dữ liệu | Ràng buộc          |
|------------|--------------|--------------------|
| id         | INT          | PK                 |
| user_id    | char(36)     | FK → tbl_users(id) |
| image_url  | VARCHAR(255) | NOT NULL           |
| is_main    | BOOLEAN      | DEFAULT FALSE      |
| created_at | TIMESTAMP    |                    |

---

### 16. tbl_provider_logo

| Trường      | Kiểu dữ liệu | Ràng buộc             |
|-------------|--------------|-----------------------|
| id          | INT          | PK                    |
| provider_id | INT          | FK → tbl_products(id) |
| image_url   | VARCHAR(255) | NOT NULL              |
| is_main     | BOOLEAN      | DEFAULT FALSE         |
| created_at  | TIMESTAMP    |                       |**

---

## II. Chức năng hệ thống (Modules)

---

### 1. Auth Module

- Đăng nhập
- Đăng xuất
- Đăng ký
- Cập nhật hồ sơ xác thực:
    - Đổi mật khẩu
    - Đổi email

---

### 2. User Module

- Tạo user
- Cập nhật thông tin cá nhân (name, addr, img)
- Xem thông tin cá nhân

---

## III. Chức năng theo vai trò

---

### 👤 Customer

#### 1. Đổi email

1. Click **Đổi email**
2. Nhập password
3. Nhập OTP gửi về email cũ
4. Nhập email mới
5. Nhập OTP gửi về email mới
6. Xác nhận thành công

#### 2. Đổi thông tin cá nhân

- Click **Chỉnh sửa profile**
- Thay đổi thông tin
- Click **Save**

#### 3. Tìm kiếm sản phẩm

- Tìm kiếm theo từ khóa (gần đúng)
- Tìm kiếm theo category (đa cấp)

#### 4. Xem chi tiết sản phẩm

- Click sản phẩm
- Hiển thị đầy đủ thông tin

#### 5. Thêm vào giỏ hàng

- Click **Add to cart**
- Server kiểm tra tồn kho
- Trả kết quả & hiển thị thành công

#### 6. Quản lý đơn hàng

- Xem danh sách sản phẩm đã mua

#### 7. Đánh giá sản phẩm

- Bình luận
- Thêm hình ảnh
- Đánh giá sao

#### 8. Tích điểm & Voucher

- Điểm danh hằng ngày
- Sử dụng voucher hoàn xu

---

### 👷 Employee

- Thêm sản phẩm mới
- Nhập mã nhà cung cấp
- Nhập thông tin sản phẩm
- Xác nhận thêm thành công

---

### 🛡️ Admin

#### Quản lý User

- Thêm nhân viên
- Tìm kiếm user theo thông tin
- Xem danh sách người dùng

### Products

- danh sách sp
- tìm kiếm
- lọc
- xem chi tiết
- quản lý ảnh sp
- quản lý tồn kho

---****