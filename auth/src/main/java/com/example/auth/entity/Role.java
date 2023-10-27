package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;

    @OneToMany(mappedBy = "role", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<UserCompanyRole> UserCompanyRole;

    @OneToMany(mappedBy = "role", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Permission> permissions;

}
