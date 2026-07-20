import { useState, type FormEvent, useContext } from 'react'
import { ShopContext } from '../context/ShopContext';
import { apiRequest } from '../lib/api';
import { toast } from 'react-toastify';

type AuthView = 'Đăng nhập' | 'Đăng ký' | 'Quên mật khẩu' | 'Xác thực OTP';

const Login = () => {

    const [currentState, setCurrentState] = useState<AuthView>('Đăng nhập');
    const context = useContext(ShopContext);
    const navigate = context?.navigate;
    const setToken = context?.setToken;
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [otp, setOtp] = useState('');
    const [pendingVerificationEmail, setPendingVerificationEmail] = useState('');
    const [message, setMessage] = useState<{ type: 'error' | 'success'; text: string } | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const resetOtpFlow = () => {
        setOtp('');
        setPendingVerificationEmail('');
    };

    const getDeviceId = () => {
        const storageKey = 'deviceId';
        const currentDeviceId = localStorage.getItem(storageKey);

        if (currentDeviceId) return currentDeviceId;

        const newDeviceId = crypto.randomUUID();
        localStorage.setItem(storageKey, newDeviceId);
        return newDeviceId;
    };

    const onSubmitHandler = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setMessage(null);
        setIsSubmitting(true);

        try {
            if (currentState === 'Quên mật khẩu') {
                const data = await apiRequest<{ success?: boolean; message?: string }>(`/v1/api/public/auth/forgot-password?email=${encodeURIComponent(email)}`, {
                    method: 'POST',
                });
                const successMessage = data.message || 'Mã xác thực đã được gửi về email của bạn.';

                setMessage({ type: 'success', text: successMessage });
                toast.success(successMessage);
                setCurrentState('Đăng nhập');
                return;
            }

            if (currentState === 'Xác thực OTP') {
                const verifyEmail = pendingVerificationEmail || email;

                if (!verifyEmail) {
                    throw new Error('Vui lòng nhập email đã đăng ký');
                }

                const data = await apiRequest<{ success?: boolean; message?: string }>('/v1/api/public/auth/regis/verify', {
                    method: 'POST',
                    body: { email: verifyEmail, otp },
                });

                const successMessage = data.message || 'Xác thực OTP thành công. Bạn có thể đăng nhập.';

                setMessage({ type: 'success', text: successMessage });
                toast.success(successMessage);
                setCurrentState('Đăng nhập');
                resetOtpFlow();
                return;
            }

            const endpoint = currentState === 'Đăng nhập' ? '/v1/api/public/auth/login' : '/v1/api/public/auth/regis';
            const payload = currentState === 'Đăng nhập' ? { username, password } : { username, email, password };
            const data = await apiRequest<{ success?: boolean; data?: { token?: string }; message?: string }>(endpoint, {
                method: 'POST',
                body: payload,
                headers: currentState === 'Đăng nhập' ? { 'X-Device-Id': getDeviceId() } : undefined,
            });

            if (data.success === false) {
                throw new Error(data.message || 'Authentication failed');
            }

            if (currentState === 'Đăng nhập') {
                if (!data.data?.token) {
                    throw new Error(data.message || 'Authentication failed');
                }

                setToken?.(data.data.token);
                navigate?.('/');
            } else {
                const successMessage = data.message || 'Đăng ký thành công. Vui lòng kiểm tra email để lấy OTP xác thực.';
                setPendingVerificationEmail(email);
                setMessage({ type: 'success', text: successMessage });
                toast.success(successMessage);
                setCurrentState('Xác thực OTP');
                setOtp('');
            }
        } catch (error) {
            const message = error instanceof Error ? error.message : 'Không thể đăng nhập';
            setMessage({ type: 'error', text: message });
            toast.error(message);
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <div className='border-t min-h-[75vh] flex items-center justify-center py-12'>
            <form onSubmit={onSubmitHandler} className='w-full max-w-[430px] bg-white border border-gray-200 px-6 sm:px-8 py-8 shadow-[0_18px_60px_rgba(0,0,0,0.06)] text-gray-800'>
                <div className='text-center'>
                    <p className='text-xs uppercase tracking-[0.24em] text-gray-400'>Tài khoản P-Shop</p>
                    <h1 className='mt-3 text-3xl font-medium'>
                        {currentState === 'Đăng nhập' ? 'Chào mừng trở lại' : currentState === 'Đăng ký' ? 'Tạo tài khoản' : 'Tạo tài khoản mới'}
                    </h1>
                    <p className='mt-2 text-sm text-gray-500'>
                        {currentState === 'Đăng nhập'
                            ? 'Đăng nhập để quản lý đơn hàng và hồ sơ mua sắm.'
                            : currentState === 'Đăng ký'
                                ? 'Tạo tài khoản để lưu giỏ hàng và theo dõi đơn hàng.'
                                : 'Nhập email tài khoản, hệ thống sẽ gửi mã xác thực cho bạn.'}
                    </p>
                </div>

                {currentState !== 'Quên mật khẩu' && currentState !== 'Xác thực OTP' && <div className='mt-7 grid grid-cols-2 border border-gray-200 p-1 text-sm'>
                    {['Đăng nhập', 'Đăng ký'].map((item) => (
                        <button
                            type='button'
                            key={item}
                            onClick={() => {
                                setCurrentState(item as AuthView);
                                setMessage(null);
                            }}
                            className={`py-2 transition-all ${currentState === item ? 'bg-black text-white' : 'text-gray-500 hover:text-black'}`}
                        >
                            {item}
                        </button>
                    ))}
                </div>}

                {message && (
                    <div className={`mt-5 flex items-start gap-3 border px-4 py-3 text-sm animate-login-alert ${message.type === 'error' ? 'border-red-200 bg-red-50 text-red-700' : 'border-green-200 bg-green-50 text-green-700'}`}>
                        <span className={`mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full text-xs text-white ${message.type === 'error' ? 'bg-red-500' : 'bg-green-500'}`}>
                            {message.type === 'error' ? '!' : '✓'}
                        </span>
                        <p>{message.text}</p>
                    </div>
                )}

                <div className='mt-6 space-y-4'>
                    {currentState !== 'Quên mật khẩu' && currentState !== 'Xác thực OTP' && (
                        <label className='block'>
                            <span className='text-xs uppercase tracking-[0.18em] text-gray-400'>Tên đăng nhập</span>
                            <input onChange={(e) => setUsername(e.target.value)} value={username} type="text" className='mt-2 w-full border border-gray-300 px-4 py-3 outline-none transition-all focus:border-black focus:shadow-[0_0_0_3px_rgba(0,0,0,0.06)]' placeholder='Nhập tên đăng nhập' required />
                        </label>
                    )}

                    {(currentState === 'Đăng ký' || currentState === 'Quên mật khẩu') && (
                        <label className='block'>
                            <span className='text-xs uppercase tracking-[0.18em] text-gray-400'>Email</span>
                            <input onChange={(e) => setEmail(e.target.value)} value={email} type="email" className='mt-2 w-full border border-gray-300 px-4 py-3 outline-none transition-all focus:border-black focus:shadow-[0_0_0_3px_rgba(0,0,0,0.06)]' placeholder='Nhập email' required />
                        </label>
                    )}

                    {currentState === 'Xác thực OTP' && (
                        <label className='block'>
                            <span className='text-xs uppercase tracking-[0.18em] text-gray-400'>Email đã đăng ký</span>
                            <input value={pendingVerificationEmail || email} type="email" className='mt-2 w-full border border-gray-300 bg-gray-50 px-4 py-3 outline-none' placeholder='Email dùng để đăng ký' readOnly />
                        </label>
                    )}

                    {currentState === 'Xác thực OTP' && (
                        <label className='block'>
                            <span className='text-xs uppercase tracking-[0.18em] text-gray-400'>Mã OTP</span>
                            <input onChange={(e) => setOtp(e.target.value)} value={otp} type="text" inputMode='numeric' autoComplete='one-time-code' maxLength={6} className='mt-2 w-full border border-gray-300 px-4 py-3 outline-none transition-all focus:border-black focus:shadow-[0_0_0_3px_rgba(0,0,0,0.06)]' placeholder='Nhập mã OTP 6 số' required />
                        </label>
                    )}

                    {currentState !== 'Quên mật khẩu' && currentState !== 'Xác thực OTP' && (
                        <label className='block'>
                            <span className='text-xs uppercase tracking-[0.18em] text-gray-400'>Mật khẩu</span>
                            <input onChange={(e) => setPassword(e.target.value)} value={password} type="password" className='mt-2 w-full border border-gray-300 px-4 py-3 outline-none transition-all focus:border-black focus:shadow-[0_0_0_3px_rgba(0,0,0,0.06)]' placeholder='Nhập mật khẩu' required />
                        </label>
                    )}
                </div>

                <div className='mt-4 flex justify-between text-sm text-gray-500'>
                    {currentState === 'Xác thực OTP' ? (
                        <button
                            type='button'
                            onClick={() => {
                                setCurrentState('Đăng ký');
                                setMessage(null);
                                resetOtpFlow();
                            }}
                            className='hover:text-black'
                        >
                            Quay lại đăng ký
                        </button>
                    ) : (
                        <button
                            type='button'
                            onClick={() => {
                                setCurrentState(currentState === 'Quên mật khẩu' ? 'Đăng nhập' : 'Quên mật khẩu');
                                setMessage(null);
                            }}
                            className='hover:text-black'
                        >
                            {currentState === 'Quên mật khẩu' ? 'Quay lại đăng nhập' : 'Quên mật khẩu?'}
                        </button>
                    )}
                    {currentState === 'Xác thực OTP' ? (
                        <button
                            type='button'
                            onClick={() => {
                                setCurrentState('Đăng nhập');
                                setMessage(null);
                                resetOtpFlow();
                            }}
                            className='hover:text-black'
                        >
                            Đăng nhập
                        </button>
                    ) : (
                        <button
                            type='button'
                            onClick={() => {
                                setCurrentState(currentState === 'Đăng nhập' ? 'Đăng ký' : 'Đăng nhập');
                                setMessage(null);
                            }}
                            className='hover:text-black'
                        >
                            {currentState === 'Đăng nhập' ? 'Tạo tài khoản' : 'Đăng nhập'}
                        </button>
                    )}
                </div>

                <button disabled={isSubmitting} className='mt-7 w-full bg-black text-white py-3 text-sm font-medium hover:bg-gray-800 transition-all disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center justify-center gap-3'>
                    {isSubmitting && <span className='h-4 w-4 rounded-full border-2 border-white/40 border-t-white animate-spin'></span>}
                    {isSubmitting ? 'Vui lòng chờ...' : currentState === 'Đăng nhập' ? 'Đăng nhập' : currentState === 'Đăng ký' ? 'Đăng ký' : currentState === 'Quên mật khẩu' ? 'Gửi mật khẩu mới' : 'Xác minh OTP'}
                </button>
            </form>
        </div>
    )
}

export default Login
