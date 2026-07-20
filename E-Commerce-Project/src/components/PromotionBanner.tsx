import { useNavigate } from 'react-router-dom';

interface PromotionBannerProps {
    bgColor: string;
    title: string;
    code: string;
    discountText: string;
    image: string;
    textColor?: string;
    btnTextColor?: string;
}

const PromotionBanner = ({
    bgColor,
    title,
    code,
    discountText,
    image,
    textColor = 'text-white',
    btnTextColor = 'text-black'
}: PromotionBannerProps) => {
    const navigate = useNavigate();

    return (
        <div
            className={`my-16 rounded-lg overflow-hidden flex flex-col md:flex-row items-center justify-between px-6 md:px-16 py-10 md:py-0 shadow-xl`}
            style={{ backgroundColor: bgColor }}
        >
            {/* Left Side: Text */}
            <div className={`flex flex-col gap-5 ${textColor} text-center md:text-left md:w-1/2 z-10`}>
                <h2 className='text-4xl md:text-6xl font-prata uppercase tracking-tight leading-tight font-outfit'>
                    {title}
                </h2>
                <div className='flex flex-col gap-1'>
                    <p className='text-base md:text-lg font-medium tracking-wide font-outfit'>
                        Nhập <span className='font-bold text-yellow-300'>{code}</span> - {discountText}
                    </p>
                </div>
                <button
                    onClick={() => navigate('/collection')}
                    className={`mt-4 bg-white ${btnTextColor} px-10 py-3 w-fit mx-auto md:mx-0 font-bold rounded-full hover:scale-105 transition-all duration-300 shadow-md font-outfit`}
                >
                    MUA NGAY
                </button>
            </div>

            {/* Right Side: Image */}
            <div className='md:w-1/2 relative flex justify-center items-end mt-8 md:mt-0'>
                <img
                    src={image}
                    alt={title}
                    className='h-[300px] md:h-[450px] object-contain hover:scale-105 transition-transform duration-500'
                />
            </div>
        </div>
    )
}

export default PromotionBanner;
