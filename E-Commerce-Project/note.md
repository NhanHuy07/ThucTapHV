Hiện tại BE đang xử lý tồn kho theo kiểu giữ hàng ngay khi thêm vào giỏ, không phải khi đặt hàng/giao hàng.
Cụ thể:
User thêm sản phẩm vào cart
BE trừ tồn kho tại CartUpdateImpl.java (line 57):
java
itemUpdateService.reduce(productDto.id(), sizeId, req.quantity());

User tăng/giảm số lượng trong cart
BE trừ thêm hoặc trả lại phần chênh lệch tại updateItemQuantity.

User xóa item khỏi cart hoặc clear cart
BE trả tồn kho lại bằng itemUpdateService.increase(...).

User đặt hàng
BE không trừ tồn kho nữa. Nó chỉ tạo order và xóa cart item bằng:

java
cartUpdateService.removeListItem(userId, orderItemDetail.cartItemDtos);
Admin đổi trạng thái đơn sang DELIVERED
BE chỉ đổi status, không trừ tồn kho.
Tóm lại: tồn kho bị trừ từ lúc sản phẩm nằm trong giỏ hàng. Khi order được tạo hoặc giao thành công, tồn kho không thay đổi thêm. Đây là lý do bạn thấy admin cập nhật Đã giao nhưng tồn kho vẫn giữ giá trị cũ.