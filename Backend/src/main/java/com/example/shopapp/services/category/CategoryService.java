package com.example.shopapp.services.category;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopapp.dtos.requests.CategoryDtoRequest;
import com.example.shopapp.models.Category;
import com.example.shopapp.models.Product;
import com.example.shopapp.repositories.CategoryRepository;
import com.example.shopapp.repositories.ProductRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CategoryService implements ICategoryService {

    CategoryRepository categoryRepository;
    ProductRepository productRepository;

    @Override
    @Transactional
    public Category createCategory(CategoryDtoRequest categoryDtoRequest) {
        Category newCategory =
                Category.builder().name(categoryDtoRequest.getName()).build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category updateCategory(long categoryId, CategoryDtoRequest categoryDtoRequest) {
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setName(categoryDtoRequest.getName());
        categoryRepository.save(existingCategory);
        return existingCategory;
    }

    @Override
    public Category deleteCategory(long id) throws Exception {
        Category category =
                categoryRepository.findById(id).orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        List<Product> products = productRepository.findByCategory(category);
        if (!products.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with associated products");
        } else {
            categoryRepository.deleteById(id);
            return category;
        }
    }
}
