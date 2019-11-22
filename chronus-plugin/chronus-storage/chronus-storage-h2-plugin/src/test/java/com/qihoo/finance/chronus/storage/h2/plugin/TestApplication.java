package com.qihoo.finance.chronus.storage.h2.plugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
        System.out.println("\r\n\r\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - > h2 load success! <- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - \r\n\r\n");
    }
}
