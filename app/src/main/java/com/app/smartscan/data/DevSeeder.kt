package com.app.smartscan.data

import com.app.smartscan.data.model.Product
import com.app.smartscan.data.model.Scan
import com.app.smartscan.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * A helper object for seeding the Firestore database with initial data for development and testing.
 */
object DevSeeder {

    /**
     * Ensures that a user profile document exists for the currently authenticated user.
     * If it doesn't exist, a new default profile is created.
     */
    suspend fun ensureUserProfile(db: FirebaseFirestore, auth: FirebaseAuth) {
        val uid = auth.currentUser?.uid ?: return
        val ref = db.collection(FsPaths.USERS).document(uid)
        if (!ref.get().await().exists()) {
            ref.set(UserProfile()).await()
        }
    }

    /**
     * Seeds the 'products' collection with a predefined list of sample products.
     * This is useful for development and UI testing.
     */
    suspend fun seedProducts(db: FirebaseFirestore) {
        val list = listOf(
            Product(
                barcode = "4005808856713",
                brand = "NIVEA",
                name = "Soft Moisturizing Cream",
                category = "moisturizer",
                ingredients = listOf("Aqua","Glycerin","Paraffinum Liquidum","Cetearyl Alcohol","Parfum"),
                source = "manual"
            ),
            Product(
                barcode = "3600521798216",
                brand = "L'Oréal Paris",
                name = "Revitalift Filler Serum",
                category = "serum",
                ingredients = listOf("Aqua","Hyaluronic Acid","Alcohol Denat.","Parfum"),
                source = "manual"
            ),
            Product(
                barcode = "3337872411991",
                brand = "La Roche-Posay",
                name = "Toleriane Sensitive",
                category = "moisturizer",
                ingredients = listOf("Aqua","Glycerin","Squalane","Niacinamide","Caprylic/Capric Triglyceride"),
                source = "manual"
            )
        )
        // Note: This bypasses security rules and should only be used in a trusted environment.
        list.forEach { p ->
            db.collection(FsPaths.PRODUCTS).document(p.barcode).set(p).await()
        }
    }

    /**
     * Creates a sample scan document for the currently authenticated user.
     */
    suspend fun createSampleScan(db: FirebaseFirestore, auth: FirebaseAuth, barcode: String): String {
        val uid = auth.currentUser?.uid ?: error("not signed in")
        val doc = db.collection(FsPaths.SCANS).document()
        val scan = Scan(uid = uid, barcode = barcode, productKey = barcode)
        doc.set(scan).await()
        return doc.id
    }

    /**
     * Placeholder for the Cloud Function call to seed products from OpenBeautyFacts.
     */
    suspend fun seedProductsViaCF(): String {
        // This will be implemented in a later phase.
        return "Cloud Function not implemented yet."
    }
}
