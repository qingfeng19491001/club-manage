package com.clubmanage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.clubmanage.mapper")
public class ClubManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClubManageApplication.class, args);
    }
}