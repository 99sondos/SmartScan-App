package com.app.smartscan.data.model

/**
 * Represents a user profile in Firestore.
 */
data class UserProfile(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val skinType: String? = null,
    val isSensitive: Boolean = false,
    val ageRange: String? = null,
    val allergies: List<String> = emptyList(),
    val blacklist: List<String> = emptyList(),
    val favorites: List<String> = emptyList()
)
