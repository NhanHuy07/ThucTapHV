
# API Quản lý sản phẩm
### Danh mục chức năng lọc collection cho sản phẩm 

1. Chưa có size  cho sản phẩm
2. Lọc theo danh mục 
    * Nam
    * Nữ
    * Unisex (Cho cả Nam, Nữ mặc được)

3. Lọc theo loại sản phẩm 

* **Nam**
    * Áo 
        * Áo thun
        * Áo thể thao
        * Áo sơ mi 
        * Áo khoác
        * Áo polo
    * Quần
        * Quần bơi
        * Quần jean
        * Quần kaki
        * Quần Jogger
        * Quần Short
* **Nữ** 
    * Áo 
        * Áo croptop
        * Áo Polo
        * Áo Bra 
        * Áo dài tay
        * Áo sơ mi 
    * Quần, váy 
        * Váy ngắn
        * Đầm 
        * Quần legging
        * Quần dài

4. Lọc theo khoảng giá, giá từ thấp đến cao và ngược lại
    - Cho phép nhập số tiền cụ thể từ 0 -> n đơn vị VNĐ
    - Cho phép lọc giá tiền từ thấp đến cao
    - Cho phép lọc giá tiền từ cao đến thấp 
    - Sản phẩm mới nhất

5. Phân trang 

---
# API QUẢN LÝ GIỎ HÀNG (CART)

Yêu cầu Auth (Token) cho tất cả các API này.

### 1. Thêm sản phẩm vào giỏ (Add to Cart)
- **Method**: `POST`
- **Endpoint**: `/v1/api/cart/add`
- **Mô tả**: Thêm 1 sản phẩm với kích thước cụ thể vào giỏ hàng của user hiện tại.
- **Body Request**:
```json
{
    "productId": "string", // ID của sản phẩm
    "size": "string",      // Kích thước (S, M, L, XL, ...)
    "quantity": 1          // Số lượng thêm vào (thường mặc định là 1)
}
```
- **Logic Backend**:
  - Người dùng bắt buộc cần chọn size để thêm sản phẩm vào giỏ hàng
  - Nếu sản phẩm + size này **chưa có** trong giỏ: Tạo mới item.
  - Nếu sản phẩm + size này **đã có** trong giỏ: Cộng dồn `quantity` lên.
  - Kiểm tra xem số lượng trong kho còn đủ không.
- **Response**: Trả về thông tin giỏ hàng hiện tại hoặc message thành công.

### 2. Lấy thông tin giỏ hàng (Get Cart)
- **Method**: `GET`
- **Endpoint**: `/v1/api/cart`
- **Mô tả**: Lấy toàn bộ danh sách sản phẩm đang có trong giỏ hàng của user hiển thị cho Frontend
- **Response Thành Công**:
```json
{
    "success": true,
    "data": {
        "cartItems": [
            {
                "cartItemId": "string",
                "productId": "string",
                "productName": "Premium Cotton T-Shirt",
                "price": 100,
                "image": "url_anh_1",
                "size": "L",
                "quantity": 2
            }
        ],
        "totalAmount": 200 // Tổng tiền (Backend tự tính để khỏi lệch với FE)
    }
}
```

### 3. Cập nhật số lượng sản phẩm trong giỏ (Update Quantity)
- **Method**: `PUT`
- **Endpoint**: `/v1/api/cart/update`
- **Mô tả**: Khi người đang đứng trong trang giỏ hàng sẽ dùng ấn nút "+" hoặc "-" trong trang Cart để tăng giảm số lượng hàng trong giỏ hàng.
- **Body Request**:
```json
{
    "cartItemId": "string", // ID của dòng sản phẩm trong giỏ
    "quantity": 3           // Số lượng sau khi đổi 
}
```
- **Logic Backend**: Nếu `quantity` = 0, tự động xóa item đó khỏi giỏ hàng. Quét check lại hàng trong kho.

### 4. Xoá sản phẩm khỏi giỏ (Remove from Cart)
- **Method**: `DELETE`
- **Endpoint**: `/v1/api/cart/remove/{cartItemId}`
- **Mô tả**: Khi user bấm icon thùng rác để xóa hẳn một mặt hàng.

---
# API QUẢN LÝ ĐẶT HÀNG (ORDER & CHECKOUT)



- Không cần Auth (Token) cho tất cả. Nếu người mua chưa đăng nhập có thể yêu cầu người mua nhập các thông tin cần thiết cho việc đặt hàng.
- Người dùng đã đăng nhập hệ thống đã có đầy đủ thông tin người người dùng vì thế nên khi đặt hàng sẽ chuyển trang sang thanh toán luôn.
### 1. Tạo đơn hàng (Place Order)
- **Method**: `POST`
- **Endpoint**: `/v1/api/order/create`
- **Mô tả**: User điền thông tin ship và bấm "Place Order".
- **Body Request**:
```json
{
    "shippingInfo": {
        "fullName": "Nguyễn Văn A",
        "email": "a@gmail.com",
        "phone": "0987654321",
        "address": "123 Đường B, Quận C",
        "city": "Hà Nội",
        "zipCode": "100000"
    },
    "paymentMethod": "COD", // "COD" (Thu hộ) hoặc Thanh toán bằng ngân hàng 
    "items": [ 
        {
            "productId": "string",
            "size": "L",
            "quantity": 2,
            "price": 100 
        }
    ],
    "deliveryFee": 10
}
```
- **Logic Backend**:
  - Trừ tồn kho (`stock`) của từng sản phẩm tướng ứng. 
  - Tính lại thành tiền một lần nữa để phòng hờ FE gửi sai giá (Bảo mật).
  - Đổi trạng thái `Cart` (xóa rỗng giỏ hàng).
  - Trả về Mã Đơn Hàng (`orderId`).

### 2. Danh sách đơn hàng của tôi (Get My Orders)
- **Method**: `GET`
- **Endpoint**: `/v1/api/order/my-orders`
- **Mô tả**: Hiển thị trên màn hình "Orders" của user (chỉ lấy đơn của user đó). 
- **Response Thành Công**: Mảng các đơn hàng gồm thông tin Payment, Trạng thái (Đang đóng gói, Đang giao, Đã giao, Hủy), Ngày giờ đặt, và Chi tiết từng loại áo.

### 3. Lấy chi tiết một đơn hàng (Order Details)
- **Method**: `GET`
- **Endpoint**: `/v1/api/order/{orderId}`
- **Mô tả**: Xem thông tin chi tiết 1 đơn hàng -> Tình trạng giao hàng, Địa chỉ, Giá trị đơn hàng, thông tin người đặt hàng  
