package com.restaurant.pos.orderservice.order.listener

import spock.lang.Specification

class OrderStatusListenerTest extends Specification {

    def listener = new OrderStatusListener()

    def "should print notification details on status update"() {
        given:
        def message = [
                orderId: "order-1",
                status: "PREPARING",
                fullName: "John Doe",
                address: "123 Main St",
                email: "john@test.com"
        ]

        def baos = new ByteArrayOutputStream()
        def ps = new PrintStream(baos)
        def originalOut = System.out
        System.setOut(ps)
        try {
            when:
            listener.handleOrderStatusUpdate(message)

            then:
            ps.flush()
            def output = baos.toString('UTF-8')
            assert output.contains('Notification:')
            assert output.contains('Order: order-1')
            assert output.contains('Status: PREPARING')
            assert output.contains('Client: John Doe')
            assert output.contains('Address: 123 Main St')
            assert output.contains('Email: john@test.com')
        } finally {
            System.setOut(originalOut)
        }
    }
}
