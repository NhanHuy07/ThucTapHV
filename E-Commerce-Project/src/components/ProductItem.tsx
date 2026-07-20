import { useContext, useState } from 'react';
import { ShopContext } from '../context/ShopContext';
import { Link } from 'react-router-dom';
import { formatCurrency } from '../lib/format';

interface ProductItemProps {
    id: string;
    image: string[];
    name: string;
    price: number;
    originalPrice?: number;
    sizes: string[];
}

const ProductItem = ({ id, image, name, price, originalPrice, sizes }: ProductItemProps) => {

    const context = useContext(ShopContext);
    const [selectedSize, setSelectedSize] = useState('');

    if (!context) {
        return null;
    }

    const { addToCart } = context;

    return (
        <div className='group relative text-gray-700 cursor-pointer'>
            <Link to={`/product/${id}`}>
                <div className='overflow-hidden relative'>
                    <img className='hover:scale-110 transition ease-in-out duration-500 w-full' src={image[0]} alt={name} />

                    {/* Size Overlay on Hover */}
                    <div className='absolute bottom-0 left-0 right-0 bg-white/90 translate-y-full group-hover:translate-y-0 transition-transform duration-300 p-3'>
                        <p className='text-[10px] font-bold text-gray-400 mb-1 uppercase tracking-tighter'>
                            {sizes.length > 0 ? 'Chọn nhanh / Kích cỡ:' : 'Hết hàng'}
                        </p>
                        <div className='flex flex-wrap gap-1'>
                            {sizes.map((size) => (
                                <span
                                    key={size}
                                    onClick={(e) => {
                                        e.preventDefault();
                                        setSelectedSize(size);
                                    }}
                                    className={`border border-gray-300 px-2 py-0.5 text-[10px] transition-colors ${size === selectedSize ? 'bg-black text-white' : 'hover:bg-black hover:text-white'}`}
                                >
                                    {size}
                                </span>
                            ))}
                        </div>
                    </div>
                </div>
                <p className='pt-3 pb-1 text-sm font-medium'>{name}</p>
                {originalPrice && originalPrice > price ? (
                    <div className='flex items-center gap-2'>
                        <span className='text-sm font-bold text-red-600'>{formatCurrency(price)}</span>
                        <span className='text-[11px] text-gray-400 line-through'>{formatCurrency(originalPrice)}</span>
                        <span className='text-[9px] font-bold text-white bg-red-500 px-1 py-0.5 rounded-sm'>
                            -{Math.round(((originalPrice - price) / originalPrice) * 100)}%
                        </span>
                    </div>
                ) : (
                    <p className='text-sm font-bold text-black'>{formatCurrency(price)}</p>
                )}
            </Link>

            {/* Add to Cart Button (Sporty Style) */}
            <button
                onClick={() => addToCart(id, selectedSize)}
                disabled={sizes.length === 0}
                className='w-full mt-2 bg-black text-white py-2 text-[10px] font-bold uppercase tracking-widest opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded-sm disabled:bg-gray-300 disabled:cursor-not-allowed'
            >
                {sizes.length > 0 ? 'Thêm vào giỏ' : 'Tạm hết hàng'}
            </button>
        </div>
    )
}

export default ProductItem
