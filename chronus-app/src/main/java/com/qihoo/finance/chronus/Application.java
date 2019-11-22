package com.qihoo.finance.chronus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("\r\n\r\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - > load success! <- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - \r\n\r\n");
    }
}
