package com.app.smartscan.frontendScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.smartscan.ui.auth.AuthUiState

/**
 * Screen for creating an account
 * Shown when user presses "Create Account" on the Home screen.
 */
@Composable
fun CreateAccountScreen(
    uiState: AuthUiState,
    onFullNameChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onBack: () -> Unit,
    onCreateAccount: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // Check if all fields are filled from the UiState
    val isFormValid = uiState.fullName.isNotBlank() &&
            uiState.email.isNotBlank() &&
            uiState.username.isNotBlank() &&
            uiState.password.isNotBlank()

    // --- Main screen layout ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Back button (top-left)
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Go back",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Center content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create Your Account",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Full name field ---
            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = onFullNameChanged,
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Email field ---
            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Username field ---
            OutlinedTextField(
                value = uiState.username,
                onValueChange = onUsernameChanged,
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Password field with visibility toggle ---
            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Display error message if it exists
            if (uiState.message.isNotEmpty() && uiState.message.startsWith("Error")) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.message,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Create Account button ---
            Button(
                onClick = onCreateAccount,
                enabled = isFormValid, // Disabled until all fields filled
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Create Account", fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateAccountScreenPreview() {
    MaterialTheme {
        // Provide a sample AuthUiState for the preview
        CreateAccountScreen(
            uiState = AuthUiState(fullName = "Jane Doe", email = "jane.doe@example.com", username = "janedoe"),
            onFullNameChanged = {},
            onUsernameChanged = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onBack = {},
            onCreateAccount = {}
        )
    }
}
