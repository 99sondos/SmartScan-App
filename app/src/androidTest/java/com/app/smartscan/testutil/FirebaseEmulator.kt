package com.app.smartscan.testutil

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions

object FirebaseEmulator {
    private var initialized = false

    fun initOnce() {
        if (initialized) return
        val context = ApplicationProvider.getApplicationContext<Context>()
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        // Point SDKs to local emulators
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
        FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
        FirebaseFunctions.getInstance("europe-west1").useEmulator("10.0.2.2", 5001)
        initialized = true
    }
}