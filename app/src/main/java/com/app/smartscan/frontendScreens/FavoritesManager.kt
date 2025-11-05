package com.app.smartscan.frontendScreens

object FavoritesManager {
    private val favorites = mutableListOf<String>()

    fun add(item: String) {
        if (!favorites.contains(item)) favorites.add(item)
    }

    fun getAll(): List<String> = favorites
}
