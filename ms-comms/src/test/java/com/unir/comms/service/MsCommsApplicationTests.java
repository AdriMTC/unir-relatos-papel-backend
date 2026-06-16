package com.unir.comms.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.mail.username=test@test.com",
    "spring.mail.password=test",
    "gemini.api.key=test-key",
    "eureka.client.enabled=false",
    "spring.rabbitmq.host=localhost"
})
class MsCommsApplicationTests {

    @Test
    void contextLoads() {
    }
}
