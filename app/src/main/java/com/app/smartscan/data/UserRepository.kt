package com.app.smartscan.data

import com.app.smartscan.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * A repository to handle all data operations related to user profiles.
 *
 * @property db An instance of FirebaseFirestore.
 */
class UserRepository(private val db: FirebaseFirestore) {

    /**
     * Retrieves a user's profile from Firestore.
     *
     * @param uid The user's unique ID.
     * @return The UserProfile object, or null if not found.
     */
    suspend fun getUser(uid: String): UserProfile? {
        return db.collection(FsPaths.USERS).document(uid).get().await()
            .toObject(UserProfile::class.java)
    }

    /**
     * Creates or updates a user's profile in Firestore.
     * This uses SetOptions.merge() to avoid overwriting fields that are not included.
     *
     * @param uid The user's unique ID.
     * @param userProfile The UserProfile object with the data to save.
     */
    suspend fun upsertUser(uid: String, userProfile: UserProfile) {
        db.collection(FsPaths.USERS).document(uid).set(userProfile, SetOptions.merge()).await()
    }
}
