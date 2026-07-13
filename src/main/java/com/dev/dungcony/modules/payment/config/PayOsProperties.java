package com.dev.dungcony.modules.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payos")
public record PayOsProperties(
        boolean enabled,
        String clientId,
        String apiKey,
        String checksumKey,
        String baseUrl,
        String returnUrl,
        String cancelUrl,
        String publicAppBaseUrl,
        long expiredMinutes
) {
    private static final String RETURN_PATH = "/v1/api/public/payment/payos/return";
    private static final String CANCEL_PATH = "/v1/api/public/payment/payos/cancel";
    private static final String DEFAULT_LOCAL_RETURN = "http://localhost:8080" + RETURN_PATH;
    private static final String DEFAULT_LOCAL_CANCEL = "http://localhost:8080" + CANCEL_PATH;
    private static final long DEFAULT_EXPIRED_MINUTES = 15;

    public PayOsProperties {
        if (expiredMinutes <= 0) expiredMinutes = DEFAULT_EXPIRED_MINUTES;
    }

    public boolean configured() {
        return enabled
                && hasText(clientId)
                && hasText(apiKey)
                && hasText(checksumKey);
    }

    public String resolvedReturnUrl() {
        if (hasText(returnUrl)) {
            return returnUrl.trim();
        }
        return resolvedUrl(RETURN_PATH, DEFAULT_LOCAL_RETURN);
    }

    public String resolvedCancelUrl() {
        if (hasText(cancelUrl)) {
            return cancelUrl.trim();
        }
        return resolvedUrl(CANCEL_PATH, DEFAULT_LOCAL_CANCEL);
    }

    public String resolvedBaseUrl() {
        return hasText(baseUrl) ? baseUrl.trim() : null;
    }

    private String resolvedUrl(String path, String localFallback) {
        String base = publicAppBaseUrl != null ? publicAppBaseUrl.trim() : "";
        if (base.isEmpty()) {
            String domain = System.getenv("RAILWAY_PUBLIC_DOMAIN");
            if (hasText(domain)) {
                domain = domain.replaceFirst("^https?://", "").replaceFirst("/$", "");
                base = "https://" + domain;
            }
        }
        if (!base.isEmpty()) {
            return stripTrailingSlashes(base) + path;
        }
        return localFallback;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String stripTrailingSlashes(String value) {
        int end = value.length();
        while (end > 0 && (value.charAt(end - 1) == '/' || value.charAt(end - 1) == '\\')) {
            end--;
        }
        return value.substring(0, end);
    }
}
