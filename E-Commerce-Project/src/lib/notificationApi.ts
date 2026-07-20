import { apiRequest } from './api';
import type { ApiResponse, NotificationItem, NotificationScope, PageResponse } from '../types/shop';

const notificationBase = (scope: NotificationScope) =>
    scope === 'admin' ? '/v1/api/admin/notification' : '/v1/api/user/notifications';

export const getNotifications = (
    scope: NotificationScope,
    token: string,
    page = 0,
    size = 8
) => {
    const base = scope === 'admin' ? `${notificationBase(scope)}/get` : `${notificationBase(scope)}/get`;
    const params = new URLSearchParams({
        page: String(page),
        size: String(size),
        sort: 'createdAt,desc',
    });

    return apiRequest<ApiResponse<PageResponse<NotificationItem>>>(`${base}?${params.toString()}`, {
        method: 'GET',
        token,
    });
};

export const getUnreadNotificationCount = async (
    scope: NotificationScope,
    token: string
) => {
    type AdminUnread = { unread?: number };
    const response = await apiRequest<ApiResponse<number | AdminUnread>>(`${notificationBase(scope)}/get/unread-count`, {
        method: 'GET',
        token,
    });

    if (typeof response.data === 'number') {
        return response.data;
    }

    return response.data?.unread || 0;
};

export const markNotificationRead = (
    scope: NotificationScope,
    token: string,
    code: string
) => apiRequest<ApiResponse<void>>(`${notificationBase(scope)}/update/read/${encodeURIComponent(code)}`, {
    method: 'PUT',
    token,
});

export const markAllNotificationsRead = (
    scope: NotificationScope,
    token: string
) => apiRequest<ApiResponse<number>>(`${notificationBase(scope)}/update/read-all`, {
    method: 'PUT',
    token,
});

export const deleteNotification = (
    scope: NotificationScope,
    token: string,
    code: string
) => apiRequest<ApiResponse<void>>(`${notificationBase(scope)}/delete/${encodeURIComponent(code)}`, {
    method: 'PUT',
    token,
});

export const clearNotifications = (
    scope: NotificationScope,
    token: string
) => apiRequest<ApiResponse<void>>(`${notificationBase(scope)}/clear`, {
    method: 'PUT',
    token,
});
