package com.restaurant.pos.menuservice.menu

import com.restaurant.pos.menuservice.menu.dto.MenuItemRequest
import com.restaurant.pos.menuservice.menu.dto.MenuItemCreateResponse
import com.restaurant.pos.menuservice.menu.dto.MenuItemUpdateResponse
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
    ResponseEntity<MenuItemCreateResponse> create(@Valid @RequestBody MenuItemRequest request) {
        def response = service.create(request)
        ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    ResponseEntity<MenuItemUpdateResponse> update(@PathVariable("id") String id, @Valid @RequestBody MenuItemRequest request) {
        def response = service.update(id, request)
        ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Map<String, String>> delete(@PathVariable("id") String id) {
        service.delete(id)
        ResponseEntity.ok([message: "Menu item deleted successfully", id: id])
    }

    @GetMapping("/{id}")
    ResponseEntity<MenuItemCreateResponse> findById(@PathVariable("id") String id) {
        ResponseEntity.ok(service.findById(id))
    }

    @GetMapping
    ResponseEntity<MenuItemListResponse> findAll(
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset) {
        ResponseEntity.ok(service.findAll(limit, offset))
    }
}
