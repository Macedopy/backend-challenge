package com.restaurant.pos.menuservice.menu.exception

import spock.lang.Specification

class MenuItemAlreadyExistsExceptionTest extends Specification {

    def "should create exception with correct message when name is provided"() {
        when:
        throw new MenuItemAlreadyExistsException("Pizza")

        then:
        def ex = thrown(MenuItemAlreadyExistsException)
        ex.message == "Menu item already exists with name: Pizza"
        ex instanceof RuntimeException
    }

    def "should handle null name gracefully in exception message"() {
        when:
        throw new MenuItemAlreadyExistsException(null)

        then:
        def ex = thrown(MenuItemAlreadyExistsException)
        ex.message == "Menu item already exists with name: null"
    }

    def "should construct correct message when instantiated directly"() {
        expect:
        new MenuItemAlreadyExistsException("Cheese").message == "Menu item already exists with name: Cheese"
        new MenuItemAlreadyExistsException(null).message == "Menu item already exists with name: null"
    }
}
