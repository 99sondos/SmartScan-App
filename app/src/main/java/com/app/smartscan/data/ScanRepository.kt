package com.app.smartscan.data

import com.app.smartscan.data.model.Scan
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * A repository to handle all data operations related to scans.
 *
 * @property db An instance of FirebaseFirestore.
 */
class ScanRepository(private val db: FirebaseFirestore) {

    /**
     * Creates a new scan document in Firestore.
     *
     * @param uid The ID of the user performing the scan.
     * @param barcode The barcode identified, if any.
     * @param ocrText The raw OCR text, if any.
     * @return The ID of the newly created scan document.
     */
    suspend fun createScan(uid: String, barcode: String?, ocrText: String?): String {
        val newScan = Scan(
            uid = uid,
            barcode = barcode,
            ocrText = ocrText,
            productKey = barcode // Default to using barcode as the product key
        )

        val docRef = db.collection(FsPaths.SCANS).add(newScan).await()
        return docRef.id
    }

    /**
     * Observes a specific scan document for real-time updates.
     *
     * @param scanId The ID of the scan to observe.
     * @return A Flow that emits the Scan object whenever it changes, or null if it doesn't exist.
     */
    fun observeScan(scanId: String): Flow<Scan?> = callbackFlow {
        val docRef = db.collection(FsPaths.SCANS).document(scanId)

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Close the flow with an error if the listener fails
                close(error)
                return@addSnapshotListener
            }

            val scan = snapshot?.toObject(Scan::class.java)
            // Offer the latest value to the flow
            trySend(scan)
        }

        // When the flow is cancelled, remove the listener
        awaitClose { listener.remove() }
    }
}
