import { useState, useEffect } from 'react';
import { assets } from '../assets/assets';
import { useNavigate } from 'react-router-dom';
const Hero = () => {
    // Danh sách các banner đã được cập nhật ảnh mới
    const navigate = useNavigate();
    const banners = [
        {
            id: 1,
            image: assets.banner_img,
            title: "Hàng mới về",
            subTitle: "BÁN CHẠY NHẤT"
        },
        {
            id: 2,
            image: assets.banner_img2,
            title: "Phong cách mùa mới",
            subTitle: "ĐANG THỊNH HÀNH"
        },
        {
            id: 3,
            image: assets.banner_img3,
            title: "Chất lượng cao",
            subTitle: "LỰA CHỌN TỐT NHẤT"
        }
    ];

    const [currentIndex, setCurrentIndex] = useState(0);

    // Hàm chuyển sang slide tiếp theo
    const nextSlide = () => {
        setCurrentIndex((prev) => (prev === banners.length - 1 ? 0 : prev + 1));
    };

    // Hàm quay lại slide trước
    const prevSlide = () => {
        setCurrentIndex((prev) => (prev === 0 ? banners.length - 1 : prev - 1));
    };

    // Tự động chuyển slide sau mỗi 5 giây
    useEffect(() => {
        const timer = setInterval(() => {
            nextSlide();
        }, 5000);

        return () => clearInterval(timer);
    }, [currentIndex]);


    return (
        <div className='relative overflow-hidden border border-gray-400'>
            {/* Slider Container */}
            <div
                className='flex transition-transform duration-700 ease-in-out'
                style={{ transform: `translateX(-${currentIndex * 100}%)` }}
            >
                {banners.map((item) => (
                    <div key={item.id} className='min-w-full flex flex-col sm:flex-row'>
                        {/* Left Content */}
                        <div className='w-full sm:w-1/2 flex items-center justify-center py-10 sm:py-0 bg-white'>
                            <div className='text-[#414141] px-10'>
                                <div className='flex items-center gap-2'>
                                    <p className='w-8 md:w-11 h-[2px] bg-[#414141]'></p>
                                    <p className='font-medium text-sm md:text-base'>{item.subTitle}</p>
                                </div>
                                <h1 className='font-prata text-3xl sm:py-3 lg:text-5xl leading-relaxed'>
                                    {item.title}
                                </h1>
                                <div
                                    onClick={() => navigate('/collection')}
                                    className='group/btn flex items-center gap-2 mt-5 cursor-pointer w-fit'
                                >
                                    <button className='bg-[#414141] text-white px-8 py-3 text-sm font-bold uppercase tracking-widest hover:bg-black transition-all duration-300 transform rounded-sm active:scale-95'>
                                        MUA NGAY
                                    </button>
                                </div>
                            </div>
                        </div>
                        {/* Right Image */}
                        <div
                            onClick={() => navigate('/collection')}
                            className='w-full sm:w-1/2 h-[300px] sm:h-auto cursor-pointer overflow-hidden'
                        >
                            <img className='w-full h-full object-cover hover:scale-105 transition-all duration-1000' src={item.image} alt={item.title} />
                        </div>
                    </div>
                ))}
            </div>

            {/* Navigation Buttons */}
            <button
                onClick={prevSlide}
                className='absolute left-4 top-1/2 -translate-y-1/2 bg-white/50 hover:bg-white p-2 rounded-full transition-all z-10'
            >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" />
                </svg>
            </button>
            <button
                onClick={nextSlide}
                className='absolute right-4 top-1/2 -translate-y-1/2 bg-white/50 hover:bg-white p-2 rounded-full transition-all z-10'
            >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
                </svg>
            </button>

            {/* Indicators (Dots) */}
            <div className='absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-2 z-10'>
                {banners.map((_, index) => (
                    <div
                        key={index}
                        onClick={() => setCurrentIndex(index)}
                        className={`h-2 w-2 rounded-full cursor-pointer transition-all ${index === currentIndex ? 'bg-black w-4' : 'bg-gray-400'}`}
                    />
                ))}
            </div>
        </div>
    );
};

export default Hero;
