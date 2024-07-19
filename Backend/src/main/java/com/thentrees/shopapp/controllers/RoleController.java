package com.thentrees.shopapp.controllers;

import java.util.List;

import com.thentrees.shopapp.dtos.responses.ResponseObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thentrees.shopapp.models.Role;
import com.thentrees.shopapp.services.role.IRoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/roles")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@CrossOrigin("*")
public class RoleController {
    private final IRoleService roleService;

    @GetMapping
    public ResponseEntity<ResponseObject> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ResponseObject.builder()
                        .code(200)
                        .message("Success")
                        .data(roles)
                .build());
    }
}
