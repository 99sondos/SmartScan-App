package com.app.smartscan.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Represents a user's profile in Firestore.
 *
 * @property fullName The user's full name.
 * @property username The user's chosen username.
 * @property skinType The user's reported skin type (e.g., "oily", "dry", "combination").
 * @property isSensitive Whether the user's skin is sensitive.
 * @property ageRange The user's selected age range (e.g., "18-25").
 * @property allergies A list of known allergens for the user.
 * @property blacklist A list of product barcodes the user wants to avoid.
 * @property favorites A list of product barcodes the user has liked.
 * @property createdAt The timestamp when the user profile was created.
 */
data class UserProfile(
    val fullName: String = "",
    val username: String = "",
    val skinType: String = "",
    val isSensitive: Boolean = false,
    val ageRange: String = "",
    val allergies: List<String> = emptyList(),

    // ✅ Products user wants to avoid
    val blacklist: List<String> = emptyList(),

    // ✅ Products user has marked as favorite
    val favorites: List<String> = emptyList(),

    @ServerTimestamp
    val createdAt: Date? = null
)
