package com.example.auth.dto.response;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder
public class ResUser {
    Long id; 
    String firstName;
    String lastName;
    String username; 
    List<ResCompanyRole> userCompanyRole;
}
