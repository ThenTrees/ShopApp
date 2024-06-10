package com.example.shopapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopapp.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
