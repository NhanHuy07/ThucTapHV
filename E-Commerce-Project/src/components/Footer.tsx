const Footer = () => {
    return (
        <div>
            <div className='flex flex-col sm:grid grid-cols-[3fr_1fr_1fr] gap-14 my-10 mt-40 text-sm'>
                <div>
                    {/* Logo Text instead of Image for no-asset safety */}
                    <h1 className="text-xl font-bold mb-5 w-32 uppercase">P-Shop.</h1>
                    <p className='w-full md:w-2/3 text-gray-600'>
                        P-Shop mang đến trải nghiệm mua sắm thời trang hiện đại, dễ chọn lựa và phù hợp với phong cách hằng ngày của bạn.
                    </p>
                </div>

                <div>
                    <p className='text-xl font-medium mb-5'>CÔNG TY</p>
                    <ul className='flex flex-col gap-1 text-gray-600'>
                        <li className="cursor-pointer hover:text-black">Trang chủ</li>
                        <li className="cursor-pointer hover:text-black">Giới thiệu</li>
                        <li className="cursor-pointer hover:text-black">Giao hàng</li>
                        <li className="cursor-pointer hover:text-black">Chính sách bảo mật</li>
                    </ul>
                </div>

                <div>
                    <p className='text-xl font-medium mb-5'>LIÊN HỆ</p>
                    <ul className='flex flex-col gap-1 text-gray-600'>
                        <li>+1-212-456-7890</li>
                        <li>contact@forever.com</li>
                    </ul>
                </div>

            </div>

            <div>
                <hr />
                <p className='py-5 text-sm text-center'>Bản quyền 2024@ p-shop.com - Đã đăng ký bản quyền.</p>
            </div>

        </div>
    )
}

export default Footer
