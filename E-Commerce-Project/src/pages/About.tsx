import Title from '../components/Title'
import { assets } from '../assets/assets'

const About = () => {
    return (
        <div>
            <div className='text-2xl text-center pt-8 border-t'>
                <Title text1={'VỀ'} text2={'CHÚNG TÔI'} />
            </div>

            <div className='my-10 flex flex-col md:flex-row gap-16'>
                <img className='w-full md:max-w-[450px]' src={assets.about_img} alt="Về chúng tôi" />
                <div className='flex flex-col justify-center gap-6 md:w-2/4 text-gray-600'>
                    <p>P-Shop được xây dựng với mong muốn mang đến trải nghiệm mua sắm thời trang tiện lợi, hiện đại và dễ tiếp cận cho mọi khách hàng.</p>
                    <p>Chúng tôi tập trung tuyển chọn các sản phẩm chất lượng, dễ phối đồ và phù hợp với nhiều phong cách khác nhau.</p>
                    <b className='text-gray-800'>Sứ mệnh của chúng tôi</b>
                    <p>P-Shop hướng tới sự tiện lợi, minh bạch và tin cậy trong từng bước mua sắm, từ lựa chọn sản phẩm đến giao hàng.</p>
                </div>
            </div>

            <div className='text-xl py-4'>
                <Title text1={'VÌ SAO'} text2={'CHỌN CHÚNG TÔI'} />
            </div>

            <div className='flex flex-col md:flex-row text-sm mb-20'>
                <div className='border px-10 md:px-16 py-8 sm:py-20 flex flex-col gap-5'>
                    <b>Đảm bảo chất lượng:</b>
                    <p className='text-gray-600'>Mỗi sản phẩm đều được chọn lọc để đáp ứng tiêu chuẩn sử dụng hằng ngày.</p>
                </div>
                <div className='border px-10 md:px-16 py-8 sm:py-20 flex flex-col gap-5'>
                    <b>Tiện lợi:</b>
                    <p className='text-gray-600'>Giao diện dễ sử dụng giúp bạn mua sắm nhanh chóng và rõ ràng.</p>
                </div>
                <div className='border px-10 md:px-16 py-8 sm:py-20 flex flex-col gap-5'>
                    <b>Hỗ trợ tận tâm:</b>
                    <p className='text-gray-600'>Đội ngũ hỗ trợ luôn sẵn sàng đồng hành trong quá trình mua sắm của bạn.</p>
                </div>
            </div>
        </div>
    )
}

export default About
