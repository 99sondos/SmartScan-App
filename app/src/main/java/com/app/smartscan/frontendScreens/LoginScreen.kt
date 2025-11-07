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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.smartscan.ui.auth.AuthUiState

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onBack: () -> Unit,
    onLogin: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val isFormValid = uiState.email.isNotBlank() && uiState.password.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back!",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = if (passwordVisible) "Hide password" else "Show password")
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

            Button(
                onClick = onLogin,
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Log In", fontSize = 18.sp)
            }
        }
    }
}