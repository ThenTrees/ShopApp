package com.example.shopapp.dtos.responses.order;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderListResponse {
    private int totalPages;
    private List<OrderDTOResponse> orderDTOResponses;
}
