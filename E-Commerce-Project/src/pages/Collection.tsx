import { useContext, useEffect, useMemo, useState } from 'react';
import { ShopContext } from '../context/ShopContext';
import ProductItem from '../components/ProductItem';
import Title from '../components/Title';
import { apiBaseUrl, apiRequest } from '../lib/api';
import { mapProductDetail, mapProductSummary } from '../lib/productMapper';
import type { ApiResponse, BackendProductDetail, BackendProductSummary, PageResponse, Product } from '../types/shop';

type CategoryOption = {
    name?: string;
    categoryCode?: string;
    code?: string;
};

type PriceRange = {
    label: string;
    min?: number;
    max?: number;
};

const priceRanges: PriceRange[] = [
    { label: 'Tất cả' },
    { label: '0 - 200.000đ', min: 0, max: 200000 },
    { label: '200.000đ - 300.000đ', min: 200000, max: 300000 },
    { label: '300.000đ - 500.000đ', min: 300000, max: 500000 },
    { label: '> 500.000đ', min: 500000 },
];

const sortOptions = [
    { label: 'Mới nhất', value: 'createdAt,desc' },
    { label: 'Giá tăng dần', value: 'price,asc' },
    { label: 'Giá giảm dần', value: 'price,desc' },
    { label: 'Đánh giá cao', value: 'rated,desc' },
];

