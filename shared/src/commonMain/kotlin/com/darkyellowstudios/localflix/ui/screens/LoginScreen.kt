package com.darkyellowstudios.localflix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkyellowstudios.localflix.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var serverUrl by remember { mutableStateOf("http://") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IndustrialObsidian)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "INDUSTRIAL CINEMA",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
            color = IndustrialYellow
        )
        
        Text(
            text = "CONNECT TO SERVER",
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.2.sp),
            color = IndustrialSlate,
            modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
        )

        IndustrialTextField(
            value = serverUrl,
            onValueChange = { serverUrl = it },
            label = "SERVER URL",
            placeholder = "http://192.168.1.100:8096"
        )

        Spacer(modifier = Modifier.height(16.dp))

        IndustrialTextField(
            value = username,
            onValueChange = { username = it },
            label = "USERNAME"
        )

        Spacer(modifier = Modifier.height(16.dp))

        IndustrialTextField(
            value = password,
            onValueChange = { password = it },
            label = "PASSWORD",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                isLoading = true
                // In a real app, call JellyfinManager here
                // For now, simulate success after a delay
                onLoginSuccess()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = IndustrialYellow,
                contentColor = IndustrialBlack
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = IndustrialBlack, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "AUTHORIZE",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp)
                )
            }
        }
        
        TextButton(
            onClick = { /* Help link */ },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                "NEED HELP CONNECTING?",
                style = MaterialTheme.typography.labelSmall,
                color = IndustrialSlate
            )
        }
    }
}

@Composable
fun IndustrialTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    isPassword: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = IndustrialSlate,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(IndustrialObsidian)
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) IndustrialYellow else IndustrialLead,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = IndustrialSlate.copy(alpha = 0.5f)
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = IndustrialWhite,
                    fontFamily = JetBrainsMono
                ),
                cursorBrush = SolidColor(IndustrialYellow),
                visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused }
            )
        }
    }
}
