package com.example.auth.dto.response;

import java.util.Set;

import lombok.Data;

@Data
public class ResRole {
    String name;
    Set<ResPermission> permissions;
}
