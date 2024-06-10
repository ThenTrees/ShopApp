package com.example.shopapp.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.requests.CategoryDtoRequest;
import com.example.shopapp.dtos.responses.CategoryDtoResponse;
import com.example.shopapp.dtos.responses.ResponseObject;
import com.example.shopapp.models.Category;
import com.example.shopapp.services.category.ICategoryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CategoryController {
    ICategoryService categoryService;
    LocalizationUtils localizationUtils;

    @PostMapping
    // Nếu tham số truyền vào là 1 object thì sao ? => Data Transfer Object = Request Object
    public ResponseEntity<CategoryDtoResponse> createCategory(
            @Valid @RequestBody CategoryDtoRequest request, BindingResult result) {
        CategoryDtoResponse categoryResponse = new CategoryDtoResponse();
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(categoryResponse);
        }
        Category category = categoryService.createCategory(request);
        categoryResponse.setCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
    }

    // Hiện tất cả các categories
    @GetMapping
    public ResponseEntity<ResponseObject> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .message("Get list of categories successfully")
                        .status(HttpStatus.OK)
                        .data(categories)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getCategoryById(@PathVariable("id") Long categoryId) {
        Category existingCategory = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingCategory)
                .message("Get category information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateCategory(
            @PathVariable Long id, @Valid @RequestBody CategoryDtoRequest categoryDto) {
        categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(ResponseObject.builder()
                .message(localizationUtils.getLocalizationMessage("category.update_category.update_successfully"))
                .status(HttpStatus.OK)
                .data(categoryService.getCategoryById(id))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteCategory(@PathVariable Long id) throws Exception {
        Category c = categoryService.deleteCategory(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Delete category successfully")
                .data(c)
                .build());
    }
}
