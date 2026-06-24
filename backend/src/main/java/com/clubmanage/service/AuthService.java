package com.clubmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clubmanage.common.BusinessException;
import com.clubmanage.common.ErrorCode;
import com.clubmanage.dto.auth.*;
import com.clubmanage.entity.User;
import com.clubmanage.mapper.UserMapper;
import com.clubmanage.security.JwtTokenProvider;
import com.clubmanage.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setStudentNo(request.getStudentNo());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(0);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        log.info("[register] ok id={} username={}", user.getId(), user.getUsername());
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (user == null) {
            log.warn("[login] username not found: {}", request.getUsername());
            throw new BusinessException(ErrorCode.PASSWORD_WRONG);
        }
        String hash = user.getPasswordHash();
        if (hash == null || hash.isBlank()) {
            log.warn("[login] password_hash is null for user: {}", user.getUsername());
            throw new BusinessException(ErrorCode.PASSWORD_WRONG);
        }
        boolean matched;
        try {
            matched = passwordEncoder.matches(request.getPassword(), hash);
        } catch (Exception e) {
            log.error("[login] passwordEncoder.matches failed for user={}, hashPrefix={}",
                    user.getUsername(),
                    hash.length() > 7 ? hash.substring(0, 7) : hash,
                    e);
            throw new BusinessException(ErrorCode.PASSWORD_WRONG);
        }
        if (!matched) {
            log.warn("[login] password mismatch for user: {}", user.getUsername());
            throw new BusinessException(ErrorCode.PASSWORD_WRONG);
        }
        log.info("[login] ok id={} username={}", user.getId(), user.getUsername());
        return buildAuthResponse(user);
    }

    public void logout() {
        // Stateless JWT: client discards token; optional Redis blacklist can be added later.
    }

    public UserProfileVO profile() {
        User user = SecurityUtils.currentUser().getUser();
        return UserProfileVO.from(user);
    }

    @Transactional
    public UserProfileVO updateProfile(UpdateProfileRequest request) {
        User user = userMapper.selectById(SecurityUtils.currentUserId());
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getStudentNo() != null) {
            user.setStudentNo(request.getStudentNo());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return UserProfileVO.from(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = userMapper.selectById(SecurityUtils.currentUserId());
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PASSWORD_WRONG);
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtTokenProvider.createToken(user.getId(), user.getUsername(),
                user.getRole() != null ? user.getRole() : 0);
        return AuthResponse.builder()
                .token(token)
                .user(UserProfileVO.from(user))
                .build();
    }
}