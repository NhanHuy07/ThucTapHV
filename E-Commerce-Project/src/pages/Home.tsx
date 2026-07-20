
import Hero from '../components/Hero'
import LatestCollection from '../components/LatestCollection'
import BestSeller from '../components/BestSeller'
import OurPolicy from '../components/OurPolicy'
import NewsletterBox from '../components/NewsletterBox'
import PromotionBanner from '../components/PromotionBanner'
import { assets } from '../assets/assets'
const Home = () => {
    return (
        <div>
            <Hero />
            <LatestCollection />
            <PromotionBanner
                bgColor="#AD1D14"
                title="ĐỒ THU ĐÔNG"
                code="TETCM200"
                discountText="Giảm 200K đơn từ 1.199K"
                image={assets.promotionbanner}
                btnTextColor="text-[#AD1D14]"
            />
            <BestSeller />
            <PromotionBanner
                bgColor="#780D15"
                title="ĐỒ THU ĐÔNG"
                code="TETCM200"
                discountText="Giảm 200K đơn từ 1.199K"
                image={assets.promotionbanner1}
                btnTextColor="text-[#AD1D14]"
            />
            < OurPolicy />
            <NewsletterBox />
        </div>
    )
}

export default Home