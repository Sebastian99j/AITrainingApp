package com.aitrainingapp.android.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    currentUsername: String,
    onUsernameChange: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf(currentUsername) }
    var darkMode by remember { mutableStateOf(false) }
    var volumeLevel by remember { mutableFloatStateOf(0.5f) }
    var brightness by remember { mutableFloatStateOf(0.5f) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Ustawienia",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            // 🔤 Edycja nazwy użytkownika
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nazwa użytkownika") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.Gray
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        onUsernameChange(username)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Zapisz nazwę")
            }

            Divider(color = Color.DarkGray)

            // 🌙 Tryb ciemny (fake)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tryb ciemny", color = Color.White)
                Switch(checked = darkMode, onCheckedChange = { darkMode = it })
            }

            // 🔊 Suwak głośności (fake)
            Text("Głośność", color = Color.White)
            Slider(
                value = volumeLevel,
                onValueChange = { volumeLevel = it },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )

            // ☀️ Jasność (fake)
            Text("Jasność", color = Color.White)
            Slider(
                value = brightness,
                onValueChange = { brightness = it },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
