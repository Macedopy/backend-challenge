package com.restaurant.pos.menuservice.shared.exception

import com.fasterxml.jackson.databind.ObjectMapper
import com.restaurant.pos.menuservice.menu.controller.MenuController
import com.restaurant.pos.menuservice.menu.service.MenuService
import com.restaurant.pos.menuservice.menu.dto.MenuItemRequest
import com.restaurant.pos.menuservice.menu.exception.MenuItemAlreadyExistsException
import com.restaurant.pos.menuservice.menu.exception.MenuItemNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import spock.lang.Specification

class GlobalExceptionHandlerTest extends Specification {

    MockMvc mockMvc
    MenuService menuService = Mock()
    ObjectMapper objectMapper = new ObjectMapper()
    GlobalExceptionHandler handler = new GlobalExceptionHandler()

    void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new MenuController(menuService))
            .setControllerAdvice(handler)
            .build()
    }

    def "should return 404 when menu item not found by id"() {
        given:
        menuService.findById("invalid") >> { throw new MenuItemNotFoundException("invalid") }

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/menu-items/invalid"))

        then:
        result.andExpect(MockMvcResultMatchers.status().isNotFound())
        result.andExpect(MockMvcResultMatchers.jsonPath('$.status').value(404))
        result.andExpect(MockMvcResultMatchers.jsonPath('$.error').value("Not Found"))
        result.andExpect(MockMvcResultMatchers.jsonPath('$.message').value("Menu item not found with id: invalid"))
        result.andExpect(MockMvcResultMatchers.jsonPath('$.timestamp').exists())
    }

    def "should return 404 when updating non-existent menu item"() {
        given:
        def request = new MenuItemRequest(name: "Pizza", description: "desc", price: 9.99)
        menuService.update("invalid", _) >> { throw new MenuItemNotFoundException("invalid") }

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.put("/menu-items/invalid")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(MockMvcResultMatchers.status().isNotFound())
        result.andExpect(MockMvcResultMatchers.jsonPath('$.status').value(404))
    }

    def "should return 404 when deleting non-existent menu item"() {
        given:
        menuService.delete("invalid") >> { throw new MenuItemNotFoundException("invalid") }

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.delete("/menu-items/invalid"))

        then:
        result.andExpect(MockMvcResultMatchers.status().isNotFound())
        result.andExpect(MockMvcResultMatchers.jsonPath('$.status').value(404))
    }

    def "should return 409 when creating menu item with duplicate name"() {
        given:
        def request = new MenuItemRequest(name: "Pizza", description: "desc", price: 9.99)
        menuService.create(_) >> { throw new MenuItemAlreadyExistsException("Pizza") }

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.post("/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(MockMvcResultMatchers.status().isConflict())
        result.andExpect(MockMvcResultMatchers.jsonPath('$.status').value(409))
        result.andExpect(MockMvcResultMatchers.jsonPath('$.error').value("Conflict"))
        result.andExpect(MockMvcResultMatchers.jsonPath('$.message').value("Menu item already exists with name: Pizza"))
    }

    def "should return 400 with errors array when name is blank"() {
        given:
        def invalidRequest = [name: "", description: "desc", price: 9.99]

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.post("/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))

        then:
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
        result.andExpect(MockMvcResultMatchers.jsonPath('$.status').value(400))
        result.andExpect(MockMvcResultMatchers.jsonPath('$.message').value("Validation failed"))
        result.andExpect(MockMvcResultMatchers.jsonPath('$.errors').isArray())
        result.andExpect(MockMvcResultMatchers.jsonPath('$.errors[0]').exists())
    }

    def "should return 400 with errors array when price is negative"() {
        given:
        def invalidRequest = [name: "Pizza", description: "desc", price: -1.0]

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.post("/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))

        then:
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
        result.andExpect(MockMvcResultMatchers.jsonPath('$.status').value(400))
        result.andExpect(MockMvcResultMatchers.jsonPath('$.message').value("Validation failed"))
        result.andExpect(MockMvcResultMatchers.jsonPath('$.errors').isArray())
        result.andExpect(MockMvcResultMatchers.jsonPath('$.errors[0]').exists())
    }

    def "should return 400 when body is malformed JSON"() {
        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.post("/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ invalid json }"))

        then:
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
        result.andExpect(MockMvcResultMatchers.jsonPath('$.status').value(400))
        result.andExpect(MockMvcResultMatchers.jsonPath('$.message').value("Malformed or unreadable request body"))
    }

    def "should return 400 when required query param is missing"() {
        when:
        def ex = new MissingServletRequestParameterException("limit", "int")
        def response = handler.handleMissingParam(ex)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body.status == 400
        response.body.message == "Missing required parameter: 'limit'"
    }

    def "should return 400 when path variable has wrong type"() {
        when:
        def ex = new MethodArgumentTypeMismatchException("abc", Integer, "id", null, new NumberFormatException("abc"))
        def response = handler.handleTypeMismatch(ex)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body.status == 400
        response.body.message == "Invalid value 'abc' for parameter 'id'"
    }

    def "should return 500 when unexpected error occurs"() {
        given:
        menuService.findById("abc") >> { throw new RuntimeException("Unexpected error") }

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/menu-items/abc"))

        then:
        result.andExpect(MockMvcResultMatchers.status().isInternalServerError())
        result.andExpect(MockMvcResultMatchers.jsonPath('$.status').value(500))
        result.andExpect(MockMvcResultMatchers.jsonPath('$.message').value("An unexpected error occurred"))
    }
}
