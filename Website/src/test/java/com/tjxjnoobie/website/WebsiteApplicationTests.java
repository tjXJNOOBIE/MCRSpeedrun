package com.tjxjnoobie.website;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ImportAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.rsocket.RSocketSecurityAutoConfiguration.class
})
class WebsiteApplicationTests {

    @Test
    void contextLoads() {
    }

}
