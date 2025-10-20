package com.app.smartscan.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.app.smartscan.data.AuthRepository
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
    val isSignedIn: Boolean = false
)

/**
 * ViewModel for the authentication screen.
 */
class AuthViewModel(
    private val repository: AuthRepository,
    private val functions: FirebaseFunctions,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Check the initial sign-in state
        val currentUser = repository.currentUser
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
                val user = repository.signUp(
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
                val user = repository.signIn(_uiState.value.email, _uiState.value.password)
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
        repository.signOut()
        _uiState.value = AuthUiState(message = "Signed out")
    }

    fun onFetchProductClicked(barcode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(message = "Fetching product...") }
            try {
                val data = mapOf("barcode" to barcode)
                val result = functions.getHttpsCallable("fetchProductFromOBF").call(data).await()
                val resultMap = result.data as? Map<*, *>
                _uiState.update { it.copy(message = "Function result: $resultMap") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Function error: ${e.message}") }
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

    // Factory to create the ViewModel with its dependencies
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // This is a simple service locator pattern. For a larger app, you'd use Hilt or Koin.
                val auth = FirebaseAuth.getInstance()
                val db = FirebaseFirestore.getInstance()
                val functions = FirebaseFunctions.getInstance()
                val repository = AuthRepository(auth, db)
                return AuthViewModel(repository, functions, db) as T
            }
        }
    }
}
