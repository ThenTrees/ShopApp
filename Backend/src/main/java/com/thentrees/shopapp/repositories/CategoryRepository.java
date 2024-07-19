package com.thentrees.shopapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thentrees.shopapp.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
