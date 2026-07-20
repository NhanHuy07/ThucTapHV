import { useContext } from 'react';
import { ShopContext } from '../context/ShopContext';
import Title from '../components/Title';
import CartTotal from '../components/CartTotal';
import { assets } from '../assets/assets';
import type { CartDetails, CartItems, Product } from '../types/shop';
import { formatCurrency } from '../lib/format';

const Cart = () => {
    const context = useContext(ShopContext);
    const products = (context?.products || []) as Product[];
    const cartItems = (context?.cartItems || {}) as CartItems;
    const cartDetails = (context?.cartDetails || {}) as CartDetails;
    const updateQuantity = context?.updateQuantity;
    const removeFromCart = context?.removeFromCart;
    const navigate = context?.navigate;

    const cartData = Object.entries(cartItems).flatMap(([productId, sizes]) =>
        Object.entries(sizes)
            .filter(([, quantity]) => quantity > 0)
            .map(([size, quantity]) => ({ productId, size, quantity }))
    );

    return (
        <div className='border-t pt-14 min-h-[70vh]'>
            <div className='text-2xl mb-3'>
                <Title text1='GIỎ' text2='HÀNG' />
            </div>

            {cartData.length === 0 ? (
                <div className='py-16 text-center text-gray-500'>
                    <p>Giỏ hàng của bạn đang trống.</p>
                    <button onClick={() => navigate?.('/collection')} className='mt-6 bg-black text-white px-8 py-3 text-sm'>
                        MUA SẮM NGAY
                    </button>
                </div>
            ) : (
                <>
                    <div>
                        {cartData.map((item) => {
                            const productData = products.find((product) => product._id === item.productId);
                            const cartDetail = cartDetails[item.productId]?.[item.size];
                            const productName = cartDetail?.productName || productData?.name || item.productId;
                            const productImage = cartDetail?.imgUrl || productData?.image[0] || assets.p_img1;
                            const productPrice = cartDetail?.finalPrice ?? productData?.price ?? 0;

                            return (
                                <div key={`${item.productId}-${item.size}`} className='py-4 border-t border-b text-gray-700 grid grid-cols-[4fr_1fr_0.5fr] sm:grid-cols-[4fr_2fr_0.5fr] items-center gap-4'>
                                    <div className='flex items-start gap-6'>
                                        <img className='w-16 sm:w-20' src={productImage} alt={productName} />
                                        <div>
                                            <p className='text-xs sm:text-lg font-medium'>{productName}</p>
                                            <div className='flex items-center gap-5 mt-2'>
                                                <p>{formatCurrency(productPrice)}</p>
                                                <p className='px-2 sm:px-3 sm:py-1 border bg-slate-50'>{item.size}</p>
                                            </div>
                                        </div>
                                    </div>

                                    <input
                                        onChange={(event) => {
                                            const quantity = Number(event.target.value);
                                            if (quantity > 0) {
                                                updateQuantity?.(item.productId, item.size, quantity);
                                            }
                                        }}
                                        className='border max-w-10 sm:max-w-20 px-1 sm:px-2 py-1'
                                        type='number'
                                        min={1}
                                        value={item.quantity}
                                    />

                                    <img
                                        onClick={() => removeFromCart?.(item.productId, item.size)}
                                        className='w-4 mr-4 sm:w-5 cursor-pointer'
                                        src={assets.bin_icon}
                                        alt='Xóa'
                                    />
                                </div>
                            );
                        })}
                    </div>

                    <div className='flex justify-end my-20'>
                        <div className='w-full sm:w-[450px]'>
                            <CartTotal />
                            <div className='w-full text-end'>
                                <button onClick={() => navigate?.('/place-order')} className='bg-black text-white text-sm my-8 px-8 py-3'>
                                    TIẾN HÀNH THANH TOÁN
                                </button>
                            </div>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
};

export default Cart;
