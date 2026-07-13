package com.dev.dungcony.modules.auth.config;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Getter
@Setter
@Configuration
public class JwtConfig {

    /**
     * Trùng default trong application.yaml (chỉ dùng khi dev không set JWT_SECRET).
     */
    private static final String JWT_DEV_PLACEHOLDER = "00000000000000000000000000000000";

    @Autowired
    private Environment environment;

    @Value("${jwt.secret:}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Value("${jwt.refresh-cookie.secure:false}")
    private boolean refreshCookieSecure;

    @Value("${jwt.refresh-cookie.path:/v1/api/public/auth}")
    private String refreshCookiePath;

    public static final String headerPrefix = "Bearer ";

    /**
     * Bổ sung nếu placeholder trong YAML chưa resolve; prod không được dùng secret
     * mặc định dev.
     */
    @PostConstruct
    void applyJwtSecret() {
        if (secret == null || secret.isBlank()) {
            String s = System.getenv("JWT_SECRET");
            if (s != null && !s.isBlank()) {
                secret = s;
            }
        }
        boolean prod = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        if (prod && (secret == null || secret.isBlank() || JWT_DEV_PLACEHOLDER.equals(secret))) {
            if (isLocalDatabaseUrl(environment)) {
                log.warn(
                        "Profile prod but JWT_SECRET missing or default dev: allowing only because datasource URL looks local ({}). Set JWT_SECRET for real production.",
                        maskJdbcUrl(environment.getProperty("spring.datasource.url", "")));
                return;
            }
            throw new IllegalStateException(
                    "Thiếu JWT_SECRET: khai trên Railway (service Variables) với chuỗi bí mật tối thiểu 32 ký tự cho HS256.");
        }
    }

    /**
     * Cho phep chay prod tren may (localhost) ma khong cai JWT_SECRET; production
     * that van bat buoc.
     */
    private static boolean isLocalDatabaseUrl(Environment env) {
        String u = env.getProperty("spring.datasource.url", "");
        if (u == null || u.isBlank()) {
            u = System.getenv("SPRING_DATASOURCE_URL");
        }
        if (u == null || u.isBlank()) {
            return false;
        }
        String s = u.toLowerCase();
        return s.contains("localhost")
                || s.contains("127.0.0.1")
                || s.contains("::1")
                || s.contains("host.docker.internal");
    }

    private static String maskJdbcUrl(String url) {
        if (url == null || url.length() < 12) {
            return "(empty)";
        }
        return url.substring(0, Math.min(32, url.length())) + "…";
    }
}
