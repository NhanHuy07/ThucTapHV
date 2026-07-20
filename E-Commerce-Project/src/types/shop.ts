export type Product = {
    _id: string;
    code?: string;
    name: string;
    description: string;
    price: number;
    originalPrice?: number;
    finalPrice?: number;
    image: string[];
    category: string;
    categoryCode?: string;
    subCategory: string;
    providerCode?: string;
    sizes: string[];
    date: number;
    bestSeller: boolean;
};

export type CartItems = Record<string, Record<string, number>>;

export type BackendCartItem = {
    productCode: string;
    productName?: string;
    imgUrl?: string;
    sizeId?: number;
    size: string;
    originalPrice?: number;
    finalPrice?: number;
    discountType?: string;
    discountValue?: number;
    quantity: number;
    lineTotal?: number;
};

export type CartDetails = Record<string, Record<string, BackendCartItem>>;

export type ApiResponse<T> = {
    success: boolean;
    message?: string;
    data: T;
};

export type PageResponse<T> = {
    items: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    hasNext: boolean;
};

export type NotificationItem = {
    code: string;
    senderName?: string | null;
    receiverName?: string | null;
    title: string;
    message: string;
    readed?: boolean;
    type?: string;
    createdAt?: string;
};

export type NotificationScope = 'user' | 'admin';

export type VoucherDiscountType = 'PERCENT' | 'FIXED';

export type UserVoucher = {
    code: string;
    type: VoucherDiscountType;
    value: number;
    minOrderAmount: number;
    status: 'AVAILABLE' | 'USED' | 'EXPIRED' | string;
    endAt?: string;
};

export type ProductComment = {
    productCode: string;
    content: string;
    rating: number;
    createdAt?: string;
};

export type ProductCommentPayload = {
    productCode: string;
    content: string;
    rating: number;
};

export type BackendProductSummary = {
    code: string;
    name: string;
    price?: number;
    originalPrice?: number;
    rated?: number;
    imgUrl?: string;
    categoryCode?: string;
};

export type BackendProductDetail = {
    name: string;
    productCode: string;
    description?: string;
    originalPrice?: number;
    finalPrice?: number;
    imgUrl?: string;
    status?: string;
    items?: Array<{
        productCode: string;
        size: string;
        status: string;
        quantity: number;
    }>;
    createdAt?: string;
    category?: {
        name?: string;
        categoryCode?: string;
    };
    provider?: {
        name?: string;
        providerCode?: string;
    };
};

export type BackendCart = {
    items?: BackendCartItem[];
    totalItems: number;
    totalAmount: number;
};

export type RankType = 'Bronze' | 'Silver' | 'Gold' | 'Platinum' | 'Diamond' | 'Ultimate';

export type UserRank = {
    type: RankType;
    minTotalPurchase: number;
    expireTime?: number | null;
};

export type UserProfile = {
    id: string;
    firstName?: string;
    lastName?: string;
    avatar?: string;
    f_name?: string;
    l_name?: string;
    img?: string;
    phone?: string;
    totalPurchase?: number;
    rank?: UserRank;
};

export type AdminUser = {
    id: string;
    firstName: string | null;
    lastName: string | null;
    avatar: string | null;
    totalPurchase: number;
    rank: UserRank;
};

export type UserProfileUpdatePayload = {
    id: string;
    firstName: string;
    lastName: string;
    avatar?: string;
};

export type AccountRole = 'CUSTOMER' | 'ADMIN' | 'EMPLOYEE';

export type OrderStatus =
    | 'UNPAID'
    | 'PAID'
    | 'PENDING'
    | 'CANCELLED'
    | 'CONFIRMED'
    | 'SHIPPING'
    | 'DELIVERED'
    | 'COMPLETED'
    | 'RETURNED';

export type OrderSummary = {
    orderCode: string;
    status: OrderStatus;
    finalPrice: number;
    totalItems: number;
    createdAt: string;
};

export type OrderItem = {
    productCode: string;
    size: string;
    quantity: number;
    originalPrice?: number;
    finalPrice?: number;
};

export type Receiver = {
    id?: number;
    fName?: string;
    lName?: string;
    phone?: string;
    addr?: {
        country?: string;
        province?: string;
        district?: string;
        street?: string;
        detail?: string;
    };
};

export type OrderDetail = {
    orderCode: string;
    status: OrderStatus;
    paymentType?: 'PAYMENT_UPON_DELIVER' | 'ONLINE';
    voucherCode?: string;
    reciever?: Receiver;
    receiver?: Receiver;
    note?: string;
    items?: OrderItem[];
    totalAmount?: number;
    voucherDiscount?: number;
    finalPrice?: number;
    paymentUrl?: string;
    bankTransferQr?: PaymentQr;
    createdAt?: string;
    updatedAt?: string;
};

export type ReceiverCreatePayload = {
    fName: string;
    lName: string;
    phone: string;
    addr: {
        country: string;
        province: string;
        district: string;
        street: string;
        detail: string;
    };
};

export type CreateOrderPayload = {
    recieverid: number;
    items: Array<{
        productCode: string;
        size: string;
        quantity: number;
        originalPrice: number;
        finalPrice?: number;
    }>;
    totalPrice: number;
    voucherCode?: string;
    voucherDiscount?: number;
    finalPrice?: number;
    paymentType: 'PAYMENT_UPON_DELIVER' | 'ONLINE';
    note?: string;
};

export type PaymentQr = {
    provider?: string;
    bankId?: string;
    accountNo?: string;
    accountName?: string;
    amount?: number;
    transferContent?: string;
    qrContent?: string;
    qrImageUrl?: string;
};

export type CreateOrderResponse = {
    orderCode: string;
    status: OrderStatus;
    paymentType?: 'PAYMENT_UPON_DELIVER' | 'ONLINE';
    paymentUrl?: string;
    bankTransferQr?: PaymentQr;
};