const Collection = () => {
    const context = useContext(ShopContext);
    const fallbackProducts = (context?.products || []) as Product[];
    const globalSearch = (context?.search || '').trim();

    const [products, setProducts] = useState<Product[]>([]);
    const [categories, setCategories] = useState<CategoryOption[]>([]);
    const [keywordInput, setKeywordInput] = useState(globalSearch);
    const [keyword, setKeyword] = useState(globalSearch);
    const [categoryCode, setCategoryCode] = useState('');
    const [priceRangeIndex, setPriceRangeIndex] = useState(0);
    const [sort, setSort] = useState('createdAt,desc');
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(12);
    const [totalElements, setTotalElements] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(false);
    const [showFilters, setShowFilters] = useState(false);

    const fallbackCategories = useMemo(() => {
        const unique = new Map<string, CategoryOption>();
        fallbackProducts.forEach((product) => {
            const code = product.categoryCode || product.category;
            if (code && !unique.has(code)) {
                unique.set(code, { name: product.category || code, categoryCode: code });
            }
        });
        return Array.from(unique.values());
    }, [fallbackProducts]);

    const applyFallbackFilters = () => {
        const range = priceRanges[priceRangeIndex];
        const normalizedKeyword = keyword.trim().toLowerCase();

        let nextProducts = fallbackProducts.filter((product) => {
            const matchesKeyword = !normalizedKeyword ||
                product.name.toLowerCase().includes(normalizedKeyword) ||
                product.description.toLowerCase().includes(normalizedKeyword) ||
                product.category.toLowerCase().includes(normalizedKeyword);
            const matchesCategory = !categoryCode || product.categoryCode === categoryCode || product.category === categoryCode;
            const matchesMin = range.min === undefined || product.price >= range.min;
            const matchesMax = range.max === undefined || product.price <= range.max;

            return matchesKeyword && matchesCategory && matchesMin && matchesMax;
        });

        nextProducts = [...nextProducts].sort((a, b) => {
            if (sort === 'price,asc') return a.price - b.price;
            if (sort === 'price,desc') return b.price - a.price;
            return b.date - a.date;
        });

        const start = page * pageSize;
        setProducts(nextProducts.slice(start, start + pageSize));
        setTotalElements(nextProducts.length);
        setTotalPages(Math.ceil(nextProducts.length / pageSize));
    };

    const fetchCategories = async () => {
        if (!apiBaseUrl) {
            setCategories(fallbackCategories);
            return;
        }

        try {
            const response = await apiRequest<ApiResponse<CategoryOption[]>>('/v1/api/public/category/get-all', {
                method: 'GET',
            });
            setCategories(Array.isArray(response.data) ? response.data : fallbackCategories);
        } catch (error) {
            console.error(error);
            setCategories(fallbackCategories);
        }
    };

    const fetchProducts = async () => {
        if (!apiBaseUrl) {
            applyFallbackFilters();
            return;
        }

        setLoading(true);
        try {
            const range = priceRanges[priceRangeIndex];
            const params = new URLSearchParams({
                page: String(page),
                size: String(pageSize),
                sort,
            });

            if (categoryCode) params.set('category_code', categoryCode);
            if (range.min !== undefined) params.set('min_price', String(range.min));
            if (range.max !== undefined) params.set('max_price', String(range.max));
            if (keyword.trim()) params.set('keyword', keyword.trim());

            const response = await apiRequest<ApiResponse<PageResponse<BackendProductSummary>>>(`/v1/api/public/product/filter?${params.toString()}`, {
                method: 'GET',
            });

            const pageData = response.data;
            const summaries = pageData?.items || [];
            const productsWithDetails = await Promise.all(
                summaries.map(async (product) => {
                    try {
                        const detail = await apiRequest<ApiResponse<BackendProductDetail>>(`/v1/api/public/product/get-by-code?code=${encodeURIComponent(product.code)}`, {
                            method: 'GET',
                        });
                        return detail.data ? mapProductDetail(detail.data) : mapProductSummary(product);
                    } catch (error) {
                        console.error(error);
                        return mapProductSummary(product);
                    }
                })
            );

            setProducts(productsWithDetails);
            setTotalElements(pageData?.totalElements || 0);
            setTotalPages(pageData?.totalPages || 0);
        } catch (error) {
            console.error(error);
            applyFallbackFilters();
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCategories();
    }, [fallbackCategories]);

    useEffect(() => {
        setKeywordInput(globalSearch);
        setKeyword(globalSearch);
        setPage(0);
    }, [globalSearch]);

    useEffect(() => {
        fetchProducts();
    }, [categoryCode, keyword, page, pageSize, priceRangeIndex, sort, fallbackProducts]);

    const submitKeyword = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setPage(0);
        setKeyword(keywordInput.trim());
    };

    const resetFilters = () => {
        setKeywordInput('');
        setKeyword('');
        setCategoryCode('');
        setPriceRangeIndex(0);
        setSort('createdAt,desc');
        setPage(0);
    };

    const categoryOptions = categories.length ? categories : fallbackCategories;

    const filterPanel = (
        <aside className='border bg-white p-5'>
            <div className='flex items-center justify-between border-b border-gray-100 pb-4'>
                <div>
                    <p className='text-lg font-medium text-gray-900'>Bộ lọc</p>
                    <p className='mt-1 text-sm text-gray-500'>{totalElements} kết quả</p>
                </div>
                <button type='button' onClick={resetFilters} className='text-xs font-medium text-gray-500 hover:text-black'>
                    Reset
                </button>
            </div>

            <form onSubmit={submitKeyword} className='mt-5'>
                <p className='mb-2 text-xs uppercase tracking-[0.16em] text-gray-400'>Từ khóa</p>
                <div className='flex gap-2'>
                    <input
                        value={keywordInput}
                        onChange={(event) => setKeywordInput(event.target.value)}
                        className='w-full border border-gray-300 px-3 py-2 text-sm outline-none focus:border-black'
                        placeholder='Tên hoặc mô tả'
                    />
                    <button type='submit' className='bg-black px-4 py-2 text-xs font-medium text-white'>
                        Tìm
                    </button>
                </div>
            </form>

            <div className='mt-6'>
                <p className='mb-2 text-xs uppercase tracking-[0.16em] text-gray-400'>Danh mục</p>
                <select
                    value={categoryCode}
                    onChange={(event) => {
                        setCategoryCode(event.target.value);
                        setPage(0);
                    }}
                    className='w-full border border-gray-300 px-3 py-2 text-sm outline-none focus:border-black'
                >
                    <option value=''>Tất cả danh mục</option>
                    {categoryOptions.map((category, index) => {
                        const code = category.categoryCode || category.code || '';
                        return code ? <option key={`${code}-${index}`} value={code}>{category.name || code}</option> : null;
                    })}
                </select>
            </div>

            <div className='mt-6'>
                <p className='mb-3 text-xs uppercase tracking-[0.16em] text-gray-400'>Giá</p>
                <div className='space-y-2'>
                    {priceRanges.map((range, index) => (
                        <label key={range.label} className='flex cursor-pointer items-center gap-3 text-sm text-gray-700'>
                            <input
                                type='radio'
                                name='priceRange'
                                checked={priceRangeIndex === index}
                                onChange={() => {
                                    setPriceRangeIndex(index);
                                    setPage(0);
                                }}
                                className='accent-black'
                            />
                            <span>{range.label}</span>
                        </label>
                    ))}
                </div>
            </div>

            <div className='mt-6'>
                <p className='mb-2 text-xs uppercase tracking-[0.16em] text-gray-400'>Sắp xếp</p>
                <select
                    value={sort}
                    onChange={(event) => {
                        setSort(event.target.value);
                        setPage(0);
                    }}
                    className='w-full border border-gray-300 px-3 py-2 text-sm outline-none focus:border-black'
                >
                    {sortOptions.map((option) => (
                        <option key={option.value} value={option.value}>{option.label}</option>
                    ))}
                </select>
            </div>
        </aside>
    );

    return (
        <div className='border-t pt-10'>
            <div className='flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between mb-6'>
                <div className='text-base sm:text-2xl'>
                    <Title text1='TẤT CẢ' text2='SẢN PHẨM' />
                </div>
                <button
                    type='button'
                    onClick={() => setShowFilters((current) => !current)}
                    className='border border-black px-4 py-2 text-sm font-medium sm:hidden'
                >
                    {showFilters ? 'Ẩn bộ lọc' : 'Hiện bộ lọc'}
                </button>
            </div>

            <div className='grid gap-8 lg:grid-cols-[280px_1fr]'>
                <div className={`${showFilters ? 'block' : 'hidden'} lg:block`}>
                    {filterPanel}
                </div>

                <section>
                    <div className='mb-5 flex flex-col gap-3 border bg-white px-4 py-3 sm:flex-row sm:items-center sm:justify-between'>
                        <p className='text-sm text-gray-500'>
                            {loading ? 'Đang tải sản phẩm...' : `${totalElements} sản phẩm`}
                        </p>
                        <div className='flex items-center gap-2'>
                            <span className='text-sm text-gray-500'>Hiển thị</span>
                            <select
                                value={pageSize}
                                onChange={(event) => {
                                    setPageSize(Number(event.target.value));
                                    setPage(0);
                                }}
                                className='border border-gray-300 px-3 py-2 text-sm outline-none focus:border-black'
                            >
                                <option value={8}>8</option>
                                <option value={12}>12</option>
                                <option value={20}>20</option>
                            </select>
                        </div>
                    </div>

                    {products.length === 0 ? (
                        <p className='py-16 text-center text-gray-500'>
                            {loading ? 'Đang tải sản phẩm...' : 'Không tìm thấy sản phẩm.'}
                        </p>
                    ) : (
                        <div className='grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-4 gap-y-6'>
                            {products.map((item) => (
                                <ProductItem
                                    key={item._id}
                                    id={item._id}
                                    image={item.image}
                                    name={item.name}
                                    price={item.price}
                                    originalPrice={item.originalPrice}
                                    sizes={item.sizes}
                                />
                            ))}
                        </div>
                    )}

                    <div className='mt-8 flex flex-col gap-3 border-t border-gray-100 pt-5 sm:flex-row sm:items-center sm:justify-between'>
                        <p className='text-sm text-gray-500'>
                            Trang {totalPages ? page + 1 : 0}/{totalPages}
                        </p>
                        <div className='flex gap-2'>
                            <button
                                type='button'
                                onClick={() => setPage((current) => Math.max(current - 1, 0))}
                                disabled={page === 0 || loading}
                                className='border border-black px-4 py-2 text-sm font-medium hover:bg-black hover:text-white disabled:border-gray-300 disabled:text-gray-300'
                            >
                                Trước
                            </button>
                            <button
                                type='button'
                                onClick={() => setPage((current) => Math.min(current + 1, Math.max(totalPages - 1, 0)))}
                                disabled={page >= totalPages - 1 || totalPages === 0 || loading}
                                className='border border-black px-4 py-2 text-sm font-medium hover:bg-black hover:text-white disabled:border-gray-300 disabled:text-gray-300'
                            >
                                Sau
                            </button>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    );
};

export default Collection;
