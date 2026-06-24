package com.clubmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clubmanage.common.Result;
import com.clubmanage.entity.User;
import com.clubmanage.mapper.UserMapper;
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

    @GetMapping("/debug")
    public Result<Map<String, Object>> debug(@RequestParam String username,
                                             @RequestParam(defaultValue = "test") String rawPassword) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("username", username);
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (user == null) {
            out.put("found", false);
            return Result.ok(out);
        }
        out.put("found", true);
        out.put("userId", user.getId());
        String hash = user.getPasswordHash();
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
        return Result.ok(out);
    }
}
