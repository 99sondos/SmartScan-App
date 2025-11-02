package com.app.smartscan.data.seed

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * A helper class to seed the Firestore database with initial data.
 * This is useful for populating data that the app relies on, like a list of known allergens.
 */
object AllergySeeder {

    // A curated list of common allergens found in beauty products.
    private val commonAllergens = listOf(
        "Fragrance",
        "Parabens",
        "Sulfates",
        "Alcohol",
        "Formaldehyde",
        "Phthalates",
        "Essential Oils",
        "Limonene",
        "Linalool",
        "Coconut",
        "Gluten",
        "Soy"
    )

    /**
     * Seeds the 'known_allergies' collection in Firestore.
     * This function is idempotent; it will not create duplicates if run multiple times
     * because the document IDs are the allergen names themselves (in lowercase).
     *
     * @param db An instance of FirebaseFirestore.
     */
    suspend fun seed(db: FirebaseFirestore) {
        // Note: You should add "known_allergies" to your FsPaths.kt file for consistency.
        val collection = db.collection("known_allergies")
        for (allergen in commonAllergens) {
            val allergenData = mapOf("name" to allergen)
            // Use the lowercase allergen name as the document ID for easy lookup and to prevent duplicates.
            collection.document(allergen.lowercase()).set(allergenData).await()
        }
    }
}
