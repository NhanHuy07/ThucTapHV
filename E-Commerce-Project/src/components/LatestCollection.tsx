import { useContext, useMemo, useState } from 'react';
import { ShopContext } from '../context/ShopContext';
import Title from './Title';
import ProductItem from './ProductItem';

const LatestCollection = () => {

    const context = useContext(ShopContext);
    const latestProducts = useMemo<any[]>(() => {
        if (!context || !context.products) return [];
        return context.products.slice(0, 10);
    }, [context]);

    const [currentPage, setCurrentPage] = useState(0);
    const itemsPerPage = 4; // Số lượng sản phẩm hiển thị trên 1 trang (1 hàng)

    const totalPages = Math.ceil(latestProducts.length / itemsPerPage); // tính số trang

    const handleNext = () => {
        setCurrentPage((prev) => (prev + 1) % totalPages);
    };

    const handlePrev = () => {
        setCurrentPage((prev) => (prev - 1 + totalPages) % totalPages);
    };

    if (!context) return null;

    return (
        <div className='my-10'>
            <div className='flex justify-between items-center py-8 text-3xl'>
                <Title text1={'SẢN PHẨM'} text2={'MỚI NHẤT'} />

                {/* Navigation Buttons */}
                <div className='flex gap-2'>
                    <button
                        onClick={handlePrev}
                        className='bg-gray-100 p-2 rounded-full hover:bg-gray-200 transition cursor-pointer'
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-5 h-5">
                            <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" />
                        </svg>
                    </button>
                    <button
                        onClick={handleNext}
                        className='bg-gray-100 p-2 rounded-full hover:bg-gray-200 transition cursor-pointer'
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-5 h-5">
                            <path strokeLinecap="round" strokeLinejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
                        </svg>
                    </button>
                </div>
            </div>

            {/* Slider Container */}
            <div className='overflow-hidden w-full'>
                <div
                    className='flex transition-transform duration-700 ease-in-out'
                    style={{ transform: `translateX(-${currentPage * 100}%)` }}
                >
                    {Array.from({ length: totalPages }).map((_, pageIndex) => (
                        <div
                            key={pageIndex}
                            className='min-w-full grid grid-cols-2 md:grid-cols-4 gap-4 gap-y-6 px-1'
                        >
                            {latestProducts
                                .slice(pageIndex * itemsPerPage, (pageIndex + 1) * itemsPerPage)
                                .map((item, index) => (
                                    <ProductItem
                                        key={index}
                                        id={item._id}
                                        image={item.image}
                                        name={item.name}
                                        price={item.price}
                                        originalPrice={item.originalPrice}
                                        sizes={item.sizes}
                                    />
                                ))
                            }
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}

export default LatestCollection
