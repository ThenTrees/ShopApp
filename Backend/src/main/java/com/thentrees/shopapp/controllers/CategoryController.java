package com.thentrees.shopapp.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.thentrees.shopapp.components.LocalizationUtils;
import com.thentrees.shopapp.dtos.requests.category.CategoryDTORequest;
import com.thentrees.shopapp.dtos.responses.ResponseObject;
import com.thentrees.shopapp.dtos.responses.category.CategoryDTOResponse;
import com.thentrees.shopapp.models.Category;
import com.thentrees.shopapp.services.category.ICategoryService;
import com.thentrees.shopapp.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class CategoryController {
    ICategoryService categoryService;
    LocalizationUtils localizationUtils;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    // Nếu tham số truyền vào là 1 object thì sao ? => Data Transfer Object = Request Object
    public ResponseEntity<ResponseObject> createCategory(@Valid @RequestBody CategoryDTORequest request) {
        CategoryDTOResponse categoryResponse = new CategoryDTOResponse();
        Category category = categoryService.createCategory(request);
        categoryResponse.setCategory(category);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .message(localizationUtils.getLocalizationMessage(MessageKeys.INSERT_CATEGORY_SUCCESSFULLY))
                        .code(HttpStatus.OK.value())
                        .data(categoryResponse)
                        .build());
    }

    // Hiện tất cả các categories
    @GetMapping
    public ResponseEntity<ResponseObject> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .message("Get list of categories successfully")
                        .code(HttpStatus.OK.value())
                        .data(categories)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getCategoryById(@PathVariable("id") Long categoryId) {
        Category existingCategory = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingCategory)
                .message("Get category information successfully")
                .code(HttpStatus.OK.value())
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateCategory(
            @PathVariable Long id, @Valid @RequestBody CategoryDTORequest categoryDto) {
        categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(ResponseObject.builder()
                .message(localizationUtils.getLocalizationMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY))
                .code(HttpStatus.OK.value())
                .data(categoryService.getCategoryById(id))
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteCategory(@PathVariable Long id) throws Exception {
        Category c = categoryService.deleteCategory(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .code(HttpStatus.OK.value())
                .message(localizationUtils.getLocalizationMessage(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY))
                .data(c)
                .build());
    }
}
