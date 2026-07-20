import { useContext, useMemo } from 'react';
import { ShopContext } from '../context/ShopContext';
import ProductItem from './ProductItem';
import Title from './Title';
import type { Product } from '../types/shop';

interface RelatedProductsProps {
    category: string;
    subCategory: string;
}

const RelatedProducts = ({ category, subCategory }: RelatedProductsProps) => {

    const context = useContext(ShopContext);
    
    const related = useMemo(() => {
        if (!context || context.products.length === 0) return [];
        let productsCopy = (context.products as Product[]).slice();
        productsCopy = productsCopy.filter((item) => category === item.category);
        productsCopy = productsCopy.filter((item) => subCategory === item.subCategory);
        return productsCopy.slice(0, 5);
    }, [context, category, subCategory]);

    return (
        <div className="my-24">
            <div className="text-center text-3xl py-2">
                <Title text1={'SẢN PHẨM'} text2={'LIÊN QUAN'} />
            </div>
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-y-6">
                {related.map((item, index) => (
                    <ProductItem
                        key={index}
                        id={item._id}
                        name={item.name}
                        price={item.price}
                        originalPrice={item.originalPrice}
                        image={item.image}
                        sizes={item.sizes}
                    />
                ))}
            </div>
        </div>
    );
};

export default RelatedProducts;
