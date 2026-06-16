package com.unir.comms.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsCommsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCommsApplication.class, args);
    }
}
