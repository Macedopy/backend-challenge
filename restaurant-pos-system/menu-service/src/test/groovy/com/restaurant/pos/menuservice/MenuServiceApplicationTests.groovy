package com.restaurant.pos.menuservice

import org.springframework.boot.SpringApplication
import spock.lang.Specification

class MenuServiceApplicationTest extends Specification {

    def "should call SpringApplication run"() {
        given:
        GroovyMock(SpringApplication, global: true)

        when:
        MenuServiceApplication.main()

        then:
        1 * SpringApplication.run(MenuServiceApplication, _ as String[])
    }
}
