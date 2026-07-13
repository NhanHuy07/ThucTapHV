package com.dev.dungcony.modules.upload.exceptions;

import com.dev.dungcony.commons.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class FileUploadException extends AppException {
    public FileUploadException(String code, String message) {
        super(HttpStatus.BAD_REQUEST, code, message);
    }

    public FileUploadException(HttpStatus status, String code, String message) {
        super(status, code, message);
    }
}
