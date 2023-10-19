package com.example.auth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.auth.config.jwt.JwtTokenUtil;
import com.example.auth.config.jwt.JwtUserDetail;
import com.example.auth.dto.SearchUser;
import com.example.auth.dto.request.ReqLogin;
import com.example.auth.dto.response.ResLogin;
import com.example.auth.dto.response.ResUser;
import com.example.auth.entity.User;
import com.example.auth.entity.UserCompanyRole;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.repository.UserRepo;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;

    public ResLogin performLogin(ReqLogin req) {
        ResLogin response = new ResLogin();

        // lakukan authenticate
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        // jika berhasil generate token
        if (authentication.isAuthenticated()) {
            JwtUserDetail user = (JwtUserDetail) authentication.getPrincipal();

            Map<String, Object> token = jwtTokenUtil.generateUserToken(user);
            response.setToken(token.get("token").toString());
            response.setExpired(Long.parseLong(token.get("expired").toString()));
        } else {
            // throw new UsernameNotFoundException("invalid user request !");
            response.setToken("error bos");

        }
        return response;
    }

    public ResUser getUserDetail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetail userDetails = (JwtUserDetail) authentication.getPrincipal();
        User user = userRepo.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Undefined user"));
        ResUser resUser = modelMapper.map(user, ResUser.class);
        return resUser;
    }

    private String privateMethod(String nama, Integer umur) {
        return nama.concat(String.valueOf(umur));
    }

    @Transactional(readOnly = true)
    public Page<ResUser> searchUser(SearchUser params) {

        Specification<User> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.add(builder.equal(root.get("user"), user));
            if (Objects.nonNull(params.getName())) {
                predicates.add((Predicate) builder.or(
                        builder.like(root.get("firstName"), "%" + params.getName() + "%"),
                        builder.like(root.get("lastName"), "%" + params.getName() + "%")));

            }

            if (Objects.nonNull(params.getUsername())) {
                predicates.add(builder.like(root.get("username"), "%" + params.getUsername() + "%"));
            }

            // disini digabungkan menggunakan and atau or 
            Predicate andPredicate = (Predicate) builder.or(predicates.toArray(new Predicate[predicates.size()]));
            return query.where(andPredicate).getRestriction();
        };

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize());
        Page<User> users = userRepo.findAll(specification, pageable);
        List<ResUser> contactResponses = users.getContent().stream()
                .map(v -> {
                    return ResUser.builder().firstName(v.getFirstName()).lastName(v.getLastName())
                            .username(v.getUsername()).build();
                })
                .toList();

        return new PageImpl<>(contactResponses, pageable, users.getTotalElements());
    }
}
