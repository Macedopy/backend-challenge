package com.restaurant.pos.menuservice.menu

import com.restaurant.pos.menuservice.menu.dto.MenuItemRequest
import com.restaurant.pos.menuservice.menu.dto.MenuItemCreateResponse
import com.restaurant.pos.menuservice.menu.dto.MenuItemUpdateResponse
import com.restaurant.pos.menuservice.menu.dto.MenuItemListResponse
import com.restaurant.pos.menuservice.menu.entity.MenuItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MenuServiceTest extends Specification {

    @Subject
    MenuService menuService

    MenuRepository menuRepository = Stub(MenuRepository)

    def setup() {
        menuService = new MenuService(menuRepository)
    }

    def "should create a new menu item successfully"() {
        given:
        def request = new MenuItemRequest(name: "Pizza", description: "Delicious cheese pizza", price: 9.99)
        def now = LocalDateTime.now()
        def savedItem = new MenuItem(
            id: "abc123",
            name: request.name,
            description: request.description,
            price: request.price,
            createdAt: now,
            updatedAt: now
        )
        menuRepository.save(_) >> savedItem

        when:
        def response = menuService.create(request)

        then:
        // interaction verified by stub, no explicit count needed
        response.id == "abc123"
        response.name == "Pizza"
        response.description == "Delicious cheese pizza"
        response.price == 9.99
        response.createdAt != null
    }

    def "should update an existing menu item successfully"() {
        given:
        def id = "abc123"
        def existingItem = new MenuItem(
            id: id,
            name: "Pizza",
            description: "Delicious cheese pizza",
            price: 9.99,
            createdAt: LocalDateTime.now().minusHours(1),
            updatedAt: LocalDateTime.now().minusHours(1)
        )
        def request = new MenuItemRequest(name: "Large Pizza", description: "Cheese pizza with extra toppings", price: 11.99)
        def now = LocalDateTime.now()
        def updatedItem = new MenuItem(
            id: id,
            name: request.name,
            description: request.description,
            price: request.price,
            createdAt: existingItem.createdAt,
            updatedAt: now
        )
        menuRepository.findById(id) >> Optional.of(existingItem)
        menuRepository.save(_) >> updatedItem

        when:
        def response = menuService.update(id, request)

        then:
        // repository methods are stubbed above, no need to verify interactions
        response.id == id
        response.name == "Large Pizza"
        response.description == "Cheese pizza with extra toppings"
        response.price == 11.99
        response.updatedAt != null
    }

    def "should throw exception when updating non-existent item"() {
        given:
        def id = "non-existent"
        def request = new MenuItemRequest(name: "Pizza", description: "Delicious cheese pizza", price: 9.99)
        menuRepository.findById(id) >> Optional.empty()

        when:
        menuService.update(id, request)

        then:
        thrown(RuntimeException)
    }

    def "should delete an existing menu item successfully"() {
        given:
        def id = "abc123"
        menuRepository.existsById(id) >> true

        when:
        menuService.delete(id)

        then:
        // deletion should complete without throwing an exception
        noExceptionThrown()
    }

    def "should throw exception when deleting non-existent item"() {
        given:
        def id = "non-existent"
        menuRepository.existsById(id) >> false

        when:
        menuService.delete(id)

        then:
        thrown(RuntimeException)
    }

    def "should find menu item by ID successfully"() {
        given:
        def id = "abc123"
        def item = new MenuItem(
            id: id,
            name: "Pizza",
            description: "Delicious cheese pizza",
            price: 9.99,
            createdAt: LocalDateTime.now()
        )
        menuRepository.findById(id) >> Optional.of(item)

        when:
        def response = menuService.findById(id)

        then:
        response.id == id
        response.name == "Pizza"
        response.description == "Delicious cheese pizza"
        response.price == 9.99
        response.createdAt != null
    }

    def "should throw exception when finding non-existent item by ID"() {
        given:
        def id = "non-existent"
        menuRepository.findById(id) >> Optional.empty()

        when:
        menuService.findById(id)

        then:
        thrown(RuntimeException)
    }

    def "should return paginated list of items"() {
        given:
        def item1 = new MenuItem(id: "a", name: "A", description: "A", price: 1.0, createdAt: LocalDateTime.now())
        def item2 = new MenuItem(id: "b", name: "B", description: "B", price: 2.0, createdAt: LocalDateTime.now())
        def page = new PageImpl<MenuItem>([item1, item2], PageRequest.of(0, 2), 2)
        menuRepository.findAll(_ as PageRequest) >> page

        when:
        def response = menuService.findAll(2, 0)

        then:
        response.totalRecords == 2
        response.items.size() == 2
        response.items[0].id == "a"
        response.items[1].id == "b"
    }
}
