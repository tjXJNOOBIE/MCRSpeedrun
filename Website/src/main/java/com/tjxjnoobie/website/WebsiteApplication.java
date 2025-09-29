package com.tjxjnoobie.website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class WebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsiteApplication.class, args);
    }



    @RestController
    @RequestMapping("/api")
    @CrossOrigin(origins = "http://localhost:5173") // allow React dev server

    public static class HelloController {

        @GetMapping("/test")
        public String hello() {
        return "Spring Boot is working!";
    }
    }
}
