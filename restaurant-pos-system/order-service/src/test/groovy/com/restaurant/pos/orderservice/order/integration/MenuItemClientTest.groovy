package com.restaurant.pos.orderservice.order.integration

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Subject

class MenuItemClientTest extends Specification {

    private RestTemplate restTemplate = Mock()

    @Subject
    private MenuItemClient menuItemClient

    def setup() {
        menuItemClient = new MenuItemClient(restTemplate, 'http://localhost:8081')
    }

    def 'should get menu item successfully with price conversion'() {
        given:
        String productId = 'BURGER-001'
        Map menuItemResponse = [
            id: 'BURGER-001',
            name: 'Classic Burger',
            price: '15.99',
            description: 'A delicious classic burger'
        ]
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(menuItemResponse, HttpStatus.OK)

        when:
        restTemplate.getForEntity('http://localhost:8081/menu-items/BURGER-001', Map) >> responseEntity
        Map result = menuItemClient.getMenuItem(productId)

        then:
        result.id == 'BURGER-001'
        result.name == 'Classic Burger'
        result.price == new BigDecimal('15.99')
        result.description == 'A delicious classic burger'
    }

    def 'should convert numeric price to BigDecimal'() {
        given:
        String productId = 'PIZZA-002'
        Map menuItemResponse = [
            id: 'PIZZA-002',
            name: 'Margherita Pizza',
            price: 25.50
        ]
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(menuItemResponse, HttpStatus.OK)

        when:
        restTemplate.getForEntity('http://localhost:8081/menu-items/PIZZA-002', Map) >> responseEntity
        Map result = menuItemClient.getMenuItem(productId)

        then:
        result.price == new BigDecimal('25.50')
        result.price instanceof BigDecimal
    }

    def 'should handle null price and convert to ZERO'() {
        given:
        String productId = 'DRINK-003'
        Map menuItemResponse = [
            id: 'DRINK-003',
            name: 'Water',
            price: null
        ]
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(menuItemResponse, HttpStatus.OK)

        when:
        restTemplate.getForEntity('http://localhost:8081/menu-items/DRINK-003', Map) >> responseEntity
        Map result = menuItemClient.getMenuItem(productId)

        then:
        result.price == BigDecimal.ZERO
    }

    def 'should throw RuntimeException when menu item not found'() {
        given:
        String productId = 'INVALID-999'
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.NOT_FOUND)

        when:
        restTemplate.getForEntity('http://localhost:8081/menu-items/INVALID-999', Map) >> responseEntity
        menuItemClient.getMenuItem(productId)

        then:
        RuntimeException ex = thrown()
        ex.message == 'Menu item not found: INVALID-999'
    }

    def 'should throw RuntimeException when service returns error status'() {
        given:
        String productId = 'ERROR-500'
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR)

        when:
        restTemplate.getForEntity('http://localhost:8081/menu-items/ERROR-500', Map) >> responseEntity
        menuItemClient.getMenuItem(productId)

        then:
        RuntimeException ex = thrown()
        ex.message == 'Menu item not found: ERROR-500'
    }

    def 'should construct correct URL for menu item endpoint'() {
        given:
        String productId = 'DESSERT-004'
        Map menuItemResponse = [
            id: 'DESSERT-004',
            name: 'Chocolate Cake',
            price: '5.99'
        ]
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(menuItemResponse, HttpStatus.OK)

        when:
        menuItemClient.getMenuItem(productId)

        then:
        1 * restTemplate.getForEntity('http://localhost:8081/menu-items/DESSERT-004', Map) >> responseEntity
    }
}
