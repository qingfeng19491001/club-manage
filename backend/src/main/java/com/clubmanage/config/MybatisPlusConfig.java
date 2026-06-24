package com.clubmanage.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Value("${club.db-type:}")
    private String dbType;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        DbType type = resolveDbType(dbType);
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(type));
        return interceptor;
    }

    private DbType resolveDbType(String value) {
        if (value == null || value.isBlank()) {
            return DbType.MYSQL;
        }
        try {
            return DbType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return DbType.MYSQL;
        }
    }
}