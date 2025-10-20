package com.app.smartscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.smartscan.ui.auth.AuthViewModel
import com.app.smartscan.ui.theme.SmartScanTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // When in debug mode, connect to the Firebase Emulator Suite.
        if (BuildConfig.USE_EMULATORS) {
            FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
            FirebaseFunctions.getInstance().useEmulator("10.0.2.2", 5001)
        }

        setContent {
            SmartScanTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AuthScreen(modifier: Modifier = Modifier, authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)) {
    val uiState by authViewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (uiState.isSignedIn) {
            Text("Welcome!")
            Spacer(Modifier.height(16.dp))
            Text(uiState.message)
            Spacer(Modifier.height(16.dp))

            // Add the test button for the cloud function
            Button(onClick = { authViewModel.onFetchProductClicked("3337872411991") }) {
                Text("Fetch Product (Test)")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { authViewModel.onSignOutClicked() }) {
                Text("Sign Out")
            }

        } else {
            Text("Sign Up / Sign In")
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { authViewModel.onEmailChange(it) },
                label = { Text("Email") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { authViewModel.onPasswordChange(it) },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(16.dp))

            Text(uiState.message, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { authViewModel.onSignInClicked() }) {
                    Text("Sign In")
                }
                Button(onClick = { authViewModel.onSignUpClicked() }) {
                    Text("Sign Up")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    SmartScanTheme {
        AuthScreen()
    }
}
