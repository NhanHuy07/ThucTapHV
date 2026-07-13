package com.dev.dungcony.modules.upload.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudinary")
public record CloudinaryProperties(
        String cloudName,
        String apiKey,
        String apiSecret,
        String folder,
        long maxFileSizeBytes
) {
    private static final String DEFAULT_FOLDER = "dungcony";
    private static final long DEFAULT_MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;

    public CloudinaryProperties {
        if (folder == null || folder.isBlank()) {
            folder = DEFAULT_FOLDER;
        }
        if (maxFileSizeBytes <= 0) {
            maxFileSizeBytes = DEFAULT_MAX_FILE_SIZE_BYTES;
        }
    }

    public boolean configured() {
        return cloudName != null && !cloudName.isBlank()
                && apiKey != null && !apiKey.isBlank()
                && apiSecret != null && !apiSecret.isBlank();
    }

    public String resolvedFolder() {
        String value = folder.trim();
        while (value.endsWith("/") || value.endsWith("\\")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
}
