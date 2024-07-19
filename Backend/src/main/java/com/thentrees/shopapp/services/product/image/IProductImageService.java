package com.thentrees.shopapp.services.product.image;

import com.thentrees.shopapp.models.ProductImage;

public interface IProductImageService {
    ProductImage deleteProductImage(Long id) throws Exception;
}
