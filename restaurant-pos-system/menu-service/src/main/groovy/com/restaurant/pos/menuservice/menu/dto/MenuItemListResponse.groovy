package com.restaurant.pos.menuservice.menu.dto

import groovy.transform.Canonical

@Canonical
class MenuItemListResponse {
    List<MenuItemCreateResponse> items = []
    long totalRecords
}
