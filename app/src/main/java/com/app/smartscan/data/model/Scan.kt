package com.app.smartscan.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Represents a scan record in Firestore.
 *
 * @property uid The ID of the user who performed the scan.
 * @property barcode The barcode identified in the scan, if any.
 * @property ocrText The raw text captured via OCR, if any.
 * @property productKey The key used to link to the `products` collection (usually the barcode).
 * @property flags A list of flags generated based on the user's profile (e.g., allergens found).
 * @property explanation A map containing the AI-generated explanation.
 * @property status The current processing status of the scan.
 * @property createdAt The timestamp when the scan was created.
 */
data class Scan(
    val uid: String = "",
    val barcode: String? = null,
    val ocrText: String? = null,
    val productKey: String? = null,
    val flags: List<String> = emptyList(),
    val explanation: Map<String, Any>? = null,
    val status: String = "pending",
    @ServerTimestamp
    val createdAt: Date? = null
)
