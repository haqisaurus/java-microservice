package com.example.auth.dto.response;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResUser {
    private Long id; 
    private String firstName;
    private String lastName;
    private String username; 
    private List<ResCompanyRole> userCompanyRole;
}
