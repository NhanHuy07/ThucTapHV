package com.dev.dungcony.commons.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tự động log tiếng Việt mô tả từng API khi có request đến.
 * Log cả thời gian xử lý và HTTP status trả về.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class ApiRequestLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiRequestLogFilter.class);

    private record RouteInfo(String method, String pattern, String desc) {
    }

    private static final Map<String, RouteInfo> EXACT_ROUTES = new LinkedHashMap<>();
    private static final Map<String, RouteInfo> PREFIX_ROUTES = new LinkedHashMap<>();

    static {
        // ==================== AUTH ====================
        exact("POST /v1/api/public/auth/login", "Đăng nhập tài khoản");
        exact("POST /v1/api/public/auth/refresh", "Làm mới access token");
        exact("POST /v1/api/public/auth/logout", "Đăng xuất");
        exact("POST /v1/api/public/auth/regis", "Đăng ký tài khoản");
        exact("POST /v1/api/public/auth/regis/verify", "Xác thực OTP đăng ký");
        exact("POST /v1/api/public/auth/forgot-password", "Quên mật khẩu — gửi mật khẩu mới qua email");
        exact("POST /v1/api/public/auth/create-admin", "Tạo tài khoản admin");

        // ==================== ACCOUNT UPDATE ====================
        exact("POST /v1/api/account/update/email/send-otp", "Gửi OTP đổi email");
        exact("POST /v1/api/account/update/email/verify-otp", "Xác nhận OTP đổi email");
        exact("POST /v1/api/account/update/password", "Đổi mật khẩu");

        // ==================== ACCOUNT CHECK ====================
        exact("GET /v1/api/account/check/exists-email", "Kiểm tra email đã tồn tại");
        exact("GET /v1/api/account/check/exists-username", "Kiểm tra username đã tồn tại");
        exact("POST /v1/api/account/check/password", "Xác nhận mật khẩu hiện tại");

        // ==================== USER ====================
        exact("GET /v1/api/user/get/me", "Lấy thông tin cá nhân");
        exact("PUT /v1/api/user/update/profile", "Cập nhật thông tin cá nhân");

        // ==================== USER — RECEIVER ====================
        exact("POST /v1/api/user/receiver/create", "Tạo địa chỉ nhận hàng");
        exact("GET /v1/api/user/receiver/get/all", "Lấy tất cả địa chỉ nhận hàng");
        exact("GET /v1/api/user/receiver/get", "Lấy địa chỉ nhận hàng theo id");
        exact("DELETE /v1/api/user/receiver/remove", "Xóa địa chỉ nhận hàng");
        exact("DELETE /v1/api/user/receiver/remove/all", "Xóa tất cả địa chỉ nhận hàng");

        // ==================== CART ====================
        exact("GET /v1/api/user/cart", "Lấy giỏ hàng");
        exact("POST /v1/api/user/cart/add", "Thêm sản phẩm vào giỏ hàng");
        exact("PATCH /v1/api/user/cart/update-quantity", "Cập nhật số lượng trong giỏ");
        exact("DELETE /v1/api/user/cart/remove", "Xóa sản phẩm khỏi giỏ hàng");
        exact("DELETE /v1/api/user/cart/clear", "Xóa toàn bộ giỏ hàng");

        // ==================== ORDER (USER) ====================
        exact("POST /v1/api/user/order/create", "Tạo đơn hàng");
        exact("GET /v1/api/user/order/my-orders", "Lấy danh sách đơn hàng của tôi");
        exact("GET /v1/api/user/order/my-orders/status", "Lấy đơn hàng theo trạng thái");
        prefix("PATCH /v1/api/user/order/", "Hủy đơn hàng");
        prefix("GET /v1/api/user/order/", "Xem chi tiết đơn hàng");

        // ==================== COMMENT (USER) ====================
        exact("POST /v1/api/user/comment", "Tạo bình luận sản phẩm");
        exact("GET /v1/api/user/comment/mine", "Lấy danh sách bình luận của tôi");
        exact("GET /v1/api/user/comment/mine/by-product", "Lấy bình luận của tôi theo sản phẩm");
        prefix("PUT /v1/api/user/comment/", "Cập nhật bình luận");
        prefix("DELETE /v1/api/user/comment/", "Xóa bình luận");

        // ==================== VOUCHER (USER) ====================
        exact("GET /v1/api/user/vouchers", "Lấy danh sách voucher của tôi");

        // ==================== PUBLIC — PRODUCT ====================
        exact("GET /v1/api/public/product/get-all", "Lấy danh sách sản phẩm");
        exact("GET /v1/api/public/product/best-seller", "Lấy sản phẩm bán chạy");
        exact("GET /v1/api/public/product/get-by-code", "Xem chi tiết sản phẩm");
        exact("GET /v1/api/public/product/search", "Tìm kiếm sản phẩm");
        exact("GET /v1/api/public/product/filter", "Lọc sản phẩm");
        exact("GET /v1/api/public/product/comments", "Lấy bình luận sản phẩm (public)");
        prefix("GET /v1/api/public/product/item/", "Lấy thông tin item (size) sản phẩm");

        // ==================== PUBLIC — CATEGORY ====================
        exact("GET /v1/api/public/category/get-all", "Lấy toàn bộ danh mục");
        exact("GET /v1/api/public/category/get-all-leaf", "Lấy toàn bộ danh mục leaf đang active");
        prefix("GET /v1/api/public/category/get-children/", "Lấy danh mục con");
        prefix("GET /v1/api/public/category/get/", "Lấy thông tin danh mục");

        // ==================== PUBLIC — PROVIDER ====================
        prefix("GET /v1/api/public/provider/get-by-code/", "Lấy nhà cung cấp theo mã");
        prefix("GET /v1/api/public/provider/get-by-name/", "Lấy nhà cung cấp theo tên");
        exact("GET /v1/api/public/provider/gets", "Lấy toàn bộ nhà cung cấp");

        // ==================== PAYMENT (payOS) ====================
        prefix("POST /v1/api/user/payment/payos/", "Tạo link thanh toán payOS");
        exact("GET /v1/api/public/payment/payos/return", "payOS redirect thanh toán thành công");
        exact("GET /v1/api/public/payment/payos/cancel", "payOS redirect hủy thanh toán");
        exact("POST /v1/api/public/payment/payos/webhook", "Webhook thanh toán payOS");

        // ==================== PUBLIC — PROMOTION ====================
        prefix("GET /v1/api/promotions/product/", "Lấy khuyến mãi theo sản phẩm");
        prefix("GET /v1/api/promotions/category/", "Lấy khuyến mãi theo danh mục");
        prefix("GET /v1/api/promotions/", "Lấy chi tiết khuyến mãi");

        // ==================== ADMIN — USER ====================
        exact("GET /v1/api/admin/user/get-all", "[ADMIN] Lấy tất cả user");
        exact("GET /v1/api/admin/user", "[ADMIN] Lấy user theo id/tên");
        exact("PUT /v1/api/admin/user/update", "[ADMIN] Cập nhật user");

        // ==================== ADMIN — RECEIVER ====================
        exact("GET /v1/api/admin/receiver/get-all", "[ADMIN] Lấy tất cả receiver");
        exact("GET /v1/api/admin/receiver", "[ADMIN] Lấy receiver theo id");
        exact("GET /v1/api/admin/receiver/by-user", "[ADMIN] Lấy receiver theo user");
        exact("DELETE /v1/api/admin/receiver", "[ADMIN] Xóa receiver");
        exact("DELETE /v1/api/admin/receiver/by-user", "[ADMIN] Xóa tất cả receiver của user");

        // ==================== ADMIN — PRODUCT ====================
        exact("POST /v1/api/admin/product/product/add-new", "[ADMIN] Thêm sản phẩm mới");
        exact("PUT /v1/api/admin/product/product/update", "[ADMIN] Cập nhật sản phẩm");
        prefix("DELETE /v1/api/admin/product/product/", "[ADMIN] Xóa sản phẩm");
        exact("POST /v1/api/admin/product/add-items", "[ADMIN] Thêm item (size) cho sản phẩm");
        exact("GET /v1/api/admin/product/items", "[ADMIN] Lấy toàn bộ size của sản phẩm");

        // ==================== ADMIN — CATEGORY ====================
        exact("POST /v1/api/admin/category/category/add-new", "[ADMIN] Thêm danh mục mới");
        prefix("DELETE /v1/api/admin/category/category/", "[ADMIN] Xóa danh mục");

        // ==================== ADMIN — PROVIDER ====================
        exact("POST /v1/api/admin/provider/add-new", "[ADMIN] Thêm nhà cung cấp");
        prefix("PUT /v1/api/admin/provider/update/", "[ADMIN] Cập nhật nhà cung cấp");
        prefix("DELETE /v1/api/admin/provider/delete/", "[ADMIN] Xóa nhà cung cấp");

        // ==================== ADMIN — PROMOTION ====================
        exact("GET /v1/api/admin/promotions/get-all", "[ADMIN] Lấy tất cả khuyến mãi");
        exact("POST /v1/api/admin/promotions/add-new", "[ADMIN] Thêm khuyến mãi mới");
        exact("PUT /v1/api/admin/promotions/update", "[ADMIN] Cập nhật khuyến mãi");
        prefix("PATCH /v1/api/admin/promotions/", "[ADMIN] Xóa khuyến mãi");

        // ==================== ADMIN — ORDER ====================
        exact("GET /v1/api/admin/order/get-all", "[ADMIN] Lấy tất cả đơn hàng");
        exact("GET /v1/api/admin/order/get-by-status", "[ADMIN] Lấy đơn hàng theo trạng thái");
        exact("PATCH /v1/api/admin/order/update-status", "[ADMIN] Cập nhật trạng thái đơn hàng");
        prefix("GET /v1/api/admin/order/", "[ADMIN] Xem chi tiết đơn hàng");

        // ==================== ADMIN - UPLOAD ====================
        exact("POST /v1/api/admin/upload/image", "[ADMIN] Upload image");

        // ==================== ADMIN — VOUCHER ====================
        exact("POST /v1/api/admin/voucher/create", "[ADMIN] Tạo voucher mới");
        exact("PATCH /v1/api/admin/voucher/update", "[ADMIN] Cập nhật voucher");
        exact("GET /v1/api/admin/user-vouchers/get-by-id", "[ADMIN] Lấy voucher user theo id");
        exact("GET /v1/api/admin/user-vouchers/get-by-name", "[ADMIN] Lấy voucher user theo tên");
    }

    private static void exact(String key, String desc) {
        String[] parts = key.split(" ", 2);
        EXACT_ROUTES.put(key, new RouteInfo(parts[0], parts[1], desc));
    }

    private static void prefix(String key, String desc) {
        String[] parts = key.split(" ", 2);
        PREFIX_ROUTES.put(key, new RouteInfo(parts[0], parts[1], desc));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();

        if (shouldSkip(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        String desc = resolve(method, uri);
        String qs = request.getQueryString();
        String fullPath = qs != null ? uri + "?" + qs : uri;

        log.info(">>> {} {} — {}", method, fullPath, desc);

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long ms = System.currentTimeMillis() - start;
            int status = response.getStatus();

            if (status >= 400) {
                log.warn("<<< {} {} — {} — HTTP {} ({}ms)", method, fullPath, desc, status, ms);
            } else {
                log.info("<<< {} {} — {} — HTTP {} ({}ms)", method, fullPath, desc, status, ms);
            }
        }
    }

    private String resolve(String method, String uri) {
        String exactKey = method + " " + uri;
        RouteInfo exact = EXACT_ROUTES.get(exactKey);
        if (exact != null)
            return exact.desc();

        for (var entry : PREFIX_ROUTES.entrySet()) {
            RouteInfo ri = entry.getValue();
            if (method.equals(ri.method()) && uri.startsWith(ri.pattern())) {
                return ri.desc();
            }
        }

        return "API không xác định";
    }

    private boolean shouldSkip(String uri) {
        return uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.equals("/") ||
                uri.equals("/favicon.ico") ||
                uri.startsWith("/actuator");
    }
}
