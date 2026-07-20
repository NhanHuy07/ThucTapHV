import { useCallback, useEffect, useRef, useState } from 'react';
import { toast } from 'react-toastify';
import {
    clearNotifications,
    deleteNotification,
    getNotifications,
    getUnreadNotificationCount,
    markAllNotificationsRead,
    markNotificationRead,
} from '../lib/notificationApi';
import type { NotificationItem, NotificationScope } from '../types/shop';

type NotificationDropdownProps = {
    token: string;
    scope: NotificationScope;
};

const pageSize = 8;

const getDisplayNotification = (title: string, message: string, scope: 'admin' | 'user') => {
    const rawTitle = title || '';
    const rawMessage = message || '';

    // 1. Đơn hàng mới
    if (rawTitle === 'create_order' || rawMessage === 'tạo đơn hàng') {
        if (scope === 'admin') {
            return {
                title: 'Bạn có đơn hàng mới',
                message: 'Hệ thống vừa tiếp nhận một đơn hàng mới từ khách hàng. Vui lòng kiểm tra và xử lý.'
            };
        } else {
            return {
                title: 'Đặt hàng thành công',
                message: 'Cảm ơn bạn! Đơn hàng mới của bạn đã được khởi tạo thành công và đang chờ xác nhận.'
            };
        }
    }

    // 2. Thanh toán đơn hàng
    if (rawTitle === 'paid_order' || rawMessage === 'thanh toán đơn hàng') {
        if (scope === 'admin') {
            return {
                title: 'Đơn hàng đã thanh toán',
                message: 'Đơn hàng của khách hàng vừa được thanh toán thành công qua cổng thanh toán online.'
            };
        } else {
            return {
                title: 'Thanh toán thành công',
                message: 'Tuyệt vời! Đơn hàng của bạn đã hoàn tất thanh toán và đang được xử lý giao hàng.'
            };
        }
    }

    // 3. Hủy đơn hàng
    if (rawTitle === 'cancel_order' || rawMessage === 'hủy đơn hàng') {
        if (scope === 'admin') {
            return {
                title: 'Khách hàng hủy đơn',
                message: 'Khách hàng vừa thực hiện thao tác hủy đơn hàng trên hệ thống.'
            };
        } else {
            return {
                title: 'Đã hủy đơn hàng',
                message: 'Đơn hàng của bạn đã được hủy thành công theo yêu cầu của hệ thống.'
            };
        }
    }

    // 4. Admin duyệt đơn hàng (CONFIRM_ORDER)
    if (rawTitle === 'CONFIRM_ORDER' || rawMessage === 'đơn hàng của bạn đã được xác nhận') {
        return {
            title: 'Đơn hàng đã được xác nhận',
            message: 'Chào bạn, đơn hàng của bạn đã được quản trị viên phê duyệt thành công và đang được chuẩn bị để giao đi.'
        };
    }

    // 5. Đơn hàng đang được giao hoặc đã giao tới nơi (DELIVERED_ORDER)
    if (rawTitle === 'DELIVERED_ORDER' || rawMessage === 'đơn hàng của bạn đã được giao vui lòng xác nhận') {
        return {
            title: 'Đơn hàng đã giao tới nơi',
            message: 'Tuyệt vời! Đơn hàng của bạn đã được vận chuyển đến nơi thành công. Chúc bạn có trải nghiệm mua sắm vui vẻ!'
        };
    }

    // Trả về mặc định nếu không khớp
    return { title: rawTitle, message: rawMessage };
};

