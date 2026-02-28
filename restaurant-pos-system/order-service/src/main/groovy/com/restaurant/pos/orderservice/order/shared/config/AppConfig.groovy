package com.restaurant.pos.orderservice.shared.config

import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class AppConfig {

    @Bean
    RestTemplate restTemplate() {
        new RestTemplate()
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory)
        template.messageConverter = new Jackson2JsonMessageConverter()
        template
    }
}
