import React from "react";
import { createContext, useCallback, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import { assets } from "../assets/assets";
import { apiBaseUrl, apiRequest } from "../lib/api";
import { mapBackendCart, mapBackendCartDetails, mapProductDetail, mapProductSummary } from "../lib/productMapper";
import type { AccountRole, ApiResponse, BackendCart, BackendProductDetail, BackendProductSummary, CartDetails, CartItems, PageResponse, Product, UserProfile, UserProfileUpdatePayload } from "../types/shop";

export const ShopContext = createContext<any>(null);

const getRoleFromToken = (authToken: string): AccountRole | null => {
    try {
        const payload = authToken.split('.')[1];
        if (!payload) return null;

        const normalizedPayload = payload.replace(/-/g, '+').replace(/_/g, '/');
        const paddedPayload = normalizedPayload.padEnd(normalizedPayload.length + (4 - normalizedPayload.length % 4) % 4, '=');
        const decodedPayload = JSON.parse(atob(paddedPayload));
        const role = decodedPayload?.role;

        return role === 'ADMIN' || role === 'CUSTOMER' || role === 'EMPLOYEE' ? role : null;
    } catch (error) {
        console.error(error);
        return null;
    }
};

const fallbackProducts: Product[] = [
    {
        _id: "aaaaa",
        name: "Premium Cotton T-Shirt",
        description: "A high-quality cotton T-shirt, perfect for everyday wear.",
        price: 100,
        image: [assets.p_img1],
        category: "Men",
        subCategory: "Topwear",
        sizes: ["S", "M", "L"],
        date: 1716634345484,
        bestSeller: true
    },
    {
        _id: "aaaab",
        name: "Athletic Running Shorts",
        description: "Lightweight and breathable shorts for your daily workout.",
        price: 150,
        image: [assets.p_img2_1],
        category: "Men",
        subCategory: "Bottomwear",
        sizes: ["M", "L", "XL"],
        date: 1716621345484,
        bestSeller: true
    },
    {
        _id: "aaaac",
        name: "Classic Denim Jacket",
        description: "A timeless denim jacket that goes with any outfit.",
        price: 210,
        image: [assets.p_img3],
        category: "Unisex",
        subCategory: "Topwear",
        sizes: ["S", "M", "L", "XL"],
        date: 1716622345484,
        bestSeller: true
    },
    {
        _id: "aaaad",
        name: "Performance Sports Hoodie",
        description: "Engineered for style and performance.",
        price: 180,
        image: [assets.p_img4],
        category: "Men",
        subCategory: "Topwear",
        sizes: ["L", "XL", "XXL"],
        date: 1716623345484,
        bestSeller: true
    },
    {
        _id: "aaaae",
        name: "Summer Floral Dress",
        description: "A beautiful floral dress perfect for summer days.",
        price: 250,
        image: [assets.p_img1],
        category: "Women",
        subCategory: "Dress",
        sizes: ["S", "M", "L"],
        date: 1716624345484,
        bestSeller: true
    },
    {
        _id: "aaaaf",
        name: "Casual Slim Fit Jeans",
        description: "Stylish slim fit jeans for a modern look.",
        price: 160,
        image: [assets.p_img2_1],
        category: "Men",
        subCategory: "Bottomwear",
        sizes: ["30", "32", "34"],
        date: 1716625345484,
        bestSeller: true
    },
    {
        _id: "aaaag",
        name: "Running Shoes",
        description: "Comfortable running shoes for your daily jog.",
        price: 120,
        image: [assets.p_img3],
        category: "Unisex",
        subCategory: "Footwear",
        sizes: ["8", "9", "10"],
        date: 1716626345484,
        bestSeller: true
    },
    {
        _id: "aaaah",
        name: "Leather Wallet",
        description: "A premium leather wallet to keep your cards safe.",
        price: 50,
        image: [assets.p_img4],
        category: "Accessories",
        subCategory: "Wallet",
        sizes: [],
        date: 1716627345484,
        bestSeller: false
    }
];

const ShopContextProvider = (props: { children: React.ReactNode }) => {

    const currency = '₫';
    const delivery_fee = 30000;
    const [search, setSearch] = useState('');
    const [showSearch, setShowSearch] = useState(false);
    const [cartItems, setCartItems] = useState<CartItems>({});
    const [cartDetails, setCartDetails] = useState<CartDetails>({});
    const [products, setProducts] = useState<Product[]>(fallbackProducts);
    const [token, setToken] = useState(localStorage.getItem('token') || '');
    const [accountRole, setAccountRole] = useState<AccountRole | null>(() => getRoleFromToken(localStorage.getItem('token') || ''));
    const [user, setUser] = useState<UserProfile | null>(null);
    const [loadingProducts, setLoadingProducts] = useState(false);
    const navigate = useNavigate();

    const getProductsData = useCallback(async () => {
        if (!apiBaseUrl) return;

        setLoadingProducts(true);
        try {
            const data = await apiRequest<ApiResponse<PageResponse<BackendProductSummary>>>('/v1/api/public/product/get-all?page=0&size=20', {
                method: 'GET',
            });
            const apiProducts = data.data?.items || [];

            if (data.success === false || !Array.isArray(apiProducts)) {
                throw new Error('Invalid products response');
            }

            const productsWithDetails = await Promise.all(
                apiProducts.map(async (product) => {
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
        } catch (error) {
            console.error(error);
            toast.error('Không thể tải sản phẩm từ backend, đang dùng dữ liệu mẫu');
        } finally {
            setLoadingProducts(false);
        }
    }, []);

    const getUserCart = useCallback(async (authToken: string) => {
        if (!apiBaseUrl || !authToken) return;

        try {
            const data = await apiRequest<ApiResponse<BackendCart>>('/v1/api/user/cart', {
                method: 'GET',
                token: authToken,
            });

            if (data.success !== false && data.data) {
                setCartItems(mapBackendCart(data.data));
                setCartDetails(mapBackendCartDetails(data.data));
            }
        } catch (error) {
            console.error(error);
        }
    }, []);

    const getUserProfile = useCallback(async (authToken: string) => {
        if (!apiBaseUrl || !authToken) return;

        try {
            const data = await apiRequest<ApiResponse<UserProfile>>('/v1/api/user/get/me', {
                method: 'GET',
                token: authToken,
            });

            setUser(data.data);
        } catch (error) {
            console.error(error);
            setToken('');
            setUser(null);
            toast.error('Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại');
        }
    }, []);

    const updateUserProfile = async (payload: UserProfileUpdatePayload) => {
        if (!token) {
            throw new Error('Bạn cần đăng nhập để cập nhật hồ sơ');
        }

        const data = await apiRequest<ApiResponse<UserProfile>>('/v1/api/user/update/profile', {
            method: 'PUT',
            token,
            body: payload,
        });

        if (data.data) {
            setUser(data.data);
        } else {
            await getUserProfile(token);
        }

        return data;
    }

    const refreshCart = async () => {
        if (token) {
            await getUserCart(token);
        }
    }

    const refreshUserProfile = async () => {
        if (token) {
            await getUserProfile(token);
        }
    }

    const addToCart = async (itemId: string, size: string) => {

        if (!size) {
            toast.error('Vui lòng chọn size');
            return;
        }

        if (token) {
            try {
                await apiRequest('/v1/api/user/cart/add', {
                    method: 'POST',
                    token,
                    body: { productCode: itemId, size, quantity: 1 },
                });
                await getUserCart(token);
                toast.success('Thêm vào giỏ hàng thành công');
                return;
            } catch (error) {
                console.error(error);
                const message = error instanceof Error ? error.message : 'Không thể đồng bộ giỏ hàng với backend';
                toast.error(message);
                return;
            }
        }

        const cartData = structuredClone(cartItems);
        const detailData = structuredClone(cartDetails);
        const product = products.find((item) => item._id === itemId);

        if (cartData[itemId]) {
            if (cartData[itemId][size]) {
                cartData[itemId][size] += 1;
            }
            else {
                cartData[itemId][size] = 1;
            }
        }
        else {
            cartData[itemId] = {};
            cartData[itemId][size] = 1;
        }
        setCartItems(cartData);
        if (product) {
            if (!detailData[itemId]) detailData[itemId] = {};
            detailData[itemId][size] = {
                productCode: itemId,
                productName: product.name,
                imgUrl: product.image[0],
                size,
                originalPrice: product.originalPrice ?? product.price,
                finalPrice: product.finalPrice ?? product.price,
                quantity: cartData[itemId][size],
            };
            setCartDetails(detailData);
        }

        toast.success('Thêm vào giỏ hàng thành công');
    }

    const getCartCount = () => {
        let totalCount = 0;
        for (const items in cartItems) {
            for (const item in cartItems[items]) {
                try {
                    if (cartItems[items][item] > 0) {
                        totalCount += cartItems[items][item];
                    }
                } catch (error) {

                }
            }
        }
        return totalCount;
    }

    const updateQuantity = async (itemId: string, size: string, quantity: number) => {
        if (quantity < 1) {
            return;
        }

        const cartData = structuredClone(cartItems);
        cartData[itemId][size] = quantity;
        setCartItems(cartData);
        setCartDetails((current) => {
            const next = structuredClone(current);
            if (next[itemId]?.[size]) {
                next[itemId][size].quantity = quantity;
                const price = next[itemId][size].finalPrice ?? next[itemId][size].originalPrice ?? 0;
                next[itemId][size].lineTotal = price * quantity;
            }
            return next;
        });

        if (token) {
            try {
                await apiRequest('/v1/api/user/cart/update-quantity', {
                    method: 'PUT',
                    token,
                    body: { productCode: itemId, size, quantity },
                });
            } catch (error) {
                console.error(error);
                toast.error('Không thể cập nhật giỏ hàng trên backend');
            }
        }
    }

    const removeFromCart = async (itemId: string, size: string) => {
        if (token) {
            try {
                await apiRequest(`/v1/api/user/cart/remove/${encodeURIComponent(itemId)}/${encodeURIComponent(size)}`, {
                    method: 'DELETE',
                    token,
                });
            } catch (error) {
                console.error(error);
                const message = error instanceof Error ? error.message : 'Không thể xóa sản phẩm khỏi giỏ hàng';
                toast.error(message);
                return;
            }
        }

        const cartData = structuredClone(cartItems);
        const detailData = structuredClone(cartDetails);

        if (cartData[itemId]) {
            delete cartData[itemId][size];
            delete detailData[itemId]?.[size];

            if (Object.keys(cartData[itemId]).length === 0) {
                delete cartData[itemId];
                delete detailData[itemId];
            }
        }

        setCartItems(cartData);
        setCartDetails(detailData);
        toast.success('Đã xóa sản phẩm khỏi giỏ hàng');
    }

    const getCartAmount = () => {
        let totalAmount = 0;
        for (const items in cartItems) {
            let itemInfo = products.find((product) => product._id === items);
            for (const item in cartItems[items]) {
                try {
                    if (cartItems[items][item] > 0) {
                        const detail = cartDetails[items]?.[item];
                        const price = detail?.finalPrice ?? itemInfo?.price ?? 0;
                        totalAmount += price * cartItems[items][item];
                    }
                } catch (error) {

                }
            }
        }
        return totalAmount;
    }

    useEffect(() => {
        getProductsData();
    }, [getProductsData]);

    useEffect(() => {
        if (token) {
            localStorage.setItem('token', token);
            setAccountRole(getRoleFromToken(token));
            getUserProfile(token);
            getUserCart(token);
        } else {
            localStorage.removeItem('token');
            setAccountRole(null);
            setUser(null);
            setCartItems({});
            setCartDetails({});
        }
    }, [getUserCart, getUserProfile, token]);

    const value = {
        products, currency, delivery_fee,
        search, setSearch, showSearch, setShowSearch,
        cartItems, cartDetails, addToCart,
        getCartCount, updateQuantity, removeFromCart, getCartAmount, refreshCart,
        token, setToken, accountRole, isAdmin: accountRole === 'ADMIN', user, updateUserProfile, refreshUserProfile, loadingProducts,
        navigate
    }

    return (
        <ShopContext.Provider value={value}>
            {props.children}
        </ShopContext.Provider>
    )

}

export default ShopContextProvider;
