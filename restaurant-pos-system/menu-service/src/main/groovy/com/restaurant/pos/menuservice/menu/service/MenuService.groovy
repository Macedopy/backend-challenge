package com.restaurant.pos.menuservice.menu.service

import com.restaurant.pos.menuservice.menu.dto.MenuItemRequest
import com.restaurant.pos.menuservice.menu.dto.MenuItemCreateResponse
import com.restaurant.pos.menuservice.menu.dto.MenuItemUpdateResponse
import com.restaurant.pos.menuservice.menu.dto.MenuItemListResponse
import com.restaurant.pos.menuservice.menu.entity.MenuItem
import com.restaurant.pos.menuservice.menu.repository.MenuRepository
import com.restaurant.pos.menuservice.menu.exception.MenuItemAlreadyExistsException
import com.restaurant.pos.menuservice.menu.exception.MenuItemNotFoundException
import groovy.transform.CompileStatic
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class MenuService {

    private final MenuRepository repository

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    MenuService(MenuRepository repository) {
        this.repository = repository
    }

    MenuItemCreateResponse create(MenuItemRequest request) {
        if (repository.existsByName(request.name)) {
            throw new MenuItemAlreadyExistsException(request.name)
        }

        def now = LocalDateTime.now()
        def item = new MenuItem(
                name: request.name,
                description: request.description,
                price: request.price,
                createdAt: now,
                updatedAt: now
        )
        def saved = repository.save(item)
        toCreateResponse(saved)
    }

    MenuItemUpdateResponse update(String id, MenuItemRequest request) {
        def item = repository.findById(id)
                .orElseThrow { new MenuItemNotFoundException(id) }

        item.name = request.name
        item.description = request.description
        item.price = request.price
        item.updatedAt = LocalDateTime.now()

        def saved = repository.save(item)
        toUpdateResponse(saved)
    }

    void delete(String id) {
        def item = repository.findById(id)
                .orElseThrow { new MenuItemNotFoundException(id) }
        repository.delete(item)
    }

    MenuItemCreateResponse findById(String id) {
        def item = repository.findById(id)
                .orElseThrow { new MenuItemNotFoundException(id) }
        toCreateResponse(item)
    }

    MenuItemListResponse findAll(int limit, int offset) {
        int pageNumber = offset.intdiv(limit)
        Pageable pageable = PageRequest.of(pageNumber, limit)
        Page<MenuItem> page = repository.findAll(pageable)
        List<MenuItemCreateResponse> responses = page.content.collect { toCreateResponse(it) }
        new MenuItemListResponse(
                items: responses,
                totalRecords: page.totalElements
        )
    }

    private MenuItemCreateResponse toCreateResponse(MenuItem item) {
        new MenuItemCreateResponse(
                id: item.id,
                name: item.name,
                description: item.description,
                price: item.price,
                createdAt: item.createdAt?.format(ISO_FORMATTER)
        )
    }

    private MenuItemUpdateResponse toUpdateResponse(MenuItem item) {
        new MenuItemUpdateResponse(
                id: item.id,
                name: item.name,
                description: item.description,
                price: item.price,
                updatedAt: item.updatedAt?.format(ISO_FORMATTER)
        )
    }
}
