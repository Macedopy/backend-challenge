package com.restaurant.pos.orderservice

import org.springframework.boot.SpringApplication
import spock.lang.Specification

class OrderServiceApplicationTest extends Specification {

    def "should call SpringApplication run"() {
        given:
        GroovyMock(SpringApplication, global: true)

        when:
        OrderServiceApplication.main()

        then:
        1 * SpringApplication.run(OrderServiceApplication, _ as String[])
    }
}
