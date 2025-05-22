package com.aitrainingapp.android.ui.settings

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aitrainingapp.android.viewmodel.UserSettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    currentUsername: String,
    onUsernameChange: (String) -> Unit,
    viewModel: UserSettingsViewModel
) {
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf(currentUsername) }
    var volumeLevel by remember { mutableFloatStateOf(0.5f) }
    var brightness by remember { mutableFloatStateOf(0.5f) }
    val activity = LocalContext.current as? Activity

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
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
                color = MaterialTheme.colorScheme.onBackground
            )

            // 🔤 Edycja nazwy użytkownika
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nazwa użytkownika") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
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
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Zapisz nazwę")
            }

            Divider(color = Color.DarkGray)

            // 🌙 Tryb ciemny
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tryb ciemny", color = MaterialTheme.colorScheme.onBackground)
                Switch(
                    checked = viewModel.darkMode.collectAsState().value,
                    onCheckedChange = { viewModel.toggleDarkMode(it) }
                )
            }

            val context = LocalContext.current

            Text("Głośność", color = MaterialTheme.colorScheme.onBackground)
            Slider(
                value = volumeLevel,
                onValueChange = {
                    volumeLevel = it
                    viewModel.setVolume(context, it)
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )

            Text("Jasność", color = MaterialTheme.colorScheme.onBackground)
            Slider(
                value = brightness,
                onValueChange = {
                    brightness = it
                    activity?.window?.attributes = activity?.window?.attributes?.apply {
                        screenBrightness = it
                    }
                }
            )
        }
    }
}
