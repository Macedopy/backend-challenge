package com.restaurant.pos.menuservice.menu

import com.restaurant.pos.menuservice.menu.dto.MenuItemRequest
import com.restaurant.pos.menuservice.menu.dto.MenuItemResponse
import com.restaurant.pos.menuservice.menu.entity.MenuItem
import groovy.transform.CompileStatic
import org.springframework.stereotype.Service

import java.time.LocalDateTime

@Service
@CompileStatic
class MenuService {

    private final MenuItemRepository repository

    MenuService(MenuItemRepository repository) {
        this.repository = repository
    }

    MenuItemResponse create(MenuItemRequest request) {
        def item = new MenuItem(
                name: request.name,
                description: request.description,
                price: request.price
        )
        def saved = repository.save(item)
        toResponse(saved)
    }

    MenuItemResponse update(String id, MenuItemRequest request) {
        def item = repository.findById(id).orElseThrow { new RuntimeException("Item not found") }
        item.name = request.name
        item.description = request.description
        item.price = request.price
        def saved = repository.save(item)
        toResponse(saved)
    }

    void delete(String id) {
        repository.deleteById(id)
    }

    MenuItemResponse findById(String id) {
        def item = repository.findById(id).orElseThrow { new RuntimeException("Item not found") }
        toResponse(item)
    }

    MenuItemListResponse findAll(int limit, int offset) {
        def page = repository.findAll(org.springframework.data.domain.PageRequest.of(offset / limit, limit))
        def responses = page.content.collect { toResponse(it) }
        new MenuItemListResponse(items: responses, totalRecords: page.totalElements)
    }

    private MenuItemResponse toResponse(MenuItem item) {
        new MenuItemResponse(
                id: item.id,
                name: item.name,
                description: item.description,
                price: item.price,
                createdAt: item.createdAt?.toString()
        )
    }
}
