package com.qihoo.finance.chronus;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableDubbo
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("\r\n\r\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - > load success! <- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - \r\n\r\n");
    }
}
