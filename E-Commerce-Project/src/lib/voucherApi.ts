import { apiRequest } from './api';
import type { ApiResponse, UserVoucher } from '../types/shop';

export const getMyVouchers = (token: string) =>
    apiRequest<ApiResponse<UserVoucher[]>>('/v1/api/user/vouchers', {
        method: 'GET',
        token,
    });
