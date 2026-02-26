package com.restaurant.pos.menuservice.menu

import com.restaurant.pos.menuservice.menu.dto.MenuItemRequest
import com.restaurant.pos.menuservice.menu.dto.MenuItemResponse
import com.restaurant.pos.menuservice.menu.dto.MenuItemListResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/menu-items")
class MenuController {

    private final MenuService service

    MenuController(MenuService service) {
        this.service = service
    }

    @PostMapping
    ResponseEntity<MenuItemResponse> create(@Valid @RequestBody MenuItemRequest request) {
        def response = service.create(request)
        ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    ResponseEntity<MenuItemResponse> update(@PathVariable String id, @Valid @RequestBody MenuItemRequest request) {
        def response = service.update(id, request)
        ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        service.delete(id)
        ResponseEntity.ok([message: "Menu item deleted successfully", id: id])
    }

    @GetMapping("/{id}")
    ResponseEntity<MenuItemResponse> findById(@PathVariable String id) {
        ResponseEntity.ok(service.findById(id))
    }

    @GetMapping
    ResponseEntity<MenuItemListResponse> findAll(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        ResponseEntity.ok(service.findAll(limit, offset))
    }
}
