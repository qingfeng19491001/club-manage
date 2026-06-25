package com.clubmanage.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clubmanage.common.TimeUtil;
import com.clubmanage.entity.User;
import com.clubmanage.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

    private static final int ROLE_ADMIN = 2;
    private static final int STATUS_ENABLED = 1;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_USERNAME:}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    @Value("${ADMIN_EMAIL:}")
    private String adminEmail;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(adminUsername)
                && !StringUtils.hasText(adminPassword)
                && !StringUtils.hasText(adminEmail)) {
            return;
        }

        if (!StringUtils.hasText(adminUsername) || !StringUtils.hasText(adminPassword)) {
            log.warn("ADMIN_USERNAME and ADMIN_PASSWORD must both be set to initialize an admin account");
            return;
        }

        String username = adminUsername.trim();
        Long existingCount = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (existingCount != null && existingCount > 0) {
            log.info("Admin account '{}' already exists; skipping initialization", username);
            return;
        }

        String now = TimeUtil.now();
        User admin = new User();
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRealName("System Admin");
        admin.setEmail(StringUtils.hasText(adminEmail) ? adminEmail.trim() : null);
        admin.setRole(ROLE_ADMIN);
        admin.setStatus(STATUS_ENABLED);
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);
        admin.setDeleted(0);
        userMapper.insert(admin);
        log.info("Initialized admin account '{}' from environment variables", username);
    }
}
