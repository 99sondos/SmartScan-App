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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Screen for creating an account
 * Shown when user presses "Create Account" on the Home screen.
 */
@Composable
fun CreateAccountScreen(
    onBack: () -> Unit,       // Go back to HomeScreen
    onCreateAccount: () -> Unit // Called when the user presses "Create Account"
) {
    // --- Remember text input values ---
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Check if all fields are filled
    val isFormValid = fullName.isNotBlank() &&
            email.isNotBlank() &&
            username.isNotBlank() &&
            password.isNotBlank()

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
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Email field ---
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Username field ---
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Password field with visibility toggle ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
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
        CreateAccountScreen(
            onBack = {},
            onCreateAccount = {}
        )
    }
}


