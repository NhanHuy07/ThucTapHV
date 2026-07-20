import { useContext, useEffect, useMemo, useState } from 'react';
import { Navigate, useParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { ShopContext } from '../context/ShopContext';
import Title from '../components/Title';
import { apiRequest } from '../lib/api';
import type { ApiResponse, OrderDetail, OrderStatus, PaymentQr, Product } from '../types/shop';
import { formatCurrency } from '../lib/format';

const trackingSteps: Array<{ status: OrderStatus; label: string }> = [
    { status: 'UNPAID', label: 'Chờ thanh toán' },
    { status: 'PAID', label: 'Đã thanh toán' },
    { status: 'PENDING', label: 'Chờ xác nhận' },
    { status: 'CONFIRMED', label: 'Đã xác nhận' },
    { status: 'SHIPPING', label: 'Đang giao' },
    { status: 'DELIVERED', label: 'Đã giao' },
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

const getStepState = (stepIndex: number, activeStepIndex: number) => {
    if (stepIndex < activeStepIndex) return 'done';
    if (stepIndex === activeStepIndex) return 'current';
    return 'next';
};

const TrackOrder = () => {
    const { orderCode } = useParams();
    const context = useContext(ShopContext);
    const token = context?.token;
    const navigate = context?.navigate;
    const products = (context?.products || []) as Product[];
    const [order, setOrder] = useState<OrderDetail | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [actionLoading, setActionLoading] = useState(false);

    const getOrder = async (silent = false) => {
        if (!token || !orderCode) return;

        if (!silent) {
            setLoading(true);
            setError('');
        }

        try {
            const data = await apiRequest<ApiResponse<OrderDetail>>(`/v1/api/user/order/${encodeURIComponent(orderCode)}`, {
                method: 'GET',
                token,
            });

            setOrder(data.data);
        } catch (error) {
            if (!silent) {
                const message = error instanceof Error ? error.message : 'Không thể tải chi tiết đơn hàng';
                setError(message);
            }
        } finally {
            if (!silent) {
                setLoading(false);
            }
        }
    };

    useEffect(() => {
        getOrder();
    }, [orderCode, token]);

    // Tự động đồng bộ ngầm chi tiết tiến trình đơn hàng mỗi 10 giây
    useEffect(() => {
        if (!token || !orderCode) return;
        const interval = setInterval(() => {
            getOrder(true);
        }, 10000);
        return () => clearInterval(interval);
    }, [orderCode, token]);

    // Tự động đồng bộ ngầm ngay lập tức khi người dùng quay lại tab/cửa sổ này
    useEffect(() => {
        if (!token || !orderCode) return;
        const handleVisibilityChange = () => {
            if (document.visibilityState === 'visible') {
                getOrder(true);
            }
        };
        document.addEventListener('visibilitychange', handleVisibilityChange);
        return () => document.removeEventListener('visibilitychange', handleVisibilityChange);
    }, [orderCode, token]);

    const activeStepIndex = useMemo(() => {
        if (!order || order.status === 'CANCELLED' || order.status === 'RETURNED') return -1;
        if (order.status === 'COMPLETED') return trackingSteps.length;
        return trackingSteps.findIndex((step) => step.status === order.status);
    }, [order]);

    const receiver = order?.reciever || order?.receiver;
    const receiverName = [receiver?.fName, receiver?.lName].filter(Boolean).join(' ');
    const address = [receiver?.addr?.detail, receiver?.addr?.street, receiver?.addr?.district, receiver?.addr?.province, receiver?.addr?.country]
        .filter(Boolean)
        .join(', ');

    const repayOrder = async () => {
        if (!token || !order) return;

        setActionLoading(true);
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
            setActionLoading(false);
        }
    };

    const cancelOrder = async () => {
        if (!token || !order) return;

        setActionLoading(true);
        try {
            await apiRequest(`/v1/api/user/order/cancel/${encodeURIComponent(order.orderCode)}`, {
                method: 'PUT',
                token,
            });
            toast.success('Đã hủy đơn hàng');
            setOrder({ ...order, status: 'CANCELLED' });
        } catch (error) {
            const message = error instanceof Error ? error.message : 'Không thể hủy đơn hàng';
            toast.error(message);
        } finally {
            setActionLoading(false);
        }
    };

    const completeOrder = async () => {
        if (!token || !order) return;

        setActionLoading(true);
        try {
            await apiRequest(`/v1/api/user/order/complete/${encodeURIComponent(order.orderCode)}`, {
                method: 'PUT',
                token,
            });
            toast.success('Đã xác nhận nhận hàng');
            setOrder({ ...order, status: 'COMPLETED' });
        } catch (error) {
            const message = error instanceof Error ? error.message : 'Không thể xác nhận nhận hàng';
            toast.error(message);
        } finally {
            setActionLoading(false);
        }
    };

    void completeOrder;

    if (!token) {
        return <Navigate to='/login' replace />;
    }

    return (
        <div className='border-t pt-10 sm:pt-14 min-h-[70vh]'>
            <div className='flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4 mb-8'>
                <div>
                    <div className='text-2xl'>
                        <Title text1='CHI TIẾT' text2='ĐƠN HÀNG' />
                    </div>
                    <p className='mt-2 text-sm text-gray-500'>Theo dõi tiến trình, giao hàng và thanh toán của đơn hàng.</p>
                </div>
                <button onClick={() => navigate?.('/orders')} className='w-fit border border-black px-5 py-2 text-sm hover:bg-black hover:text-white transition-all'>
                    Quay lại
                </button>
            </div>

            {loading ? (
                <p className='py-14 text-center text-gray-500'>Đang tải chi tiết đơn hàng...</p>
            ) : error ? (
                <p className='py-14 text-center text-red-500'>{error}</p>
            ) : order ? (
                <div className='grid lg:grid-cols-[1.3fr_0.8fr] gap-8'>
                    <div className='space-y-6'>
                        <section className='border bg-white p-5 sm:p-6 shadow-sm'>
                            <div className='flex flex-col md:flex-row md:items-start md:justify-between gap-4'>
                                <div>
                                    <p className='text-xs uppercase tracking-[0.18em] text-gray-400'>Mã đơn hàng</p>
                                    <p className='mt-1 text-xl font-medium text-gray-900'>{order.orderCode}</p>
                                    <p className='mt-2 text-sm text-gray-500'>Đặt lúc {formatDate(order.createdAt)}</p>
                                </div>
                                <span className={`w-fit border px-3 py-1 text-sm ${statusTone[order.status]}`}>
                                    {statusLabel[order.status]}
                                </span>
                            </div>

                            <div className='mt-6 flex flex-wrap gap-2'>
                                {order.status === 'UNPAID' && (
                                    <>
                                        <button onClick={repayOrder} disabled={actionLoading} className='bg-black px-5 py-2 text-sm text-white disabled:bg-gray-300'>
                                            Thanh toán lại
                                        </button>
                                        <button onClick={cancelOrder} disabled={actionLoading} className='border border-red-500 px-5 py-2 text-sm text-red-600 hover:bg-red-500 hover:text-white disabled:border-gray-200 disabled:text-gray-400'>
                                            Hủy đơn
                                        </button>
                                    </>
                                )}
                                {order.status === 'PENDING' && (
                                    <button onClick={cancelOrder} disabled={actionLoading} className='border border-red-500 px-5 py-2 text-sm text-red-600 hover:bg-red-500 hover:text-white disabled:border-gray-200 disabled:text-gray-400'>
                                        Hủy đơn
                                    </button>
                                )}
                                {/* Tạm thời ẩn nút Xác nhận đã nhận hàng do lỗi API 409 của BE */}
                                {/* {order.status === 'DELIVERED' && (
                                    <button onClick={completeOrder} disabled={actionLoading} className='bg-green-600 px-5 py-2 text-sm text-white hover:bg-green-700 disabled:bg-gray-300'>
                                        Xác nhận đã nhận hàng
                                    </button>
                                )} */}
                            </div>

                            <div className='mt-8'>
                                {order.status === 'CANCELLED' || order.status === 'RETURNED' ? (
                                    <div className={`border p-4 text-sm ${order.status === 'CANCELLED' ? 'border-red-100 bg-red-50 text-red-600' : 'border-yellow-100 bg-yellow-50 text-yellow-700'}`}>
                                        Đơn hàng này {statusLabel[order.status].toLowerCase()}.
                                    </div>
                                ) : (
                                    <div className='grid grid-cols-1 sm:grid-cols-7 gap-4'>
                                        {trackingSteps.map((step, index) => {
                                            const stepState = getStepState(index, activeStepIndex);
                                            const isDone = stepState === 'done';
                                            const isCurrent = stepState === 'current';

                                            return (
                                                <div key={step.status} className='flex sm:flex-col items-center sm:items-start gap-3'>
                                                    <div className={`w-8 h-8 rounded-full flex items-center justify-center text-xs border transition-all ${isCurrent
                                                        ? 'bg-green-500 text-white border-green-500 ring-4 ring-green-100'
                                                        : isDone
                                                            ? 'bg-black text-white border-black'
                                                            : 'bg-white text-gray-400 border-gray-300'
                                                        }`}>
                                                        {index + 1}
                                                    </div>
                                                    <div>
                                                        <p className={`text-sm font-medium ${isCurrent ? 'text-green-600' : isDone ? 'text-gray-900' : 'text-gray-400'}`}>{step.label}</p>
                                                        <p className={`text-xs mt-1 ${isCurrent ? 'text-green-600' : 'text-gray-400'}`}>
                                                            {isCurrent ? 'Giai đoạn hiện tại' : isDone ? 'Đã hoàn thành' : 'Đang chờ'}
                                                        </p>
                                                    </div>
                                                </div>
                                            );
                                        })}
                                    </div>
                                )}
                            </div>
                        </section>

                        <section className='border bg-white p-5 sm:p-6 shadow-sm'>
                            <p className='text-lg font-medium text-gray-900 mb-4'>Sản phẩm</p>
                            <div className='divide-y'>
                                {(order.items || []).map((item) => {
                                    const product = products.find((product) => product._id === item.productCode);
                                    const itemPrice = item.finalPrice ?? item.originalPrice ?? 0;

                                    return (
                                        <div key={`${item.productCode}-${item.size}`} className='py-4 flex items-start justify-between gap-4'>
                                            <div className='flex gap-4'>
                                                {product?.image[0] && <img className='w-16 h-16 object-cover border' src={product.image[0]} alt={product.name} />}
                                                <div>
                                                    <p className='font-medium text-gray-900'>{product?.name || item.productCode}</p>
                                                    <p className='text-sm text-gray-500 mt-1'>Kích cỡ {item.size} | Số lượng {item.quantity}</p>
                                                </div>
                                            </div>
                                            <p className='text-sm font-medium text-gray-900'>{formatCurrency(itemPrice * item.quantity)}</p>
                                        </div>
                                    );
                                })}
                            </div>
                        </section>

                        <section className='border bg-white p-5 sm:p-6 shadow-sm'>
                            <p className='text-lg font-medium text-gray-900'>Lịch sử trạng thái</p>
                            <div className='mt-4 space-y-3 text-sm text-gray-600'>
                                <div className='flex justify-between border-b border-gray-100 pb-3'>
                                    <span>Tạo đơn hàng</span>
                                    <span>{formatDate(order.createdAt)}</span>
                                </div>
                                <div className='flex justify-between'>
                                    <span>Cập nhật gần nhất</span>
                                    <span>{formatDate(order.updatedAt)}</span>
                                </div>
                            </div>
                        </section>
                    </div>

                    <div className='space-y-6'>
                        <section className='border bg-white p-5 sm:p-6 shadow-sm'>
                            <p className='text-lg font-medium text-gray-900'>Thông tin người nhận</p>
                            <div className='mt-4 space-y-3 text-sm text-gray-600'>
                                <p><span className='text-gray-400'>Người nhận:</span> {receiverName || 'Chưa có'}</p>
                                <p><span className='text-gray-400'>Số điện thoại:</span> {receiver?.phone || 'Chưa có'}</p>
                                <p><span className='text-gray-400'>Địa chỉ:</span> {address || 'Chưa có'}</p>
                            </div>
                        </section>

                        <section className='border bg-white p-5 sm:p-6 shadow-sm'>
                            <p className='text-lg font-medium text-gray-900'>Tổng kết thanh toán</p>
                            <div className='mt-4 space-y-3 text-sm'>
                                <div className='flex justify-between'><span className='text-gray-500'>Phương thức</span><span>{paymentLabel[order.paymentType || ''] || 'Không có'}</span></div>
                                <div className='flex justify-between'><span className='text-gray-500'>Tạm tính</span><span>{formatCurrency(order.totalAmount)}</span></div>
                                <div className='flex justify-between'><span className='text-gray-500'>Voucher</span><span>{order.voucherCode || 'Không áp dụng'}</span></div>
                                <div className='flex justify-between'><span className='text-gray-500'>Giảm giá</span><span>{formatCurrency(order.voucherDiscount)}</span></div>
                                <div className='border-t pt-3 flex justify-between font-medium text-base'><span>Tổng thanh toán</span><span>{formatCurrency(order.finalPrice)}</span></div>
                            </div>
                        </section>

                        <section className='border bg-white p-5 sm:p-6 shadow-sm'>
                            <p className='text-lg font-medium text-gray-900'>Ghi chú đơn hàng</p>
                            <p className='mt-4 text-sm text-gray-600 leading-6'>{order.note || 'Không có ghi chú.'}</p>
                        </section>
                    </div>
                </div>
            ) : null}
        </div>
    );
};

export default TrackOrder;
