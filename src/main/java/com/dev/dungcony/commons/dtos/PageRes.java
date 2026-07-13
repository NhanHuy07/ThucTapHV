package com.dev.dungcony.commons.dtos;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageRes<T>(
        List<T> items, // dữ liệu theo từng trang
        int page, // trang hiện tại
        int size, // size
        long totalElements, // tổng số sp
        int totalPages, // tổng số trang
        boolean hasNext // có thể chuyển trang
) {
    public static <T> PageRes<T> from(Page<T> page) {
        return new PageRes<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
    }
}
