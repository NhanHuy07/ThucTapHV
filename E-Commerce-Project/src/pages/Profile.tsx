import { useContext, useEffect, useMemo, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { ShopContext } from '../context/ShopContext';
import Title from '../components/Title';
import { apiRequest } from '../lib/api';
import type { ApiResponse, RankType, UserProfile, UserProfileUpdatePayload } from '../types/shop';
import { formatCurrency } from '../lib/format';

const rankTiers: RankType[] = ['Bronze', 'Silver', 'Gold', 'Platinum', 'Diamond', 'Ultimate'];

type UploadImageResponse = ApiResponse<string | { imageUrl?: string; url?: string }> & {
    imageUrl?: string;
    url?: string;
};

const getUploadImageUrl = (response: UploadImageResponse) => {
    const data = response.data;

    return (
        (typeof data === 'object' && data !== null && (data.imageUrl || data.url)) ||
        (typeof data === 'string' ? data : undefined) ||
        response.imageUrl ||
        response.url
    );
};

const rankConfig: Record<RankType, { emoji: string; bg: string; text: string; gradient: string }> = {
    Bronze: { emoji: '🥉', bg: 'bg-amber-100', text: 'text-amber-800', gradient: 'from-amber-600 to-amber-400' },
    Silver: { emoji: '🥈', bg: 'bg-gray-200', text: 'text-gray-700', gradient: 'from-gray-500 to-gray-300' },
    Gold: { emoji: '🥇', bg: 'bg-yellow-100', text: 'text-yellow-800', gradient: 'from-yellow-600 to-yellow-400' },
    Platinum: { emoji: '💎', bg: 'bg-blue-100', text: 'text-blue-800', gradient: 'from-blue-600 to-blue-400' },
    Diamond: { emoji: '💠', bg: 'bg-cyan-100', text: 'text-cyan-800', gradient: 'from-cyan-600 to-cyan-400' },
    Ultimate: { emoji: '👑', bg: 'bg-purple-100', text: 'text-purple-800', gradient: 'from-purple-600 to-purple-400' },
};

const ProfileField = ({ label, value }: { label: string; value?: string }) => (
    <div className='border-b border-gray-100 py-4'>
        <p className='text-xs uppercase tracking-[0.18em] text-gray-400'>{label}</p>
        <p className='mt-1 text-sm sm:text-base text-gray-800 break-words'>{value || 'Chưa cập nhật'}</p>
    </div>
);

const Profile = () => {
    const context = useContext(ShopContext);
    const token = context?.token;
    const user = context?.user as UserProfile | null;
    const updateUserProfile = context?.updateUserProfile as ((payload: UserProfileUpdatePayload) => Promise<unknown>) | undefined;
    const [isEditing, setIsEditing] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [isUploadingAvatar, setIsUploadingAvatar] = useState(false);
    const [formData, setFormData] = useState<UserProfileUpdatePayload>({
        id: '',
        firstName: '',
        lastName: '',
        avatar: '',
    });

    const firstName = user?.f_name || user?.firstName;
    const lastName = user?.l_name || user?.lastName;
    const avatar = user?.img || user?.avatar;
    const displayName = [firstName, lastName].filter(Boolean).join(' ') || 'Khách hàng';
    const previewName = [formData.firstName, formData.lastName].filter(Boolean).join(' ') || displayName;
    const previewAvatar = formData.avatar || avatar;
    const initials = previewName
        .split(' ')
        .filter(Boolean)
        .slice(0, 2)
        .map((name) => name[0])
        .join('')
        .toUpperCase();
    const hasProfile = Boolean(user);
    const formChanged = useMemo(() => {
        return (
            formData.firstName !== (firstName || '') ||
            formData.lastName !== (lastName || '') ||
            (formData.avatar || '') !== (avatar || '')
        );
    }, [avatar, firstName, formData, lastName]);

    const currentRank = user?.rank?.type || 'Bronze';
    const currentRankIndex = rankTiers.indexOf(currentRank);
    const currentRankConfig = rankConfig[currentRank];

    useEffect(() => {
        setFormData({
            id: user?.id || '',
            firstName: firstName || '',
            lastName: lastName || '',
            avatar: avatar || '',
        });
    }, [avatar, firstName, lastName, user?.id]);

    if (!token) {
        return <Navigate to='/login' replace />;
    }

    const handleChange = (field: keyof UserProfileUpdatePayload, value: string) => {
        setFormData((prev) => ({ ...prev, [field]: value }));
    };

    const handleCancel = () => {
        setFormData({
            id: user?.id || '',
            firstName: firstName || '',
            lastName: lastName || '',
            avatar: avatar || '',
        });
        setIsEditing(false);
    };

    const handleAvatarUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        event.target.value = '';
        if (!file) return;

        const toastId = toast.loading('Đang tải ảnh đại diện lên...');
        setIsUploadingAvatar(true);
        try {
            const uploadData = new FormData();
            uploadData.append('image', file);
            uploadData.append('file', file);

            const response = await apiRequest<UploadImageResponse>('/v1/api/admin/upload/image', {
                method: 'POST',
                token,
                body: uploadData,
            });

            const imageUrl = getUploadImageUrl(response);

            if (typeof imageUrl !== 'string' || !imageUrl) {
                throw new Error('Định dạng phản hồi API upload ảnh không đúng');
            }

            handleChange('avatar', imageUrl);
            toast.update(toastId, {
                render: 'Tải ảnh đại diện thành công',
                type: 'success',
                isLoading: false,
                autoClose: 3000,
            });
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể tải ảnh đại diện lên';
            toast.update(toastId, {
                render: message,
                type: 'error',
                isLoading: false,
                autoClose: 3000,
            });
        } finally {
            setIsUploadingAvatar(false);
        }
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        const payload = {
            id: user?.id || formData.id,
            firstName: formData.firstName.trim(),
            lastName: formData.lastName.trim(),
            avatar: formData.avatar?.trim(),
        };

        if (!payload.id) {
            toast.error('Không tìm thấy mã người dùng để cập nhật hồ sơ');
            return;
        }

        if (!payload.firstName || !payload.lastName) {
            toast.error('Vui lòng nhập đầy đủ họ và tên');
            return;
        }

        if (!updateUserProfile) {
            toast.error('Chưa cấu hình chức năng cập nhật hồ sơ');
            return;
        }

        setIsSaving(true);
        try {
            await updateUserProfile(payload);
            toast.success('Cập nhật hồ sơ thành công');
            setIsEditing(false);
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể cập nhật hồ sơ';
            toast.error(message);
        } finally {
            setIsSaving(false);
        }
    };

    return (
        <div className='border-t pt-10 sm:pt-14 min-h-[70vh]'>
            <div className='text-2xl mb-8 flex items-center justify-between'>
                <Title text1='HỒ' text2='SƠ' />
            </div>

            <div className='grid lg:grid-cols-[0.9fr_1.4fr] gap-8 items-start'>
                <div className='space-y-6'>
                    <div className='border bg-white shadow-sm'>
                        <div className='h-28 bg-gradient-to-r from-black via-gray-800 to-gray-500'></div>
                        <div className='px-6 pb-7 -mt-14'>
                            {previewAvatar ? (
                                <img
                                    className='w-28 h-28 rounded-full object-cover border-4 border-white bg-white shadow-md'
                                    src={previewAvatar}
                                    alt={previewName}
                                />
                            ) : (
                                <div className='w-28 h-28 rounded-full border-4 border-white bg-gray-100 flex items-center justify-center text-3xl font-medium text-gray-700 shadow-md'>
                                    {initials || 'U'}
                                </div>
                            )}

                            <div className='mt-5'>
                                <p className='text-2xl font-medium text-gray-900'>{previewName}</p>
                                <p className='mt-2 text-sm text-gray-500'>Hồ sơ mua sắm cá nhân</p>
                            </div>

                            <button
                                type='button'
                                onClick={() => setIsEditing(true)}
                                disabled={!hasProfile || isSaving}
                                className='mt-6 w-full border border-black py-3 text-sm hover:bg-black hover:text-white transition-all disabled:cursor-not-allowed disabled:border-gray-200 disabled:text-gray-400 disabled:hover:bg-white'
                            >
                                CHỈNH SỬA HỒ SƠ
                            </button>
                        </div>
                    </div>

                    {/* Rank Card */}
                    {user?.rank && (
                        <div className='border bg-white shadow-sm p-5 sm:p-6'>
                            <div className='flex items-center gap-3 border-b border-gray-100 pb-4'>
                                <span className='text-xl'>🛡️</span>
                                <p className='text-lg font-medium text-gray-900'>Hạng thành viên</p>
                            </div>

                            <div className='mt-5'>
                                {/* Current Rank Badge */}
                                <div className={`inline-flex items-center gap-2 px-4 py-2.5 rounded-lg ${currentRankConfig.bg}`}>
                                    <span className='text-2xl'>{currentRankConfig.emoji}</span>
                                    <span className={`text-lg font-bold ${currentRankConfig.text}`}>{currentRank}</span>
                                </div>

                                {/* Rank Stats */}
                                <div className='mt-5 space-y-3'>
                                    <div className='flex justify-between text-sm'>
                                        <span className='text-gray-500'>Tổng chi tiêu</span>
                                        <span className='font-medium text-gray-900'>{formatCurrency(user.totalPurchase)}</span>
                                    </div>
                                    <div className='flex justify-between text-sm'>
                                        <span className='text-gray-500'>Mức tối thiểu rank hiện tại</span>
                                        <span className='font-medium text-gray-900'>{formatCurrency(user.rank.minTotalPurchase)}</span>
                                    </div>
                                </div>

                                {/* Progress Bar */}
                                {currentRankIndex < rankTiers.length - 1 && (
                                    <div className='mt-5'>
                                        <div className='flex justify-between text-xs text-gray-400 mb-2'>
                                            <span>{currentRank}</span>
                                            <span>{rankTiers[currentRankIndex + 1]}</span>
                                        </div>
                                        <div className='h-2 bg-gray-100 rounded-full overflow-hidden'>
                                            <div
                                                className={`h-full rounded-full bg-gradient-to-r ${currentRankConfig.gradient} transition-all duration-500`}
                                                style={{ width: `${Math.min(((user.totalPurchase || 0) / Math.max(user.rank.minTotalPurchase || 1, 1)) * 100, 100)}%` }}
                                            />
                                        </div>
                                    </div>
                                )}

                                {/* Rank Tier Timeline */}
                                <div className='mt-6 pt-5 border-t border-gray-100'>
                                    <p className='text-xs uppercase tracking-[0.16em] text-gray-400 mb-4'>Các cấp hạng</p>
                                    <div className='flex items-center gap-1'>
                                        {rankTiers.map((tier, index) => {
                                            const config = rankConfig[tier];
                                            const isActive = index <= currentRankIndex;
                                            const isCurrent = tier === currentRank;

                                            return (
                                                <div key={tier} className='flex items-center flex-1'>
                                                    <div className='flex flex-col items-center gap-1.5 flex-1'>
                                                        <div
                                                            className={`w-8 h-8 rounded-full flex items-center justify-center text-sm transition-all ${
                                                                isCurrent
                                                                    ? `${config.bg} ring-2 ring-offset-1 ring-gray-400 scale-110`
                                                                    : isActive
                                                                    ? config.bg
                                                                    : 'bg-gray-100'
                                                            }`}
                                                            title={tier}
                                                        >
                                                            {isActive ? config.emoji : '○'}
                                                        </div>
                                                        <span className={`text-[10px] font-medium ${isCurrent ? config.text : isActive ? 'text-gray-600' : 'text-gray-300'}`}>
                                                            {tier}
                                                        </span>
                                                    </div>
                                                    {index < rankTiers.length - 1 && (
                                                        <div className={`h-0.5 w-full -mt-4 ${index < currentRankIndex ? 'bg-gray-400' : 'bg-gray-200'}`} />
                                                    )}
                                                </div>
                                            );
                                        })}
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                <div className='border bg-white px-6 sm:px-8 py-6 shadow-sm'>
                    <div className='flex items-center justify-between border-b pb-5'>
                        <div>
                            <p className='text-lg font-medium text-gray-900'>
                                {isEditing ? 'Chỉnh sửa thông tin' : 'Thông tin tài khoản'}
                            </p>
                            <p className='text-sm text-gray-500 mt-1'>
                                {isEditing ? 'Cập nhật thông tin cá nhân dùng cho tài khoản của bạn' : 'Thông tin khách hàng đã lưu'}
                            </p>
                        </div>
                    </div>

                    {isEditing ? (
                        <form onSubmit={handleSubmit} className='mt-6 space-y-5'>
                            <div className='grid sm:grid-cols-2 gap-5'>
                                <div>
                                    <label className='text-xs uppercase tracking-[0.16em] text-gray-500'>Tên</label>
                                    <input
                                        value={formData.firstName}
                                        onChange={(event) => handleChange('firstName', event.target.value)}
                                        className='mt-2 w-full border border-gray-300 px-4 py-3 outline-none transition-all focus:border-black focus:shadow-[0_0_0_3px_rgba(0,0,0,0.06)]'
                                        placeholder='Nhập tên'
                                    />
                                </div>
                                <div>
                                    <label className='text-xs uppercase tracking-[0.16em] text-gray-500'>Họ</label>
                                    <input
                                        value={formData.lastName}
                                        onChange={(event) => handleChange('lastName', event.target.value)}
                                        className='mt-2 w-full border border-gray-300 px-4 py-3 outline-none transition-all focus:border-black focus:shadow-[0_0_0_3px_rgba(0,0,0,0.06)]'
                                        placeholder='Nhập họ'
                                    />
                                </div>
                            </div>

                            <div>
                                <label className='text-xs uppercase tracking-[0.16em] text-gray-500'>Ảnh đại diện</label>
                                <div className='mt-2 flex flex-col sm:flex-row gap-3'>
                                    <input
                                        value={formData.avatar || ''}
                                        onChange={(event) => handleChange('avatar', event.target.value)}
                                        className='w-full border border-gray-300 px-4 py-3 outline-none transition-all focus:border-black focus:shadow-[0_0_0_3px_rgba(0,0,0,0.06)]'
                                        placeholder='https://example.com/avatar.jpg'
                                    />
                                    <label className='shrink-0 border border-gray-300 px-5 py-3 text-sm font-medium text-center transition-all hover:border-black cursor-pointer'>
                                        {isUploadingAvatar ? 'ĐANG TẢI...' : 'CHỌN ẢNH'}
                                        <input
                                            type='file'
                                            accept='image/*'
                                            onChange={handleAvatarUpload}
                                            disabled={isUploadingAvatar || isSaving}
                                            className='hidden'
                                        />
                                    </label>
                                </div>
                            </div>

                            <p className='text-xs text-gray-500'>
                                Backend hiện chỉ hỗ trợ cập nhật họ, tên và ảnh đại diện. Số điện thoại chưa có trong API cập nhật hồ sơ.
                            </p>

                            <div className='flex flex-col sm:flex-row gap-3 pt-2'>
                                <button
                                    type='submit'
                                    disabled={isSaving || isUploadingAvatar || !formChanged}
                                    className='bg-black text-white px-8 py-3 text-sm font-medium transition-all hover:bg-gray-800 disabled:cursor-not-allowed disabled:bg-gray-300'
                                >
                                    {isSaving ? 'ĐANG LƯU...' : 'LƯU THAY ĐỔI'}
                                </button>
                                <button
                                    type='button'
                                    onClick={handleCancel}
                                    disabled={isSaving}
                                    className='border border-gray-300 px-8 py-3 text-sm font-medium transition-all hover:border-black disabled:cursor-not-allowed disabled:text-gray-400'
                                >
                                    HỦY
                                </button>
                            </div>
                        </form>
                    ) : (
                        <div className='mt-2'>
                            <ProfileField label='Mã người dùng' value={user?.id} />
                            <div className='grid sm:grid-cols-2 sm:gap-8'>
                                <ProfileField label='Tên' value={firstName} />
                                <ProfileField label='Họ' value={lastName} />
                            </div>
                            <ProfileField label='Ảnh đại diện' value={avatar} />
                            <ProfileField label='Số điện thoại' value={user?.phone} />
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Profile;
