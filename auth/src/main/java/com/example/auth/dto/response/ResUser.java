package com.example.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data 
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResUser {
    private long id;
    private String firstName;
    private String lastName;
    private String username; 
    private String companyName;

    private List<ResCompanyRole> userCompanyRole;
}
