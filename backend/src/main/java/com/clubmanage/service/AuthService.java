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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        User user = new User();
        user.setUsername(request.getUsername());
        String rawPwd = request.getPassword();
        String encoded = passwordEncoder.encode(rawPwd);
        user.setPasswordHash(encoded);
        user.setRealName(request.getRealName());
        user.setStudentNo(request.getStudentNo());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(0);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        log.info("[register] username={} userId={} password_hash_len={}",
                user.getUsername(), user.getId(), encoded.length());
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("[login] 尝试登录 username={}", request.getUsername());
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (user == null) {
            log.warn("[login] username={} 不存在", request.getUsername());
            throw new BusinessException(ErrorCode.PASSWORD_WRONG);
        }
        log.info("[login] 找到用户 id={} password_hash_prefix={}",
                user.getId(),
                user.getPasswordHash() == null ? "(null)" : user.getPasswordHash().substring(0, Math.min(8, user.getPasswordHash().length())));

        // 先手动比对密码，避免 AuthenticationManager 内部吞掉异常堆栈
        boolean matched;
        try {
            matched = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        } catch (Exception e) {
            log.error("[login] passwordEncoder.matches 抛出异常: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.PASSWORD_WRONG);
        }
        if (!matched) {
            log.warn("[login] 密码错误 username={}", request.getUsername());
            throw new BusinessException(ErrorCode.PASSWORD_WRONG);
        }

        // 然后再走 Spring Security 认证（为了通过 JWT 与授权上下文一致）
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            log.info("[login] 认证成功 principal={}", authentication.getPrincipal());
        } catch (BadCredentialsException bc) {
            log.warn("[login] BadCredentialsException: {}", bc.getMessage());
            throw new BusinessException(ErrorCode.PASSWORD_WRONG);
        } catch (Exception e) {
            log.error("[login] authenticationManager.authenticate 未捕获异常: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.PASSWORD_WRONG);
        }
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