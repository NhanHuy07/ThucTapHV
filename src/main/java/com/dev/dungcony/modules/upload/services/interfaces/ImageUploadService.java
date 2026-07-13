package com.dev.dungcony.modules.upload.services.interfaces;

import com.dev.dungcony.modules.upload.dtos.res.ImageUploadRes;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
    ImageUploadRes uploadImage(MultipartFile file);

    ImageUploadRes uploadProductImage(String productCode, MultipartFile file);
}
