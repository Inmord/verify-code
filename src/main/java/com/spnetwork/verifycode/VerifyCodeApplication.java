package com.spnetwork.verifycode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class VerifyCodeApplication {

    @Value("${spring.application.name}")
    private String applicationName;

    public static void main(String[] args) {
        SpringApplication.run(VerifyCodeApplication.class, args);
    }

    @GetMapping("/app")
    public String helloApp() {
        return "Hello!" + applicationName;
    }

}
