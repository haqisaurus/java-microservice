package com.example.auth.service;

import com.example.auth.config.jwt.JwtTokenUtil;
import com.example.auth.config.jwt.JwtUserDetail;
import com.example.auth.dto.SearchUser;
import com.example.auth.dto.request.ReqLogin;
import com.example.auth.dto.request.ReqUser;
import com.example.auth.dto.response.ResLogin;
import com.example.auth.dto.response.ResUser;
import com.example.auth.entity.Role;
import com.example.auth.entity.User;
import com.example.auth.entity.UserCompanyRole;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.repository.UserRepo;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    @Autowired
    UserService(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserRepo userRepo, ModelMapper modelMapper) {
        this.authenticationManager=authenticationManager;
        this.jwtTokenUtil=jwtTokenUtil;
        this.userRepo=userRepo;
        this.modelMapper=modelMapper;
    }

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
//        biar ngga error aja panggil privae method
        String check = this.privateMethod("mama", 18);
        log.info(check);
        return modelMapper.map(user, ResUser.class);
    }
    @SuppressWarnings("all")
    private String privateMethod( String nama,  Integer umur) {
        return nama.concat(String.valueOf(umur));
    }

    @Transactional
    public void updateUser(ReqUser payload) {
        User user = userRepo.findById(payload.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data not found"));
        user.setFirstName(payload.getFirstName());
        user.setLastName(payload.getLastName());
        userRepo.save(user);
    }

    @Transactional(readOnly = true)
    public ResUser getByRole() {
        User user = userRepo.findTopByUserCompanyRole_RoleName("ADMIN")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data not found"));

        ResUser response = modelMapper.map(user, ResUser.class);
        response.setCompanyName(user.getUserCompanyRole().get(0).getCompany().getName());
        return response;
    }

    @Transactional(readOnly = true)
    public Page<ResUser> searchUser(SearchUser params) {

        Specification<User> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.add(builder.equal(root.get("user"), user));
            if (Objects.nonNull(params.getName())) {
                predicates.add(builder.or(
                        builder.like(root.get("firstName"), "%" + params.getName() + "%"),
                        builder.like(root.get("lastName"), "%" + params.getName() + "%")));

            }

            if (Objects.nonNull(params.getUsername())) {
                predicates.add(builder.like(root.get("username"), "%" + params.getUsername() + "%"));
            }

            // predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"),
            // System.currentTimeMillis() - 10000000L));

            // jika join
            Join<User, UserCompanyRole> joinRelation = root.join("userCompanyRole", JoinType.INNER);
            Join<UserCompanyRole, Role> joinRole = joinRelation.join("role", JoinType.INNER);

            predicates.add(builder.equal(joinRole.get("name"), "ADMIN"));

            // disini digabungkan menggunakan and atau or
            Predicate andPredicate = builder.and(predicates.toArray(new Predicate[0]));
            return query.where(andPredicate).getRestriction();
        };

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize());
        Page<User> users = userRepo.findAll(specification, pageable);
        List<ResUser> contactResponses = users.getContent().stream()
                .map(v -> ResUser.builder().firstName(v.getFirstName()).lastName(v.getLastName()).companyName(v.getUserCompanyRole().get(0).getCompany().getName())
                        .username(v.getUsername()).build())
                .toList();

        return new PageImpl<>(contactResponses, pageable, users.getTotalElements());
    }

    public List<User> getUsers() {
        return userRepo.findAll();
    }
}
