package com.thentrees.shopapp.repositories.searchHandle;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.thentrees.shopapp.dtos.responses.PageResponse;

@Component
public class ProductSearchRepository {

    //    package com.thentrees.shopapp.dtos.responses.product;

    @PersistenceContext
    private EntityManager entityManager;

    public PageResponse<?> searchProduct(int pageNo, int pageSize, String sortBy, String keyword, Long categoryId) {

        StringBuilder sqlQuery = new StringBuilder(
                "SELECT new com.thentrees.shopapp.dtos.responses.product.ProductDTOResponse(p.name, p.price, p.description, p.categoryId) FROM Product p WHERE 1=1");

        if (StringUtils.hasLength(keyword)) {
            sqlQuery.append(" AND lower(p.name) LIKE lower(:keyword)");
            sqlQuery.append(" OR lower(p.price) LIKE lower(:price)");
            sqlQuery.append(" OR lower(p.description) LIKE lower(:description)");
            sqlQuery.append(" OR lower(p.categoryId) LIKE lower(:categoryId)");
        }

        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                sqlQuery.append(String.format(" ORDER BY u.%s %s", matcher.group(1), matcher.group(3)));
            }
        }

        // get list of product
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        if (StringUtils.hasLength(keyword)) {
            selectQuery.setParameter("keyword", String.format("%%%s%%", keyword));
            selectQuery.setParameter("price", String.format("%%%s%%", keyword));
            selectQuery.setParameter("description", String.format("%%%s%%", keyword));
            selectQuery.setParameter("categoryId", String.format("%%%s%%", keyword));
        }
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        List<?> products = selectQuery.getResultList();

        // count product

        StringBuilder sqlCountQuery = new StringBuilder("SELECT COUNT(*) FROM Product p");
        if (StringUtils.hasLength(keyword)) {
            sqlQuery.append(" WHERE lower(p.name) LIKE lower(?1)");
            sqlQuery.append(" OR lower(p.price) LIKE lower(?2)");
            sqlQuery.append(" OR lower(p.description) LIKE lower(?3)");
            sqlQuery.append(" OR lower(p.categoryId) LIKE lower(?4)");
        }

        Query countQuery = entityManager.createQuery(sqlCountQuery.toString());
        if (StringUtils.hasLength(keyword)) {
            countQuery.setParameter(1, String.format("%%%s%%", keyword));
            countQuery.setParameter(2, String.format("%%%s%%", keyword));
            countQuery.setParameter(3, String.format("%%%s%%", keyword));
            countQuery.setParameter(4, String.format("%%%s%%", keyword));
            countQuery.getSingleResult();
        }

        Long totalElement = (Long) countQuery.getSingleResult();
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<?> page = new PageImpl<>(products, pageable, totalElement);

        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .total(page.getTotalPages())
                .datas(products)
                .build();
    }
}
