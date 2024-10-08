package com.andrejka.dictionary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Dictionary {

    public static void main(String[] args) {
        SpringApplication.run(Dictionary.class, args);
    }
}
