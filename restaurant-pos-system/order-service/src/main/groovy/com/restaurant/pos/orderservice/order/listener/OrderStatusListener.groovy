package com.restaurant.pos.orderservice.order.listener

import groovy.transform.CompileStatic
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
@CompileStatic
class OrderStatusListener {

    @RabbitListener(queues = "order.status.updated")
    void handleOrderStatusUpdate(Map message) {
        println "Notificação simulada:"
        println "Pedido: ${message.orderId}"
        println "Status: ${message.status}"
        println "Cliente: ${message.fullName}"
        println "Endereço: ${message.address}"
        println "Email: ${message.email}"
        println "---------------------"
    }
}
