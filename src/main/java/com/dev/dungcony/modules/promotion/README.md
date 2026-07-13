# Promotions Module - API Documentation

## 📦 Tổng quan

Module quản lý khuyến mãi (promotions) cho hệ thống e-commerce, hỗ trợ nhiều loại khuyến mãi và phạm vi áp dụng.

## 🏗️ Cấu trúc

```
promotions/
├── Api/                    # Internal API service cho các module khác
├── controllers/            # REST Controllers
│   ├── AdminController.java
│   └── PromotionController.java
├── dtos/
│   ├── req/               # Request DTOs
│   │   ├── PromoAddReq.java
│   │   └── PromoUpdateReq.java
│   └── res/               # Response DTOs
│       ├── PromotionDto.java
│       ├── PromotionDetailDto.java
│       └── PromotionApiDto.java
├── entities/              # JPA Entities
│   ├── Promotion.java
│   ├── PromotionProduct.java
│   └── PromotionCategory.java
├── enums/                 # Enumerations
│   ├── PromotionType.java
│   ├── PromotionScope.java
│   └── PromotionStatus.java
├── exceptions/            # Custom Exceptions
│   ├── PromotionNotFoundException.java
│   ├── InvalidPromotionException.java
│   └── PromotionExceptionHandler.java
├── repositories/          # JPA Repositories
│   ├── PromotionRepository.java
│   ├── PromotionProductRepository.java
│   └── PromotionCategoryRepository.java
└── services/              # Business Logic
    ├── interfaces/
    └── impl/
```

## 🎯 Features

### 1. **Promotion Types**

- **PERCENT**: Giảm giá theo phần trăm (0-100%)
- **FIXED**: Giảm giá cố định (số tiền)

### 2. **Promotion Scopes**

- **GLOBAL**: Áp dụng toàn hệ thống
- **PRODUCT**: Áp dụng cho sản phẩm cụ thể
- **CATEGORY**: Áp dụng cho danh mục
- **PROVIDER**: Áp dụng cho nhà cung cấp

### 3. **Promotion Status**

- **SCHEDULED**: Đã lên lịch, chưa bắt đầu
- **ACTIVE**: Đang hoạt động
- **ENDED**: Đã kết thúc
- **DELETED**: Đã xóa (soft delete)

### 4. **Auto Scheduling**

Hệ thống tự động cập nhật status mỗi phút:

- SCHEDULED → ACTIVE (khi đến thời gian bắt đầu)
- ACTIVE → ENDED (khi hết hạn)

## 🔌 API Endpoints

### Admin APIs

#### 1. Lấy danh sách promotions (Paginated)

```http
GET /v1/api/admin/promotions/get-all?page=0&size=10
```

**Response:**

```json
{
  "status": "success",
  "message": "list promotion",
  "data": {
    "content": [
      ...
    ],
    "totalElements": 50,
    "totalPages": 5
  }
}
```

#### 2. Tạo promotion mới

```http
POST /v1/api/admin/promotions/add-new
Content-Type: application/json

{
  "type": "PERCENT",
  "value": 20,
  "scope": "PRODUCT",
  "startAt": "2026-02-15T00:00:00Z",
  "endAt": "2026-02-20T23:59:59Z",
  "priority": 1,
  "priceRequire": 100000,
  "productIds": [1, 2, 3],
  "categoryIds": null
}
```

**Response:**

```http
HTTP/1.1 201 Created
Location: /v1/api/promotions/123
```

#### 3. Cập nhật promotion

```http
PUT /v1/api/admin/promotions/update
Content-Type: application/json

{
  "id": 123,
  "value": 25,
  "priority": 2,
  "status": "ACTIVE"
}
```

**Response:**

```json
{
  "status": "success",
  "message": "Promotion updated successfully"
}
```

#### 4. Xóa promotion (Soft Delete)

```http
DELETE /v1/api/admin/promotions/delete-by-id
Content-Type: application/json

123
```

### User APIs

#### 1. Lấy promotions theo sản phẩm

```http
GET /v1/api/promotions/product/123
```

**Response:**

```json
{
  "status": "success",
  "message": "Promotions for product",
  "data": [
    {
      "promotionId": 1,
      "type": "PERCENT",
      "value": 20,
      "minPrice": 100000,
      "startAt": "2026-02-15T00:00:00Z",
      "endAt": "2026-02-20T23:59:59Z"
    }
  ]
}
```

