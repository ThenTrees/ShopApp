package com.example.shopapp.services.category;

import java.util.List;

import com.example.shopapp.dtos.requests.CategoryDtoRequest;
import com.example.shopapp.models.Category;

public interface ICategoryService {
    Category createCategory(CategoryDtoRequest categoryDtoRequest);

    Category getCategoryById(long id);

    List<Category> getAllCategories();

    Category updateCategory(long categoryId, CategoryDtoRequest categoryDtoRequest);

    Category deleteCategory(long id) throws Exception;
}
