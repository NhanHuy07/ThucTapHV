import Title from '../components/Title'
import { assets } from '../assets/assets'

const Contact = () => {
    return (
        <div>
            <div className='text-center text-2xl pt-10 border-t'>
                <Title text1={'LIÊN'} text2={'HỆ'} />
            </div>

            <div className='my-10 flex flex-col justify-center md:flex-row gap-10 mb-28'>
                <img className='w-full md:max-w-[480px]' src={assets.contact_img} alt="" />
                <div className='flex flex-col justify-center items-start gap-6'>
                    <p className='font-semibold text-xl text-gray-600'>Cửa hàng</p>
                    <p className='text-gray-500'>Đại Linh, Trung Văn <br /> Nam Từ Liêm, Hà Nội</p>
                    <p className='text-gray-500'>Điện thoại: abcxyz <br /> Email: voan07082004@gmail.com</p>
                    <p className='font-semibold text-xl text-gray-600'>Cơ hội nghề nghiệp tại P-Shop</p>
                    <p className='text-gray-500'>Tìm hiểu thêm về đội ngũ và các vị trí đang tuyển dụng.</p>
                    <button className='border border-black px-8 py-4 text-sm hover:bg-black hover:text-white transition-all duration-500'>Xem vị trí</button>
                </div>
            </div>
        </div>
    )
}

export default Contact
