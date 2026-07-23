package com.tjxjnoobie.website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.tjxjnoobie")
@ConfigurationPropertiesScan(basePackages = "com.tjxjnoobie")
@EntityScan(basePackages = "com.tjxjnoobie.store.persistence.entity")
@EnableJpaRepositories(basePackages = "com.tjxjnoobie.store.persistence.repository")
public class WebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsiteApplication.class, args);
    }
}
