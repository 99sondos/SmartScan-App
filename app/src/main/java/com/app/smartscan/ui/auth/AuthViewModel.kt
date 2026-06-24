package com.app.smartscan.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.app.smartscan.data.*
import com.app.smartscan.data.model.Product
import com.app.smartscan.data.model.Scan
import com.app.smartscan.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val message: String = "",
    val isSignedIn: Boolean = false,
    val isAnonymous: Boolean = true, // Track guest status
    val scanId: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val scanRepository: ScanRepository,
    private val functions: FirebaseFunctions
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        signInAnonymouslyIfNeeded()
    }

    private fun signInAnonymouslyIfNeeded() {
        viewModelScope.launch {
            val currentUser = authRepository.currentUser
            if (currentUser == null) {
                try {
                    val guestUser = authRepository.signInAnonymously()
                    _uiState.update { it.copy(
                        isSignedIn = true, 
                        isAnonymous = guestUser.isAnonymous, 
                        message = "Signed in as guest."
                    ) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isSignedIn = false, message = "Guest sign-in failed: ${e.message}") }
                }
            } else {
                _uiState.update { it.copy(
                    isSignedIn = true, 
                    isAnonymous = currentUser.isAnonymous, 
                    message = "Signed in as ${currentUser.email ?: "guest"}"
                ) }
            }
        }
    }

    fun onFullNameChange(fullName: String) { _uiState.update { it.copy(fullName = fullName) } }
    fun onUsernameChange(username: String) { _uiState.update { it.copy(username = username) } }
    fun onEmailChange(email: String) { _uiState.update { it.copy(email = email) } }
    fun onPasswordChange(password: String) { _uiState.update { it.copy(password = password) } }

    fun onSignUpClicked() {
        viewModelScope.launch {
            try {
                val user = authRepository.signUp(_uiState.value.email, _uiState.value.password, _uiState.value.fullName, _uiState.value.username)
                _uiState.update { it.copy(
                    message = "Sign up successful: ${user.email}", 
                    isSignedIn = true, 
                    isAnonymous = user.isAnonymous,
                    password = ""
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error: ${e.message}") }
            }
        }
    }

    fun onSignInClicked() {
        viewModelScope.launch {
            try {
                val user = authRepository.signIn(_uiState.value.email, _uiState.value.password)
                _uiState.update { it.copy(
                    message = "Sign in successful: ${user.email}", 
                    isSignedIn = true, 
                    isAnonymous = user.isAnonymous,
                    password = ""
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error: ${e.message}") }
            }
        }
    }

    fun onSignOutClicked() {
        viewModelScope.launch {
            authRepository.signOut()
            signInAnonymouslyIfNeeded()
        }
    }

    fun onFetchProductClicked(barcode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(message = "Fetching product...") }
            try {
                val data = mapOf("barcode" to barcode)
                functions.getHttpsCallable("fetchProductFromOBF").call(data).await()
                val uid = authRepository.currentUser?.uid ?: throw Exception("User not signed in")
                val userProfile = userRepository.getUser(uid) ?: UserProfile()
                val product = productRepository.getProduct(barcode) ?: throw Exception("Product not found after fetch")
                val scanId = scanRepository.createScan(uid, barcode, null)
                val userLists = userProfile.allergies + userProfile.blacklist
                val flags = product.ingredients.filter { ingredient -> userLists.any { userListItem -> ingredient.contains(userListItem, ignoreCase = true) } }
                scanRepository.updateScanFlags(scanId, flags)
                _uiState.update { it.copy(message = "Scan created. Triggering explanation...", scanId = scanId) }
                triggerExplanationGeneration(scanId)
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Function error: ${e.message}") }
            }
        }
    }

    fun onOcrScanClicked(ocrText: String) {
        viewModelScope.launch {
            // Safeguard: Reject empty or meaningless OCR text
            if (ocrText.length < 10) { 
                _uiState.update { it.copy(message = "Error: No meaningful text found in image.", scanId = null) }
                return@launch
            }
            _uiState.update { it.copy(message = "Creating OCR scan...") }
            try {
                val uid = authRepository.currentUser?.uid ?: throw Exception("User not signed in")
                val scanId = scanRepository.createScan(uid, null, ocrText)
                _uiState.update { it.copy(message = "OCR Scan created. Triggering explanation...", scanId = scanId) }
                triggerExplanationGeneration(scanId)
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error creating OCR scan: ${e.message}") }
            }
        }
    }

    private fun triggerExplanationGeneration(scanId: String) {
        viewModelScope.launch {
            try {
                val data = mapOf("scanId" to scanId)
                functions.getHttpsCallable("generateExplanation").call(data).await()
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Explanation trigger error: ${e.message}") }
            }
        }
    }

    fun observeScan(scanId: String): Flow<Scan?> {
        return scanRepository.observeScan(scanId)
    }
    
    suspend fun getProduct(barcode: String): Product? {
        return productRepository.getProduct(barcode)
    }
    
    fun onQuestionnaireSubmitted(skinType: String, isSensitive: Boolean, ageRange: String, allergies: List<String>) {
        viewModelScope.launch {
            try {
                val uid = authRepository.currentUser?.uid ?: throw Exception("User not signed in")
                userRepository.updateUserQuestionnaire(uid, skinType, isSensitive, ageRange, allergies)
                _uiState.update { it.copy(message = "Profile updated successfully!") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error updating profile: ${e.message}") }
            }
        }
    }

    fun updateUserSkinTypeFromAnalysis(analysisResult: String) {
        viewModelScope.launch {
            try {
                val uid = authRepository.currentUser?.uid ?: throw Exception("User not signed in")
                val existingProfile = userRepository.getUser(uid) ?: UserProfile()
                val updatedProfile = existingProfile.copy(skinType = analysisResult)
                userRepository.upsertUser(uid, updatedProfile)
                _uiState.update { it.copy(message = "AI Analysis Result: $analysisResult") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error saving skin analysis: ${e.message}") }
            }
        }
    }

    fun onAddToFavoritesClicked(barcode: String) {
        viewModelScope.launch {
            try {
                val uid = authRepository.currentUser?.uid ?: throw Exception("User not signed in")
                userRepository.addToFavorites(uid, barcode)
                _uiState.update { it.copy(message = "Added to favorites.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error adding to favorites: ${e.message}") }
            }
        }
    }

    fun onAddToBlacklistClicked(barcode: String) {
        viewModelScope.launch {
            try {
                val uid = authRepository.currentUser?.uid ?: throw Exception("User not signed in")
                userRepository.addToBlacklist(uid, barcode)
                _uiState.update { it.copy(message = "Added to blacklist.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error adding to blacklist: ${e.message}") }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = "") }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
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
                    functions
                ) as T
            }
        }
    }
}