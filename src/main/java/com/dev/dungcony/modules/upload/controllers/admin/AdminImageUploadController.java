package com.dev.dungcony.modules.upload.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.upload.dtos.res.ImageUploadRes;
import com.dev.dungcony.modules.upload.services.interfaces.ImageUploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/upload")
@Tag(name = "Uploads")
public class AdminImageUploadController {

    private final ImageUploadService imageUploadService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiRes<ImageUploadRes>> uploadImage(
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(ApiRes.success(
                "Upload image successfully",
                imageUploadService.uploadImage(file)));
    }
}
