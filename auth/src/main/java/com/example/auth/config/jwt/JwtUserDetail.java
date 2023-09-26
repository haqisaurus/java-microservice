package com.example.auth.config.jwt;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter; 

@Setter
@Getter
public class JwtUserDetail extends User {
    private String firstName;
    private String lastName; 
    private String username;
    private String password;
    private Long id;  
    Collection<GrantedAuthority> authorities;

    public JwtUserDetail(Long id, String username, String password, String firstName, String lastName, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities); 
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authorities = (Collection<GrantedAuthority>) authorities;
    }
    
}
