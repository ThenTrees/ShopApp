package com.example.shopapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopapp.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {}
