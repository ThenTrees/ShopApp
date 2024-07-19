package com.thentrees.shopapp.repositories.searchHandle;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.thentrees.shopapp.dtos.responses.PageResponse;
import com.thentrees.shopapp.dtos.responses.order.OrderDTOResponse;

public class OrderSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public PageResponse<OrderDTOResponse> searchOrder(int pageNo, int pageSize, String sortBy, String keyword) {
        return null;
    }
}
