package com.clubmanage.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

@Component
public class DatabaseMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigration.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigration(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ensureClubCategoryColumn();
    }

    private void ensureClubCategoryColumn() throws Exception {
        if (hasColumn("clubs", "category")) {
            return;
        }
        jdbcTemplate.execute("ALTER TABLE clubs ADD COLUMN category VARCHAR(64) NOT NULL DEFAULT '其他'");
        log.info("[Migration] Added clubs.category column.");
    }

    private boolean hasColumn(String tableName, String columnName) throws Exception {
        try (Connection conn = dataSource.getConnection();
             ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName)) {
            if (rs.next()) {
                return true;
            }
        }
        try (Connection conn = dataSource.getConnection();
             ResultSet rs = conn.getMetaData().getColumns(null, null, tableName.toUpperCase(), columnName.toUpperCase())) {
            return rs.next();
        }
    }
}