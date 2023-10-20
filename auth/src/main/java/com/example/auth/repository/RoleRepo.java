package com.example.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth.entity.Role;

public interface RoleRepo extends JpaRepository<Role, Long>{
    
}
