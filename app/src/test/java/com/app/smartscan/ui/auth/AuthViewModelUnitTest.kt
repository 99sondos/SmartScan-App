package com.app.smartscan.ui.auth

import com.app.smartscan.data.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelUnitTest {

    private lateinit var authRepo: AuthRepository
    private lateinit var userRepo: UserRepository
    private lateinit var productRepo: ProductRepository
    private lateinit var scanRepo: ScanRepository
    private lateinit var functions: FirebaseFunctions
    private lateinit var db: FirebaseFirestore

    private lateinit var vm: AuthViewModel

    @Before
    fun setup() {
        // relaxed mocks so unused calls don't crash
        authRepo = mockk(relaxed = true)
        userRepo = mockk(relaxed = true)
        productRepo = mockk(relaxed = true)
        scanRepo = mockk(relaxed = true)
        functions = mockk(relaxed = true)
        db = mockk(relaxed = true)

        // currentUser is null by default
        every { authRepo.currentUser } returns null

        vm = AuthViewModel(authRepo, userRepo, productRepo, scanRepo, functions, db)
    }

    @Test
    fun `full name change updates state`() = runTest {
        vm.onFullNameChange("Alice Doe")
        assertEquals("Alice Doe", vm.uiState.value.fullName)
    }

    @Test
    fun `email and password changes update state`() = runTest {
        vm.onEmailChange("alice@example.com")
        vm.onPasswordChange("Secret123!")
        assertEquals("alice@example.com", vm.uiState.value.email)
        assertEquals("Secret123!", vm.uiState.value.password)
    }

    @Test
    fun `sign out resets UI state and calls repository`() = runTest {
        vm.onEmailChange("x@y.z")
        vm.onPasswordChange("p")
        vm.onFullNameChange("X")
        vm.onUsernameChange("y")

        vm.onSignOutClicked()

        // repo called
        verify(exactly = 1) { authRepo.signOut() }

        // UI state reset (only message is "Signed out")
        val s = vm.uiState.value
        assertEquals(false, s.isSignedIn)
        assertEquals("", s.fullName)
        assertEquals("", s.username)
        assertEquals("", s.email)
        assertEquals("", s.password)
        assertEquals(null, s.scanId)
        assertEquals("Signed out", s.message)
    }
}