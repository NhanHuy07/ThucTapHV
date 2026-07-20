import { useContext, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { ShopContext } from '../context/ShopContext';
import { assets } from '../assets/assets';
import type { Product as ProductType, ProductComment } from '../types/shop';
import RelatedProducts from '../components/RelatedProducts'; // RelatedProducts is already converted
import { formatCurrency } from '../lib/format';
import { getProductComments } from '../lib/commentApi';
import { apiBaseUrl, apiRequest } from '../lib/api';
import { mapProductDetail } from '../lib/productMapper';
import type { ApiResponse, BackendProductDetail } from '../types/shop';

const Product = () => {
    const params = useParams(); // useParams returns a string based on route definitions
    const productId = params.productId;

    const context = useContext(ShopContext);
    const products = (context?.products || []) as ProductType[];
    const addToCart = context?.addToCart; // Safe access

    const [productData, setProductData] = useState<ProductType | null>(null);
    const [image, setImage] = useState('');
    const [size, setSize] = useState('');
    const [comments, setComments] = useState<ProductComment[]>([]);
    const [commentPage, setCommentPage] = useState(0);
    const [commentTotalPages, setCommentTotalPages] = useState(0);
    const [commentTotalElements, setCommentTotalElements] = useState(0);
    const [loadingComments, setLoadingComments] = useState(false);

    useEffect(() => {
        let cancelled = false;

        const fetchProductData = async () => {
            if (!productId) return;

            const product = products.find((item) => item._id === productId);
            if (product) {
                setProductData(product);
                setImage(product.image[0]);
                return;
            }

            if (!apiBaseUrl) {
                setProductData(null);
                setImage('');
                return;
            }

            try {
                const response = await apiRequest<ApiResponse<BackendProductDetail>>(`/v1/api/public/product/get-by-code?code=${encodeURIComponent(productId)}`, {
                    method: 'GET',
                });

                if (!cancelled && response.data) {
                    const nextProduct = mapProductDetail(response.data);
                    setProductData(nextProduct);
                    setImage(nextProduct.image[0]);
                }
            } catch (error) {
                console.error(error);
                if (!cancelled) {
                    setProductData(null);
                    setImage('');
                }
            }
        };

        fetchProductData();

        return () => {
            cancelled = true;
        };
    }, [productId, products]);

    useEffect(() => {
        const loadComments = async () => {
            if (!productData?._id) return;

            setLoadingComments(true);
            try {
                const response = await getProductComments(productData._id, commentPage, 5);
                setComments(response.data?.items || []);
                setCommentTotalPages(response.data?.totalPages || 0);
                setCommentTotalElements(response.data?.totalElements || 0);
            } catch (error) {
                console.error(error);
                setComments([]);
                setCommentTotalPages(0);
                setCommentTotalElements(0);
            } finally {
                setLoadingComments(false);
            }
        };

        loadComments();
    }, [commentPage, productData?._id]);

    useEffect(() => {
        setCommentPage(0);
    }, [productData?._id]);

    const renderRating = (rating?: number) => (
        <div className='flex items-center gap-1'>
            {Array.from({ length: 5 }).map((_, index) => (
                <img
                    key={index}
                    src={index < Math.round(rating || 0) ? assets.star_icon : assets.star_dull_icon}
                    className='w-3.5'
                    alt=''
                />
            ))}
        </div>
    );

    return productData ? (
        <div className="border-t-2 pt-10 transition-opacity ease-in duration-500 opacity-100">
            {/* Product Section */}
            <div className="flex flex-col sm:flex-row gap-12">
                {/* Left Section: Images */}
                <div className="flex-1 flex flex-col sm:flex-row gap-3">
                    {/* Thumbnails */}
                    <div className="flex sm:flex-col overflow-x-auto sm:overflow-y-auto sm:w-[20%] w-full gap-2">
                        {productData.image.map((item, index) => (
                            <img
                                onClick={() => setImage(item)}
                                src={item}
                                key={index}
                                className={`w-24 h-24 object-cover cursor-pointer border ${image === item ? 'border-orange-500' : 'border-gray-200'
                                    }`}
                                alt={`Thumbnail ${index + 1}`}
                            />
                        ))}
                    </div>
                    {/* Main Image */}
                    <div className="w-full sm:w-[80%]">
                        <img src={image} className="w-full h-auto border border-gray-200" alt={productData.name} />
                    </div>
                </div>

                {/* Right Section: Product Details */}
                <div className="flex-1">
                    <h1 className="font-medium text-2xl mt-2">{productData.name}</h1>
                    <div className="flex items-center gap-1 mt-2">
                        <img src={assets.star_icon} className="w-3.5" alt="Star" />
                        <img src={assets.star_icon} className="w-3.5" alt="Star" />
                        <img src={assets.star_icon} className="w-3.5" alt="Star" />
                        <img src={assets.star_icon} className="w-3.5" alt="Star" />
                        <img src={assets.star_dull_icon} className="w-3.5" alt="Dull Star" />
                        <p className="pl-2">122</p>
                    </div>
                    {productData.originalPrice && productData.originalPrice > productData.price ? (
                        <div className="flex items-center gap-3 mt-5">
                            <p className="text-3xl font-bold text-red-600">
                                {formatCurrency(productData.price)}
                            </p>
                            <p className="text-lg text-gray-400 line-through">
                                {formatCurrency(productData.originalPrice)}
                            </p>
                            <span className="text-xs font-bold text-white bg-red-500 px-2.5 py-1 rounded-sm">
                                GIẢM {Math.round(((productData.originalPrice - productData.price) / productData.originalPrice) * 100)}%
                            </span>
                        </div>
                    ) : (
                        <p className="mt-5 text-3xl font-medium text-black">
                            {formatCurrency(productData.price)}
                        </p>
                    )}
                    <p className="mt-5 text-gray-500">{productData.description}</p>
                    <div className="flex flex-col gap-4 my-8">
                        <p>Chọn size</p>
                        <div className="flex gap-2">
                            {productData.sizes.length > 0
                                ? productData.sizes.map((item, index) => (
                                    <button
                                        onClick={() => setSize(item)}
                                        key={index}
                                        className={`bg-gray-100 py-2 px-4 border ${item === size ? 'border-orange-500' : ''
                                            }`}
                                    >
                                        {item}
                                    </button>
                                ))
                                : <p className="text-sm text-gray-500">Hết hàng</p>
                            }
                        </div>
                    </div>
                    <button
                        onClick={() => addToCart && addToCart(productData._id, size)} // Safe call
                        disabled={productData.sizes.length === 0}
                        className="bg-black text-white px-8 py-3 text-sm active:bg-gray-700 disabled:bg-gray-300 disabled:cursor-not-allowed"
                    >
                        {productData.sizes.length > 0 ? 'THÊM VÀO GIỎ' : 'TẠM HẾT HÀNG'}
                    </button>
                    <hr className="mt-8 sm:w-4/5" />
                    <div className="text-sm text-gray-500 mt-5 flex flex-col gap-1">
                        <p>Sản phẩm chính hãng 100%.</p>
                        <p>Hỗ trợ thanh toán khi nhận hàng.</p>
                        <p>Dễ dàng đổi trả trong vòng 7 ngày.</p>
                    </div>
                </div>
            </div>

            {/* Description and Reviews */}
            <div className="mt-20">
                <div className="flex">
                    <b className="border px-5 py-3 text-sm">Mô tả</b>
                    <p className="border px-5 py-3 text-sm">Đánh giá ({commentTotalElements})</p>
                </div>
                <div className="flex flex-col gap-4 border px-6 py-6 text-sm text-gray-500">
                    <p>
                        Sản phẩm được chọn lọc nhằm mang lại trải nghiệm mua sắm tiện lợi,
                        dễ phối đồ và phù hợp với nhu cầu sử dụng hằng ngày.
                    </p>
                    <p>
                        Vui lòng chọn đúng size trước khi thêm vào giỏ hàng để hệ thống kiểm tra tồn kho chính xác.
                    </p>
                </div>
                <div className='border border-t-0 px-6 py-6'>
                    <div className='flex items-center justify-between gap-4'>
                        <p className='text-sm font-medium text-gray-900'>Đánh giá từ khách hàng</p>
                        <p className='text-xs text-gray-500'>Trang {commentTotalPages ? commentPage + 1 : 0}/{commentTotalPages}</p>
                    </div>

                    {loadingComments ? (
                        <p className='py-8 text-center text-sm text-gray-500'>Đang tải đánh giá...</p>
                    ) : comments.length === 0 ? (
                        <p className='py-8 text-center text-sm text-gray-500'>Sản phẩm chưa có đánh giá.</p>
                    ) : (
                        <div className='mt-4 divide-y divide-gray-100'>
                            {comments.map((comment, index) => (
                                <div key={`${comment.productCode}-${comment.createdAt || index}`} className='py-4'>
                                    <div className='flex items-center justify-between gap-3'>
                                        {renderRating(comment.rating)}
                                        <span className='text-xs text-gray-400'>
                                            {comment.createdAt ? new Intl.DateTimeFormat('vi-VN', { dateStyle: 'medium' }).format(new Date(comment.createdAt)) : ''}
                                        </span>
                                    </div>
                                    <p className='mt-2 text-sm leading-6 text-gray-600'>{comment.content}</p>
                                </div>
                            ))}
                        </div>
                    )}

                    <div className='mt-5 flex justify-end gap-2'>
                        <button
                            type='button'
                            onClick={() => setCommentPage((current) => Math.max(current - 1, 0))}
                            disabled={commentPage === 0 || loadingComments}
                            className='border border-gray-300 px-4 py-2 text-xs font-medium hover:border-black disabled:text-gray-300'
                        >
                            Trước
                        </button>
                        <button
                            type='button'
                            onClick={() => setCommentPage((current) => Math.min(current + 1, Math.max(commentTotalPages - 1, 0)))}
                            disabled={commentPage >= commentTotalPages - 1 || commentTotalPages === 0 || loadingComments}
                            className='border border-gray-300 px-4 py-2 text-xs font-medium hover:border-black disabled:text-gray-300'
                        >
                            Sau
                        </button>
                    </div>
                </div>
            </div>

            {/* Related Products Section */}
            <RelatedProducts
                category={productData.category}
                subCategory={productData.subCategory}
            />
        </div>
    ) : (
        <div className="opacity-0"></div>
    );
};

export default Product;
