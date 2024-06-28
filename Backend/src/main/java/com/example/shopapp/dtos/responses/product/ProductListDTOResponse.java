package com.example.shopapp.dtos.responses.product;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ProductListDTOResponse {
    private List<ProductDTOResponse> products;
    private int totalPages;
}