#### 2. Lấy promotions theo danh mục

```http
GET /v1/api/promotions/category/5
```

#### 3. Lấy chi tiết promotion

```http
GET /v1/api/promotions/123
```

**Response:**

```json
{
  "status": "success",
  "message": "Promotion detail",
  "data": {
    "promotionId": 123,
    "type": "PERCENT",
    "value": 20,
    "minPrice": 100000,
    "startAt": "2026-02-15T00:00:00Z",
    "endAt": "2026-02-20T23:59:59Z"
  }
}
```

## 💡 Business Rules

### Validation Rules

1. **End date** phải sau **Start date**
2. **PERCENT type**: value phải từ 0-100
3. **PRODUCT scope**: phải có productIds
4. **CATEGORY scope**: phải có categoryIds
5. **minPriceApply**: phải >= 0

### Priority Logic

- Promotion có **priority cao hơn** được ưu tiên
- Cùng priority thì chọn promotion cho **discount lớn nhất**

### Scope Priority

1. **PRODUCT** - Ưu tiên cao nhất
2. **CATEGORY** - Ưu tiên thứ 2
3. **GLOBAL** - Mặc định

## 🔧 Internal API (cho modules khác)

```java

@Autowired
private ApiPromotionService apiPromotionService;

// Lấy promotion tốt nhất cho product
PromotionApiDto promotion = apiPromotionService.getByProductId(
        productId,
        categoryId,
        price
);

// promotion.finalPrice() - Giá sau khi giảm
```

## 📊 Database Schema

### tbl_promotions

```sql
- id: INT (PK)
- type: ENUM('PERCENT', 'FIXED')
- value: INT
- scope: ENUM('GLOBAL', 'PRODUCT', 'CATEGORY', 'PROVIDER')
- start_at: TIMESTAMP
- end_at: TIMESTAMP
- priority: INT
- status: ENUM('SCHEDULED', 'ACTIVE', 'ENDED', 'DELETED')
- min_price_apply: INT
```

### tbl_promotion_product

```sql
- product_id: INT (PK, FK)
- promotion_id: INT (PK, FK)
```

### tbl_promotion_category

```sql
- category_id: INT (PK, FK)
- promotion_id: INT (PK, FK)
```

## 🧪 Testing

```bash
# Run unit tests
mvn test -Dtest=PromotionServiceImplTest

# Run all promotion tests
mvn test -Dtest=*Promotion*
```

## 🚀 Usage Examples

### Example 1: Tạo Flash Sale (Giảm 50% trong 2 giờ)

```json
{
  "type": "PERCENT",
  "value": 50,
  "scope": "GLOBAL",
  "startAt": "2026-02-15T10:00:00Z",
  "endAt": "2026-02-15T12:00:00Z",
  "priority": 10,
  "priceRequire": 0
}
```

### Example 2: Giảm giá cho sản phẩm cụ thể

```json
{
  "type": "FIXED",
  "value": 50000,
  "scope": "PRODUCT",
  "startAt": "2026-02-15T00:00:00Z",
  "endAt": "2026-02-28T23:59:59Z",
  "priority": 5,
  "priceRequire": 200000,
  "productIds": [
    101,
    102,
    103
  ]
}
```

## ⚠️ Error Handling

### PromotionNotFoundException (404)

```json
{
  "status": "error",
  "message": "Promotion not found with id: 999"
}
```

### InvalidPromotionException (400)

```json
{
  "status": "error",
  "message": "End date must be after start date"
}
```

### Validation Error (400)

```json
{
  "status": "error",
  "message": "Validation failed",
  "data": {
    "value": "Value must be non-negative",
    "startAt": "Start date is required"
  }
}
```

## 📝 Notes

- Scheduled tasks chạy mỗi phút để update status
- Soft delete: status = DELETED thay vì xóa khỏi DB
- Promotions được cache (TODO: implement Redis cache)
- Audit log (TODO: implement promotion history tracking)

## 🔜 Future Enhancements

- [ ] Promotion codes/coupons
- [ ] Usage limit per customer
- [ ] Max discount cap
- [ ] Combination rules
- [ ] Analytics & Reports
- [ ] Redis caching
- [ ] Webhook notifications
