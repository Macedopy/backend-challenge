package com.restaurant.pos.menuservice.menu.service

import com.restaurant.pos.menuservice.menu.repository.MenuRepository
import com.restaurant.pos.menuservice.menu.dto.MenuItemRequest
import com.restaurant.pos.menuservice.menu.dto.MenuItemCreateResponse
import com.restaurant.pos.menuservice.menu.dto.MenuItemUpdateResponse
import com.restaurant.pos.menuservice.menu.dto.MenuItemListResponse
import com.restaurant.pos.menuservice.menu.entity.MenuItem
import com.restaurant.pos.menuservice.menu.exception.MenuItemAlreadyExistsException
import com.restaurant.pos.menuservice.menu.exception.MenuItemNotFoundException
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class MenuServiceTest extends Specification {

    @Subject
    MenuService menuService

    MenuRepository menuRepository = Mock(MenuRepository)

    def setup() {
        menuService = new MenuService(menuRepository)
    }

    def "should create menu item successfully"() {
        given:
        def request = new MenuItemRequest(name: "Pizza", description: "Delicious cheese pizza", price: 9.99)
        def now = LocalDateTime.now()
        def savedItem = new MenuItem(id: "abc123", name: "Pizza", description: "Delicious cheese pizza", price: 9.99, createdAt: now, updatedAt: now)

        menuRepository.existsByName("Pizza") >> false
        menuRepository.save(_) >> savedItem

        when:
        def response = menuService.create(request)

        then:
        response.id == "abc123"
        response.name == "Pizza"
        response.description == "Delicious cheese pizza"
        response.price == 9.99
        response.createdAt != null
    }

    def "should throw MenuItemAlreadyExistsException when name is duplicate"() {
        given:
        def request = new MenuItemRequest(name: "Pizza", description: "desc", price: 9.99)
        menuRepository.existsByName("Pizza") >> true

        when:
        menuService.create(request)

        then:
        def ex = thrown(MenuItemAlreadyExistsException)
        ex.message == "Menu item already exists with name: Pizza"
    }

    def "should update menu item successfully"() {
        given:
        def id = "abc123"
        def existingItem = new MenuItem(id: id, name: "Pizza", description: "Old desc", price: 9.99, createdAt: LocalDateTime.now().minusHours(1), updatedAt: LocalDateTime.now().minusHours(1))
        def request = new MenuItemRequest(name: "Large Pizza", description: "New desc", price: 11.99)
        def updatedItem = new MenuItem(id: id, name: "Large Pizza", description: "New desc", price: 11.99, createdAt: existingItem.createdAt, updatedAt: LocalDateTime.now())

        menuRepository.findById(id) >> Optional.of(existingItem)
        menuRepository.save(_) >> updatedItem

        when:
        def response = menuService.update(id, request)

        then:
        response.id == id
        response.name == "Large Pizza"
        response.description == "New desc"
        response.price == 11.99
        response.updatedAt != null
    }

    def "should throw MenuItemNotFoundException when updating non-existent item"() {
        given:
        menuRepository.findById("invalid") >> Optional.empty()

        when:
        menuService.update("invalid", new MenuItemRequest(name: "Pizza", description: "desc", price: 9.99))

        then:
        def ex = thrown(MenuItemNotFoundException)
        ex.message == "Menu item not found with id: invalid"
    }

    def "should delete menu item successfully"() {
        given:
        def id = "abc123"
        def item = new MenuItem(id: id, name: "Pizza", description: "desc", price: 9.99, createdAt: LocalDateTime.now(), updatedAt: LocalDateTime.now())
        menuRepository.findById(id) >> Optional.of(item)

        when:
        menuService.delete(id)

        then:
        1 * menuRepository.delete(item)
    }

    def "should throw MenuItemNotFoundException when deleting non-existent item"() {
        given:
        menuRepository.findById("invalid") >> Optional.empty()

        when:
        menuService.delete("invalid")

        then:
        def ex = thrown(MenuItemNotFoundException)
        ex.message == "Menu item not found with id: invalid"
    }

    def "should find menu item by id successfully"() {
        given:
        def id = "abc123"
        def item = new MenuItem(id: id, name: "Pizza", description: "Delicious cheese pizza", price: 9.99, createdAt: LocalDateTime.now())
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

    def "should throw MenuItemNotFoundException when finding non-existent item"() {
        given:
        menuRepository.findById("invalid") >> Optional.empty()

        when:
        menuService.findById("invalid")

        then:
        def ex = thrown(MenuItemNotFoundException)
        ex.message == "Menu item not found with id: invalid"
    }

    def "should return null createdAt when item has no createdAt"() {
        given:
        def item = new MenuItem(id: "abc123", name: "Pizza", description: "desc", price: 9.99, createdAt: null)
        menuRepository.findById("abc123") >> Optional.of(item)

        when:
        def response = menuService.findById("abc123")

        then:
        response.createdAt == null
    }

    def "should return paginated list of menu items"() {
        given:
        def item1 = new MenuItem(id: "a", name: "A", description: "A", price: 1.0, createdAt: LocalDateTime.now())
        def item2 = new MenuItem(id: "b", name: "B", description: "B", price: 2.0, createdAt: LocalDateTime.now())
        def page = new PageImpl<>([item1, item2], PageRequest.of(0, 2), 2)
        menuRepository.findAll(_ as PageRequest) >> page

        when:
        def response = menuService.findAll(2, 0)

        then:
        response.totalRecords == 2
        response.items.size() == 2
        response.items[0].id == "a"
        response.items[1].id == "b"
    }

    def "should calculate correct page number from offset"() {
        given:
        def page = new PageImpl<>([], PageRequest.of(1, 5), 10)
        menuRepository.findAll(_ as PageRequest) >> page

        when:
        def response = menuService.findAll(5, 5)

        then:
        response.totalRecords == 10
        response.items.size() == 0
    }
}
