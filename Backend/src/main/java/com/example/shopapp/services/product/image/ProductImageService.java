package com.example.shopapp.services.product.image;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.exceptions.ResourceNotFoundException;
import com.example.shopapp.models.ProductImage;
import com.example.shopapp.repositories.ProductImageRepository;
import com.example.shopapp.utils.MessageKeys;

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
