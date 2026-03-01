package com.restaurant.pos.orderservice.shared.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    static final String ORDER_STATUS_QUEUE = "order.status.updated"
    static final String ORDER_EXCHANGE = "order.exchange"
    static final String ORDER_STATUS_ROUTING_KEY = "order.status.update"

    @Bean
    Queue orderStatusQueue() {
        new Queue(ORDER_STATUS_QUEUE, true)
    }

    @Bean
    DirectExchange orderExchange() {
        new DirectExchange(ORDER_EXCHANGE, true, false)
    }

    @Bean
    Binding binding(Queue orderStatusQueue, DirectExchange orderExchange) {
        BindingBuilder.bind(orderStatusQueue)
                     .to(orderExchange)
                     .with(ORDER_STATUS_ROUTING_KEY)
    }

    @Bean
    Jackson2JsonMessageConverter jsonMessageConverter() {
        new Jackson2JsonMessageConverter()
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory)
        template.setMessageConverter(converter)
        template
    }
}
