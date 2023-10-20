package com.example.auth.entity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @UpdateTimestamp
    Timestamp updatedAt;
    @CreationTimestamp
    Timestamp createdAt;
    String firstName;
    String lastName;
    String username;
    String password;

    @OneToMany(mappedBy = "user",targetEntity = UserCompanyRole.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UserCompanyRole> userCompanyRole;
}
