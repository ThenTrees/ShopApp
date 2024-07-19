package com.thentrees.shopapp.services.category;

import java.util.List;

import com.thentrees.shopapp.dtos.requests.category.CategoryDTORequest;
import com.thentrees.shopapp.models.Category;

public interface ICategoryService {
    Category createCategory(CategoryDTORequest categoryDTORequest);

    Category getCategoryById(long id);

    List<Category> getAllCategories();

    Category updateCategory(long categoryId, CategoryDTORequest categoryDTORequest);

    Category deleteCategory(long id) throws Exception;
}
