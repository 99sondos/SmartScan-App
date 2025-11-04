package com.app.smartscan.data

import com.app.smartscan.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * A repository to handle all authentication-related tasks.
 *
 * @property auth An instance of FirebaseAuth.
 * @property db An instance of FirebaseFirestore.
 */
class AuthRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    /**
     * Gets the currently signed-in user.
     */
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Creates a new user with email and password, and also creates their profile in Firestore.
     *
     * @param email The user's email.
     * @param password The user's chosen password.
     * @param fullName The user's full name.
     * @param username The user's chosen username.
     * @return The newly created FirebaseUser.
     * @throws Exception if sign-up or profile creation fails.
     */
    suspend fun signUp(email: String, password: String, fullName: String, username: String): FirebaseUser {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val user = authResult.user ?: throw IllegalStateException("User not created")

        // Create the user profile document in Firestore
        val userProfile = UserProfile(
            fullName = fullName,
            username = username
        )
        db.collection(FsPaths.USERS).document(user.uid).set(userProfile).await()

        return user
    }

    /**
     * Signs in a user with their email and password.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @return The signed-in FirebaseUser.
     * @throws Exception if sign-in fails.
     */
    suspend fun signIn(email: String, password: String): FirebaseUser {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        return authResult.user ?: throw IllegalStateException("User not found")
    }

    /**
     * Signs out the currently authenticated user.
     */
    fun signOut() {
        auth.signOut()
    }
}
