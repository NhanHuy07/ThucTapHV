import { useContext, useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import Title from '../components/Title';
import { ShopContext } from '../context/ShopContext';
import { apiRequest } from '../lib/api';
import { formatCurrency } from '../lib/format';
import type { ApiResponse, BackendProductDetail, BackendProductSummary, OrderDetail, OrderStatus, OrderSummary, PageResponse, Product } from '../types/shop';
import AdminUserManagement from '../components/AdminUserManagement';

type AdminTab = 'orders' | 'vouchers' | 'products' | 'providers' | 'promotions' | 'users';
type DiscountType = 'PERCENT' | 'FIXED';
type VoucherType = 'NEWBIE' | 'GLOBAL';
type ProductSize = 'S' | 'M' | 'L' | 'XL' | 'XXL' | 'XXXL';
type ItemStatus = 'AVAILABLE' | 'OUT_OF_STOCK' | 'DISCONTINUED';

type PromotionScope = 'GLOBAL' | 'PRODUCT' | 'CATEGORY' | 'PROVIDER';
type PromotionStatus = 'SCHEDULED' | 'ACTIVE' | 'ENDED' | 'DELETED';

type PromotionSummary = {
    id: number;
    value: number;
    scope: PromotionScope;
    startAt: string;
    endAt: string;
    priority: number;
    status: PromotionStatus;
};

type PromotionForm = {
    value: string;
    scope: PromotionScope;
    startAt: string;
    endAt: string;
    priority: string;
    productCodes: string;
    categoryCodes: string;
};

type RankTypeOption = 'Bronze' | 'Silver' | 'Gold' | 'Platinum' | 'Diamond' | 'Ultimate';

type VoucherForm = {
    code: string;
    discountType: DiscountType;
    voucherType: VoucherType;
    rankType: RankTypeOption;
    value: string;
    minOrderAmount: string;
    startAt: string;
    endAt: string;
};

type CreatedVoucher = {
    code: string;
    discountType: DiscountType;
    voucherType: VoucherType;
    rankType: RankTypeOption;
    value: number;
    minOrderAmount: number;
    status: string;
    startAt: string;
    endAt: string;
    createdAt: string;
};

type ProviderOption = {
    name?: string;
    providerCode?: string;
    code?: string;
    email?: string;
    phone?: string;
    description?: string;
    img?: string;
};

type CategoryOption = {
    name?: string;
    categoryCode?: string;
    code?: string;
};

type ProviderForm = {
    name: string;
    providerCode: string;
    email: string;
    phone: string;
    description: string;
    img: string;
};

type ProductForm = {
    productCode: string;
    categoryCode: string;
    providerCode: string;
    name: string;
    description: string;
    price: string;
    sold: string;
    rate: string;
    videoUrl: string;
    imgUrl: string;
};

type CategoryForm = {
    name: string;
    code: string;
    description: string;
    parentCode: string;
    imgUrl: string;
};

type ItemRow = {
    size: ProductSize;
    status: ItemStatus;
    quantity: string;
};

type ProductAdminModal = 'create-product' | 'edit-product' | 'items' | 'category' | null;

type AdminProductRow = Product & {
    totalQuantity: number;
    stockSummary: string;
    items?: BackendProductDetail['items'];
    status?: string;
    sold?: number;
    rating?: number;
};

const orderStatuses: OrderStatus[] = [
    'UNPAID',
    'PAID',
    'PENDING',
    'CANCELLED',
    'CONFIRMED',
    'SHIPPING',
    'DELIVERED',
    'COMPLETED',
    'RETURNED',
];

const statusLabel: Record<OrderStatus, string> = {
    UNPAID: 'Chưa thanh toán',
    PAID: 'Đã thanh toán',
    PENDING: 'Đang xử lý',
    CANCELLED: 'Đã hủy',
    CONFIRMED: 'Đã xác nhận',
    SHIPPING: 'Đang giao',
    DELIVERED: 'Đã giao',
    COMPLETED: 'Hoàn tất',
    RETURNED: 'Đã trả hàng',
};

const emptyVoucherForm: VoucherForm = {
    code: '',
    discountType: 'PERCENT',
    voucherType: 'GLOBAL',
    rankType: 'Bronze',
    value: '',
    minOrderAmount: '',
    startAt: '',
    endAt: '',
};

const createdVouchersStorageKey = 'admin-created-vouchers';

const emptyPromotionForm: PromotionForm = {
    value: '',
    scope: 'GLOBAL',
    startAt: '',
    endAt: '',
    priority: '1',
    productCodes: '',
    categoryCodes: '',
};

const emptyProviderForm: ProviderForm = {
    name: '',
    providerCode: '',
    email: '',
    phone: '',
    description: '',
    img: '',
};

const emptyProductForm: ProductForm = {
    productCode: '',
    categoryCode: '',
    providerCode: '',
    name: '',
    description: '',
    price: '',
    sold: '',
    rate: '',
    videoUrl: '',
    imgUrl: '',
};

const emptyCategoryForm: CategoryForm = {
    name: '',
    code: '',
    description: '',
    parentCode: '',
    imgUrl: '',
};

const productSizes: ProductSize[] = ['S', 'M', 'L', 'XL', 'XXL', 'XXXL'];
const itemStatuses: ItemStatus[] = ['AVAILABLE', 'OUT_OF_STOCK', 'DISCONTINUED'];

const emptyItemRow = (): ItemRow => ({
    size: 'M',
    status: 'AVAILABLE',
    quantity: '0',
});

const formatDate = (date?: string) =>
    date ? new Intl.DateTimeFormat('vi-VN', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(date)) : 'Không có';

const Admin = () => {
    const context = useContext(ShopContext);
    const token = context?.token as string;
    const isAdmin = Boolean(context?.isAdmin);
    const products = (context?.products || []) as Product[];

    const [activeTab, setActiveTab] = useState<AdminTab>('orders');
    const [orders, setOrders] = useState<OrderSummary[]>([]);
    const [orderStatusFilter, setOrderStatusFilter] = useState<OrderStatus | 'ALL'>('ALL');
    const [orderCode, setOrderCode] = useState('');
    const [orderDetail, setOrderDetail] = useState<OrderDetail | null>(null);
    const [loadingOrders, setLoadingOrders] = useState(false);
    const [updatingOrder, setUpdatingOrder] = useState(false);

    const [voucherForm, setVoucherForm] = useState<VoucherForm>(emptyVoucherForm);
    const [createdVouchers, setCreatedVouchers] = useState<CreatedVoucher[]>(() => {
        try {
            const stored = localStorage.getItem(createdVouchersStorageKey);
            const parsed = stored ? JSON.parse(stored) : [];
            return Array.isArray(parsed) ? parsed : [];
        } catch {
            return [];
        }
    });
    const [creatingVoucher, setCreatingVoucher] = useState(false);

    // States for Promotions
    const [promotions, setPromotions] = useState<PromotionSummary[]>([]);
    const [loadingPromotions, setLoadingPromotions] = useState(false);
    const [creatingPromotion, setCreatingPromotion] = useState(false);
    const [promotionForm, setPromotionForm] = useState<PromotionForm>(emptyPromotionForm);
    const [promotionPage, setPromotionPage] = useState(0);
    const [promotionTotalPages, setPromotionTotalPages] = useState(0);

    const [providers, setProviders] = useState<ProviderOption[]>([]);
    const [categories, setCategories] = useState<CategoryOption[]>([]);
    const [providerForm, setProviderForm] = useState<ProviderForm>(emptyProviderForm);
    const [providerUpdateCode, setProviderUpdateCode] = useState('');
    const [editingProviderCode, setEditingProviderCode] = useState('');
    const [productForm, setProductForm] = useState<ProductForm>(emptyProductForm);
    const [selectedProductCode, setSelectedProductCode] = useState('');
    const [adminProducts, setAdminProducts] = useState<AdminProductRow[]>([]);
    const [productSearchInput, setProductSearchInput] = useState('');
    const [productSearchKeyword, setProductSearchKeyword] = useState('');
    const [productPage, setProductPage] = useState(0);
    const [productPageSize, setProductPageSize] = useState(10);
    const [productTotalPages, setProductTotalPages] = useState(0);
    const [productTotalElements, setProductTotalElements] = useState(0);
    const [loadingAdminProducts, setLoadingAdminProducts] = useState(false);
    const [productModal, setProductModal] = useState<ProductAdminModal>(null);
    const [categoryForm, setCategoryForm] = useState<CategoryForm>(emptyCategoryForm);
    const [itemProductCode, setItemProductCode] = useState('');
    const [itemRows, setItemRows] = useState<ItemRow[]>([emptyItemRow()]);
    const [submittingProductAdmin, setSubmittingProductAdmin] = useState(false);
    const [loadingProductMeta, setLoadingProductMeta] = useState(false);

    const fetchOrders = async (status = orderStatusFilter) => {
        if (!token) return;

        setLoadingOrders(true);
        try {
            const endpoint = status === 'ALL'
                ? '/v1/api/admin/order/get/all?page=0&size=20&sort=createdAt,desc'
                : `/v1/api/admin/order/get/by-status?status=${encodeURIComponent(status)}&page=0&size=20&sort=createdAt,desc`;

            const response = await apiRequest<ApiResponse<PageResponse<OrderSummary>>>(endpoint, {
                method: 'GET',
                token,
            });

            setOrders(response.data?.items || []);
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể tải danh sách đơn hàng admin';
            toast.error(message);
        } finally {
            setLoadingOrders(false);
        }
    };

    useEffect(() => {
        if (token) {
            fetchOrders('ALL');
        }
    }, [token]);

    const fetchProductMeta = async () => {
        setLoadingProductMeta(true);
        try {
            const [providerResponse, categoryResponse] = await Promise.all([
                apiRequest<ApiResponse<ProviderOption[]>>('/v1/api/public/provider/gets', { method: 'GET' }),
                apiRequest<ApiResponse<CategoryOption[]>>('/v1/api/public/category/get-all', { method: 'GET' }),
            ]);

            setProviders(Array.isArray(providerResponse.data) ? providerResponse.data : []);
            setCategories(Array.isArray(categoryResponse.data) ? categoryResponse.data : []);
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Khong the tai danh sach nha cung cap/danh muc';
            toast.error(message);
        } finally {
            setLoadingProductMeta(false);
        }
    };

    const fetchAdminProducts = async () => {
        setLoadingAdminProducts(true);
        try {
            const query = new URLSearchParams({
                page: String(productPage),
                size: String(productPageSize),
                sort: 'createdAt,desc',
            });

            let endpoint = `/v1/api/public/product/get-all?${query.toString()}`;
            if (productSearchKeyword.trim()) {
                query.set('keyword', productSearchKeyword.trim());
                endpoint = `/v1/api/public/product/search?${query.toString()}`;
            }

            const response = await apiRequest<ApiResponse<PageResponse<BackendProductSummary>>>(endpoint, {
                method: 'GET',
            });

            const pageData = response.data;
            const summaries = pageData?.items || [];
            const rows = await Promise.all(
                summaries.map(async (summary) => {
                    try {
                        const detailResponse = await apiRequest<ApiResponse<BackendProductDetail>>(`/v1/api/public/product/get-by-code?code=${encodeURIComponent(summary.code)}`, {
                            method: 'GET',
                        });
                        const detail = detailResponse.data;
                        const stats = detail as BackendProductDetail & { sold?: number; rating?: number; rated?: number };
                        const totalQuantity = detail.items?.reduce((total, item) => total + (item.quantity || 0), 0) || 0;
                        const stockSummary = detail.items?.length
                            ? detail.items.map((item) => `${item.size}: ${item.quantity ?? 0}`).join(', ')
                            : 'Chưa có size';

                        return {
                            _id: detail.productCode || summary.code,
                            code: detail.productCode || summary.code,
                            name: detail.name || summary.name,
                            description: detail.description || '',
                            price: detail.finalPrice ?? detail.originalPrice ?? summary.price ?? 0,
                            originalPrice: detail.originalPrice ?? summary.originalPrice,
                            finalPrice: detail.finalPrice ?? summary.price,
                            image: [detail.imgUrl || summary.imgUrl || ''],
                            category: detail.category?.name || detail.category?.categoryCode || summary.categoryCode || '',
                            categoryCode: detail.category?.categoryCode || summary.categoryCode,
                            subCategory: '',
                            providerCode: detail.provider?.providerCode,
                            sizes: detail.items?.map((item) => item.size) || [],
                            date: detail.createdAt ? new Date(detail.createdAt).getTime() : Date.now(),
                            bestSeller: false,
                            totalQuantity,
                            stockSummary,
                            items: detail.items,
                            status: detail.status,
                            sold: stats.sold,
                            rating: stats.rating ?? stats.rated,
                        } satisfies AdminProductRow;
                    } catch (error) {
                        console.error(error);
                        return {
                            _id: summary.code,
                            code: summary.code,
                            name: summary.name,
                            description: '',
                            price: summary.price ?? summary.originalPrice ?? 0,
                            originalPrice: summary.originalPrice,
                            finalPrice: summary.price,
                            image: [summary.imgUrl || ''],
                            category: summary.categoryCode || '',
                            categoryCode: summary.categoryCode,
                            subCategory: '',
                            sizes: [],
                            date: Date.now(),
                            bestSeller: false,
                            totalQuantity: 0,
                            stockSummary: 'Chưa tải được tồn kho',
                        } satisfies AdminProductRow;
                    }
                })
            );

            setAdminProducts(rows);
            setProductTotalPages(pageData?.totalPages || 0);
            setProductTotalElements(pageData?.totalElements || 0);
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể tải danh sách sản phẩm';
            toast.error(message);
        } finally {
            setLoadingAdminProducts(false);
        }
    };

    useEffect(() => {
        if (token && isAdmin && (activeTab === 'products' || activeTab === 'providers')) {
            fetchProductMeta();
        }
    }, [token, isAdmin, activeTab]);

    useEffect(() => {
        if (token && isAdmin && activeTab === 'products') {
            fetchAdminProducts();
        }
    }, [token, isAdmin, activeTab, productPage, productPageSize, productSearchKeyword]);

    useEffect(() => {
        if (token && isAdmin && activeTab === 'promotions') {
            fetchPromotions();
        }
    }, [token, isAdmin, activeTab, promotionPage]);

    const fetchOrderDetail = async (code?: string) => {
        const targetCode = (code || orderCode).trim();
        if (!targetCode) {
            toast.error('Vui lòng nhập mã đơn hàng');
            return;
        }

        setLoadingOrders(true);
        try {
            const response = await apiRequest<ApiResponse<OrderDetail>>(`/v1/api/admin/order/get/${encodeURIComponent(targetCode)}`, {
                method: 'GET',
                token,
            });

            setOrderDetail(response.data);
            setOrderCode(response.data?.orderCode || targetCode);
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể tải chi tiết đơn hàng';
            toast.error(message);
        } finally {
            setLoadingOrders(false);
        }
    };

    const updateOrderStatus = async (targetStatus: OrderStatus) => {
        if (!orderDetail?.orderCode && !orderCode.trim()) {
            toast.error('Chưa có mã đơn hàng để cập nhật');
            return;
        }

        setUpdatingOrder(true);
        const targetCode = orderDetail?.orderCode || orderCode.trim();
        try {
            // Trường hợp chuyển từ UNPAID sang CONFIRMED (bỏ qua PENDING trung gian)
            if (orderDetail?.status === 'UNPAID' && targetStatus === 'CONFIRMED') {
                toast.info('Đang xử lý thanh toán và phê duyệt đơn hàng...');
                try {
                    // Thử chuyển thẳng UNPAID -> CONFIRMED
                    await apiRequest('/v1/api/admin/order/update/status', {
                        method: 'PUT',
                        token,
                        body: {
                            orderCode: targetCode,
                            status: 'CONFIRMED',
                        },
                    });
                } catch (err) {
                    console.log('Không thể chuyển thẳng, đang tự động chuyển qua PENDING...', err);
                    // Fallback: Chuyển UNPAID -> PENDING
                    await apiRequest('/v1/api/admin/order/update/status', {
                        method: 'PUT',
                        token,
                        body: {
                            orderCode: targetCode,
                            status: 'PENDING',
                        },
                    });
                    // Sau đó chuyển PENDING -> CONFIRMED
                    await apiRequest('/v1/api/admin/order/update/status', {
                        method: 'PUT',
                        token,
                        body: {
                            orderCode: targetCode,
                            status: 'CONFIRMED',
                        },
                    });
                }
            } else {
                // Các trường hợp chuyển đổi bình thường khác
                await apiRequest('/v1/api/admin/order/update/status', {
                    method: 'PUT',
                    token,
                    body: {
                        orderCode: targetCode,
                        status: targetStatus,
                    },
                });
            }

            toast.success(`Đã cập nhật đơn hàng thành: ${statusLabel[targetStatus]}`);
            await fetchOrderDetail(targetCode);
            await fetchOrders();
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể cập nhật trạng thái đơn hàng';
            toast.error(message);
        } finally {
            setUpdatingOrder(false);
        }
    };

    const createVoucher = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (!voucherForm.code.trim() || !voucherForm.value || voucherForm.minOrderAmount === '' || !voucherForm.startAt || !voucherForm.endAt) {
            toast.error('Vui lòng nhập đầy đủ mã voucher, giá trị giảm, đơn tối thiểu và thời gian áp dụng');
            return;
        }

        const startAt = new Date(voucherForm.startAt);
        const endAt = new Date(voucherForm.endAt);
        if (Number.isNaN(startAt.getTime()) || Number.isNaN(endAt.getTime())) {
            toast.error('Thoi gian voucher khong hop le');
            return;
        }

        if (endAt <= startAt) {
            toast.error('Thoi gian ket thuc phai lon hon thoi gian bat dau');
            return;
        }

        if (Number(voucherForm.value) < 1 || Number(voucherForm.minOrderAmount) < 0) {
            toast.error('Gia tri voucher phai lon hon 0 va don toi thieu khong duoc am');
            return;
        }

        setCreatingVoucher(true);
        try {
            const code = voucherForm.code.trim().toUpperCase();
            const response = await apiRequest<ApiResponse<Partial<CreatedVoucher>>>('/v1/api/admin/voucher/create', {
                method: 'POST',
                token,
                body: {
                    code,
                    discountType: voucherForm.discountType,
                    voucherType: voucherForm.voucherType,
                    rankType: voucherForm.rankType,
                    value: Number(voucherForm.value),
                    minOrderAmount: Number(voucherForm.minOrderAmount),
                    startAt: startAt.toISOString(),
                    endAt: endAt.toISOString(),
                },
            });

            const createdVoucher: CreatedVoucher = {
                code,
                discountType: response.data?.discountType || voucherForm.discountType,
                voucherType: response.data?.voucherType || voucherForm.voucherType,
                rankType: voucherForm.rankType,
                value: Number(response.data?.value ?? voucherForm.value),
                minOrderAmount: Number(response.data?.minOrderAmount ?? voucherForm.minOrderAmount),
                status: response.data?.status || (startAt > new Date() ? 'COMMING_SOON' : 'ACTIVE'),
                startAt: startAt.toISOString(),
                endAt: response.data?.endAt || endAt.toISOString(),
                createdAt: new Date().toISOString(),
            };

            setCreatedVouchers((current) => {
                const next = [createdVoucher, ...current.filter((voucher) => voucher.code !== createdVoucher.code)];
                localStorage.setItem(createdVouchersStorageKey, JSON.stringify(next));
                return next;
            });

            toast.success('Đã tạo voucher');
            setVoucherForm(emptyVoucherForm);
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể tạo voucher';
            toast.error(message);
        } finally {
            setCreatingVoucher(false);
        }
    };

    const clearCreatedVouchers = () => {
        setCreatedVouchers([]);
        localStorage.removeItem(createdVouchersStorageKey);
    };

    const fetchPromotions = async () => {
        if (!token) return;
        setLoadingPromotions(true);
        try {
            const size = 10;
            const endpoint = `/v1/api/admin/promotions/get-all?page=${promotionPage}&size=${size}`;
            const response = await apiRequest<ApiResponse<{ content: Omit<PromotionSummary, 'id' | 'scope' | 'priority' | 'status'>[], totalPages: number }>>(endpoint, {
                method: 'GET',
                token,
            });
            const pageData = response.data;
            const content = pageData?.content || [];
            
            const mappedPromotions: PromotionSummary[] = content.map((item, index) => {
                const now = new Date();
                const start = new Date(item.startAt);
                const end = new Date(item.endAt);
                let status: PromotionStatus = 'SCHEDULED';
                if (now >= start && now <= end) {
                    status = 'ACTIVE';
                } else if (now > end) {
                    status = 'ENDED';
                }

                return {
                    id: index + 1,
                    value: item.value,
                    scope: 'GLOBAL',
                    startAt: item.startAt,
                    endAt: item.endAt,
                    priority: 1,
                    status: status,
                };
            });

            setPromotions(mappedPromotions);
            setPromotionTotalPages(pageData?.totalPages || 0);
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể tải danh sách khuyến mãi';
            toast.error(message);
        } finally {
            setLoadingPromotions(false);
        }
    };

    const createPromotion = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!promotionForm.value || !promotionForm.startAt || !promotionForm.endAt) {
            toast.error('Vui lòng nhập giá trị giảm và khoảng thời gian');
            return;
        }

        setCreatingPromotion(true);
        try {
            const productIds = promotionForm.productCodes.trim()
                ? promotionForm.productCodes.split(',').map(code => code.trim()).filter(Boolean)
                : [];
            const categoryIds = promotionForm.categoryCodes.trim()
                ? promotionForm.categoryCodes.split(',').map(code => code.trim()).filter(Boolean)
                : [];

            await apiRequest('/v1/api/admin/promotions/add-new', {
                method: 'POST',
                token,
                body: {
                    value: Number(promotionForm.value),
                    scope: promotionForm.scope,
                    startAt: new Date(promotionForm.startAt).toISOString(),
                    endAt: new Date(promotionForm.endAt).toISOString(),
                    priority: Number(promotionForm.priority || 1),
                    productCodes: promotionForm.scope === 'PRODUCT' ? productIds : undefined,
                    categoryCodes: promotionForm.scope === 'CATEGORY' ? categoryIds : undefined,
                },
            });

            toast.success('Đã tạo khuyến mãi mới');
            setPromotionForm(emptyPromotionForm);
            fetchPromotions();
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể tạo khuyến mãi';
            toast.error(message);
        } finally {
            setCreatingPromotion(false);
        }
    };

    const handleUnavailableFeature = (featureName: string) => {
        toast.info(
            `Tính năng ${featureName} yêu cầu API Backend cung cấp trường ID của Khuyến mãi để định danh. Hiện tại API danh sách chỉ trả về giá trị và thời gian nên tính năng này tạm thời bị giới hạn.`,
            { autoClose: 6000 }
        );
    };

    const createProvider = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!providerForm.name.trim()) {
            toast.error('Vui lòng nhập tên nhà cung cấp');
            return;
        }

        setSubmittingProductAdmin(true);
        try {
            await apiRequest('/v1/api/admin/provider/add-new', {
                method: 'POST',
                token,
                body: {
                    name: providerForm.name.trim(),
                    providerCode: providerForm.providerCode.trim() || undefined,
                    email: providerForm.email.trim() || undefined,
                    phone: providerForm.phone.trim() || undefined,
                    description: providerForm.description.trim() || undefined,
                    img: providerForm.img.trim() || undefined,
                },
            });

            toast.success('Đã tạo nhà cung cấp');
            setProviderForm(emptyProviderForm);
            await fetchProductMeta();
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể tạo nhà cung cấp';
            toast.error(message);
        } finally {
            setSubmittingProductAdmin(false);
        }
    };

    const startEditProvider = async (provider: ProviderOption) => {
        const code = provider.providerCode || provider.code || '';
        if (!code) {
            toast.error('Không tìm thấy mã nhà cung cấp');
            return;
        }

        setSubmittingProductAdmin(true);
        try {
            let detail = provider;
            if (!provider.email && !provider.phone && !provider.description && !provider.img) {
                const response = await apiRequest<ApiResponse<ProviderOption>>(`/v1/api/public/provider/get-by-code/${encodeURIComponent(code)}`, {
                    method: 'GET',
                });
                detail = response.data || provider;
            }

            setEditingProviderCode(code);
            setProviderUpdateCode(code);
            setProviderForm({
                name: detail.name || '',
                providerCode: code,
                email: detail.email || '',
                phone: detail.phone || '',
                description: detail.description || '',
                img: detail.img || '',
            });
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể tải thông tin nhà cung cấp';
            toast.error(message);
        } finally {
            setSubmittingProductAdmin(false);
        }
    };

    const resetProviderForm = () => {
        setEditingProviderCode('');
        setProviderUpdateCode('');
        setProviderForm(emptyProviderForm);
    };

    const updateProvider = async () => {
        const code = (editingProviderCode || providerUpdateCode).trim();
        if (!code) {
            toast.error('Vui lòng nhập mã nhà cung cấp cần cập nhật');
            return;
        }

        setSubmittingProductAdmin(true);
        try {
            await apiRequest(`/v1/api/admin/provider/update/${encodeURIComponent(code)}`, {
                method: 'PUT',
                token,
                body: {
                    name: providerForm.name.trim() || undefined,
                    email: providerForm.email.trim() || undefined,
                    phone: providerForm.phone.trim() || undefined,
                    description: providerForm.description.trim() || undefined,
                    img: providerForm.img.trim() || undefined,
                },
            });

            toast.success('Đã cập nhật nhà cung cấp');
            setEditingProviderCode(code);
            setProviderUpdateCode(code);
            await fetchProductMeta();
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể cập nhật nhà cung cấp';
            toast.error(message);
        } finally {
            setSubmittingProductAdmin(false);
        }
    };

    const deleteProvider = async () => {
        const code = (editingProviderCode || providerUpdateCode).trim();
        if (!code) {
            toast.error('Vui lòng chọn nhà cung cấp cần xóa');
            return;
        }

        const confirmed = window.confirm(`Xóa nhà cung cấp "${code}"? Thao tác này không thể hoàn tác.`);
        if (!confirmed) return;

        setSubmittingProductAdmin(true);
        try {
            await apiRequest(`/v1/api/admin/provider/delete/${encodeURIComponent(code)}`, {
                method: 'DELETE',
                token,
            });

            toast.success('Đã xóa nhà cung cấp');
            resetProviderForm();
            await fetchProductMeta();
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể xóa nhà cung cấp';
            toast.error(message);
        } finally {
            setSubmittingProductAdmin(false);
        }
    };

    const createCategory = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!categoryForm.name.trim() || !categoryForm.code.trim() || !categoryForm.description.trim()) {
            toast.error('Vui lòng nhập tên, mã và mô tả danh mục');
            return;
        }

        setSubmittingProductAdmin(true);
        try {
            await apiRequest('/v1/api/admin/category/category/add-new', {
                method: 'POST',
                token,
                body: {
                    name: categoryForm.name.trim(),
                    code: categoryForm.code.trim(),
                    description: categoryForm.description.trim(),
                    parentCode: categoryForm.parentCode.trim() || undefined,
                    imgUrl: categoryForm.imgUrl.trim() || undefined,
                },
            });

            toast.success('Đã tạo danh mục');
            setCategoryForm(emptyCategoryForm);
            setProductModal(null);
            await fetchProductMeta();
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể tạo danh mục';
            toast.error(message);
        } finally {
            setSubmittingProductAdmin(false);
        }
    };

    const handleImageUpload = async (event: React.ChangeEvent<HTMLInputElement>, onSuccess: (url: string) => void) => {
        const file = event.target.files?.[0];
        if (!file) return;

        const toastId = toast.loading('Đang tải ảnh lên...');
        try {
            const formData = new FormData();
            formData.append('image', file);
            formData.append('file', file);

            const response = await apiRequest<any>('/v1/api/admin/upload/image', {
                method: 'POST',
                token,
                body: formData,
            });

            const imageUrl =
                response.data?.imageUrl ||
                response.data?.url ||
                response.data ||
                response.imageUrl ||
                response.url;

            if (typeof imageUrl !== 'string' || !imageUrl) {
                throw new Error('Định dạng phản hồi API upload ảnh không đúng');
            }

            onSuccess(imageUrl);
            toast.update(toastId, {
                render: 'Tải ảnh lên thành công',
                type: 'success',
                isLoading: false,
                autoClose: 3000,
            });
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể tải ảnh lên';
            toast.update(toastId, {
                render: message,
                type: 'error',
                isLoading: false,
                autoClose: 3000,
            });
        }
    };

    const createProduct = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!productForm.categoryCode.trim() || !productForm.providerCode.trim() || !productForm.name.trim() || !productForm.price) {
            toast.error('Vui lòng nhập danh mục, nhà cung cấp, tên và giá sản phẩm');
            return;
        }

        if (Number(productForm.price) <= 0) {
            toast.error('Gia san pham phai lon hon 0');
            return;
        }

        setSubmittingProductAdmin(true);
        try {
            await apiRequest('/v1/api/admin/product/add-new', {
                method: 'POST',
                token,
                body: {
                    categoryCode: productForm.categoryCode.trim(),
                    providerCode: productForm.providerCode.trim(),
                    name: productForm.name.trim(),
                    description: productForm.description.trim() || undefined,
                    price: Number(productForm.price),
                    imgUrl: productForm.imgUrl.trim() || undefined,
                },
            });

            toast.success('Đã tạo sản phẩm');
            setProductForm(emptyProductForm);
            setSelectedProductCode('');
            setProductModal(null);
            await fetchAdminProducts();
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể tạo sản phẩm';
            toast.error(message);
        } finally {
            setSubmittingProductAdmin(false);
        }
    };

    const startEditProduct = async (product: Product, nextModal: ProductAdminModal = 'edit-product') => {
        const code = product.code || product._id;
        if (!code) {
            toast.error('Không tìm thấy mã sản phẩm');
            return;
        }

        setSubmittingProductAdmin(true);
        try {
            const response = await apiRequest<ApiResponse<BackendProductDetail>>(`/v1/api/public/product/get-by-code?code=${encodeURIComponent(code)}`, {
                method: 'GET',
            });
            const detail = response.data;

            setSelectedProductCode(code);
            setProductForm({
                productCode: detail?.productCode || code,
                categoryCode: detail?.category?.categoryCode || product.categoryCode || '',
                providerCode: detail?.provider?.providerCode || product.providerCode || '',
                name: detail?.name || product.name || '',
                description: detail?.description || product.description || '',
                price: String(detail?.originalPrice ?? detail?.finalPrice ?? product.originalPrice ?? product.price ?? ''),
                sold: '',
                rate: '',
                videoUrl: '',
                imgUrl: detail?.imgUrl || product.image?.[0] || '',
            });
            setItemProductCode(detail?.productCode || code);
            setItemRows(
                detail?.items?.length
                    ? detail.items.map((item) => ({
                        size: item.size as ProductSize,
                        status: item.status as ItemStatus,
                        quantity: String(item.quantity ?? 0),
                    }))
                    : [emptyItemRow()]
            );
            setProductModal(nextModal);
        } catch (error) {
            console.error(error);
            setSelectedProductCode(code);
            setProductForm({
                productCode: code,
                categoryCode: product.categoryCode || '',
                providerCode: product.providerCode || '',
                name: product.name || '',
                description: product.description || '',
                price: String(product.originalPrice ?? product.price ?? ''),
                sold: '',
                rate: '',
                videoUrl: '',
                imgUrl: product.image?.[0] || '',
            });
            setItemProductCode(code);
            setItemRows([emptyItemRow()]);
            setProductModal(nextModal);
            const message = error instanceof Error ? error.message : 'Không thể tải chi tiết sản phẩm, đã dùng dữ liệu danh sách';
            toast.error(message);
        } finally {
            setSubmittingProductAdmin(false);
        }
    };

    const resetProductSelection = () => {
        setSelectedProductCode('');
        setProductForm(emptyProductForm);
        setItemProductCode('');
        setItemRows([emptyItemRow()]);
        setProductModal(null);
    };

    const updateProduct = async () => {
        const code = productForm.productCode.trim();
        if (!code) {
            toast.error('Vui lòng nhập mã sản phẩm cần cập nhật');
            return;
        }

        setSubmittingProductAdmin(true);
        try {
            await apiRequest(`/v1/api/admin/product/update?product_code=${encodeURIComponent(code)}`, {
                method: 'PUT',
                token,
                body: {
                    name: productForm.name.trim() || undefined,
                    description: productForm.description.trim() || undefined,
                    price: productForm.price ? Number(productForm.price) : undefined,
                    sold: productForm.sold ? Number(productForm.sold) : undefined,
                    rate: productForm.rate ? Number(productForm.rate) : undefined,
                    videoUrl: productForm.videoUrl.trim() || undefined,
                    imgUrl: productForm.imgUrl.trim() || undefined,
                },
            });

            toast.success('Đã cập nhật sản phẩm');
            setSelectedProductCode(code);
            setProductModal(null);
            await fetchAdminProducts();
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể cập nhật sản phẩm';
            toast.error(message);
        } finally {
            setSubmittingProductAdmin(false);
        }
    };

    const deleteProduct = async () => {
        const code = productForm.productCode.trim() || selectedProductCode.trim();
        if (!code) {
            toast.error('Vui lòng chọn sản phẩm cần xóa');
            return;
        }

        const confirmed = window.confirm(`Xóa sản phẩm "${code}"? Thao tác này không thể hoàn tác.`);
        if (!confirmed) return;

        setSubmittingProductAdmin(true);
        try {
            await apiRequest(`/v1/api/admin/product/delete/${encodeURIComponent(code)}`, {
                method: 'DELETE',
                token,
            });

            toast.success('Đã xóa sản phẩm');
            resetProductSelection();
            await fetchAdminProducts();
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể xóa sản phẩm';
            toast.error(message);
        } finally {
            setSubmittingProductAdmin(false);
        }
    };

    const addItemRow = () => {
        setItemRows((prev) => [...prev, emptyItemRow()]);
    };

    const updateItemRow = (index: number, nextRow: Partial<ItemRow>) => {
        setItemRows((prev) => prev.map((row, rowIndex) => rowIndex === index ? { ...row, ...nextRow } : row));
    };

    const addProductItems = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const productCode = itemProductCode.trim();
        if (!productCode) {
            toast.error('Vui lòng nhập mã sản phẩm để thêm size');
            return;
        }

        setSubmittingProductAdmin(true);
        try {
            await apiRequest('/v1/api/admin/product/add-items', {
                method: 'POST',
                token,
                body: {
                    productCode,
                    items: itemRows.map((row) => ({
                        productCode,
                        size: row.size,
                        status: row.status,
                        quantity: Number(row.quantity || 0),
                    })),
                },
            });

            toast.success('Đã thêm/cập nhật item sản phẩm');
            setProductModal(null);
            await fetchAdminProducts();
            if (!selectedProductCode) {
                setItemProductCode('');
                setItemRows([emptyItemRow()]);
            }
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể thêm item sản phẩm';
            toast.error(message);
        } finally {
            setSubmittingProductAdmin(false);
        }
    };

    const updateSingleItemQuantity = async (row: ItemRow) => {
        const productCode = itemProductCode.trim() || selectedProductCode.trim();
        if (!productCode) {
            toast.error('Mã sản phẩm không hợp lệ');
            return;
        }

        if (!row.size) {
            toast.error('Vui lòng chọn size');
            return;
        }

        setSubmittingProductAdmin(true);
        try {
            await apiRequest('/v1/api/admin/product/item/update-quantity', {
                method: 'PUT',
                token,
                body: {
                    productCode,
                    size: row.size,
                    status: row.status,
                    quantity: Number(row.quantity || 0),
                },
            });

            toast.success(`Đã cập nhật số lượng size ${row.size} thành công`);
            await fetchAdminProducts();
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : 'Không thể cập nhật số lượng';
            toast.error(message);
        } finally {
            setSubmittingProductAdmin(false);
        }
    };

    const submitProductSearch = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setProductPage(0);
        setProductSearchKeyword(productSearchInput.trim());
    };

    const clearProductSearch = () => {
        setProductSearchInput('');
        setProductSearchKeyword('');
        setProductPage(0);
    };

    const openCreateProductModal = () => {
        setSelectedProductCode('');
        setProductForm(emptyProductForm);
        setProductModal('create-product');
    };

    const openItemsModal = () => {
        if (!selectedProductCode && !itemProductCode.trim()) {
            toast.error('Vui lòng chọn sản phẩm từ danh sách trước');
            return;
        }
        setProductModal('items');
    };

    const openCategoryModal = () => {
        setCategoryForm(emptyCategoryForm);
        setProductModal('category');
    };

    const closeProductModal = () => {
        setProductModal(null);
    };

    if (!token) {
        return <Navigate to='/login' replace />;
    }

    if (!isAdmin) {
        return <Navigate to='/' replace />;
    }

    const receiver = orderDetail?.reciever || orderDetail?.receiver;
    const receiverAddress = [
        receiver?.addr?.detail,
        receiver?.addr?.street,
        receiver?.addr?.district,
        receiver?.addr?.province,
        receiver?.addr?.country,
    ].filter(Boolean).join(', ');

    return (
        <div className='border-t pt-10 sm:pt-14 min-h-[80vh]'>
            <div className='flex flex-col lg:flex-row lg:items-end lg:justify-between gap-5 mb-8'>
                <div>
                    <div className='text-2xl'>
                        <Title text1='QUẢN TRỊ' text2='HỆ THỐNG' />
                    </div>
                </div>

                <div className='grid grid-cols-3 sm:grid-cols-6 gap-2 border bg-white p-1'>
                    <button
                        type='button'
                        onClick={() => setActiveTab('orders')}
                        className={`px-5 py-2.5 text-sm font-medium transition-all ${activeTab === 'orders' ? 'bg-black text-white' : 'text-gray-500 hover:text-black'}`}
                    >
                        Đơn hàng
                    </button>
                    <button
                        type='button'
                        onClick={() => setActiveTab('vouchers')}
                        className={`px-5 py-2.5 text-sm font-medium transition-all ${activeTab === 'vouchers' ? 'bg-black text-white' : 'text-gray-500 hover:text-black'}`}
                    >
                        Voucher
                    </button>
                    <button
                        type='button'
                        onClick={() => setActiveTab('products')}
                        className={`px-5 py-2.5 text-sm font-medium transition-all ${activeTab === 'products' ? 'bg-black text-white' : 'text-gray-500 hover:text-black'}`}
                    >
                        Sản phẩm
                    </button>
                    <button
                        type='button'
                        onClick={() => setActiveTab('promotions')}
                        className={`px-5 py-2.5 text-sm font-medium transition-all ${activeTab === 'promotions' ? 'bg-black text-white' : 'text-gray-500 hover:text-black'}`}
                    >
                        Khuyến mãi
                    </button>
                    <button
                        type='button'
                        onClick={() => setActiveTab('providers')}
                        className={`px-5 py-2.5 text-sm font-medium transition-all ${activeTab === 'providers' ? 'bg-black text-white' : 'text-gray-500 hover:text-black'}`}
                    >
                        Nhà cung cấp
                    </button>
                    <button
                        type='button'
                        onClick={() => setActiveTab('users')}
                        className={`px-5 py-2.5 text-sm font-medium transition-all ${activeTab === 'users' ? 'bg-black text-white' : 'text-gray-500 hover:text-black'}`}
                    >
                        Người dùng
                    </button>
                </div>
            </div>

            {activeTab === 'orders' ? (
                <div className='grid xl:grid-cols-[1.35fr_0.9fr] gap-8 items-start'>
                    <div className='border bg-white p-5 sm:p-6 shadow-sm'>
                        <div className='flex flex-col md:flex-row md:items-center md:justify-between gap-4 border-b border-gray-100 pb-5'>
                            <div>
                                <p className='text-lg font-medium text-gray-900'>Giám sát đơn hàng</p>
                                <p className='mt-1 text-sm text-gray-500'>Lọc danh sách theo trạng thái hoặc mở chi tiết từng mã đơn.</p>
                            </div>

                            <div className='flex gap-2'>
                                <select
                                    value={orderStatusFilter}
                                    onChange={(event) => setOrderStatusFilter(event.target.value as OrderStatus | 'ALL')}
                                    className='border border-gray-300 px-3 py-2 text-sm outline-none focus:border-black'
                                >
                                    <option value='ALL'>Tất cả trạng thái</option>
                                    {orderStatuses.map((status) => (
                                        <option key={status} value={status}>{statusLabel[status]}</option>
                                    ))}
                                </select>
                                <button
                                    type='button'
                                    onClick={() => fetchOrders()}
                                    disabled={loadingOrders}
                                    className='bg-black px-5 py-2 text-sm text-white disabled:bg-gray-300'
                                >
                                    Tải đơn
                                </button>
                            </div>
                        </div>

                        <div className='mt-5 overflow-x-auto'>
                            <table className='w-full min-w-[720px] text-left text-sm'>
                                <thead>
                                    <tr className='border-b text-xs uppercase tracking-[0.16em] text-gray-400'>
                                        <th className='py-3 font-medium'>Mã đơn</th>
                                        <th className='py-3 font-medium'>Trạng thái</th>
                                        <th className='py-3 font-medium'>Sản phẩm</th>
                                        <th className='py-3 font-medium'>Tổng tiền</th>
                                        <th className='py-3 font-medium'>Ngày tạo</th>
                                        <th className='py-3 font-medium text-right'>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {orders.length === 0 ? (
                                        <tr>
                                            <td colSpan={6} className='py-10 text-center text-gray-500'>
                                                {loadingOrders ? 'Đang tải đơn hàng...' : 'Chưa có dữ liệu đơn hàng'}
                                            </td>
                                        </tr>
                                    ) : orders.map((order) => (
                                        <tr key={order.orderCode} className='border-b last:border-b-0'>
                                            <td className='py-4 font-medium text-gray-900'>{order.orderCode}</td>
                                            <td className='py-4'>
                                                <span className={`px-3 py-1 text-xs ${order.status === 'CANCELLED' ? 'bg-red-50 text-red-600' : 'bg-green-50 text-green-700'}`}>
                                                    {statusLabel[order.status]}
                                                </span>
                                            </td>
                                            <td className='py-4 text-gray-600'>{order.totalItems}</td>
                                            <td className='py-4 text-gray-900'>{formatCurrency(order.finalPrice)}</td>
                                            <td className='py-4 text-gray-600'>{formatDate(order.createdAt)}</td>
                                            <td className='py-4 text-right'>
                                                <button
                                                    type='button'
                                                    onClick={() => fetchOrderDetail(order.orderCode)}
                                                    className='border border-black px-4 py-2 text-xs font-medium hover:bg-black hover:text-white transition-all'
                                                >
                                                    Chi tiết
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div className='space-y-6'>
                        <div className='border bg-white p-5 sm:p-6 shadow-sm'>
                            <p className='text-lg font-medium text-gray-900'>Tra cứu mã đơn</p>
                            <div className='mt-4 flex gap-2'>
                                <input
                                    value={orderCode}
                                    onChange={(event) => setOrderCode(event.target.value)}
                                    className='w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    placeholder='Nhập orderCode'
                                />
                                <button
                                    type='button'
                                    onClick={() => fetchOrderDetail()}
                                    disabled={loadingOrders}
                                    className='bg-black px-5 py-3 text-sm text-white disabled:bg-gray-300'
                                >
                                    Xem
                                </button>
                            </div>
                        </div>

                        {orderDetail && (
                            <div className='border bg-white p-5 sm:p-6 shadow-sm'>
                                <div className='flex items-start justify-between gap-4 border-b border-gray-100 pb-5'>
                                    <div>
                                        <p className='text-xs uppercase tracking-[0.16em] text-gray-400'>Mã đơn hàng</p>
                                        <p className='mt-1 text-lg font-medium text-gray-900'>{orderDetail.orderCode}</p>
                                        <p className='mt-1 text-sm text-gray-500'>{formatDate(orderDetail.createdAt)}</p>
                                    </div>
                                    <span className='bg-green-50 px-3 py-1 text-xs text-green-700'>{statusLabel[orderDetail.status]}</span>
                                </div>

                                <div className='mt-5 space-y-4 text-sm'>
                                    <div>
                                        <p className='text-xs uppercase tracking-[0.16em] text-gray-400'>Người nhận</p>
                                        <p className='mt-1 font-medium text-gray-900'>{[receiver?.fName, receiver?.lName].filter(Boolean).join(' ') || 'Chưa có'}</p>
                                        <p className='mt-1 text-gray-500'>{receiver?.phone || 'Chưa có số điện thoại'}</p>
                                        <p className='mt-1 text-gray-500'>{receiverAddress || 'Chưa có địa chỉ'}</p>
                                    </div>

                                    <div className='border-t border-gray-100 pt-4'>
                                        <p className='text-xs uppercase tracking-[0.16em] text-gray-400'>Sản phẩm</p>
                                        <div className='mt-3 space-y-3'>
                                            {(orderDetail.items || []).map((item) => {
                                                const product = products.find((productItem) => productItem._id === item.productCode);
                                                return (
                                                    <div key={`${item.productCode}-${item.size}`} className='flex items-center justify-between gap-3'>
                                                        <div>
                                                            <p className='font-medium text-gray-900'>{product?.name || item.productCode}</p>
                                                            <p className='text-gray-500'>Size {item.size} | SL {item.quantity}</p>
                                                        </div>
                                                        <p className='font-medium'>{formatCurrency(item.finalPrice ?? item.originalPrice)}</p>
                                                    </div>
                                                );
                                            })}
                                        </div>
                                    </div>

                                    <div className='border-t border-gray-100 pt-4 space-y-2'>
                                        <div className='flex justify-between'><span className='text-gray-500'>Tạm tính</span><span>{formatCurrency(orderDetail.totalAmount)}</span></div>
                                        <div className='flex justify-between'><span className='text-gray-500'>Giảm giá</span><span>{formatCurrency(orderDetail.voucherDiscount)}</span></div>
                                        <div className='flex justify-between font-medium text-gray-900'><span>Tổng cộng</span><span>{formatCurrency(orderDetail.finalPrice)}</span></div>
                                    </div>

                                    <div className='border-t border-gray-100 pt-4'>
                                        <p className='text-xs uppercase tracking-[0.16em] text-gray-400 mb-3'>Thao tác xử lý đơn hàng</p>
                                        <div className='flex flex-wrap gap-2'>
                                            {/* ĐƠN ONLINE: UNPAID */}
                                            {orderDetail.status === 'UNPAID' && (
                                                <button
                                                    type='button'
                                                    onClick={() => updateOrderStatus('CONFIRMED')}
                                                    disabled={updatingOrder}
                                                    className='bg-black hover:bg-neutral-800 text-white px-5 py-3 text-xs uppercase tracking-wider font-semibold transition-all disabled:bg-gray-300'
                                                >
                                                    Xác nhận đã thanh toán & Phê duyệt
                                                </button>
                                            )}

                                            {/* ĐƠN COD / CHỜ DUYỆT: PENDING */}
                                            {orderDetail.status === 'PENDING' && (
                                                <>
                                                    <button
                                                        type='button'
                                                        onClick={() => updateOrderStatus('CONFIRMED')}
                                                        disabled={updatingOrder}
                                                        className='bg-black hover:bg-neutral-800 text-white px-5 py-3 text-xs uppercase tracking-wider font-semibold transition-all disabled:bg-gray-300'
                                                    >
                                                        Xác nhận đơn hàng
                                                    </button>
                                                    <button
                                                        type='button'
                                                        onClick={() => {
                                                            if (window.confirm("Bạn có chắc chắn muốn hủy đơn hàng này không?")) {
                                                                updateOrderStatus('CANCELLED');
                                                            }
                                                        }}
                                                        disabled={updatingOrder}
                                                        className='border border-red-500 text-red-500 hover:bg-red-50 px-5 py-3 text-xs uppercase tracking-wider font-semibold transition-all disabled:border-gray-300 disabled:text-gray-300'
                                                    >
                                                        Hủy đơn
                                                    </button>
                                                </>
                                            )}

                                            {/* ĐƠN ĐÃ DUYỆT: CONFIRMED */}
                                            {orderDetail.status === 'CONFIRMED' && (
                                                <button
                                                    type='button'
                                                    onClick={() => updateOrderStatus('SHIPPING')}
                                                    disabled={updatingOrder}
                                                    className='bg-black hover:bg-neutral-800 text-white px-5 py-3 text-xs uppercase tracking-wider font-semibold transition-all disabled:bg-gray-300'
                                                >
                                                    Bắt đầu giao hàng
                                                </button>
                                            )}

                                            {/* ĐƠN ĐANG GIAO: SHIPPING */}
                                            {orderDetail.status === 'SHIPPING' && (
                                                <>
                                                    <button
                                                        type='button'
                                                        onClick={() => updateOrderStatus('DELIVERED')}
                                                        disabled={updatingOrder}
                                                        className='bg-green-600 hover:bg-green-700 text-white px-5 py-3 text-xs uppercase tracking-wider font-semibold transition-all disabled:bg-gray-300'
                                                    >
                                                        Giao thành công
                                                    </button>
                                                    <button
                                                        type='button'
                                                        onClick={() => {
                                                            if (window.confirm("Đơn hàng này bị khách hàng trả lại?")) {
                                                                updateOrderStatus('RETURNED');
                                                            }
                                                        }}
                                                        disabled={updatingOrder}
                                                        className='border border-orange-500 text-orange-500 hover:bg-orange-50 px-5 py-3 text-xs uppercase tracking-wider font-semibold transition-all disabled:bg-gray-300'
                                                    >
                                                        Khách hoàn trả
                                                    </button>
                                                </>
                                            )}

                                            {/* ĐƠN ĐÃ GIAO: DELIVERED */}
                                            {orderDetail.status === 'DELIVERED' && (
                                                <button
                                                    type='button'
                                                    onClick={() => {
                                                        if (window.confirm("Xác nhận đơn hàng bị hoàn trả / trả hàng?")) {
                                                            updateOrderStatus('RETURNED');
                                                        }
                                                    }}
                                                    disabled={updatingOrder}
                                                    className='bg-red-50 text-red-600 border border-red-200 hover:bg-red-100 px-5 py-3 text-xs uppercase tracking-wider font-semibold transition-all disabled:bg-gray-300'
                                                >
                                                    Yêu cầu trả hàng (Hoàn đơn)
                                                </button>
                                            )}

                                            {/* TRẠNG THÁI CUỐI (COMPLETED, CANCELLED, RETURNED, PAID) */}
                                            {(['COMPLETED', 'CANCELLED', 'RETURNED', 'PAID'].includes(orderDetail.status)) && (
                                                <div className='flex items-center gap-2 py-2 bg-gray-50 px-4 w-full border border-gray-100 rounded-sm'>
                                                    <span className='w-2 h-2 rounded-full bg-gray-400 inline-block'></span>
                                                    <span className='text-xs text-gray-500 font-medium uppercase tracking-wider'>
                                                        Đơn hàng đã hoàn tất xử lý (Trạng thái kết thúc hoặc đã thanh toán)
                                                    </span>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            ) : activeTab === 'vouchers' ? (
                <div className='grid xl:grid-cols-[0.95fr_1.05fr] gap-8 items-start'>
                    <form onSubmit={createVoucher} className='border bg-white p-5 sm:p-6 shadow-sm'>
                        <div className='border-b border-gray-100 pb-5'>
                            <p className='text-lg font-medium text-gray-900'>Tạo voucher</p>
                        </div>

                        <div className='mt-5 grid gap-4 sm:grid-cols-2'>
                            <input
                                value={voucherForm.code}
                                onChange={(event) => setVoucherForm((prev) => ({ ...prev, code: event.target.value }))}
                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black sm:col-span-2'
                                placeholder='Mã voucher, ví dụ SALE30'
                                required
                            />
                            <select
                                value={voucherForm.discountType}
                                onChange={(event) => setVoucherForm((prev) => ({ ...prev, discountType: event.target.value as DiscountType }))}
                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                            >
                                <option value='PERCENT'>Giảm theo phần trăm</option>
                                <option value='FIXED'>Giảm số tiền cố định</option>
                            </select>
                            <select
                                value={voucherForm.voucherType}
                                onChange={(event) => setVoucherForm((prev) => ({ ...prev, voucherType: event.target.value as VoucherType }))}
                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                            >
                                <option value='GLOBAL'>Áp dụng toàn hệ thống</option>
                                <option value='NEWBIE'>Khách hàng mới</option>
                            </select>
                            <select
                                value={voucherForm.rankType}
                                onChange={(event) => setVoucherForm((prev) => ({ ...prev, rankType: event.target.value as RankTypeOption }))}
                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black sm:col-span-2'
                            >
                                <option value='Bronze'>Bronze</option>
                                <option value='Silver'>Silver</option>
                                <option value='Gold'>Gold</option>
                                <option value='Platinum'>Platinum</option>
                                <option value='Diamond'>Diamond</option>
                                <option value='Ultimate'>Ultimate</option>
                            </select>
                            <input
                                value={voucherForm.value}
                                onChange={(event) => setVoucherForm((prev) => ({ ...prev, value: event.target.value }))}
                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                type='number'
                                min={1}
                                placeholder='Giá trị giảm'
                                required
                            />
                            <input
                                value={voucherForm.minOrderAmount}
                                onChange={(event) => setVoucherForm((prev) => ({ ...prev, minOrderAmount: event.target.value }))}
                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                type='number'
                                min={0}
                                placeholder='Đơn tối thiểu'
                                required
                            />
                            <div>
                                <p className='mb-2 text-xs uppercase tracking-[0.16em] text-gray-400'>Bắt đầu</p>
                                <input
                                    value={voucherForm.startAt}
                                    onChange={(event) => setVoucherForm((prev) => ({ ...prev, startAt: event.target.value }))}
                                    className='w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    type='datetime-local'
                                    required
                                />
                            </div>
                            <div>
                                <p className='mb-2 text-xs uppercase tracking-[0.16em] text-gray-400'>Kết thúc</p>
                                <input
                                    value={voucherForm.endAt}
                                    onChange={(event) => setVoucherForm((prev) => ({ ...prev, endAt: event.target.value }))}
                                    className='w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    type='datetime-local'
                                    required
                                />
                            </div>
                        </div>

                        <button
                            type='submit'
                            disabled={creatingVoucher}
                            className='mt-6 w-full bg-black px-6 py-3 text-sm font-medium text-white disabled:bg-gray-300'
                        >
                            {creatingVoucher ? 'ĐANG TẠO...' : 'TẠO VOUCHER'}
                        </button>
                    </form>

                    <div className='border bg-white p-5 sm:p-6 shadow-sm'>
                        <div className='flex items-start justify-between gap-4 border-b border-gray-100 pb-5'>
                            <div>
                                <p className='text-lg font-medium text-gray-900'>Voucher đã tạo</p>
                                <p className='mt-1 text-sm text-gray-500'>Danh sách mã voucher được tạo từ màn hình quản trị.</p>
                            </div>
                            <button
                                type='button'
                                onClick={clearCreatedVouchers}
                                disabled={createdVouchers.length === 0}
                                className='border border-gray-300 px-4 py-2 text-xs font-medium text-gray-700 hover:border-black disabled:text-gray-300'
                            >
                                Dọn danh sách
                            </button>
                        </div>

                        <div className='mt-6 overflow-x-auto'>
                            <table className='w-full min-w-[620px] text-left text-sm'>
                                <thead>
                                    <tr className='border-b text-xs uppercase tracking-[0.16em] text-gray-400'>
                                        <th className='py-3 font-medium'>Mã</th>
                                        <th className='py-3 font-medium'>Loại</th>
                                        <th className='py-3 font-medium'>Rank</th>
                                        <th className='py-3 font-medium'>Giá trị</th>
                                        <th className='py-3 font-medium'>Đơn tối thiểu</th>
                                        <th className='py-3 font-medium'>Trạng thái</th>
                                        <th className='py-3 font-medium'>Thời gian</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {createdVouchers.length === 0 ? (
                                        <tr>
                                            <td colSpan={7} className='py-10 text-center text-gray-500'>
                                                Chưa có voucher nào được tạo từ màn hình này
                                            </td>
                                        </tr>
                                    ) : createdVouchers.map((voucher) => (
                                        <tr key={`${voucher.code}-${voucher.createdAt}`} className='border-b last:border-b-0'>
                                            <td className='py-4 font-medium text-gray-900'>{voucher.code}</td>
                                            <td className='py-4 text-gray-600'>{voucher.voucherType}</td>
                                            <td className='py-4 text-gray-600'>{(voucher as any).rankType || 'Bronze'}</td>
                                            <td className='py-4 text-gray-900'>{voucher.discountType === 'PERCENT' ? `${voucher.value}%` : formatCurrency(voucher.value)}</td>
                                            <td className='py-4 text-gray-600'>{formatCurrency(voucher.minOrderAmount)}</td>
                                            <td className='py-4'><span className='bg-green-50 px-3 py-1 text-xs text-green-700'>{voucher.status}</span></td>
                                            <td className='py-4 text-gray-600'>
                                                <p>{formatDate(voucher.startAt)}</p>
                                                <p className='mt-1 text-xs text-gray-400'>{formatDate(voucher.endAt)}</p>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            ) : activeTab === 'promotions' ? (
                <div className='grid xl:grid-cols-[1.35fr_0.9fr] gap-8 items-start'>
                    {/* BẢNG DANH SÁCH PROMOTIONS */}
                    <div className='border bg-white p-5 sm:p-6 shadow-sm'>
                        <div className='flex flex-col md:flex-row md:items-center md:justify-between gap-4 border-b border-gray-100 pb-5'>
                            <div>
                                <p className='text-lg font-medium text-gray-900'>Chương trình Khuyến mãi</p>
                                <p className='mt-1 text-sm text-gray-500'>Các chương trình giảm giá tự động đang hoạt động trên hệ thống.</p>
                            </div>
                        </div>

                        {loadingPromotions ? (
                            <div className='py-20 text-center text-sm text-gray-400 uppercase tracking-widest'>Đang tải...</div>
                        ) : promotions.length === 0 ? (
                            <div className='py-20 text-center text-sm text-gray-400 uppercase tracking-widest'>Chưa có chương trình khuyến mãi nào</div>
                        ) : (
                            <div className='overflow-x-auto mt-4'>
                                <table className='w-full text-left text-sm text-gray-700 min-w-[700px]'>
                                    <thead>
                                        <tr className='border-b border-gray-100 text-xs font-semibold uppercase tracking-wider text-gray-400'>
                                            <th className='pb-3'>Giá trị giảm</th>
                                            <th className='pb-3'>Thời gian bắt đầu</th>
                                            <th className='pb-3'>Thời gian kết thúc</th>
                                            <th className='pb-3'>Trạng thái</th>
                                            <th className='pb-3 text-right'>Hành động</th>
                                        </tr>
                                    </thead>
                                    <tbody className='divide-y divide-gray-50'>
                                        {promotions.map((promo) => (
                                            <tr key={promo.id} className='hover:bg-gray-50/50 transition-all'>
                                                <td className='py-4 font-semibold text-gray-900'>
                                                    {promo.value < 100 ? `${promo.value}%` : `${promo.value.toLocaleString()}₫`}
                                                </td>
                                                <td className='py-4 text-gray-600'>{formatDate(promo.startAt)}</td>
                                                <td className='py-4 text-gray-600'>{formatDate(promo.endAt)}</td>
                                                <td className='py-4'>
                                                    {promo.status === 'ACTIVE' ? (
                                                        <span className='bg-green-50 px-3 py-1 text-xs font-medium text-green-700 rounded-full border border-green-200'>Đang hoạt động</span>
                                                    ) : promo.status === 'SCHEDULED' ? (
                                                        <span className='bg-amber-50 px-3 py-1 text-xs font-medium text-amber-700 rounded-full border border-amber-200'>Đã lên lịch</span>
                                                    ) : (
                                                        <span className='bg-gray-100 px-3 py-1 text-xs font-medium text-gray-500 rounded-full border border-gray-200'>Đã kết thúc</span>
                                                    )}
                                                </td>
                                                <td className='py-4 text-right space-x-2'>
                                                    <button
                                                        type='button'
                                                        onClick={() => handleUnavailableFeature('Sửa Khuyến mãi')}
                                                        className='text-xs font-medium text-black border border-gray-200 px-3 py-1.5 bg-white hover:bg-gray-50'
                                                    >
                                                        Sửa
                                                    </button>
                                                    <button
                                                        type='button'
                                                        onClick={() => handleUnavailableFeature('Xóa Khuyến mãi')}
                                                        className='text-xs font-medium text-red-600 border border-red-200 px-3 py-1.5 bg-white hover:bg-red-50'
                                                    >
                                                        Xóa
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>

                                {/* PHÂN TRANG */}
                                {promotionTotalPages > 1 && (
                                    <div className='flex items-center justify-end gap-2 border-t border-gray-100 pt-5 mt-5'>
                                        <button
                                            type='button'
                                            disabled={promotionPage === 0}
                                            onClick={() => setPromotionPage(p => Math.max(0, p - 1))}
                                            className='border px-3 py-1.5 text-xs font-medium text-gray-700 disabled:opacity-50 hover:bg-gray-50'
                                        >
                                            Trước
                                        </button>
                                        <span className='text-xs text-gray-500'>
                                            Trang {promotionPage + 1} / {promotionTotalPages}
                                        </span>
                                        <button
                                            type='button'
                                            disabled={promotionPage >= promotionTotalPages - 1}
                                            onClick={() => setPromotionPage(p => p + 1)}
                                            className='border px-3 py-1.5 text-xs font-medium text-gray-700 disabled:opacity-50 hover:bg-gray-50'
                                        >
                                            Sau
                                        </button>
                                    </div>
                                )}
                            </div>
                        )}
                    </div>

                    {/* FORM TẠO PROMOTION MỚI */}
                    <div className='space-y-6'>
                        <form onSubmit={createPromotion} className='border bg-white p-5 sm:p-6 shadow-sm'>
                            <div className='border-b border-gray-100 pb-5'>
                                <p className='text-lg font-medium text-gray-900'>Tạo chương trình Khuyến mãi</p>
                                <p className='mt-1 text-sm text-gray-500'>Thiết lập đợt giảm giá tự động cho sản phẩm.</p>
                            </div>

                            <div className='mt-5 grid gap-4'>
                                <div>
                                    <label className='mb-1.5 block text-xs font-medium text-gray-600 uppercase tracking-wider text-left'>Loại phạm vi áp dụng (Scope)</label>
                                    <select
                                        value={promotionForm.scope}
                                        onChange={(e) => setPromotionForm(prev => ({ ...prev, scope: e.target.value as PromotionScope }))}
                                        className='w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    >
                                        <option value='GLOBAL'>Áp dụng toàn bộ cửa hàng (GLOBAL)</option>
                                        <option value='PRODUCT'>Áp dụng cho sản phẩm cụ thể (PRODUCT)</option>
                                        <option value='CATEGORY'>Áp dụng cho danh mục sản phẩm (CATEGORY)</option>
                                    </select>
                                </div>

                                {promotionForm.scope === 'PRODUCT' && (
                                    <div>
                                        <label className='mb-1.5 block text-xs font-medium text-gray-600 uppercase tracking-wider text-left'>Mã các sản phẩm áp dụng</label>
                                        <input
                                            value={promotionForm.productCodes}
                                            onChange={(e) => setPromotionForm(prev => ({ ...prev, productCodes: e.target.value }))}
                                            className='w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                            placeholder='Nhập các mã code, phân tách bằng dấu phẩy. Ví dụ: MTS-003, MTS-001'
                                        />
                                    </div>
                                )}

                                {promotionForm.scope === 'CATEGORY' && (
                                    <div>
                                        <label className='mb-1.5 block text-xs font-medium text-gray-600 uppercase tracking-wider text-left'>Mã danh mục áp dụng</label>
                                        <input
                                            value={promotionForm.categoryCodes}
                                            onChange={(e) => setPromotionForm(prev => ({ ...prev, categoryCodes: e.target.value }))}
                                            className='w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                            placeholder='Nhập các mã danh mục, phân tách bằng dấu phẩy. Ví dụ: CATEGORY-01'
                                        />
                                    </div>
                                )}

                                <div className='grid grid-cols-2 gap-4'>
                                    <div>
                                        <label className='mb-1.5 block text-xs font-medium text-gray-600 uppercase tracking-wider text-left'>Giá trị giảm</label>
                                        <input
                                            value={promotionForm.value}
                                            onChange={(e) => setPromotionForm(prev => ({ ...prev, value: e.target.value }))}
                                            className='w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                            type='number'
                                            min={0}
                                            placeholder='Ví dụ: 20 (giảm 20%) hoặc 50000'
                                        />
                                    </div>
                                    <div>
                                        <label className='mb-1.5 block text-xs font-medium text-gray-600 uppercase tracking-wider text-left'>Độ ưu tiên (Priority)</label>
                                        <input
                                            value={promotionForm.priority}
                                            onChange={(e) => setPromotionForm(prev => ({ ...prev, priority: e.target.value }))}
                                            className='w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                            type='number'
                                            min={0}
                                            placeholder='Mặc định: 1'
                                        />
                                    </div>
                                </div>

                                <div className='grid grid-cols-2 gap-4'>
                                    <div>
                                        <label className='mb-1.5 block text-xs font-medium text-gray-600 uppercase tracking-wider text-left'>Bắt đầu</label>
                                        <input
                                            value={promotionForm.startAt}
                                            onChange={(e) => setPromotionForm(prev => ({ ...prev, startAt: e.target.value }))}
                                            className='w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                            type='datetime-local'
                                        />
                                    </div>
                                    <div>
                                        <label className='mb-1.5 block text-xs font-medium text-gray-600 uppercase tracking-wider text-left'>Kết thúc</label>
                                        <input
                                            value={promotionForm.endAt}
                                            onChange={(e) => setPromotionForm(prev => ({ ...prev, endAt: e.target.value }))}
                                            className='w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                            type='datetime-local'
                                        />
                                    </div>
                                </div>
                            </div>

                            <button
                                type='submit'
                                disabled={creatingPromotion}
                                className='mt-6 w-full bg-black px-6 py-3 text-sm font-medium text-white disabled:bg-gray-300 transition-all hover:bg-black/90'
                            >
                                {creatingPromotion ? 'ĐANG TẠO...' : 'TẠO KHUYẾN MÃI'}
                            </button>
                        </form>
                    </div>
                </div>
            ) : activeTab === 'products' ? (
                <div className='space-y-8'>
                    <div className='border bg-white p-5 sm:p-6 shadow-sm'>
                        <div className='flex flex-col xl:flex-row xl:items-start xl:justify-between gap-5 border-b border-gray-100 pb-5'>
                            <div>
                                <p className='text-lg font-medium text-gray-900'>Danh sách sản phẩm</p>
                                <p className='mt-1 text-sm text-gray-500'>Tìm kiếm, phân trang và chọn sản phẩm để cập nhật thông tin hoặc tồn kho.</p>
                            </div>
                            <div className='flex flex-col gap-3 xl:items-end'>
                                <div className='flex flex-wrap gap-2'>
                                    <button type='button' onClick={openCreateProductModal} className='bg-black px-4 py-2 text-xs font-medium text-white'>
                                        Thêm sản phẩm
                                    </button>
                                    <button type='button' onClick={openItemsModal} className='border border-black px-4 py-2 text-xs font-medium hover:bg-black hover:text-white transition-all'>
                                        Size tồn kho
                                    </button>
                                    <button type='button' onClick={openCategoryModal} className='border border-black px-4 py-2 text-xs font-medium hover:bg-black hover:text-white transition-all'>
                                        Thêm danh mục
                                    </button>
                                    {selectedProductCode && (
                                        <button
                                            type='button'
                                            onClick={resetProductSelection}
                                            className='border border-gray-300 px-4 py-2 text-xs font-medium hover:border-black'
                                        >
                                            Bỏ chọn
                                        </button>
                                    )}
                                </div>
                                <form onSubmit={submitProductSearch} className='flex flex-col sm:flex-row gap-2'>
                                    <input
                                        value={productSearchInput}
                                        onChange={(event) => setProductSearchInput(event.target.value)}
                                        className='w-full sm:w-72 border border-gray-300 px-4 py-2 text-sm outline-none focus:border-black'
                                        placeholder='Tìm theo tên hoặc mô tả'
                                    />
                                    <button type='submit' className='bg-black px-4 py-2 text-xs font-medium text-white'>
                                        Tìm kiếm
                                    </button>
                                    <button type='button' onClick={clearProductSearch} className='border border-gray-300 px-4 py-2 text-xs font-medium hover:border-black'>
                                        Xóa lọc
                                    </button>
                                </form>
                            </div>
                        </div>

                        <div className='mt-5 overflow-x-auto'>
                            <table className='w-full min-w-[1080px] text-left text-sm'>
                                <thead>
                                    <tr className='border-b text-xs uppercase tracking-[0.16em] text-gray-400'>
                                        <th className='py-3 font-medium'>Sản phẩm</th>
                                        <th className='py-3 font-medium'>Mã</th>
                                        <th className='py-3 font-medium'>Danh mục</th>
                                        <th className='py-3 font-medium'>Nhà cung cấp</th>
                                        <th className='py-3 font-medium'>Giá</th>
                                        <th className='py-3 font-medium'>Tồn kho</th>
                                        <th className='py-3 font-medium'>Size</th>
                                        <th className='py-3 font-medium text-right'>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {adminProducts.length === 0 ? (
                                        <tr>
                                            <td colSpan={8} className='py-10 text-center text-gray-500'>
                                                {loadingAdminProducts ? 'Đang tải sản phẩm...' : 'Chưa có dữ liệu sản phẩm'}
                                            </td>
                                        </tr>
                                    ) : adminProducts.map((product) => {
                                        const code = product.code || product._id;
                                        const isSelected = selectedProductCode === code;
                                        return (
                                            <tr key={code} className={`border-b last:border-b-0 ${isSelected ? 'bg-gray-50' : ''}`}>
                                                <td className='py-4'>
                                                    <div className='flex items-center gap-3'>
                                                        <img
                                                            src={product.image?.[0] || '/vite.svg'}
                                                            alt={product.name}
                                                            className='h-12 w-12 object-cover border border-gray-100'
                                                        />
                                                        <div>
                                                            <p className='font-medium text-gray-900'>{product.name}</p>
                                                            <p className='mt-1 line-clamp-1 max-w-[260px] text-xs text-gray-500'>{product.description || 'Không có mô tả'}</p>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td className='py-4 font-medium text-gray-900'>{code}</td>
                                                <td className='py-4 text-gray-600'>{product.categoryCode || product.category || 'Không có'}</td>
                                                <td className='py-4 text-gray-600'>{product.providerCode || 'Không có'}</td>
                                                <td className='py-4 text-gray-900'>{formatCurrency(product.price)}</td>
                                                <td className='py-4 font-medium text-gray-900'>{product.totalQuantity}</td>
                                                <td className='py-4 text-gray-600'>{product.stockSummary}</td>
                                                <td className='py-4 text-right'>
                                                    <div className='flex justify-end gap-2'>
                                                        <button
                                                            type='button'
                                                            onClick={() => startEditProduct(product)}
                                                            disabled={submittingProductAdmin}
                                                            className='border border-black px-4 py-2 text-xs font-medium hover:bg-black hover:text-white transition-all disabled:border-gray-300 disabled:text-gray-300'
                                                        >
                                                            Edit
                                                        </button>
                                                        <button
                                                            type='button'
                                                            onClick={() => startEditProduct(product, 'items')}
                                                            disabled={submittingProductAdmin}
                                                            className='border border-gray-300 px-4 py-2 text-xs font-medium hover:border-black transition-all disabled:text-gray-300'
                                                        >
                                                            Tồn kho
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>
                        <div className='mt-5 flex flex-col gap-3 border-t border-gray-100 pt-4 sm:flex-row sm:items-center sm:justify-between'>
                            <p className='text-sm text-gray-500'>
                                Tổng {productTotalElements} sản phẩm · Trang {productTotalPages ? productPage + 1 : 0}/{productTotalPages}
                            </p>
                            <div className='flex flex-wrap items-center gap-2'>
                                <select
                                    value={productPageSize}
                                    onChange={(event) => {
                                        setProductPageSize(Number(event.target.value));
                                        setProductPage(0);
                                    }}
                                    className='border border-gray-300 px-3 py-2 text-sm outline-none focus:border-black'
                                >
                                    <option value={5}>5 / trang</option>
                                    <option value={10}>10 / trang</option>
                                    <option value={20}>20 / trang</option>
                                </select>
                                <button
                                    type='button'
                                    onClick={() => setProductPage((prev) => Math.max(prev - 1, 0))}
                                    disabled={productPage === 0 || loadingAdminProducts}
                                    className='border border-black px-4 py-2 text-xs font-medium hover:bg-black hover:text-white transition-all disabled:border-gray-300 disabled:text-gray-300'
                                >
                                    Trước
                                </button>
                                <button
                                    type='button'
                                    onClick={() => setProductPage((prev) => Math.min(prev + 1, Math.max(productTotalPages - 1, 0)))}
                                    disabled={productPage >= productTotalPages - 1 || productTotalPages === 0 || loadingAdminProducts}
                                    className='border border-black px-4 py-2 text-xs font-medium hover:bg-black hover:text-white transition-all disabled:border-gray-300 disabled:text-gray-300'
                                >
                                    Sau
                                </button>
                            </div>
                        </div>
                    </div>

                    <div className='hidden'>
                        <form
                            onSubmit={(event) => {
                                if (selectedProductCode) {
                                    event.preventDefault();
                                    updateProduct();
                                    return;
                                }
                                createProduct(event);
                            }}
                            className='border bg-white p-5 sm:p-6 shadow-sm'
                        >
                            <div className='flex flex-col md:flex-row md:items-start md:justify-between gap-3 border-b border-gray-100 pb-5'>
                                <div>
                                    <p className='text-lg font-medium text-gray-900'>Sản phẩm</p>
                                    <p className='mt-1 text-sm text-gray-500'>
                                        {selectedProductCode ? `Đang chỉnh sửa ${selectedProductCode}` : 'Tạo sản phẩm mới hoặc chọn Edit từ danh sách để cập nhật.'}
                                    </p>
                                </div>
                                <button
                                    type='button'
                                    onClick={fetchProductMeta}
                                    disabled={loadingProductMeta}
                                    className='border border-black px-4 py-2 text-xs font-medium hover:bg-black hover:text-white transition-all disabled:border-gray-300 disabled:text-gray-300'
                                >
                                    Tải danh mục
                                </button>
                            </div>

                            <div className='mt-5 grid gap-4 sm:grid-cols-2'>
                                <input
                                    value={productForm.productCode}
                                    onChange={(event) => setProductForm((prev) => ({ ...prev, productCode: event.target.value }))}
                                    disabled={Boolean(selectedProductCode)}
                                    className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    placeholder='Mã sản phẩm khi cập nhật'
                                />
                                <input
                                    value={productForm.name}
                                    onChange={(event) => setProductForm((prev) => ({ ...prev, name: event.target.value }))}
                                    className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    placeholder='Tên sản phẩm'
                                />
                                <input
                                    list='admin-category-codes'
                                    value={productForm.categoryCode}
                                    onChange={(event) => setProductForm((prev) => ({ ...prev, categoryCode: event.target.value }))}
                                    className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    placeholder='Mã danh mục'
                                />
                                <input
                                    list='admin-provider-codes'
                                    value={productForm.providerCode}
                                    onChange={(event) => setProductForm((prev) => ({ ...prev, providerCode: event.target.value }))}
                                    className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    placeholder='Mã nhà cung cấp'
                                />
                                <input
                                    value={productForm.price}
                                    onChange={(event) => setProductForm((prev) => ({ ...prev, price: event.target.value }))}
                                    className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    type='number'
                                    min={0}
                                    placeholder='Giá bán'
                                />
                                <div className='flex items-center gap-2 border border-gray-300 px-4 focus-within:border-black'>
                                    <input
                                        value={productForm.imgUrl}
                                        onChange={(event) => setProductForm((prev) => ({ ...prev, imgUrl: event.target.value }))}
                                        className='w-full text-sm outline-none py-3 bg-transparent'
                                        placeholder='URL ảnh'
                                    />
                                    <label className='cursor-pointer text-xs font-semibold uppercase bg-gray-100 hover:bg-black hover:text-white px-2.5 py-1.5 transition-all text-gray-700 whitespace-nowrap shrink-0'>
                                        Tải lên
                                        <input
                                            type='file'
                                            accept='image/*'
                                            className='hidden'
                                            onChange={(event) => handleImageUpload(event, (url) => setProductForm((prev) => ({ ...prev, imgUrl: url })))}
                                        />
                                    </label>
                                </div>
                                <input
                                    value={productForm.sold}
                                    onChange={(event) => setProductForm((prev) => ({ ...prev, sold: event.target.value }))}
                                    className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    type='number'
                                    min={0}
                                    placeholder='Đã bán'
                                />
                                <input
                                    value={productForm.rate}
                                    onChange={(event) => setProductForm((prev) => ({ ...prev, rate: event.target.value }))}
                                    className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    type='number'
                                    min={0}
                                    max={5}
                                    step='0.1'
                                    placeholder='Đánh giá'
                                />
                                <input
                                    value={productForm.videoUrl}
                                    onChange={(event) => setProductForm((prev) => ({ ...prev, videoUrl: event.target.value }))}
                                    className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black sm:col-span-2'
                                    placeholder='URL video'
                                />
                                <textarea
                                    value={productForm.description}
                                    onChange={(event) => setProductForm((prev) => ({ ...prev, description: event.target.value }))}
                                    className='min-h-28 border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black sm:col-span-2'
                                    placeholder='Mô tả sản phẩm'
                                />
                            </div>

                            {selectedProductCode ? (
                                <div className='mt-6 grid gap-3 sm:grid-cols-3'>
                                    <button
                                        type='submit'
                                        disabled={submittingProductAdmin}
                                        className='bg-black px-6 py-3 text-sm font-medium text-white disabled:bg-gray-300'
                                    >
                                        Lưu thay đổi
                                    </button>
                                    <button
                                        type='button'
                                        onClick={deleteProduct}
                                        disabled={submittingProductAdmin}
                                        className='border border-red-500 px-6 py-3 text-sm font-medium text-red-600 hover:bg-red-50 transition-all disabled:border-gray-300 disabled:text-gray-300'
                                    >
                                        Xóa sản phẩm
                                    </button>
                                    <button
                                        type='button'
                                        onClick={resetProductSelection}
                                        disabled={submittingProductAdmin}
                                        className='border border-black px-6 py-3 text-sm font-medium hover:bg-black hover:text-white transition-all disabled:border-gray-300 disabled:text-gray-300'
                                    >
                                        Hủy
                                    </button>
                                </div>
                            ) : (
                                <div className='mt-6 grid gap-3 sm:grid-cols-2'>
                                    <button
                                        type='submit'
                                        disabled={submittingProductAdmin}
                                        className='bg-black px-6 py-3 text-sm font-medium text-white disabled:bg-gray-300'
                                    >
                                        Tạo sản phẩm
                                    </button>
                                    <button
                                        type='button'
                                        onClick={updateProduct}
                                        disabled={submittingProductAdmin}
                                        className='border border-black px-6 py-3 text-sm font-medium hover:bg-black hover:text-white transition-all disabled:border-gray-300 disabled:text-gray-300'
                                    >
                                        Cập nhật theo mã
                                    </button>
                                </div>
                            )}
                        </form>

                        <form onSubmit={addProductItems} className='border bg-white p-5 sm:p-6 shadow-sm'>
                            <div className='border-b border-gray-100 pb-5'>
                                <p className='text-lg font-medium text-gray-900'>Size và tồn kho</p>
                                <p className='mt-1 text-sm text-gray-500'>Thêm item cho sản phẩm theo size, trạng thái và số lượng.</p>
                            </div>

                            <input
                                value={itemProductCode}
                                onChange={(event) => setItemProductCode(event.target.value)}
                                className='mt-5 w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                placeholder='Mã sản phẩm'
                            />

                            <div className='mt-4 space-y-3'>
                                {itemRows.map((row, index) => (
                                    <div key={`${row.size}-${index}`} className='grid grid-cols-[1.2fr_1.2fr_1fr_auto] gap-2'>
                                        <select
                                            value={row.size}
                                            onChange={(event) => updateItemRow(index, { size: event.target.value as ProductSize })}
                                            className='border border-gray-300 px-3 py-3 text-sm outline-none focus:border-black'
                                        >
                                            {productSizes.map((size) => <option key={size} value={size}>{size}</option>)}
                                        </select>
                                        <select
                                            value={row.status}
                                            onChange={(event) => updateItemRow(index, { status: event.target.value as ItemStatus })}
                                            className='border border-gray-300 px-3 py-3 text-sm outline-none focus:border-black'
                                        >
                                            {itemStatuses.map((status) => <option key={status} value={status}>{status}</option>)}
                                        </select>
                                        <input
                                            value={row.quantity}
                                            onChange={(event) => {
                                                const value = event.target.value;
                                                if (/^\d*$/.test(value)) {
                                                    updateItemRow(index, { quantity: value });
                                                }
                                            }}
                                            className='border border-gray-300 px-3 py-3 text-sm outline-none focus:border-black'
                                            type='text'
                                            inputMode='numeric'
                                            placeholder='SL'
                                        />
                                        <button
                                            type='button'
                                            onClick={() => updateSingleItemQuantity(row)}
                                            disabled={submittingProductAdmin}
                                            className='min-w-11 bg-black px-4 text-lg font-medium text-white disabled:bg-gray-300'
                                            title='Cáº­p nháº­t sá»‘ lÆ°á»£ng'
                                        >
                                            +
                                        </button>
                                    </div>
                                ))}
                            </div>

                            <div className='mt-5 grid gap-3 sm:grid-cols-2'>
                                <button
                                    type='button'
                                    onClick={addItemRow}
                                    className='border border-black px-5 py-3 text-sm font-medium hover:bg-black hover:text-white transition-all'
                                >
                                    Thêm dòng
                                </button>
                                <button
                                    type='submit'
                                    disabled={submittingProductAdmin}
                                    className='bg-black px-5 py-3 text-sm font-medium text-white disabled:bg-gray-300'
                                >
                                    Lưu item
                                </button>
                            </div>
                        </form>
                    </div>

                    <div className='hidden'>
                        <form onSubmit={createCategory} className='border bg-white p-5 sm:p-6 shadow-sm'>
                            <div className='border-b border-gray-100 pb-5'>
                                <p className='text-lg font-medium text-gray-900'>Danh mục</p>
                                <p className='mt-1 text-sm text-gray-500'>Thêm loại sản phẩm mới cho hệ thống.</p>
                            </div>

                            <div className='mt-5 grid gap-4 sm:grid-cols-2'>
                                <input
                                    value={categoryForm.name}
                                    onChange={(event) => setCategoryForm((prev) => ({ ...prev, name: event.target.value }))}
                                    className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    placeholder='Tên danh mục'
                                />
                                <input
                                    value={categoryForm.code}
                                    onChange={(event) => setCategoryForm((prev) => ({ ...prev, code: event.target.value }))}
                                    className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    placeholder='Mã danh mục'
                                />
                                <input
                                    list='admin-category-codes'
                                    value={categoryForm.parentCode}
                                    onChange={(event) => setCategoryForm((prev) => ({ ...prev, parentCode: event.target.value }))}
                                    className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                    placeholder='Mã danh mục cha'
                                />
                                <div className='flex items-center gap-2 border border-gray-300 px-4 focus-within:border-black'>
                                    <input
                                        value={categoryForm.imgUrl}
                                        onChange={(event) => setCategoryForm((prev) => ({ ...prev, imgUrl: event.target.value }))}
                                        className='w-full text-sm outline-none py-3 bg-transparent'
                                        placeholder='URL ảnh'
                                    />
                                    <label className='cursor-pointer text-xs font-semibold uppercase bg-gray-100 hover:bg-black hover:text-white px-2.5 py-1.5 transition-all text-gray-700 whitespace-nowrap shrink-0'>
                                        Tải lên
                                        <input
                                            type='file'
                                            accept='image/*'
                                            className='hidden'
                                            onChange={(event) => handleImageUpload(event, (url) => setCategoryForm((prev) => ({ ...prev, imgUrl: url })))}
                                        />
                                    </label>
                                </div>
                                <textarea
                                    value={categoryForm.description}
                                    onChange={(event) => setCategoryForm((prev) => ({ ...prev, description: event.target.value }))}
                                    className='min-h-24 border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black sm:col-span-2'
                                    placeholder='Mô tả danh mục'
                                />
                            </div>

                            <button type='submit' disabled={submittingProductAdmin} className='mt-6 w-full bg-black px-6 py-3 text-sm font-medium text-white disabled:bg-gray-300'>
                                Tạo danh mục
                            </button>
                        </form>
                    </div>

                    <datalist id='admin-provider-codes'>
                        {providers.map((provider, index) => {
                            const code = provider.providerCode || provider.code || '';
                            return code ? <option key={`${code}-${index}`} value={code}>{provider.name}</option> : null;
                        })}
                    </datalist>
                    <datalist id='admin-category-codes'>
                        {categories
                            .filter((cat) => {
                                const code = cat.categoryCode || cat.code || '';
                                return ['MJK', 'MTS', 'WTS', 'SDL', 'SNK', 'HAT', 'MJN', 'WPN', 'BAG', 'WDR'].includes(code);
                            })
                            .map((category, index) => {
                                const code = category.categoryCode || category.code || '';
                                return <option key={`${code}-${index}`} value={code}>{category.name}</option>;
                            })}
                    </datalist>

                    {productModal && (
                        <div className='fixed inset-0 z-[70] flex items-start justify-center overflow-y-auto bg-black/40 px-4 py-8'>
                            <div className='w-full max-w-3xl bg-white p-5 sm:p-6 shadow-xl'>
                                <div className='flex items-start justify-between gap-4 border-b border-gray-100 pb-5'>
                                    <div>
                                        <p className='text-lg font-medium text-gray-900'>
                                            {productModal === 'create-product' && 'Thêm sản phẩm'}
                                            {productModal === 'edit-product' && `Cập nhật ${selectedProductCode}`}
                                            {productModal === 'items' && `Size và tồn kho ${itemProductCode || selectedProductCode}`}
                                            {productModal === 'category' && 'Thêm danh mục'}
                                        </p>
                                        <p className='mt-1 text-sm text-gray-500'>
                                            {productModal === 'items'
                                                ? 'Cập nhật size, trạng thái và số lượng tồn kho cho sản phẩm đã chọn.'
                                                : 'Nhập thông tin và lưu để đồng bộ với backend.'}
                                        </p>
                                    </div>
                                    <button
                                        type='button'
                                        onClick={closeProductModal}
                                        className='border border-gray-300 px-3 py-2 text-xs font-medium hover:border-black'
                                    >
                                        Đóng
                                    </button>
                                </div>

                                {(productModal === 'create-product' || productModal === 'edit-product') && (
                                    <form
                                        onSubmit={(event) => {
                                            if (productModal === 'edit-product') {
                                                event.preventDefault();
                                                updateProduct();
                                                return;
                                            }
                                            createProduct(event);
                                        }}
                                        className='mt-5'
                                    >
                                        <div className='grid gap-4 sm:grid-cols-2'>
                                            <input
                                                value={productForm.productCode}
                                                onChange={(event) => setProductForm((prev) => ({ ...prev, productCode: event.target.value }))}
                                                disabled={productModal === 'edit-product'}
                                                className={`border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black ${productModal === 'create-product' ? 'hidden' : ''}`}
                                                placeholder='Mã sản phẩm'
                                            />
                                            <input
                                                value={productForm.name}
                                                onChange={(event) => setProductForm((prev) => ({ ...prev, name: event.target.value }))}
                                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                                placeholder='Tên sản phẩm'
                                            />
                                            <input
                                                list='admin-category-codes'
                                                value={productForm.categoryCode}
                                                onChange={(event) => setProductForm((prev) => ({ ...prev, categoryCode: event.target.value }))}
                                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                                placeholder='Mã danh mục'
                                            />
                                            <input
                                                list='admin-provider-codes'
                                                value={productForm.providerCode}
                                                onChange={(event) => setProductForm((prev) => ({ ...prev, providerCode: event.target.value }))}
                                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                                placeholder='Mã nhà cung cấp'
                                            />
                                            <input
                                                value={productForm.price}
                                                onChange={(event) => setProductForm((prev) => ({ ...prev, price: event.target.value }))}
                                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                                type='number'
                                                min={productModal === 'create-product' ? 1 : 0}
                                                placeholder='Giá bán'
                                            />
                                            <div className='flex items-center gap-2 border border-gray-300 px-4 focus-within:border-black'>
                                                <input
                                                    value={productForm.imgUrl}
                                                    onChange={(event) => setProductForm((prev) => ({ ...prev, imgUrl: event.target.value }))}
                                                    className='w-full text-sm outline-none py-3 bg-transparent'
                                                    placeholder='URL ảnh'
                                                />
                                                <label className='cursor-pointer text-xs font-semibold uppercase bg-gray-100 hover:bg-black hover:text-white px-2.5 py-1.5 transition-all text-gray-700 whitespace-nowrap shrink-0'>
                                                    Tải lên
                                                    <input
                                                        type='file'
                                                        accept='image/*'
                                                        className='hidden'
                                                        onChange={(event) => handleImageUpload(event, (url) => setProductForm((prev) => ({ ...prev, imgUrl: url })))}
                                                    />
                                                </label>
                                            </div>
                                            <input
                                                value={productForm.sold}
                                                onChange={(event) => setProductForm((prev) => ({ ...prev, sold: event.target.value }))}
                                                className={`border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black ${productModal === 'create-product' ? 'hidden' : ''}`}
                                                type='number'
                                                min={0}
                                                placeholder='Đã bán'
                                            />
                                            <input
                                                value={productForm.rate}
                                                onChange={(event) => setProductForm((prev) => ({ ...prev, rate: event.target.value }))}
                                                className={`border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black ${productModal === 'create-product' ? 'hidden' : ''}`}
                                                type='number'
                                                min={0}
                                                max={5}
                                                step='0.1'
                                                placeholder='Đánh giá'
                                            />
                                            <input
                                                value={productForm.videoUrl}
                                                onChange={(event) => setProductForm((prev) => ({ ...prev, videoUrl: event.target.value }))}
                                                className={`border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black sm:col-span-2 ${productModal === 'create-product' ? 'hidden' : ''}`}
                                                placeholder='URL video'
                                            />
                                            <textarea
                                                value={productForm.description}
                                                onChange={(event) => setProductForm((prev) => ({ ...prev, description: event.target.value }))}
                                                className='min-h-28 border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black sm:col-span-2'
                                                placeholder='Mô tả sản phẩm'
                                            />
                                        </div>
                                        <div className='mt-6 flex justify-between gap-3'>
                                            {productModal === 'edit-product' ? (
                                                <button
                                                    type='button'
                                                    onClick={deleteProduct}
                                                    disabled={submittingProductAdmin}
                                                    className='border border-red-500 text-red-500 px-6 py-3 text-sm font-medium hover:bg-red-500 hover:text-white disabled:opacity-50 transition-all'
                                                >
                                                    Xóa sản phẩm
                                                </button>
                                            ) : <div />}
                                            <div className='flex gap-3'>
                                                <button type='button' onClick={closeProductModal} className='border border-black px-6 py-3 text-sm font-medium hover:bg-black hover:text-white transition-all'>
                                                    Hủy
                                                </button>
                                                <button type='submit' disabled={submittingProductAdmin} className='bg-black px-6 py-3 text-sm font-medium text-white disabled:bg-gray-300'>
                                                    {productModal === 'edit-product' ? 'Lưu thay đổi' : 'Tạo sản phẩm'}
                                                </button>
                                            </div>
                                        </div>
                                    </form>
                                )}

                                {productModal === 'items' && (
                                    <form onSubmit={addProductItems} className='mt-5'>
                                        <input
                                            value={itemProductCode}
                                            onChange={(event) => setItemProductCode(event.target.value)}
                                            className='w-full border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                            placeholder='Mã sản phẩm'
                                        />
                                        <div className='mt-4 space-y-3'>
                                            {itemRows.map((row, index) => (
                                                <div key={`${row.size}-${index}`} className='grid grid-cols-[1.2fr_1.2fr_1fr_auto] gap-2'>
                                                    <select
                                                        value={row.size}
                                                        onChange={(event) => updateItemRow(index, { size: event.target.value as ProductSize })}
                                                        className='border border-gray-300 px-3 py-3 text-sm outline-none focus:border-black'
                                                    >
                                                        {productSizes.map((size) => <option key={size} value={size}>{size}</option>)}
                                                    </select>
                                                    <select
                                                        value={row.status}
                                                        onChange={(event) => updateItemRow(index, { status: event.target.value as ItemStatus })}
                                                        className='border border-gray-300 px-3 py-3 text-sm outline-none focus:border-black'
                                                    >
                                                        {itemStatuses.map((status) => <option key={status} value={status}>{status}</option>)}
                                                    </select>
                                                    <input
                                                        value={row.quantity}
                                                        onChange={(event) => {
                                                            const value = event.target.value;
                                                            if (/^\d*$/.test(value)) {
                                                                updateItemRow(index, { quantity: value });
                                                            }
                                                        }}
                                                        className='border border-gray-300 px-3 py-3 text-sm outline-none focus:border-black'
                                                        type='text'
                                                        inputMode='numeric'
                                                        placeholder='SL'
                                                    />
                                                    <button
                                                        type='button'
                                                        onClick={() => updateSingleItemQuantity(row)}
                                                        disabled={submittingProductAdmin}
                                                        className='min-w-11 bg-black px-4 text-lg font-medium text-white disabled:bg-gray-300'
                                                        title='Cáº­p nháº­t sá»‘ lÆ°á»£ng'
                                                    >
                                                        +
                                                    </button>
                                                </div>
                                            ))}
                                        </div>
                                        <div className='mt-6 flex justify-between gap-3'>
                                            <button type='button' onClick={addItemRow} className='border border-black px-6 py-3 text-sm font-medium hover:bg-black hover:text-white transition-all'>
                                                Thêm dòng
                                            </button>
                                            <div className='flex gap-3'>
                                                <button type='button' onClick={closeProductModal} className='border border-black px-6 py-3 text-sm font-medium hover:bg-black hover:text-white transition-all'>
                                                    Hủy
                                                </button>
                                                <button type='submit' disabled={submittingProductAdmin} className='bg-black px-6 py-3 text-sm font-medium text-white disabled:bg-gray-300'>
                                                    Lưu tồn kho
                                                </button>
                                            </div>
                                        </div>
                                    </form>
                                )}

                                {productModal === 'category' && (
                                    <form onSubmit={createCategory} className='mt-5'>
                                        <div className='grid gap-4 sm:grid-cols-2'>
                                            <input
                                                value={categoryForm.name}
                                                onChange={(event) => setCategoryForm((prev) => ({ ...prev, name: event.target.value }))}
                                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                                placeholder='Tên danh mục'
                                            />
                                            <input
                                                value={categoryForm.code}
                                                onChange={(event) => setCategoryForm((prev) => ({ ...prev, code: event.target.value }))}
                                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                                placeholder='Mã danh mục'
                                            />
                                            <input
                                                list='admin-category-codes'
                                                value={categoryForm.parentCode}
                                                onChange={(event) => setCategoryForm((prev) => ({ ...prev, parentCode: event.target.value }))}
                                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                                placeholder='Mã danh mục cha'
                                            />
                                            <div className='flex items-center gap-2 border border-gray-300 px-4 focus-within:border-black'>
                                                <input
                                                    value={categoryForm.imgUrl}
                                                    onChange={(event) => setCategoryForm((prev) => ({ ...prev, imgUrl: event.target.value }))}
                                                    className='w-full text-sm outline-none py-3 bg-transparent'
                                                    placeholder='URL ảnh'
                                                />
                                                <label className='cursor-pointer text-xs font-semibold uppercase bg-gray-100 hover:bg-black hover:text-white px-2.5 py-1.5 transition-all text-gray-700 whitespace-nowrap shrink-0'>
                                                    Tải lên
                                                    <input
                                                        type='file'
                                                        accept='image/*'
                                                        className='hidden'
                                                        onChange={(event) => handleImageUpload(event, (url) => setCategoryForm((prev) => ({ ...prev, imgUrl: url })))}
                                                    />
                                                </label>
                                            </div>
                                            <textarea
                                                value={categoryForm.description}
                                                onChange={(event) => setCategoryForm((prev) => ({ ...prev, description: event.target.value }))}
                                                className='min-h-24 border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black sm:col-span-2'
                                                placeholder='Mô tả danh mục'
                                            />
                                        </div>
                                        <div className='mt-6 flex justify-end gap-3'>
                                            <button type='button' onClick={closeProductModal} className='border border-black px-6 py-3 text-sm font-medium hover:bg-black hover:text-white transition-all'>
                                                Hủy
                                            </button>
                                            <button type='submit' disabled={submittingProductAdmin} className='bg-black px-6 py-3 text-sm font-medium text-white disabled:bg-gray-300'>
                                                Tạo danh mục
                                            </button>
                                        </div>
                                    </form>
                                )}
                            </div>
                        </div>
                    )}
                </div>
            ) : activeTab === 'providers' ? (
                <div className='grid xl:grid-cols-[0.95fr_1.05fr] gap-8 items-start'>
                    <form
                        onSubmit={(event) => {
                            if (editingProviderCode) {
                                event.preventDefault();
                                updateProvider();
                                return;
                            }
                            createProvider(event);
                        }}
                        className='border bg-white p-5 sm:p-6 shadow-sm'
                    >
                        <div className='flex flex-col md:flex-row md:items-start md:justify-between gap-3 border-b border-gray-100 pb-5'>
                            <div>
                                <p className='text-lg font-medium text-gray-900'>Nhà cung cấp</p>
                                <p className='mt-1 text-sm text-gray-500'>
                                    {editingProviderCode ? `Đang chỉnh sửa ${editingProviderCode}` : 'Tạo mới hoặc chọn Edit từ danh sách để cập nhật nhà phân phối.'}
                                </p>
                            </div>
                            <button
                                type='button'
                                onClick={fetchProductMeta}
                                disabled={loadingProductMeta}
                                className='border border-black px-4 py-2 text-xs font-medium hover:bg-black hover:text-white transition-all disabled:border-gray-300 disabled:text-gray-300'
                            >
                                Tải danh sách
                            </button>
                        </div>

                        <div className='mt-5 grid gap-4 sm:grid-cols-2'>
                            <input
                                value={providerUpdateCode}
                                onChange={(event) => setProviderUpdateCode(event.target.value)}
                                disabled={Boolean(editingProviderCode)}
                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                placeholder='Mã cần cập nhật'
                            />
                            <input
                                value={providerForm.providerCode}
                                onChange={(event) => setProviderForm((prev) => ({ ...prev, providerCode: event.target.value }))}
                                disabled={Boolean(editingProviderCode)}
                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                placeholder='Mã khi tạo mới'
                            />
                            <input
                                value={providerForm.name}
                                onChange={(event) => setProviderForm((prev) => ({ ...prev, name: event.target.value }))}
                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black sm:col-span-2'
                                placeholder='Tên nhà cung cấp'
                            />
                            <input
                                value={providerForm.email}
                                onChange={(event) => setProviderForm((prev) => ({ ...prev, email: event.target.value }))}
                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                placeholder='Email'
                            />
                            <input
                                value={providerForm.phone}
                                onChange={(event) => setProviderForm((prev) => ({ ...prev, phone: event.target.value }))}
                                className='border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black'
                                placeholder='Số điện thoại'
                            />
                            <div className='flex items-center gap-2 border border-gray-300 px-4 focus-within:border-black sm:col-span-2'>
                                <input
                                    value={providerForm.img}
                                    onChange={(event) => setProviderForm((prev) => ({ ...prev, img: event.target.value }))}
                                    className='w-full text-sm outline-none py-3 bg-transparent'
                                    placeholder='URL ảnh'
                                />
                                <label className='cursor-pointer text-xs font-semibold uppercase bg-gray-100 hover:bg-black hover:text-white px-2.5 py-1.5 transition-all text-gray-700 whitespace-nowrap shrink-0'>
                                    Tải lên
                                    <input
                                        type='file'
                                        accept='image/*'
                                        className='hidden'
                                        onChange={(event) => handleImageUpload(event, (url) => setProviderForm((prev) => ({ ...prev, img: url })))}
                                    />
                                </label>
                            </div>
                            <textarea
                                value={providerForm.description}
                                onChange={(event) => setProviderForm((prev) => ({ ...prev, description: event.target.value }))}
                                className='min-h-24 border border-gray-300 px-4 py-3 text-sm outline-none focus:border-black sm:col-span-2'
                                placeholder='Mô tả'
                            />
                        </div>

                        {editingProviderCode ? (
                            <div className='mt-6 grid gap-3 sm:grid-cols-3'>
                                <button type='submit' disabled={submittingProductAdmin} className='bg-black px-6 py-3 text-sm font-medium text-white disabled:bg-gray-300'>
                                    Save
                                </button>
                                <button type='button' onClick={deleteProvider} disabled={submittingProductAdmin} className='border border-red-500 px-6 py-3 text-sm font-medium text-red-600 hover:bg-red-50 transition-all disabled:border-gray-300 disabled:text-gray-300'>
                                    Delete
                                </button>
                                <button type='button' onClick={resetProviderForm} disabled={submittingProductAdmin} className='border border-black px-6 py-3 text-sm font-medium hover:bg-black hover:text-white transition-all disabled:border-gray-300 disabled:text-gray-300'>
                                    Hủy
                                </button>
                            </div>
                        ) : (
                            <button type='submit' disabled={submittingProductAdmin} className='mt-6 w-full bg-black px-6 py-3 text-sm font-medium text-white disabled:bg-gray-300'>
                                Tạo nhà cung cấp
                            </button>
                        )}
                    </form>

                    <div className='border bg-white p-5 sm:p-6 shadow-sm'>
                        <div className='border-b border-gray-100 pb-5'>
                            <p className='text-lg font-medium text-gray-900'>Danh sách nhà cung cấp</p>
                            <p className='mt-1 text-sm text-gray-500'>Dùng mã nhà cung cấp để cập nhật hoặc gán vào sản phẩm.</p>
                        </div>

                        <div className='mt-5 overflow-x-auto'>
                            <table className='w-full min-w-[520px] text-left text-sm'>
                                <thead>
                                    <tr className='border-b text-xs uppercase tracking-[0.16em] text-gray-400'>
                                        <th className='py-3 font-medium'>Mã</th>
                                        <th className='py-3 font-medium'>Tên</th>
                                        <th className='py-3 font-medium text-right'>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {providers.length === 0 ? (
                                        <tr>
                                            <td colSpan={3} className='py-10 text-center text-gray-500'>
                                                {loadingProductMeta ? 'Đang tải nhà cung cấp...' : 'Chưa có dữ liệu nhà cung cấp'}
                                            </td>
                                        </tr>
                                    ) : providers.map((provider, index) => {
                                        const code = provider.providerCode || provider.code || '';
                                        return (
                                            <tr key={`${code}-${index}`} className='border-b last:border-b-0'>
                                                <td className='py-4 font-medium text-gray-900'>{code || 'Không có'}</td>
                                                <td className='py-4 text-gray-600'>{provider.name || 'Không có'}</td>
                                                <td className='py-4 text-right'>
                                                    <button
                                                        type='button'
                                                        onClick={() => startEditProvider(provider)}
                                                        disabled={submittingProductAdmin}
                                                        className='border border-black px-4 py-2 text-xs font-medium hover:bg-black hover:text-white transition-all disabled:border-gray-300 disabled:text-gray-300'
                                                    >
                                                        Edit
                                                    </button>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            ) : activeTab === 'users' ? (
                <AdminUserManagement token={token} />
            ) : null}
        </div>
    );
};

export default Admin;
