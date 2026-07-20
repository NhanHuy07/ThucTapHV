import { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { apiRequest } from '../lib/api';
import { formatCurrency } from '../lib/format';
import type { AdminUser, ApiResponse, RankType } from '../types/shop';

type RankConfig = {
    emoji: string;
    bg: string;
    text: string;
};

const rankConfig: Record<RankType, RankConfig> = {
    Bronze: { emoji: '🥉', bg: 'bg-amber-100', text: 'text-amber-800' },
    Silver: { emoji: '🥈', bg: 'bg-gray-200', text: 'text-gray-700' },
    Gold: { emoji: '🥇', bg: 'bg-yellow-100', text: 'text-yellow-800' },
    Platinum: { emoji: '💎', bg: 'bg-blue-100', text: 'text-blue-800' },
    Diamond: { emoji: '💠', bg: 'bg-cyan-100', text: 'text-cyan-800' },
    Ultimate: { emoji: '👑', bg: 'bg-purple-100', text: 'text-purple-800' },
};

type AdminUserManagementProps = {
    token: string;
};

const AdminUserManagement = ({ token }: AdminUserManagementProps) => {
    const [users, setUsers] = useState<AdminUser[]>([]);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [searchQuery, setSearchQuery] = useState('');
    const [rankFilter, setRankFilter] = useState<RankType | 'ALL'>('ALL');

    const fetchUsers = async () => {
        if (!token) return;

        setLoading(true);
        try {
            const endpoint = `/v1/api/admin/user/get-all?page=${page}&size=10`;
            const response = await apiRequest<ApiResponse<any>>(endpoint, {
                method: 'GET',
                token,
            });

            const data = response.data;
            const userList: AdminUser[] = data?.items || data?.content || [];
            setUsers(userList);
            setTotalPages(data?.totalPages || 0);
            setTotalElements(data?.totalElements || 0);
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể tải danh sách người dùng';
            toast.error(message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, [page]);

    const filteredUsers = users.filter((user) => {
        const fullName = `${user.firstName || ''} ${user.lastName || ''}`.toLowerCase();
        const matchesSearch =
            !searchQuery.trim() ||
            fullName.includes(searchQuery.toLowerCase()) ||
            user.id.toLowerCase().includes(searchQuery.toLowerCase());
        const matchesRank = rankFilter === 'ALL' || user.rank?.type === rankFilter;
        return matchesSearch && matchesRank;
    });

    const getInitials = (firstName: string | null, lastName: string | null) => {
        const first = (firstName || '').charAt(0).toUpperCase();
        const last = (lastName || '').charAt(0).toUpperCase();
        return first + last || '?';
    };

    return (
        <div className='border bg-white p-5 sm:p-6 shadow-sm'>
            <div className='flex flex-col md:flex-row md:items-center md:justify-between gap-4 border-b border-gray-100 pb-5'>
                <div>
                    <p className='text-lg font-medium text-gray-900'>Quản lý người dùng</p>
                    <p className='mt-1 text-sm text-gray-500'>Tổng cộng {totalElements} người dùng</p>
                </div>
            </div>

            <div className='mt-5 grid gap-3 sm:grid-cols-2'>
                <input
                    value={searchQuery}
                    onChange={(event) => setSearchQuery(event.target.value)}
                    className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                    type='text'
                    placeholder='Tìm kiếm theo tên hoặc mã người dùng...'
                />
                <select
                    value={rankFilter}
                    onChange={(event) => setRankFilter(event.target.value as RankType | 'ALL')}
                    className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                >
                    <option value='ALL'>Tất cả hạng</option>
                    <option value='Bronze'>🥉 Bronze</option>
                    <option value='Silver'>🥈 Silver</option>
                    <option value='Gold'>🥇 Gold</option>
                    <option value='Platinum'>💎 Platinum</option>
                    <option value='Diamond'>💠 Diamond</option>
                    <option value='Ultimate'>👑 Ultimate</option>
                </select>
            </div>

            <div className='mt-6 overflow-x-auto'>
                <table className='w-full min-w-[820px] text-left text-sm'>
                    <thead>
                        <tr className='border-b text-xs uppercase tracking-[0.16em] text-gray-400'>
                            <th className='py-3 font-medium'>Avatar</th>
                            <th className='py-3 font-medium'>Họ tên</th>
                            <th className='py-3 font-medium'>Mã người dùng</th>
                            <th className='py-3 font-medium'>Tổng mua hàng</th>
                            <th className='py-3 font-medium'>Rank</th>
                            <th className='py-3 font-medium'>Min Purchase</th>
                        </tr>
                    </thead>
                    <tbody>
                        {loading ? (
                            <tr>
                                <td colSpan={6} className='py-10 text-center text-gray-500'>
                                    Đang tải danh sách người dùng...
                                </td>
                            </tr>
                        ) : filteredUsers.length === 0 ? (
                            <tr>
                                <td colSpan={6} className='py-10 text-center text-gray-500'>
                                    Không tìm thấy người dùng nào
                                </td>
                            </tr>
                        ) : (
                            filteredUsers.map((user) => {
                                const rank = user.rank?.type || 'Bronze';
                                const config = rankConfig[rank] || rankConfig.Bronze;
                                return (
                                    <tr key={user.id} className='border-b last:border-b-0'>
                                        <td className='py-4'>
                                            {user.avatar ? (
                                                <img
                                                    src={user.avatar}
                                                    alt={`${user.firstName || ''} ${user.lastName || ''}`}
                                                    className='h-10 w-10 rounded-full object-cover'
                                                />
                                            ) : (
                                                <div className='flex h-10 w-10 items-center justify-center rounded-full bg-gray-200 text-sm font-medium text-gray-600'>
                                                    {getInitials(user.firstName, user.lastName)}
                                                </div>
                                            )}
                                        </td>
                                        <td className='py-4 font-medium text-gray-900'>
                                            {user.firstName || ''} {user.lastName || ''}
                                        </td>
                                        <td className='py-4 text-gray-600' title={user.id}>
                                            {user.id.length > 8 ? `${user.id.substring(0, 8)}...` : user.id}
                                        </td>
                                        <td className='py-4 text-gray-900'>
                                            {formatCurrency(user.totalPurchase)}
                                        </td>
                                        <td className='py-4'>
                                            <span className={`inline-flex items-center gap-1 px-3 py-1 text-xs font-medium ${config.bg} ${config.text}`}>
                                                {config.emoji} {rank}
                                            </span>
                                        </td>
                                        <td className='py-4 text-gray-600'>
                                            {formatCurrency(user.rank?.minTotalPurchase)}
                                        </td>
                                    </tr>
                                );
                            })
                        )}
                    </tbody>
                </table>
            </div>

            {totalPages > 0 && (
                <div className='mt-6 flex items-center justify-between border-t border-gray-100 pt-5'>
                    <button
                        type='button'
                        onClick={() => setPage((prev) => Math.max(0, prev - 1))}
                        disabled={page === 0}
                        className='border border-black px-5 py-2.5 text-sm font-medium hover:bg-black hover:text-white transition-all disabled:border-gray-300 disabled:text-gray-300 disabled:hover:bg-white'
                    >
                        Trang trước
                    </button>
                    <span className='text-sm text-gray-600'>
                        Trang {page + 1} / {totalPages}
                    </span>
                    <button
                        type='button'
                        onClick={() => setPage((prev) => prev + 1)}
                        disabled={page + 1 >= totalPages}
                        className='border border-black px-5 py-2.5 text-sm font-medium hover:bg-black hover:text-white transition-all disabled:border-gray-300 disabled:text-gray-300 disabled:hover:bg-white'
                    >
                        Trang sau
                    </button>
                </div>
            )}
        </div>
    );
};

export default AdminUserManagement;
