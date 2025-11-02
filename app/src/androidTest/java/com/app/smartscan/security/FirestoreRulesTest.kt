package com.app.smartscan.security

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.smartscan.testutil.FirebaseEmulator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.tasks.await
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirestoreRulesTest {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    @Before
    fun setUp() {
        FirebaseEmulator.initOnce()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun clientCannotUpdateScanExplanationOrStatus() = runTest {
        // 1) Create user and sign in (emulator)
        val email = "t1@example.com"; val pwd = "Password123!"
        auth.createUserWithEmailAndPassword(email, pwd).await()
        auth.signInWithEmailAndPassword(email, pwd).await()
        val uid = auth.currentUser!!.uid

        // 2) Create a scan doc owned by the user (allowed)
        val scanRef = db.collection("scans").document()
        scanRef.set(
            mapOf(
                "uid" to uid,
                "barcode" to "3337872411991",
                "status" to "pending",
                "explanation" to null
            )
        ).await()

        // 3) Try to change status → should be denied by rules
        try {
            scanRef.update(mapOf("status" to "enriched")).await()
            fail("Expected permission denied when updating status")
        } catch (_: Exception) { /* OK */ }

        // 4) Try to change explanation → should be denied by rules
        try {
            scanRef.update(mapOf("explanation" to mapOf("text" to "fake"))).await()
            fail("Expected permission denied when updating explanation")
        } catch (_: Exception) { /* OK */ }

        // 5) Updating allowed field (e.g., flags) should pass
        scanRef.update(mapOf("flags" to listOf("parfum"))).await()
    }
}
