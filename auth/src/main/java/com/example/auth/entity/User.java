package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @UpdateTimestamp
    Timestamp updatedAt;
    @CreationTimestamp
    Timestamp createdAt;
    String firstName;
    String lastName;
    String username;
    String password;
    @Version
    private int version;

    @OneToMany(mappedBy = "user",targetEntity = UserCompanyRole.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UserCompanyRole> userCompanyRole;
}
