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

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserSearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private final static String LIKE_FORMAT = "%%%s%%";

    public PageResponse<Object> searchUser(int pageNo, int pageSize, String sortBy, String keyword) {

        StringBuilder sqlQuery = new StringBuilder(
                "SELECT new com.thentrees.shopapp.dtos.responses.user.UserDTOResponse(u.fullName, u.phoneNumber, u.address) FROM User u WHERE 1=1");

        if (StringUtils.hasLength(keyword)) {
            sqlQuery.append(" AND lower(u.fullName) LIKE lower(?1)");
            sqlQuery.append(" OR lower(u.phoneNumber) LIKE lower(?2)");
            sqlQuery.append(" OR lower(u.address) LIKE lower(?3)");
        }

        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                sqlQuery.append(String.format(" ORDER BY u.%s %s", matcher.group(1), matcher.group(3)));
            }
            log.info("Sort By: {}", matcher.group(3));
        }

        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        if (StringUtils.hasLength(keyword)) {
            selectQuery.setParameter(1, String.format(LIKE_FORMAT, keyword));
            selectQuery.setParameter(2, String.format(LIKE_FORMAT, keyword));
            selectQuery.setParameter(3, String.format(LIKE_FORMAT, keyword));
        }

        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        List<?> users = selectQuery.getResultList();

        // count users
        StringBuilder sqlCountQuery = new StringBuilder("SELECT COUNT(u) FROM User u");
        if (StringUtils.hasLength(keyword)) {
            sqlCountQuery.append(" WHERE lower(u.fullName) LIKE lower(?1)");
            sqlCountQuery.append(" OR lower(u.phoneNumber) LIKE lower(?2)");
            sqlCountQuery.append(" OR lower(u.address) LIKE lower(?3)");
        }
        Query countQuery = entityManager.createQuery(sqlCountQuery.toString());
        if (StringUtils.hasLength(keyword)) {
            countQuery.setParameter(1, String.format(LIKE_FORMAT, keyword));
            countQuery.setParameter(2, String.format(LIKE_FORMAT, keyword));
            countQuery.setParameter(3, String.format(LIKE_FORMAT, keyword));
            countQuery.getSingleResult();
        }

        Long totalElements = (Long) countQuery.getSingleResult();

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<?> page = new PageImpl<>(users, pageable, totalElements);

        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .total(page.getTotalPages())
                .datas(users)
                .build();
    }
}