const NotificationDropdown = ({ token, scope }: NotificationDropdownProps) => {
    const wrapperRef = useRef<HTMLDivElement | null>(null);
    const [open, setOpen] = useState(false);
    const [notifications, setNotifications] = useState<NotificationItem[]>([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(false);
    const [updatingCode, setUpdatingCode] = useState('');

    const loadUnreadCount = useCallback(async () => {
        if (!token) return;

        try {
            const count = await getUnreadNotificationCount(scope, token);
            setUnreadCount(count);
        } catch (error) {
            console.error(error);
            setUnreadCount(0);
        }
    }, [scope, token]);

    const loadNotifications = useCallback(async () => {
        if (!token) return;

        setLoading(true);
        try {
            const response = await getNotifications(scope, token, page, pageSize);
            const pageData = response.data;
            setNotifications(pageData?.items || []);
            setTotalPages(pageData?.totalPages || 0);
        } catch (error) {
            console.error(error);
            toast.error('Không thể tải thông báo');
        } finally {
            setLoading(false);
        }
    }, [page, scope, token]);

    useEffect(() => {
        loadUnreadCount();
        const timer = window.setInterval(loadUnreadCount, 60000);
        return () => window.clearInterval(timer);
    }, [loadUnreadCount]);

    useEffect(() => {
        if (open) {
            loadNotifications();
            loadUnreadCount();
        }
    }, [loadNotifications, loadUnreadCount, open]);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (wrapperRef.current && !wrapperRef.current.contains(event.target as Node)) {
                setOpen(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const refreshNotifications = async () => {
        await Promise.all([loadNotifications(), loadUnreadCount()]);
    };

    const handleMarkRead = async (code: string) => {
        setUpdatingCode(code);
        try {
            await markNotificationRead(scope, token, code);
            await refreshNotifications();
        } catch (error) {
            console.error(error);
            toast.error('Không thể đánh dấu đã đọc');
        } finally {
            setUpdatingCode('');
        }
    };

    const handleDelete = async (code: string) => {
        setUpdatingCode(code);
        try {
            await deleteNotification(scope, token, code);
            await refreshNotifications();
        } catch (error) {
            console.error(error);
            toast.error('Không thể xóa thông báo');
        } finally {
            setUpdatingCode('');
        }
    };

    const handleMarkAllRead = async () => {
        try {
            await markAllNotificationsRead(scope, token);
            await refreshNotifications();
            toast.success('Đã đánh dấu tất cả thông báo');
        } catch (error) {
            console.error(error);
            toast.error('Không thể đánh dấu tất cả');
        }
    };

    const handleClear = async () => {
        const confirmed = window.confirm('Xóa toàn bộ thông báo?');
        if (!confirmed) return;

        try {
            await clearNotifications(scope, token);
            setPage(0);
            setNotifications([]);
            setTotalPages(0);
            setUnreadCount(0);
            toast.success('Đã dọn dẹp thông báo');
        } catch (error) {
            console.error(error);
            toast.error('Không thể dọn dẹp thông báo');
        }
    };

    return (
        <div ref={wrapperRef} className='relative'>
            <button
                type='button'
                onClick={() => setOpen((current) => !current)}
                className='relative flex h-8 w-8 items-center justify-center border border-gray-300 text-gray-700 hover:border-black hover:text-black'
                title='Thông báo'
                aria-label='Thông báo'
            >
                <svg
                    xmlns='http://www.w3.org/2000/svg'
                    width='17'
                    height='17'
                    viewBox='0 0 24 24'
                    fill='none'
                    stroke='currentColor'
                    strokeWidth='2'
                    strokeLinecap='round'
                    strokeLinejoin='round'
                    aria-hidden='true'
                >
                    <path d='M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9' />
                    <path d='M13.73 21a2 2 0 0 1-3.46 0' />
                </svg>
                {unreadCount > 0 && (
                    <span className='absolute -right-2 -top-2 min-w-4 rounded-full bg-red-600 px-1 text-center text-[9px] leading-4 text-white'>
                        {unreadCount > 99 ? '99+' : unreadCount}
                    </span>
                )}
            </button>

            {open && (
                <div className='absolute right-0 top-9 z-[80] w-[min(88vw,380px)] border border-gray-200 bg-white shadow-xl'>
                    <div className='flex items-start justify-between gap-3 border-b border-gray-100 px-4 py-3'>
                        <div>
                            <p className='text-sm font-semibold text-gray-900'>Thông báo</p>
                            <p className='mt-1 text-xs text-gray-500'>
                                {scope === 'admin' ? 'Thông báo quản trị' : 'Thông báo của bạn'}
                            </p>
                        </div>
                        <div className='flex gap-2'>
                            <button type='button' onClick={handleMarkAllRead} className='text-xs font-medium text-gray-500 hover:text-black'>
                                Đọc hết
                            </button>
                            <button type='button' onClick={handleClear} className='text-xs font-medium text-red-500 hover:text-red-700'>
                                Xóa hết
                            </button>
                        </div>
                    </div>

                    <div className='max-h-[420px] overflow-y-auto'>
                        {loading ? (
                            <p className='px-4 py-8 text-center text-sm text-gray-500'>Đang tải thông báo...</p>
                        ) : notifications.length === 0 ? (
                            <p className='px-4 py-8 text-center text-sm text-gray-500'>Chưa có thông báo.</p>
                        ) : (
                            <div className='divide-y divide-gray-100'>
                                {notifications.map((notification) => {
                                    const display = getDisplayNotification(notification.title, notification.message, scope);
                                    return (
                                        <div key={notification.code} className='px-4 py-3'>
                                            <div className='flex items-start justify-between gap-3'>
                                                <div>
                                                    <p className='text-sm font-medium text-gray-900'>{display.title || 'Thông báo'}</p>
                                                    <p className='mt-1 text-sm leading-5 text-gray-600'>{display.message}</p>
                                                {notification.senderName && (
                                                    <p className='mt-2 text-xs text-gray-400'>Từ: {notification.senderName}</p>
                                                )}
                                            </div>
                                            <div className='flex shrink-0 flex-col gap-2'>
                                                <button
                                                    type='button'
                                                    onClick={() => handleMarkRead(notification.code)}
                                                    disabled={updatingCode === notification.code}
                                                    className='border border-gray-300 px-2 py-1 text-[11px] font-medium hover:border-black disabled:text-gray-300'
                                                >
                                                    Đã đọc
                                                </button>
                                                <button
                                                    type='button'
                                                    onClick={() => handleDelete(notification.code)}
                                                    disabled={updatingCode === notification.code}
                                                    className='border border-red-200 px-2 py-1 text-[11px] font-medium text-red-500 hover:border-red-500 disabled:text-gray-300'
                                                >
                                                    Xóa
                                                </button>
                                                </div>
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        )}
                    </div>

                    <div className='flex items-center justify-between border-t border-gray-100 px-4 py-3'>
                        <p className='text-xs text-gray-500'>Trang {totalPages ? page + 1 : 0}/{totalPages}</p>
                        <div className='flex gap-2'>
                            <button
                                type='button'
                                onClick={() => setPage((current) => Math.max(current - 1, 0))}
                                disabled={page === 0 || loading}
                                className='border border-gray-300 px-3 py-1 text-xs font-medium hover:border-black disabled:text-gray-300'
                            >
                                Trước
                            </button>
                            <button
                                type='button'
                                onClick={() => setPage((current) => Math.min(current + 1, Math.max(totalPages - 1, 0)))}
                                disabled={page >= totalPages - 1 || totalPages === 0 || loading}
                                className='border border-gray-300 px-3 py-1 text-xs font-medium hover:border-black disabled:text-gray-300'
                            >
                                Sau
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default NotificationDropdown;
