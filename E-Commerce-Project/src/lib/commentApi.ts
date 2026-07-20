import { apiRequest } from './api';
import type { ApiResponse, PageResponse, ProductComment, ProductCommentPayload } from '../types/shop';

export const getProductComments = (
    productCode: string,
    page = 0,
    size = 5
) => {
    const params = new URLSearchParams({
        productCode,
        page: String(page),
        size: String(size),
        sort: 'createdAt,desc',
    });

    return apiRequest<ApiResponse<PageResponse<ProductComment>>>(`/v1/api/public/product/comments?${params.toString()}`, {
        method: 'GET',
    });
};

export const createProductComment = (
    token: string,
    payload: ProductCommentPayload
) => apiRequest<ApiResponse<string>>('/v1/api/user/comment', {
    method: 'POST',
    token,
    body: payload,
});

export const getMyProductComments = (
    token: string,
    page = 0,
    size = 100
) => {
    const params = new URLSearchParams({
        page: String(page),
        size: String(size),
        sort: 'createdAt,desc',
    });

    return apiRequest<ApiResponse<PageResponse<ProductComment>>>(`/v1/api/user/comment/mine?${params.toString()}`, {
        method: 'GET',
        token,
    });
};

export const deleteMyProductComment = (
    token: string,
    productCode: string
) => apiRequest<ApiResponse<void>>(`/v1/api/user/comment/${encodeURIComponent(productCode)}`, {
    method: 'DELETE',
    token,
});
