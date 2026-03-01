package com.restaurant.pos.orderservice.shared.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class AppConfig {

    @Bean
    RestTemplate restTemplate() {
        new RestTemplate()
    }
}
