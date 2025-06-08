package com.SES;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
@EnableScheduling //开启异步支持
@EnableAsync
public class SESApplication {
    public static void main(String[] args) {
        SpringApplication.run(SESApplication.class, args);
        log.info("server started");
    }
}


