package com.thentrees.shopapp.dtos.requests.category;

import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTORequest {
    @NotEmpty(message = "Category's name cannot be empty")
    private String name;
}
