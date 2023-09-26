package com.example.auth.config.jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority; 
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.auth.repository.UserRepo;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class JwtUserDetailService implements UserDetailsService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JwtUserDetailService.class);

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<com.example.auth.entity.User> existUser = userRepo.findTopByUsername(username);
        if (existUser.isPresent()) {
            Collection<GrantedAuthority> authorities = Collections.emptySet();
            com.example.auth.entity.User user = existUser.get();
            JwtUserDetail userDetail = new JwtUserDetail(user.getId(), user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), authorities);
            return userDetail;
        } 
        throw new UsernameNotFoundException("User not found " + username);
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        Optional<com.example.auth.entity.User> existUser = userRepo.findById(id);
        if (existUser.isPresent()) {
            Collection<GrantedAuthority> authorities = Collections.emptySet();
            com.example.auth.entity.User user = existUser.get();
            JwtUserDetail userDetail = new JwtUserDetail(user.getId(), user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), authorities);
            return userDetail;
        } 
        throw new UsernameNotFoundException("User not found " + id);
    }
}