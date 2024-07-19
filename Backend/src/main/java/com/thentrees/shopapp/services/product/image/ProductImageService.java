package com.thentrees.shopapp.services.product.image;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thentrees.shopapp.components.LocalizationUtils;
import com.thentrees.shopapp.exceptions.ResourceNotFoundException;
import com.thentrees.shopapp.models.ProductImage;
import com.thentrees.shopapp.repositories.ProductImageRepository;
import com.thentrees.shopapp.utils.MessageKeys;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductImageService implements IProductImageService {
    private final ProductImageRepository productImageRepository;
    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public ProductImage deleteProductImage(Long id) throws Exception {
        Optional<ProductImage> productImage = productImageRepository.findById(id);
        if (productImage.isEmpty()) {
            throw new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND));
        }
        productImageRepository.deleteById(id);
        return productImage.get();
    }
}
