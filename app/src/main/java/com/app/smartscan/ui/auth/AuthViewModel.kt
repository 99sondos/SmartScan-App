package com.app.smartscan.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.app.smartscan.data.*
import com.app.smartscan.data.model.UserProfile
import com.app.smartscan.data.seed.AllergySeeder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Data class to hold the state for the authentication screen.
 */
data class AuthUiState(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val message: String = "",
    val isSignedIn: Boolean = false,
    val scanId: String? = null // Add this to hold the current scan ID
)

/**
 * ViewModel for the authentication screen.
 */
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val scanRepository: ScanRepository,
    private val functions: FirebaseFunctions,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Check the initial sign-in state
        val currentUser = authRepository.currentUser
        _uiState.update { it.copy(isSignedIn = currentUser != null, message = if(currentUser != null) "Signed in as ${currentUser.email}" else "Signed out") }
    }

    fun onFullNameChange(fullName: String) {
        _uiState.update { it.copy(fullName = fullName) }
    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onSignUpClicked() {
        viewModelScope.launch {
            try {
                val user = authRepository.signUp(
                    _uiState.value.email,
                    _uiState.value.password,
                    _uiState.value.fullName,
                    _uiState.value.username
                )
                _uiState.update {
                    it.copy(
                        message = "Sign up successful: ${user.email}",
                        isSignedIn = true,
                        password = "" // Clear password field for security
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error: ${e.message}") }
            }
        }
    }

    fun onSignInClicked() {
        viewModelScope.launch {
            try {
                val user = authRepository.signIn(_uiState.value.email, _uiState.value.password)
                _uiState.update {
                    it.copy(
                        message = "Sign in successful: ${user.email}",
                        isSignedIn = true,
                        password = "" // Clear password field for security
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error: ${e.message}") }
            }
        }
    }

    fun onSignOutClicked() {
        authRepository.signOut()
        _uiState.value = AuthUiState(message = "Signed out")
    }

    fun onFetchProductClicked(barcode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(message = "Fetching product...") }
            try {
                // Step 1: Call the Cloud Function to fetch the product.
                val data = mapOf("barcode" to barcode)
                val result = functions.getHttpsCallable("fetchProductFromOBF").call(data).await()
                val resultMap = result.data as? Map<*, *>

                if (resultMap?.get("found") != true) {
                    throw Exception("Product not found in OpenBeautyFacts.")
                }

                // Step 2: Get the user and product data from repositories.
                val uid = authRepository.currentUser?.uid ?: throw Exception("User not signed in")
                val userProfile = userRepository.getUser(uid) ?: UserProfile() // Assume default if null
                val product = productRepository.getProduct(barcode) ?: throw Exception("Product not found after fetch")

                // Step 3: Create the initial scan document.
                val scanId = scanRepository.createScan(uid, barcode, null)

                // Step 4: Compute flags by comparing ingredients with user's lists.
                val userLists = userProfile.allergies + userProfile.blacklist
                val flags = product.ingredients.filter { ingredient ->
                    userLists.any { userListItem ->
                        ingredient.contains(userListItem, ignoreCase = true)
                    }
                }

                // Step 5: Update the scan with the computed flags.
                scanRepository.updateScanFlags(scanId, flags)

                _uiState.update { it.copy(message = "Scan created with ${flags.size} flags.", scanId = scanId) }

            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Function error: ${e.message}") }
            }
        }
    }

    fun onGenerateExplanationClicked(scanId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(message = "Generating explanation...") }
            try {
                val data = mapOf("scanId" to scanId)
                val result = functions.getHttpsCallable("generateExplanation").call(data).await()
                val resultMap = result.data as? Map<*, *>
                _uiState.update { it.copy(message = "Explanation result: $resultMap") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Explanation error: ${e.message}") }
            }
        }
    }

    fun onSeedAllergiesClicked() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(message = "Seeding allergies...") }
                AllergySeeder.seed(db)
                _uiState.update { it.copy(message = "Allergies seeded successfully!") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error seeding allergies: ${e.message}") }
            }
        }
    }
    fun saveOcrScan(text: String) {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: return@launch
            scanRepository.createScan(uid, null, text)
        }
    }

    fun saveSkinScan(text: String) {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: return@launch
            scanRepository.createScan(uid, null, text)
        }
    }

    fun saveBarcodeScan(barcode: String) {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: return@launch
            scanRepository.createScan(uid, barcode, null) // save scan
            onFetchProductClicked(barcode) // continue backend pipeline
        }
    }


    // Factory to create the ViewModel with its dependencies
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // This is a simple service locator pattern. For a larger app, you'd use Hilt or Koin.
                val auth = FirebaseAuth.getInstance()
                val db = FirebaseFirestore.getInstance()
                val functions = FirebaseFunctions.getInstance("europe-west1")
                val authRepository = AuthRepository(auth, db)
                val userRepository = UserRepository(db)
                val productRepository = ProductRepository(db)
                val scanRepository = ScanRepository(db)
                return AuthViewModel(
                    authRepository,
                    userRepository,
                    productRepository,
                    scanRepository,
                    functions,
                    db
                ) as T
            }
        }
    }
}
