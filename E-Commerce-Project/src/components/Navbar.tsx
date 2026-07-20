import React, { useState, useContext } from 'react';
import { assets } from '../assets/assets';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { ShopContext } from '../context/ShopContext';
import NotificationDropdown from './NotificationDropdown';

const Navbar: React.FC = () => {
    const [visible, setVisible] = useState<boolean>(false);

    // Sử dụng context an toàn
    const context = useContext(ShopContext);
    const navigate = useNavigate();

    // Nếu không có context thì trả về null hoặc báo lỗi
    if (!context) {
        return <div className="p-4 text-red-500">Lỗi: Navbar cần được đặt trong ShopProvider</div>;
    }

    const { showSearch, setShowSearch, getCartCount, token, setToken, isAdmin } = context;

    const logout = () => {
        setToken('');
        navigate('/login');
    };

    const handleProfileClick = () => {
        navigate(token ? '/profile' : '/login');
    };

    return (
        <div className='sticky top-0 z-50 bg-white flex items-center justify-between py-5 font-medium border-b border-gray-200'>

            {/* Logo */}
            <Link to='/'>
                <h1 className='w-36 text-xl font-bold cursor-pointer'>P-Shop</h1>
            </Link>

            {/* Desktop Menu */}
            <ul className='hidden sm:flex gap-5 text-sm text-gray-700'>
                <NavLink to='/' className='flex flex-col items-center gap-1'>
                    <p>TRANG CHỦ</p>
                    <hr className='w-2/4 border-none h-[1.5px] bg-gray-700 hidden' />
                </NavLink>
                <NavLink to='/collection' className='flex flex-col items-center gap-1'>
                    <p>BỘ SƯU TẬP</p>
                    <hr className='w-2/4 border-none h-[1.5px] bg-gray-700 hidden' />
                </NavLink>
                <NavLink to='/about' className='flex flex-col items-center gap-1'>
                    <p>GIỚI THIỆU</p>
                    <hr className='w-2/4 border-none h-[1.5px] bg-gray-700 hidden' />
                </NavLink>
                <NavLink to='/contact' className='flex flex-col items-center gap-1'>
                    <p>LIÊN HỆ</p>
                    <hr className='w-2/4 border-none h-[1.5px] bg-gray-700 hidden' />
                </NavLink>
            </ul>

            {/* Right Side Actions */}
            <div className='flex items-center gap-6'>
                <img
                    onClick={() => setShowSearch(!showSearch)}
                    src={assets.search_icon}
                    alt="Tìm kiếm"
                    className="w-5 cursor-pointer"
                />
                {token && <NotificationDropdown token={token} scope={isAdmin ? 'admin' : 'user'} />}
                <div className='group relative'>
                    <img
                        onClick={handleProfileClick}
                        src={assets.profile_icon}
                        className='w-5 cursor-pointer'
                        alt="Tài khoản"
                    />
                    <div className='group-hover:block hidden absolute dropdown-menu right-0 pt-4'>
                        <div className='flex flex-col gap-2 w-36 py-3 px-5 bg-slate-100 text-gray-500 rounded'>
                            <p onClick={handleProfileClick} className='cursor-pointer hover:text-black'>Hồ sơ</p>
                            <p onClick={() => navigate('/orders')} className='cursor-pointer hover:text-black'>Đơn hàng</p>
                            {isAdmin && <p onClick={() => navigate('/admin')} className='cursor-pointer hover:text-black'>Quản trị</p>}
                            {token
                                ? <p onClick={logout} className='cursor-pointer hover:text-black'>Đăng xuất</p>
                                : <p onClick={() => navigate('/login')} className='cursor-pointer hover:text-black'>Đăng nhập</p>
                            }
                        </div>
                    </div>
                </div>

                <Link to='/cart' className='relative'>
                    <img src={assets.cart_icon} className='w-5 min-w-5' alt="Giỏ hàng" />
                    <p className='absolute right-[-5px] bottom-[-5px] w-4 text-center leading-4 bg-black text-white aspect-square rounded-full text-[8px]'>
                        {getCartCount()}
                    </p>
                </Link>

                <img
                    onClick={() => setVisible(true)}
                    src={assets.menu_icon}
                    className='w-5 cursor-pointer sm:hidden'
                    alt="Menu"
                />
            </div>

            {/* Sidebar Menu for Mobile */}
            <div className={`absolute top-0 right-0 bottom-0 overflow-hidden bg-white transition-all ${visible ? 'w-full' : 'w-0'}`}>
                <div className='flex flex-col text-gray-600'>
                    <div onClick={() => setVisible(false)} className='flex items-center gap-4 p-3 cursor-pointer'>
                        <img className='h-4 rotate-180' src={assets.dropdown_icon} alt="Quay lại" />
                        <p>Quay lại</p>
                    </div>
                    <NavLink onClick={() => setVisible(false)} className='py-2 pl-6 border' to='/'>TRANG CHỦ</NavLink>
                    <NavLink onClick={() => setVisible(false)} className='py-2 pl-6 border' to='/collection'>BỘ SƯU TẬP</NavLink>
                    <NavLink onClick={() => setVisible(false)} className='py-2 pl-6 border' to='/about'>GIỚI THIỆU</NavLink>
                    <NavLink onClick={() => setVisible(false)} className='py-2 pl-6 border' to='/contact'>LIÊN HỆ</NavLink>
                    {isAdmin && <NavLink onClick={() => setVisible(false)} className='py-2 pl-6 border' to='/admin'>QUẢN TRỊ</NavLink>}
                </div>
            </div>
        </div>
    )
}

export default Navbar;
