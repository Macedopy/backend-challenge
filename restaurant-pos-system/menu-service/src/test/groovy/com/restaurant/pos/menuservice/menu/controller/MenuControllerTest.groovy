package com.restaurant.pos.menuservice.menu.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.restaurant.pos.menuservice.menu.controller.MenuController
import com.restaurant.pos.menuservice.menu.service.MenuService
import com.restaurant.pos.menuservice.menu.dto.MenuItemRequest
import com.restaurant.pos.menuservice.menu.dto.MenuItemCreateResponse
import com.restaurant.pos.menuservice.menu.dto.MenuItemUpdateResponse
import com.restaurant.pos.menuservice.menu.dto.MenuItemListResponse
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

class MenuControllerTest extends Specification {

    MockMvc mockMvc
    MenuService menuService = Mock()

    void setup() {
        def controller = new MenuController(menuService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj)
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
    }

    def "should create a new menu item and return 201 Created"() {
        given:
        def request = new MenuItemRequest(name: "Pizza", description: "Delicious cheese pizza", price: 9.99)
        def response = new MenuItemCreateResponse(
            id: "abc123",
            name: "Pizza",
            description: "Delicious cheese pizza",
            price: 9.99,
            createdAt: "2024-10-21T10:00:30"
        )
        menuService.create(request) >> response

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.post("/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))

        then:
        result.andExpect(MockMvcResultMatchers.status().isCreated())

        def idPath = MockMvcResultMatchers.jsonPath('$.id')
        result.andExpect(idPath.value("abc123"))

        def namePath = MockMvcResultMatchers.jsonPath('$.name')
        result.andExpect(namePath.value("Pizza"))

        def descPath = MockMvcResultMatchers.jsonPath('$.description')
        result.andExpect(descPath.value("Delicious cheese pizza"))

        def pricePath = MockMvcResultMatchers.jsonPath('$.price')
        result.andExpect(pricePath.value(9.99))
    }

    def "should update an existing menu item and return 200 OK"() {
        given:
        def id = "abc123"
        def request = new MenuItemRequest(name: "Large Pizza", description: "Cheese pizza with extra toppings", price: 11.99)
        def response = new MenuItemUpdateResponse(
            id: id,
            name: "Large Pizza",
            description: "Cheese pizza with extra toppings",
            price: 11.99,
            updatedAt: "2024-10-21T10:15:00"
        )
        menuService.update(id, request) >> response

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.put("/menu-items/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))

        then:
        result.andExpect(MockMvcResultMatchers.status().isOk())

        def idPath = MockMvcResultMatchers.jsonPath('$.id')
        result.andExpect(idPath.value(id))

        def namePath = MockMvcResultMatchers.jsonPath('$.name')
        result.andExpect(namePath.value("Large Pizza"))

        def descPath = MockMvcResultMatchers.jsonPath('$.description')
        result.andExpect(descPath.value("Cheese pizza with extra toppings"))

        def pricePath = MockMvcResultMatchers.jsonPath('$.price')
        result.andExpect(pricePath.value(11.99))
    }

    def "should delete an existing menu item and return 200 OK with message"() {
        given:
        def id = "abc123"
        menuService.delete(id) >> { }

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.delete("/menu-items/{id}", id))

        then:
        result.andExpect(MockMvcResultMatchers.status().isOk())

        def messagePath = MockMvcResultMatchers.jsonPath('$.message')
        result.andExpect(messagePath.value("Menu item deleted successfully"))

        def idPath = MockMvcResultMatchers.jsonPath('$.id')
        result.andExpect(idPath.value(id))
    }

    def "should find menu item by ID and return 200 OK"() {
        given:
        def id = "abc123"
        def response = new MenuItemCreateResponse(
            id: id,
            name: "Pizza",
            description: "Delicious cheese pizza",
            price: 9.99,
            createdAt: "2024-10-21T10:00:30"
        )
        menuService.findById(id) >> response

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/menu-items/{id}", id))

        then:
        result.andExpect(MockMvcResultMatchers.status().isOk())

        def idPath = MockMvcResultMatchers.jsonPath('$.id')
        result.andExpect(idPath.value(id))

        def namePath = MockMvcResultMatchers.jsonPath('$.name')
        result.andExpect(namePath.value("Pizza"))

        def descPath = MockMvcResultMatchers.jsonPath('$.description')
        result.andExpect(descPath.value("Delicious cheese pizza"))

        def pricePath = MockMvcResultMatchers.jsonPath('$.price')
        result.andExpect(pricePath.value(9.99))

        def createdAtPath = MockMvcResultMatchers.jsonPath('$.createdAt')
        result.andExpect(createdAtPath.value("2024-10-21T10:00:30"))
    }

    def "should find all menu items with pagination and return 200 OK"() {
        given:
        def response = new MenuItemListResponse(
            items: [
                new MenuItemCreateResponse(id: "abc123", name: "Pizza", description: "Delicious cheese pizza", price: 9.99, createdAt: "2024-10-21T10:00:30"),
                new MenuItemCreateResponse(id: "xyz456", name: "Soda", description: "Refreshing soda", price: 1.99, createdAt: "2024-10-21T10:05:30")
            ],
            totalRecords: 2
        )
        menuService.findAll(10, 0) >> response

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/menu-items")
            .param("limit", "10")
            .param("offset", "0"))

        then:
        result.andExpect(MockMvcResultMatchers.status().isOk())

        def totalPath = MockMvcResultMatchers.jsonPath('$.totalRecords')
        result.andExpect(totalPath.value(2))

        def item1IdPath = MockMvcResultMatchers.jsonPath('$.items[0].id')
        result.andExpect(item1IdPath.value("abc123"))

        def item2IdPath = MockMvcResultMatchers.jsonPath('$.items[1].id')
        result.andExpect(item2IdPath.value("xyz456"))
    }
}
