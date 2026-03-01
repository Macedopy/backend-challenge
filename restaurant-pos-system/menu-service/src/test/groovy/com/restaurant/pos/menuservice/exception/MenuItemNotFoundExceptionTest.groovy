package com.restaurant.pos.menuservice.menu.exception

import spock.lang.Specification

class MenuItemNotFoundExceptionTest extends Specification {

    def "should create exception with correct message when id is provided"() {
        when:
        throw new MenuItemNotFoundException("abc123")

        then:
        def ex = thrown(MenuItemNotFoundException)
        ex.message == "Menu item not found with id: abc123"
        ex instanceof RuntimeException
    }

    def "should handle null id gracefully in exception message"() {
        when:
        throw new MenuItemNotFoundException(null)

        then:
        def ex = thrown(MenuItemNotFoundException)
        ex.message == "Menu item not found with id: null"
    }

    def "should construct correct message when instantiated directly"() {
        expect:
        new MenuItemNotFoundException("xyz789").message == "Menu item not found with id: xyz789"
        new MenuItemNotFoundException(null).message == "Menu item not found with id: null"
    }
}
