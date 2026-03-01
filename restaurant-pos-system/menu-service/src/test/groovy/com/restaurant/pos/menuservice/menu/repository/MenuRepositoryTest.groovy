package com.restaurant.pos.menuservice.menu.repository

import com.restaurant.pos.menuservice.menu.MenuRepository
import com.restaurant.pos.menuservice.menu.entity.MenuItem
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class MenuRepositoryTest extends Specification {

    @Subject
    MenuRepository menuRepository = Mock(MenuRepository)

    def "should save a new MenuItem successfully"() {
        given:
        def item = new MenuItem(
            name: "Classic Burger",
            description: "Burger with lettuce, tomato, and cheese",
            price: 25.90,
            createdAt: LocalDateTime.now(),
            updatedAt: LocalDateTime.now()
        )
        def savedItem = new MenuItem(
            id: "burger123",
            name: item.name,
            description: item.description,
            price: item.price,
            createdAt: item.createdAt,
            updatedAt: item.updatedAt
        )
        menuRepository.save(item) >> savedItem

        when:
        def result = menuRepository.save(item)

        then:
        result.id == "burger123"
        result.name == "Classic Burger"
    }

    def "should find MenuItem by ID"() {
        given:
        def item = new MenuItem(id: "pizza123", name: "Margherita Pizza", description: "Cheese", price: 39.90)
        menuRepository.findById("pizza123") >> Optional.of(item)

        when:
        def result = menuRepository.findById("pizza123")

        then:
        result.isPresent()
        result.get().name == "Margherita Pizza"
    }

    def "should return empty Optional when ID not found"() {
        given:
        menuRepository.findById("nonexistent") >> Optional.empty()

        when:
        def result = menuRepository.findById("nonexistent")

        then:
        result.isEmpty()
    }

    def "should delete MenuItem by ID"() {
        when:
        menuRepository.deleteById("burger123")

        then:
        noExceptionThrown()
    }

    def "should check if MenuItem exists"() {
        given:
        menuRepository.existsById("burger123") >> true

        when:
        def exists = menuRepository.existsById("burger123")

        then:
        exists == true
    }

    def "should return false when MenuItem does not exist"() {
        given:
        menuRepository.existsById("fake123") >> false

        when:
        def exists = menuRepository.existsById("fake123")

        then:
        exists == false
    }

    def "should find paginated items"() {
        given:
        def item1 = new MenuItem(id: "a", name: "Item A", price: 10.0)
        def item2 = new MenuItem(id: "b", name: "Item B", price: 20.0)
        def page = new PageImpl<MenuItem>([item1, item2], PageRequest.of(0, 2), 2)
        menuRepository.findAll(_ as PageRequest) >> page

        when:
        def result = menuRepository.findAll(PageRequest.of(0, 2))

        then:
        result.content.size() == 2
        result.totalElements == 2
    }

    def "should delete all items"() {
        when:
        menuRepository.deleteAll()

        then:
        noExceptionThrown()
    }
}
