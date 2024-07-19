package com.thentrees.shopapp.services.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thentrees.shopapp.components.LocalizationUtils;
import com.thentrees.shopapp.dtos.requests.category.CategoryDTORequest;
import com.thentrees.shopapp.exceptions.ResourceNotFoundException;
import com.thentrees.shopapp.models.Category;
import com.thentrees.shopapp.models.Product;
import com.thentrees.shopapp.repositories.CategoryRepository;
import com.thentrees.shopapp.repositories.ProductRepository;
import com.thentrees.shopapp.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CategoryService implements ICategoryService {

    CategoryRepository categoryRepository;
    ProductRepository productRepository;
    LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public Category createCategory(CategoryDTORequest categoryDTORequest) {
        Category newCategory =
                Category.builder().name(categoryDTORequest.getName()).build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id) throws ResourceNotFoundException {
        return categoryRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category updateCategory(long categoryId, CategoryDTORequest categoryDTORequest) {
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setName(categoryDTORequest.getName());
        categoryRepository.save(existingCategory);
        return existingCategory;
    }

    @Override
    @Transactional
    public Category deleteCategory(long id) throws IllegalStateException {
        Category category = categoryRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));

        List<Product> products = productRepository.findByCategory(category);
        if (!products.isEmpty()) {
            throw new IllegalStateException(localizationUtils.getLocalizationMessage(MessageKeys.CAN_NOT_DELETE));
        } else {
            categoryRepository.deleteById(id);
            return category;
        }
    }
}
