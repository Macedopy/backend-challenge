package com.restaurant.pos.orderservice.shared.config

import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Subject

class AppConfigTest extends Specification {

    @Subject
    AppConfig config = new AppConfig()

    def "deve criar RestTemplate como bean"() {
        when:
        RestTemplate restTemplate = config.restTemplate()

        then:
        restTemplate != null
        restTemplate instanceof RestTemplate
    }
}
