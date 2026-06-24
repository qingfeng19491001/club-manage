package com.clubmanage.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseHealthCheck implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseHealthCheck.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final Environment env;

    public DatabaseHealthCheck(DataSource dataSource, JdbcTemplate jdbcTemplate, Environment env) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) {
        String jwtSecret = env.getProperty("club.jwt.secret", "").trim();
        if (jwtSecret.isBlank()) {
            log.error("[HealthCheck] club.jwt.secret / JWT_SECRET 为空！登录签发 JWT 必然失败，请在 Railway Variables 中配置 JWT_SECRET（建议至少 32 字符）。");
        } else if (jwtSecret.length() < 32) {
            log.warn("[HealthCheck] club.jwt.secret 长度仅 {} 位，建议 ≥ 32 位以满足 HS256 安全要求。", jwtSecret.length());
        } else {
            log.info("[HealthCheck] club.jwt.secret 已配置（长度 {} 位）。", jwtSecret.length());
        }

        log.info("==========================================");
        log.info("[HealthCheck] 正在验证数据库连接及 users 表 ...");
        log.info("[HealthCheck] active profiles: {}", String.join(",", env.getActiveProfiles()));

        try (Connection conn = dataSource.getConnection()) {
            log.info("[HealthCheck] 数据库产品: {} / URL: {}",
                    conn.getMetaData().getDatabaseProductName(),
                    conn.getMetaData().getURL());
        } catch (Exception e) {
            log.error("[HealthCheck] 数据库连接失败: {}", e.getMessage());
            return;
        }

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT id, username, role, status FROM users LIMIT 3");
            log.info("[HealthCheck] users 表存在，共查询到 {} 条记录。示例:", rows.size());
            for (Map<String, Object> row : rows) {
                log.info("   -> {}", row);
            }
            log.info("[HealthCheck] OK — 后端可以正常响应注册 / 登录请求。");
        } catch (Exception e) {
            log.warn("[HealthCheck] users 表查询失败: {} — 请确认 schema-mysql.sql 是否已在 MySQL 中执行。",
                    e.getMessage());
        }
        log.info("==========================================");
    }
}
