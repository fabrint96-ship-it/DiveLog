package com.example.divelog.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.divelog.ui.components.buttons.DiveButton
import com.example.divelog.ui.components.buttons.DiveOutlinedButton
import com.example.divelog.ui.components.textfields.DivePasswordField
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal
import com.example.divelog.viewmodel.AuthUiState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import com.example.divelog.R

@Composable
fun LoginScreen(
    authUiState: AuthUiState,
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DiveDeepBlue,
                        DiveOceanBlue,
                        DiveTeal
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = DiveDeepBlue.copy(alpha = 0.78f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.divelog_logo),
                    contentDescription = "Logo de DiveLog",
                    modifier = Modifier.size(110.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "DiveLog",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = DiveFoam,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tu bitácora submarina",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DiveFoam.copy(alpha = 0.82f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text("Correo electrónico")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = DiveTeal,
                        unfocusedBorderColor = DiveFoam.copy(alpha = 0.45f),
                        focusedLabelColor = DiveTeal,
                        unfocusedLabelColor = DiveFoam.copy(alpha = 0.75f),
                        cursorColor = DiveTeal,
                        focusedLeadingIconColor = DiveTeal,
                        unfocusedLeadingIconColor = DiveFoam.copy(alpha = 0.75f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                DivePasswordField(
                    value = password,
                    onValueChange = { password = it },
                    passwordVisible = passwordVisible,
                    onTogglePasswordVisibility = {
                        passwordVisible = !passwordVisible
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                authUiState.errorMessage?.let { message ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                if (authUiState.isLoading) {
                    CircularProgressIndicator(
                        color = DiveFoam
                    )
                } else {
                    DiveButton(
                        text = "Iniciar sesión",
                        onClick = {
                            onLoginClick(email.trim(), password)
                        },
                        enabled = email.isNotBlank() && password.isNotBlank()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DiveOutlinedButton(
                        text = "Crear cuenta nueva",
                        onClick = {
                            onRegisterClick(email.trim(), password)
                        },
                        enabled = email.isNotBlank() && password.isNotBlank()
                    )
                }
            }
        }
    }
}