package com.app.smartscan.data

import com.app.smartscan.data.model.UserProfile
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun signIn(email: String, password: String): FirebaseUser {
        return auth.signInWithEmailAndPassword(email, password).await().user!!
    }

    suspend fun signUp(email: String, password: String, fullName: String, username: String): FirebaseUser {
        val currentUser = auth.currentUser
        return if (currentUser != null && currentUser.isAnonymous) {
            // If the user is anonymous, link the new credentials
            val credential = EmailAuthProvider.getCredential(email, password)
            val authResult = currentUser.linkWithCredential(credential).await()
            val user = authResult.user!!

            // Now that the account is linked, create the user profile document
            val userProfile = UserProfile(fullName = fullName, username = username, email = email)
            db.collection(FsPaths.USERS).document(user.uid).set(userProfile).await()
            user
        } else {
            // If there is no anonymous user, create a new account from scratch
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user!!
            val userProfile = UserProfile(fullName = fullName, username = username, email = email)
            db.collection(FsPaths.USERS).document(user.uid).set(userProfile).await()
            user
        }
    }

    suspend fun signInAnonymously(): FirebaseUser {
        return auth.signInAnonymously().await().user!!
    }

    fun signOut() {
        auth.signOut()
    }
}