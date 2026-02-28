package com.restaurant.pos.orderservice.order.integration

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@CompileStatic
class MenuItemClient {

    private final RestTemplate restTemplate
    private final String menuServiceUrl

    MenuItemClient(RestTemplate restTemplate,
                   @Value('${menu.service.base-url:http://localhost:8081}') String menuServiceUrl) {
        this.restTemplate = restTemplate
        this.menuServiceUrl = menuServiceUrl
    }

    Map getMenuItem(String productId) {
        String url = "$menuServiceUrl/menu-items/$productId"
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map)
        if (response.statusCode.is2xxSuccessful()) {
            def body = response.body
            body.price = body.price ? new BigDecimal(body.price.toString()) : BigDecimal.ZERO
            return body
        }
        throw new RuntimeException("Menu item not found: $productId")
    }
}
