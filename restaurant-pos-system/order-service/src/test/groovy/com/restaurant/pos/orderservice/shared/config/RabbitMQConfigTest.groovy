package com.restaurant.pos.orderservice.shared.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import spock.lang.Specification
import spock.lang.Subject

class RabbitMQConfigTest extends Specification {

    @Subject
    RabbitMQConfig config = new RabbitMQConfig()

    def "deve criar queue com nome 'order.status.updated' e durável"() {
        when:
        Queue queue = config.orderStatusQueue()

        then:
        queue != null
        queue.name == RabbitMQConfig.ORDER_STATUS_QUEUE
        queue.durable
    }

    def "deve criar exchange DirectExchange com nome 'order.exchange'"() {
        when:
        DirectExchange exchange = config.orderExchange()

        then:
        exchange != null
        exchange.name == RabbitMQConfig.ORDER_EXCHANGE
        exchange.durable
        !exchange.autoDelete
    }

    def "deve criar binding entre queue e exchange com routing key correto"() {
        given:
        Queue queue = config.orderStatusQueue()
        DirectExchange exchange = config.orderExchange()

        when:
        Binding binding = config.binding(queue, exchange)

        then:
        binding != null
        binding.destination == RabbitMQConfig.ORDER_STATUS_QUEUE
        binding.exchange == RabbitMQConfig.ORDER_EXCHANGE
        binding.routingKey == RabbitMQConfig.ORDER_STATUS_ROUTING_KEY
    }

    def "deve criar Jackson2JsonMessageConverter"() {
        when:
        Jackson2JsonMessageConverter converter = config.jsonMessageConverter()

        then:
        converter != null
        converter instanceof Jackson2JsonMessageConverter
    }

    def "deve criar RabbitTemplate com message converter configurado"() {
        given:
        ConnectionFactory connectionFactory = Mock(ConnectionFactory)
        Jackson2JsonMessageConverter converter = config.jsonMessageConverter()

        when:
        RabbitTemplate template = config.rabbitTemplate(connectionFactory, converter)

        then:
        template != null
        template.connectionFactory == connectionFactory
        template.messageConverter == converter
    }

    def "deve ter constantes com valores corretos"() {
        expect:
        RabbitMQConfig.ORDER_STATUS_QUEUE == "order.status.updated"
        RabbitMQConfig.ORDER_EXCHANGE == "order.exchange"
        RabbitMQConfig.ORDER_STATUS_ROUTING_KEY == "order.status.update"
    }
}
