package com.clubmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clubmanage.common.Result;
import com.clubmanage.entity.User;
import com.clubmanage.mapper.UserMapper;
import com.clubmanage.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 调试端点 — 用于在 Railway 上直接观察 password_hash 是否被正确写入 / 格式是否合法。
 * 生产环境可删除。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class DebugAuthController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/debug")
    public Result<Map<String, Object>> debug(@RequestParam String username,
                                             @RequestParam(defaultValue = "test") String rawPassword) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("username", username);
        User user;
        try {
            user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username));
        } catch (Exception e) {
            out.put("error_at", "userMapper.selectOne");
            out.put("error", e.getClass().getSimpleName() + ": " + e.getMessage());
            if (e.getCause() != null) {
                out.put("cause", e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage());
            }
            return Result.ok(out);
        }
        if (user == null) {
            out.put("found", false);
            return Result.ok(out);
        }
        out.put("found", true);
        try {
            out.put("userId", user.getId());
        } catch (Exception e) {
            out.put("error_at", "getId");
            out.put("error", e.getMessage());
            return Result.ok(out);
        }
        String hash;
        try {
            hash = user.getPasswordHash();
        } catch (Exception e) {
            out.put("error_at", "getPasswordHash");
            out.put("error", e.getMessage());
            return Result.ok(out);
        }
        out.put("password_hash", hash == null ? "(null)" : hash);
        out.put("password_hash_length", hash == null ? 0 : hash.length());
        if (hash != null && !hash.isBlank()) {
            out.put("password_hash_prefix", hash.substring(0, Math.min(10, hash.length())));
            out.put("password_hash_starts_with_$2a", hash.startsWith("$2a$"));
            out.put("password_hash_starts_with_$2b", hash.startsWith("$2b$"));
            out.put("password_hash_starts_with_$2y", hash.startsWith("$2y$"));
        }
        boolean matched;
        String matchStatus;
        String matchException;
        long t0 = System.nanoTime();
        try {
            matched = passwordEncoder.matches(rawPassword, hash);
            matchStatus = matched ? "MATCH" : "NO_MATCH";
            matchException = null;
        } catch (Exception e) {
            matched = false;
            matchStatus = "EXCEPTION";
            matchException = e.getClass().getSimpleName() + ": " + e.getMessage();
        }
        long t1 = System.nanoTime();
        out.put("rawPassword_tested", rawPassword);
        out.put("matches", matched);
        out.put("matchStatus", matchStatus);
        out.put("matchException", matchException);
        out.put("matchDurationNs", t1 - t0);

        // 再额外检查一下是否能成功签发 token
        try {
            String testToken = jwtTokenProvider.createToken(
                    user.getId(),
                    user.getUsername(),
                    user.getRole() != null ? user.getRole() : 0);
            out.put("tokenGenerated", true);
            out.put("tokenLength", testToken.length());
            out.put("tokenPrefix", testToken.substring(0, Math.min(24, testToken.length())));
        } catch (Exception e) {
            out.put("tokenGenerated", false);
            out.put("tokenError", e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return Result.ok(out);
    }
}
