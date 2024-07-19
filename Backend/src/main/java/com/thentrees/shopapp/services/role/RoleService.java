package com.thentrees.shopapp.services.role;

import java.util.List;

import org.springframework.stereotype.Service;

import com.thentrees.shopapp.models.Role;
import com.thentrees.shopapp.repositories.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
