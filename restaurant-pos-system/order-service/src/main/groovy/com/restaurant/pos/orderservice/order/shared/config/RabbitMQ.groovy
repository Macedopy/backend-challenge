package com.restaurant.pos.orderservice.shared.config

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    static final String ORDER_STATUS_QUEUE = "order.status.updated"

    @Bean
    Queue orderStatusQueue() {
        new Queue(ORDER_STATUS_QUEUE, true)
    }
}
