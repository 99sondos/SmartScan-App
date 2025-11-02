package com.app.smartscan.data

import com.app.smartscan.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * A repository to handle all data operations related to products.
 *
 * @property db An instance of FirebaseFirestore.
 */
class ProductRepository(private val db: FirebaseFirestore) {

    /**
     * Retrieves a product from Firestore by its barcode.
     *
     * @param barcode The product's barcode.
     * @return The Product object, or null if not found.
     */
    suspend fun getProduct(barcode: String): Product? {
        return db.collection(FsPaths.PRODUCTS).document(barcode).get().await()
            .toObject(Product::class.java)
    }
}
