package com.dev.dungcony.commons.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // URL tương đối: "Try it out" dùng đúng scheme/host với trang Swagger (tránh gọi http khi mở bằng https).
                .servers(List.of(new Server().url("/")))
                .info(new Info()
                        .title("Dungcony E-Commerce API")
                        .version("1.0")
                        .description("""
                                API documentation cho hệ thống thương mại điện tử.
                                
                                ## Hướng dẫn sử dụng:
                                1. Gọi **POST /v1/api/public/auth/login** để lấy access token
                                2. Click nút **Authorize** 🔓 phía trên
                                3. Nhập token vào ô **Value** (không cần gõ prefix `Bearer`)
                                4. Gọi các API có icon 🔒 bình thường
                                
                                ## Modules:
                                - **Auth** — Đăng ký, đăng nhập, refresh token, quên mật khẩu, cập nhật tài khoản
                                - **Products** — Sản phẩm, danh mục, nhà cung cấp
                                - **Promotions** — Khuyến mãi
                                - **Users** — Thông tin người dùng, địa chỉ
                                """)
                        .contact(new Contact()
                                .name("dungcony")
                                .email("dungcony@dev.com")))
                .addTagsItem(new Tag().name("Auth")
                        .description("Đăng ký / Đăng nhập / Refresh token / Đăng xuất / Quên mật khẩu / Cập nhật tài khoản"))
                .addTagsItem(new Tag().name("Products")
                        .description("Sản phẩm / Danh mục / Nhà cung cấp — public API và admin API"))
                .addTagsItem(new Tag().name("Promotions")
                        .description("Khuyến mãi — public API và admin API"))
                .addTagsItem(new Tag().name("Users")
                        .description("Thông tin người dùng / Địa chỉ — 🔒 Yêu cầu đăng nhập"))
                .addTagsItem(new Tag().name("Vouchers")
                        .description("Thông tin voucher — 🔒 Yêu cầu đăng nhập"))
                .addTagsItem(new Tag().name("Orders")
                        .description("Thông tin order — 🔒 Yêu cầu đăng nhập"))
                .addTagsItem(new Tag().name("Carts")
                        .description("Thông tin Giỏ hàng — 🔒 Yêu cầu đăng nhập"))
                .addTagsItem(new Tag().name("Payment")
                        .description("Thanh toán payOS — yêu cầu đăng nhập, trừ return/cancel/webhook"))
                .addTagsItem(new Tag().name("Notifications")
                        .description("Thông báo cho user và admin — 🔒 Yêu cầu đăng nhập"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Nhập JWT token (không cần prefix 'Bearer ')")
                        ));
    }
}
