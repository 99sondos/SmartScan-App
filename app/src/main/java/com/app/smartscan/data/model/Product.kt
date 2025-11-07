package com.app.smartscan.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Represents a product in Firestore.
 *
 * @property barcode The EAN-13 barcode of the product.
 * @property brand The brand name of the product.
 * @property name The name of the product.
 * @property category The product category (e.g., \"moisturizer\", \"serum\").
 * @property ingredients A list of the product's ingredients.
 * @property source The origin of the data (e.g., \"obf\" for OpenBeautyFacts, \"manual\").
 * @property obfId The ID from OpenBeautyFacts, if applicable.
 * @property lastSynced The timestamp when the product was last updated from an external source.
 */
data class Product(
    val barcode: String = "",
    val brand: String = "",
    val name: String = "",
    val category: String = "",
    val ingredients: List<String> = emptyList(),
    val source: String = "manual",
    val obfId: String? = null,
    @ServerTimestamp
    val lastSynced: Date? = null
)
