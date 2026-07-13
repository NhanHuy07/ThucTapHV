package com.dev.dungcony.commons.dtos;

public record ApiRes<T>(
        boolean success,
        String message,
        T data
) {

    public static <T> ApiRes<T> success(String message, T data) {
        return new ApiRes<>(true, message, data);
    }

    public static <T> ApiRes<T> success(T data) {
        return new ApiRes<>(true, "", data);
    }

    public static ApiRes<Void> success(String message) {
        return new ApiRes<>(true, message, null);
    }

    public static <T> ApiRes<T> error(String message) {
        return new ApiRes<>(false, message, null);
    }

    public static <T> ApiRes<T> error(String message, T data) {
        return new ApiRes<>(false, message, data);
    }

    public T getData() {
        return data;
    }
}
