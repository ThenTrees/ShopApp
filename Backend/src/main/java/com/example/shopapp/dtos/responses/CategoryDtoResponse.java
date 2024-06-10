package com.example.shopapp.dtos.responses;

import java.util.List;

import com.example.shopapp.models.Category;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CategoryDtoResponse {
    @JsonProperty("message")
    String message;

    @JsonProperty("errors")
    List<String> errors;

    @JsonProperty("category")
    Category category;
}
