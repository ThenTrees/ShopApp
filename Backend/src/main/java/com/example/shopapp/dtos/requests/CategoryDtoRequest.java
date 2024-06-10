package com.example.shopapp.dtos.requests;

import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDtoRequest {
    @NotEmpty(message = "Category's name cannot be empty")
    private String name;
}
