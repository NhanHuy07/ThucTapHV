import { assets } from '../assets/assets';
import type { BackendCart, BackendProductDetail, BackendProductSummary, CartDetails, CartItems, Product } from '../types/shop';

const fallbackImage = assets.p_img1;

export const mapProductSummary = (product: BackendProductSummary): Product => ({
    _id: product.code,
    code: product.code,
    name: product.name,
    description: '',
    price: product.price ?? product.originalPrice ?? 0,
    originalPrice: product.originalPrice,
    finalPrice: product.price,
    image: [product.imgUrl || fallbackImage],
    category: product.categoryCode || '',
    categoryCode: product.categoryCode,
    subCategory: '',
    sizes: [],
    date: Date.now(),
    bestSeller: false,
});

export const mapProductDetail = (product: BackendProductDetail): Product => {
    const sizes = product.items
        ?.filter((item) => item.status === 'AVAILABLE' && item.quantity > 0)
        .map((item) => item.size) || [];

    return {
        _id: product.productCode,
        code: product.productCode,
        name: product.name,
        description: product.description || '',
        price: product.finalPrice ?? product.originalPrice ?? 0,
        originalPrice: product.originalPrice,
        finalPrice: product.finalPrice,
        image: [product.imgUrl || fallbackImage],
        category: product.category?.name || product.category?.categoryCode || '',
        categoryCode: product.category?.categoryCode,
        subCategory: '',
        providerCode: product.provider?.providerCode,
        sizes,
        date: product.createdAt ? new Date(product.createdAt).getTime() : Date.now(),
        bestSeller: product.status === 'BESTSELLER',
    };
};

export const mapBackendCart = (cart: BackendCart): CartItems => {
    const cartItems: CartItems = {};

    cart.items?.forEach((item) => {
        if (!cartItems[item.productCode]) {
            cartItems[item.productCode] = {};
        }

        cartItems[item.productCode][item.size] = item.quantity;
    });

    return cartItems;
};

export const mapBackendCartDetails = (cart: BackendCart): CartDetails => {
    const cartDetails: CartDetails = {};

    cart.items?.forEach((item) => {
        if (!cartDetails[item.productCode]) {
            cartDetails[item.productCode] = {};
        }

        cartDetails[item.productCode][item.size] = item;
    });

    return cartDetails;
};
