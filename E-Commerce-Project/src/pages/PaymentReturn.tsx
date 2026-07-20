import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { apiRequest } from '../lib/api';
import type { ApiResponse } from '../types/shop';

type PaymentReturnData = {
    orderCode?: string;
    paid?: boolean;
};

const PaymentReturn = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [success, setSuccess] = useState<boolean | null>(null);
    const [orderCode, setOrderCode] = useState('');
    const [message, setMessage] = useState('Dang xac nhan ket qua thanh toan...');
    const [countdown, setCountdown] = useState(5);

    const queryString = useMemo(() => searchParams.toString(), [searchParams]);

    useEffect(() => {
        let mounted = true;

        const confirmPayment = async () => {
            if (!queryString) {
                setSuccess(false);
                setMessage('Thieu thong tin thanh toan tra ve.');
                setLoading(false);
                return;
            }

            try {
                const response = await apiRequest<ApiResponse<PaymentReturnData>>(`/v1/api/public/payment/vnpay/return?${queryString}`, {
                    method: 'GET',
                });

                if (!mounted) return;

                const paid = Boolean(response.data?.paid);
                setSuccess(paid);
                setOrderCode(response.data?.orderCode || searchParams.get('vnp_TxnRef') || '');
                setMessage(paid ? 'Thanh toan thanh cong.' : response.message || 'Thanh toan chua thanh cong.');
            } catch (error) {
                console.error(error);
                if (!mounted) return;

                setSuccess(false);
                setOrderCode(searchParams.get('vnp_TxnRef') || '');
                setMessage(error instanceof Error ? error.message : 'Khong the xac nhan ket qua thanh toan.');
            } finally {
                if (mounted) {
                    setLoading(false);
                }
            }
        };

        confirmPayment();

        return () => {
            mounted = false;
        };
    }, [queryString, searchParams]);

    useEffect(() => {
        if (loading) return;

        const timer = window.setInterval(() => {
            setCountdown((current) => Math.max(current - 1, 0));
        }, 1000);

        return () => window.clearInterval(timer);
    }, [loading]);

    useEffect(() => {
        if (loading || countdown > 0) return;

        if (orderCode) {
            navigate(`/orders/${orderCode}`, { replace: true });
            return;
        }

        navigate('/', { replace: true });
    }, [countdown, loading, navigate, orderCode]);

    return (
        <div className='border-t pt-16 min-h-[70vh]'>
            <div className='mx-auto max-w-xl border bg-white p-6 text-center shadow-sm'>
                <div className={`mx-auto flex h-12 w-12 items-center justify-center rounded-full text-xl font-semibold ${
                    loading ? 'bg-gray-100 text-gray-500' : success ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-600'
                }`}>
                    {loading ? '...' : success ? '✓' : '!'}
                </div>
                <h1 className='mt-5 text-xl font-medium text-gray-900'>
                    {loading ? 'Dang xac nhan thanh toan' : success ? 'Thanh toan thanh cong' : 'Thanh toan that bai'}
                </h1>
                <p className='mt-3 text-sm text-gray-500'>{message}</p>
                {orderCode && <p className='mt-2 text-sm font-medium text-gray-900'>Ma don: {orderCode}</p>}
                {!loading && (
                    <p className='mt-5 text-xs uppercase tracking-[0.16em] text-gray-400'>
                        Tu dong chuyen trang sau {countdown}s
                    </p>
                )}
                <button
                    type='button'
                    onClick={() => navigate(orderCode ? `/orders/${orderCode}` : '/', { replace: true })}
                    className='mt-6 bg-black px-6 py-3 text-sm font-medium text-white'
                >
                    {orderCode ? 'Xem don hang' : 'Ve trang chu'}
                </button>
            </div>
        </div>
    );
};

export default PaymentReturn;
