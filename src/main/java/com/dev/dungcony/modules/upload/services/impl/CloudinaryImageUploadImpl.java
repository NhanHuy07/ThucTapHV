package com.dev.dungcony.modules.upload.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dev.dungcony.modules.upload.config.CloudinaryProperties;
import com.dev.dungcony.modules.upload.dtos.res.ImageUploadRes;
import com.dev.dungcony.modules.upload.exceptions.FileUploadException;
import com.dev.dungcony.modules.upload.services.interfaces.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryImageUploadImpl implements ImageUploadService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final CloudinaryProperties properties;

    @Override
    public ImageUploadRes uploadImage(MultipartFile file) {
        String publicId = "images/" + UUID.randomUUID();
        return upload(file, publicId, false);
    }

    @Override
    public ImageUploadRes uploadProductImage(String productCode, MultipartFile file) {
        String publicId = "products/" + sanitizePublicId(productCode);
        return upload(file, publicId, true);
    }

    private ImageUploadRes upload(MultipartFile file, String publicId, boolean overwrite) {
        validateCloudinaryConfig();
        validateImage(file);

        try {
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", properties.cloudName(),
                    "api_key", properties.apiKey(),
                    "api_secret", properties.apiSecret(),
                    "secure", true
            ));

            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", properties.resolvedFolder(),
                    "public_id", publicId,
                    "overwrite", overwrite
            ));

            Object secureUrl = result.get("secure_url");
            Object uploadedPublicId = result.get("public_id");
            if (secureUrl == null || uploadedPublicId == null) {
                throw new FileUploadException(
                        HttpStatus.BAD_GATEWAY,
                        "CLOUDINARY_INVALID_RESPONSE",
                        "Cloudinary upload response is invalid");
            }

            return new ImageUploadRes(secureUrl.toString(), uploadedPublicId.toString());
        } catch (IOException ex) {
            throw new FileUploadException(
                    HttpStatus.BAD_GATEWAY,
                    "CLOUDINARY_UPLOAD_FAILED",
                    "Upload image to Cloudinary failed");
        }
    }

    private void validateCloudinaryConfig() {
        if (!properties.configured()) {
            throw new FileUploadException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "CLOUDINARY_NOT_CONFIGURED",
                    "Cloudinary is not configured");
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("IMAGE_EMPTY", "Image file is required");
        }
        if (file.getSize() > properties.maxFileSizeBytes()) {
            throw new FileUploadException("IMAGE_TOO_LARGE", "Image file is too large");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new FileUploadException("IMAGE_TYPE_INVALID", "Only JPG, PNG, WEBP and GIF images are allowed");
        }
    }

    private String sanitizePublicId(String value) {
        if (value == null || value.isBlank()) {
            return UUID.randomUUID().toString();
        }
        String sanitized = value
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_-]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        return sanitized.isBlank() ? UUID.randomUUID().toString() : sanitized;
    }
}
