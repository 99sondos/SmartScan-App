package com.app.smartscan.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Represents a user's profile in Firestore.
 *
 * @property skinType The user's reported skin type (e.g., \"oily\", \"dry\", \"combination\").
 * @property allergies A list of known allergens for the user.
 * @property blacklist A list of ingredients the user wants to avoid.
 * @property createdAt The timestamp when the user profile was created.
 */
data class UserProfile(
    val skinType: String = "",
    val allergies: List<String> = emptyList(),
    val blacklist: List<String> = emptyList(),
    @ServerTimestamp
    val createdAt: Date? = null
)
