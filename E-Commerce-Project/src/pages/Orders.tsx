import { useContext, useEffect, useMemo, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { ShopContext } from '../context/ShopContext';
import Title from '../components/Title';
import { apiRequest } from '../lib/api';
import { assets } from '../assets/assets';
import type { ApiResponse, OrderDetail, OrderItem, OrderStatus, OrderSummary, PageResponse, PaymentQr, Product } from '../types/shop';
import { formatCurrency } from '../lib/format';
import { createProductComment, deleteMyProductComment, getMyProductComments } from '../lib/commentApi';

type OrderGroup = 'ALL' | 'UNPAID' | 'PROCESSING' | 'SHIPPING' | 'DELIVERED' | 'CANCELLED' | 'RETURNED';

type OrderListItem = OrderSummary & {
    paymentType?: 'PAYMENT_UPON_DELIVER' | 'ONLINE';
    paymentUrl?: string;
    bankTransferQr?: PaymentQr;
    previewProductCode?: string;
    items?: OrderItem[];
};

type RatingTarget = {
    orderCode: string;
    productCode: string;
};

const orderGroups: Array<{ key: OrderGroup; label: string; statuses: OrderStatus[] | null }> = [
    { key: 'ALL', label: 'Tất cả', statuses: null },
    { key: 'UNPAID', label: 'Chờ thanh toán', statuses: ['UNPAID'] },
    { key: 'PROCESSING', label: 'Đang xử lý', statuses: ['PAID', 'PENDING', 'CONFIRMED'] },
    { key: 'SHIPPING', label: 'Đang giao', statuses: ['SHIPPING'] },
    { key: 'DELIVERED', label: 'Đã giao', statuses: ['DELIVERED', 'COMPLETED'] },
    { key: 'CANCELLED', label: 'Đã hủy', statuses: ['CANCELLED'] },
    { key: 'RETURNED', label: 'Hoàn trả', statuses: ['RETURNED'] },
];

const statusLabel: Record<OrderStatus, string> = {
    UNPAID: 'Chờ thanh toán',
    PAID: 'Đã thanh toán',
    PENDING: 'Chờ xác nhận',
    CANCELLED: 'Đã hủy',
    CONFIRMED: 'Đã xác nhận',
    SHIPPING: 'Đang giao hàng',
    DELIVERED: 'Đã giao hàng',
    COMPLETED: 'Hoàn tất',
    RETURNED: 'Đã hoàn trả',
};

const paymentLabel: Record<string, string> = {
    ONLINE: 'Thanh toán online',
    PAYMENT_UPON_DELIVER: 'Thanh toán khi nhận hàng',
};

const statusTone: Record<OrderStatus, string> = {
    UNPAID: 'bg-amber-50 text-amber-700 border-amber-100',
    PAID: 'bg-blue-50 text-blue-700 border-blue-100',
    PENDING: 'bg-sky-50 text-sky-700 border-sky-100',
    CONFIRMED: 'bg-indigo-50 text-indigo-700 border-indigo-100',
    SHIPPING: 'bg-cyan-50 text-cyan-700 border-cyan-100',
    DELIVERED: 'bg-green-50 text-green-700 border-green-100',
    COMPLETED: 'bg-emerald-50 text-emerald-700 border-emerald-100',
    CANCELLED: 'bg-red-50 text-red-600 border-red-100',
    RETURNED: 'bg-yellow-50 text-yellow-700 border-yellow-100',
};

const formatDate = (date?: string) =>
    date ? new Intl.DateTimeFormat('vi-VN', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(date)) : 'Không có';

const Orders = () => {
    const context = useContext(ShopContext);
    const token = context?.token;
    const navigate = context?.navigate;
    const refreshUserProfile = context?.refreshUserProfile as (() => Promise<void>) | undefined;
    const products = (context?.products || []) as Product[];
    const [orders, setOrders] = useState<OrderListItem[]>([]);
    const [activeGroup, setActiveGroup] = useState<OrderGroup>('ALL');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [actionOrderCode, setActionOrderCode] = useState('');
    const [ratingTarget, setRatingTarget] = useState<RatingTarget | null>(null);
    const [ratingValue, setRatingValue] = useState(5);
    const [ratingContent, setRatingContent] = useState('');
    const [submittingRating, setSubmittingRating] = useState(false);
    const [deletingRating, setDeletingRating] = useState(false);
    const [reviewedProductCodes, setReviewedProductCodes] = useState<Set<string>>(new Set());

    const refreshOrders = async (silent = false) => {
        if (!token) return;

        if (!silent) {
            setLoading(true);
            setError('');
        }

        try {
            const data = await apiRequest<ApiResponse<PageResponse<OrderSummary>>>('/v1/api/user/order/my-orders?page=0&size=50&sort=createdAt,desc', {
                method: 'GET',
                token,
            });

            const orderSummaries = data.data?.items || [];
            const ordersWithDetails = await Promise.all(
                orderSummaries.map(async (order) => {
                    try {
                        const detail = await apiRequest<ApiResponse<OrderDetail>>(`/v1/api/user/order/${encodeURIComponent(order.orderCode)}`, {
                            method: 'GET',
                            token,
                        });

                        return {
                            ...order,
                            paymentType: detail.data?.paymentType,
                            paymentUrl: detail.data?.paymentUrl,
                            bankTransferQr: detail.data?.bankTransferQr,
                            previewProductCode: detail.data?.items?.[0]?.productCode,
                            items: detail.data?.items || [],
                        };
                    } catch (error) {
                        console.error(error);
                        return order;
                    }
                })
            );

            setOrders(ordersWithDetails);
        } catch (error) {
            if (!silent) {
                const message = error instanceof Error ? error.message : 'Không thể tải danh sách đơn hàng';
                setError(message);
            }
        } finally {
            if (!silent) {
                setLoading(false);
            }
        }
    };

    useEffect(() => {
        refreshOrders();
    }, [token]);

    const refreshMyComments = async () => {
        if (!token) return;

        try {
            const response = await getMyProductComments(token, 0, 100);
            const productCodes = (response.data?.items || []).map((comment) => comment.productCode);
            setReviewedProductCodes(new Set(productCodes));
        } catch (error) {
            console.error(error);
            setReviewedProductCodes(new Set());
        }
    };

    useEffect(() => {
        refreshMyComments();
    }, [token]);

    // Tự động đồng bộ ngầm trạng thái đơn hàng mỗi 10 giây
    useEffect(() => {
        if (!token) return;
        const interval = setInterval(() => {
            refreshOrders(true);
        }, 10000);
        return () => clearInterval(interval);
    }, [token]);

    const openRatingModal = (order: OrderListItem) => {
        const productCodes = getOrderProductCodes(order);
        const productCode = productCodes.find((code) => !reviewedProductCodes.has(code)) || productCodes[0];
        if (!productCode) {
            toast.error('Không tìm thấy sản phẩm để đánh giá');
            return;
        }

        setRatingTarget({ orderCode: order.orderCode, productCode });
        setRatingValue(5);
        setRatingContent('');
    };

    const closeRatingModal = () => {
        setRatingTarget(null);
        setRatingValue(5);
        setRatingContent('');
    };

    const submitRating = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!token || !ratingTarget) return;
        if (!ratingContent.trim()) {
            toast.error('Vui lòng nhập nội dung đánh giá');
            return;
        }
        if (reviewedProductCodes.has(ratingTarget.productCode)) {
            toast.error('Sản phẩm này đã được đánh giá');
            return;
        }

        setSubmittingRating(true);
        try {
            await createProductComment(token, {
                productCode: ratingTarget.productCode,
                content: ratingContent.trim(),
                rating: ratingValue,
            });

            toast.success('Đã gửi đánh giá sản phẩm');
            setReviewedProductCodes((current) => new Set(current).add(ratingTarget.productCode));
            closeRatingModal();
        } catch (error) {
            const message = error instanceof Error ? error.message : 'Không thể gửi đánh giá';
            toast.error(message);
        } finally {
            setSubmittingRating(false);
        }
    };

    const deleteRating = async () => {
        if (!token || !ratingTarget) return;

        const confirmed = window.confirm('Xóa đánh giá của bạn cho sản phẩm này?');
        if (!confirmed) return;

        setDeletingRating(true);
        try {
            await deleteMyProductComment(token, ratingTarget.productCode);
            toast.success('Đã xóa đánh giá sản phẩm');
            setReviewedProductCodes((current) => {
                const next = new Set(current);
                next.delete(ratingTarget.productCode);
                return next;
            });
            closeRatingModal();
        } catch (error) {
            const message = error instanceof Error ? error.message : 'Không thể xóa đánh giá';
            toast.error(message);
        } finally {
            setDeletingRating(false);
        }
    };

    // Tự động đồng bộ ngầm ngay lập tức khi người dùng quay lại tab/cửa sổ này
    useEffect(() => {
        if (!token) return;
        const handleVisibilityChange = () => {
            if (document.visibilityState === 'visible') {
                refreshOrders(true);
            }
        };
        document.addEventListener('visibilitychange', handleVisibilityChange);
        return () => document.removeEventListener('visibilitychange', handleVisibilityChange);
    }, [token]);

    const filteredOrders = useMemo(() => {
        const group = orderGroups.find((item) => item.key === activeGroup);
        if (!group?.statuses) return orders;
        return orders.filter((order) => group.statuses?.includes(order.status));
    }, [activeGroup, orders]);

    const groupCount = (group: OrderGroup) => {
        const groupConfig = orderGroups.find((item) => item.key === group);
        if (!groupConfig?.statuses) return orders.length;
        return orders.filter((order) => groupConfig.statuses?.includes(order.status)).length;
    };

    const getOrderProductCodes = (order: OrderListItem) => {
        const codes = order.items?.map((item) => item.productCode).filter(Boolean) || [];
        if (order.previewProductCode) {
            codes.push(order.previewProductCode);
        }
        return Array.from(new Set(codes));
    };

    const getReviewStats = (order: OrderListItem) => {
        const productCodes = getOrderProductCodes(order);
        const reviewedCount = productCodes.filter((code) => reviewedProductCodes.has(code)).length;
        return {
            productCodes,
            reviewedCount,
            total: productCodes.length,
            allReviewed: productCodes.length > 0 && reviewedCount === productCodes.length,
            partiallyReviewed: reviewedCount > 0 && reviewedCount < productCodes.length,
        };
    };

    const cancelOrder = async (orderCode: string) => {
        if (!token) return;

        setActionOrderCode(orderCode);
        try {
            await apiRequest(`/v1/api/user/order/cancel/${encodeURIComponent(orderCode)}`, {
                method: 'PUT',
                token,
            });

            setOrders((currentOrders) =>
                currentOrders.map((order) =>
                    order.orderCode === orderCode ? { ...order, status: 'CANCELLED' } : order
                )
            );
            await refreshUserProfile?.();
            toast.success('Đã hủy đơn hàng');
        } catch (error) {
            const message = error instanceof Error ? error.message : 'Không thể hủy đơn hàng';
            toast.error(message);
        } finally {
            setActionOrderCode('');
        }
    };

    const repayOrder = async (order: OrderListItem) => {
        if (!token) return;

        setActionOrderCode(order.orderCode);
        try {
            if (order.paymentUrl) {
                window.location.href = order.paymentUrl;
                return;
            }

            const response = await apiRequest<ApiResponse<{ paymentUrl?: string; bankTransferQr?: PaymentQr }>>(`/v1/api/user/payment/vnpay/${encodeURIComponent(order.orderCode)}`, {
                method: 'POST',
                token,
            });

            if (response.data?.paymentUrl) {
                window.location.href = response.data.paymentUrl;
                return;
            }

            toast.info('Backend chưa trả link thanh toán cho đơn hàng này');
        } catch (error) {
            const message = error instanceof Error ? error.message : 'Không thể tạo lại link thanh toán';
            toast.error(message);
        } finally {
            setActionOrderCode('');
        }
    };

    if (!token) {
        return <Navigate to='/login' replace />;
    }

    const ratingOrder = ratingTarget ? orders.find((order) => order.orderCode === ratingTarget.orderCode) : undefined;
    const ratingItems = ratingOrder?.items?.length ? ratingOrder.items : ratingTarget ? [{ productCode: ratingTarget.productCode }] : [];
    const ratingProduct = products.find((product) => product._id === ratingTarget?.productCode);
    const selectedProductReviewed = ratingTarget ? reviewedProductCodes.has(ratingTarget.productCode) : false;

    return (
        <div className='border-t pt-10 sm:pt-14 min-h-[70vh]'>
            <div className='flex flex-col md:flex-row md:items-end md:justify-between gap-5 mb-6'>
                <div>
                    <div className='text-2xl'>
                        <Title text1='ĐƠN HÀNG' text2='CỦA TÔI' />
                    </div>
                    <p className='mt-2 text-sm text-gray-500'>Lọc, theo dõi trạng thái và xử lý nhanh các đơn hàng của bạn.</p>
                </div>
                <button onClick={() => refreshOrders()} disabled={loading} className='w-fit border border-black px-5 py-2.5 text-sm hover:bg-black hover:text-white transition-all disabled:border-gray-200 disabled:text-gray-400'>
                    {loading ? 'Đang tải...' : 'Tải lại'}
                </button>
            </div>

            <div className='overflow-x-auto border-b border-gray-200'>
                <div className='flex min-w-max gap-2 pb-3'>
                    {orderGroups.map((group) => (
                        <button
                            key={group.key}
                            onClick={() => setActiveGroup(group.key)}
                            className={`px-4 py-2.5 text-sm border transition-all ${
                                activeGroup === group.key ? 'bg-black text-white border-black' : 'bg-white text-gray-600 border-gray-200 hover:border-black'
                            }`}
                        >
                            {group.label}
                            <span className={`ml-2 text-xs ${activeGroup === group.key ? 'text-white/80' : 'text-gray-400'}`}>{groupCount(group.key)}</span>
                        </button>
                    ))}
                </div>
            </div>

            {loading ? (
                <p className='py-14 text-center text-gray-500'>Đang tải đơn hàng...</p>
            ) : error ? (
                <p className='py-14 text-center text-red-500'>{error}</p>
            ) : orders.length === 0 ? (
                <div className='my-10 border bg-white px-6 py-16 text-center text-gray-500'>
                    <p className='text-lg font-medium text-gray-900'>Bạn chưa có đơn hàng nào</p>
                    <p className='mt-2 text-sm'>Khi đặt hàng thành công, đơn hàng sẽ xuất hiện tại đây.</p>
                    <button onClick={() => navigate?.('/collection')} className='mt-6 bg-black text-white px-8 py-3 text-sm'>TIẾP TỤC MUA SẮM</button>
                </div>
            ) : filteredOrders.length === 0 ? (
                <div className='my-10 border bg-white px-6 py-14 text-center text-gray-500'>
                    <p>Không có đơn hàng trong nhóm này.</p>
                </div>
            ) : (
                <div className='mt-6 space-y-4'>
                    {filteredOrders.map((order) => {
                        const previewProduct = products.find((product) => product._id === order.previewProductCode);
                        const busy = actionOrderCode === order.orderCode;
                        const reviewStats = getReviewStats(order);

                        return (
                            <article key={order.orderCode} className='border bg-white p-5 shadow-sm'>
                                <div className='flex flex-col lg:flex-row lg:items-start lg:justify-between gap-5'>
                                    <div className='flex items-start gap-4 min-w-0'>
                                        <img
                                            className='w-20 h-20 object-cover border bg-gray-50'
                                            src={previewProduct?.image[0] || assets.p_img1}
                                            alt={previewProduct?.name || order.orderCode}
                                        />
                                        <div className='min-w-0'>
                                            <div className='flex flex-wrap items-center gap-2'>
                                                <p className='font-medium text-gray-900'>{order.orderCode}</p>
                                                <span className={`border px-2.5 py-1 text-xs ${statusTone[order.status]}`}>
                                                    {statusLabel[order.status]}
                                                </span>
                                                {order.status === 'DELIVERED' && reviewStats.allReviewed && (
                                                    <span className='border border-emerald-100 bg-emerald-50 px-2.5 py-1 text-xs text-emerald-700'>
                                                        Đã đánh giá
                                                    </span>
                                                )}
                                                {order.status === 'DELIVERED' && reviewStats.partiallyReviewed && (
                                                    <span className='border border-sky-100 bg-sky-50 px-2.5 py-1 text-xs text-sky-700'>
                                                        Đã đánh giá {reviewStats.reviewedCount}/{reviewStats.total}
                                                    </span>
                                                )}
                                            </div>
                                            <p className='mt-1 text-sm text-gray-500'>{previewProduct?.name || 'Sản phẩm trong đơn'}</p>
                                            <div className='mt-3 grid sm:grid-cols-2 lg:grid-cols-4 gap-x-6 gap-y-2 text-sm'>
                                                <p><span className='text-gray-400'>Ngày đặt:</span> {formatDate(order.createdAt)}</p>
                                                <p><span className='text-gray-400'>Số lượng:</span> {order.totalItems} sản phẩm</p>
                                                <p><span className='text-gray-400'>Thanh toán:</span> {paymentLabel[order.paymentType || ''] || 'Chưa có'}</p>
                                                <p><span className='text-gray-400'>Tổng:</span> <span className='font-medium text-gray-900'>{formatCurrency(order.finalPrice)}</span></p>
                                            </div>
                                        </div>
                                    </div>

                                    <div className='flex flex-wrap lg:justify-end gap-2'>
                                        {order.status === 'UNPAID' && (
                                            <>
                                                <button onClick={() => repayOrder(order)} disabled={busy} className='bg-black px-5 py-2 text-sm text-white disabled:bg-gray-300'>
                                                    Thanh toán lại
                                                </button>
                                                <button onClick={() => cancelOrder(order.orderCode)} disabled={busy} className='border border-red-500 px-5 py-2 text-sm text-red-600 hover:bg-red-500 hover:text-white disabled:border-gray-200 disabled:text-gray-400 disabled:hover:bg-white'>
                                                    Hủy đơn
                                                </button>
                                            </>
                                        )}
                                        {order.status === 'PENDING' && (
                                            <button onClick={() => cancelOrder(order.orderCode)} disabled={busy} className='border border-red-500 px-5 py-2 text-sm text-red-600 hover:bg-red-500 hover:text-white disabled:border-gray-200 disabled:text-gray-400 disabled:hover:bg-white'>
                                                Hủy đơn
                                            </button>
                                        )}
                                        {/* Tạm thời ẩn nút Xác nhận đã nhận hàng do lỗi API 409 của BE */}
                                        {/* {order.status === 'DELIVERED' && (
                                            <button onClick={() => completeOrder(order.orderCode)} disabled={busy} className='bg-green-600 px-5 py-2 text-sm text-white hover:bg-green-700 disabled:bg-gray-300'>
                                                Xác nhận đã nhận hàng
                                            </button>
                                        )} */}
                                        {order.status === 'DELIVERED' && reviewStats.total > 0 && !reviewStats.allReviewed && (
                                            <button onClick={() => openRatingModal(order)} disabled={busy} className='border border-black px-5 py-2 text-sm hover:bg-black hover:text-white disabled:border-gray-200 disabled:text-gray-400 disabled:hover:bg-white'>
                                                {reviewStats.partiallyReviewed ? 'Đánh giá còn lại' : 'Đánh giá'}
                                            </button>
                                        )}
                                        <button onClick={() => navigate?.(`/orders/${order.orderCode}`)} className='border border-black px-5 py-2 text-sm hover:bg-black hover:text-white transition-all'>
                                            Xem chi tiết
                                        </button>
                                    </div>
                                </div>
                            </article>
                        );
                    })}
                </div>
            )}

            {ratingTarget && (
                <div className='fixed inset-0 z-[90] flex items-start justify-center overflow-y-auto bg-black/40 px-4 py-8'>
                    <form onSubmit={submitRating} className='w-full max-w-lg bg-white p-5 shadow-xl sm:p-6'>
                        <div className='flex items-start justify-between gap-4 border-b border-gray-100 pb-4'>
                            <div>
                                <p className='text-lg font-medium text-gray-900'>Đánh giá sản phẩm</p>
                                <p className='mt-1 text-sm text-gray-500'>{ratingProduct?.name || ratingTarget.productCode}</p>
                            </div>
                            <button
                                type='button'
                                onClick={closeRatingModal}
                                className='border border-gray-300 px-3 py-2 text-xs font-medium hover:border-black'
                            >
                                Đóng
                            </button>
                        </div>

                        <div className='mt-5 space-y-4'>
                            <div>
                                <label className='mb-2 block text-xs uppercase tracking-[0.16em] text-gray-400'>Sản phẩm</label>
                                <select
                                    value={ratingTarget.productCode}
                                    onChange={(event) => setRatingTarget((current) => current ? { ...current, productCode: event.target.value } : current)}
                                    className='w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                >
                                    {ratingItems.map((item) => {
                                        const product = products.find((candidate) => candidate._id === item.productCode);
                                        return (
                                            <option key={item.productCode} value={item.productCode}>
                                                {product?.name || item.productCode}{reviewedProductCodes.has(item.productCode) ? ' - đã đánh giá' : ''}
                                            </option>
                                        );
                                    })}
                                </select>
                            </div>

                            <div>
                                <label className='mb-2 block text-xs uppercase tracking-[0.16em] text-gray-400'>Rating</label>
                                <div className='flex gap-2'>
                                    {[1, 2, 3, 4, 5].map((value) => (
                                        <button
                                            key={value}
                                            type='button'
                                            onClick={() => setRatingValue(value)}
                                            className={`h-10 w-10 border text-sm font-medium ${ratingValue === value ? 'border-black bg-black text-white' : 'border-gray-300 text-gray-600 hover:border-black'}`}
                                        >
                                            {value}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            <div>
                                <label className='mb-2 block text-xs uppercase tracking-[0.16em] text-gray-400'>Nội dung</label>
                                <textarea
                                    value={ratingContent}
                                    onChange={(event) => setRatingContent(event.target.value)}
                                    className='min-h-32 w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    placeholder='Nhập đánh giá của bạn về sản phẩm'
                                />
                            </div>
                        </div>

                        <div className='mt-6 flex justify-end gap-3'>
                            <button
                                type='button'
                                onClick={deleteRating}
                                disabled={deletingRating || submittingRating || !selectedProductReviewed}
                                className='border border-red-500 px-6 py-3 text-sm font-medium text-red-600 hover:bg-red-500 hover:text-white disabled:border-gray-300 disabled:text-gray-300'
                            >
                                {deletingRating ? 'Đang xóa...' : 'Xóa đánh giá'}
                            </button>
                            <button
                                type='button'
                                onClick={closeRatingModal}
                                className='border border-black px-6 py-3 text-sm font-medium hover:bg-black hover:text-white'
                            >
                                Hủy
                            </button>
                            <button
                                type='submit'
                                disabled={submittingRating || selectedProductReviewed}
                                className='bg-black px-6 py-3 text-sm font-medium text-white disabled:bg-gray-300'
                            >
                                {selectedProductReviewed ? 'Đã đánh giá' : submittingRating ? 'Đang gửi...' : 'Gửi đánh giá'}
                            </button>
                        </div>
                    </form>
                </div>
            )}
        </div>
    );
};

export default Orders;
