package com.example.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import com.example.auth.entity.Role;
import com.example.auth.entity.User;

import jakarta.persistence.LockModeType;

public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>{
     
    Optional<User> findTopByUsername(String username);
 
    Optional<User> findTopByUserCompanyRole_RoleName(String name);
    
}
