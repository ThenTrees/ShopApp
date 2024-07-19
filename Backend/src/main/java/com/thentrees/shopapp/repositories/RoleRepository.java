package com.thentrees.shopapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thentrees.shopapp.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
