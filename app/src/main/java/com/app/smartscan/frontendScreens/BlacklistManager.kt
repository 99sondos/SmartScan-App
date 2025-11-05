package com.app.smartscan.frontendScreens

object BlacklistManager {
    private val blacklist = mutableListOf<String>()

    fun add(item: String) {
        if (!blacklist.contains(item)) blacklist.add(item)
    }

    fun getAll(): List<String> = blacklist
}
