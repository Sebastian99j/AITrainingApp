package com.aitrainingapp.android.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.aitrainingapp.android.room.ProfileEntity
import com.aitrainingapp.android.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val profiles by viewModel.profiles.collectAsState()
    val username by viewModel.username.collectAsState()
    val aiId by viewModel.aiIdentifier.collectAsState()
    val selectedId by viewModel.selectedProfileId.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedProfile by remember { mutableStateOf<ProfileEntity?>(null) }

    LaunchedEffect(profiles, selectedId) {
        if (selectedProfile == null && profiles.isNotEmpty()) {
            selectedProfile = profiles.find { it.id == selectedId } ?: profiles.first()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Profil użytkownika",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            val avatarUrl = remember {
                val id = (1..1000).random()
                "https://api.dicebear.com/7.x/personas/png?seed=user$id"
            }

            Image(
                painter = rememberAsyncImagePainter(avatarUrl),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = username,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = aiId,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Wybierz profil",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedProfile?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Profil") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.exposedDropdownSize()
                ) {
                    profiles.forEach { profile ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    profile.name,
                                    color = Color.Black
                                )
                            },
                            onClick = {
                                selectedProfile = profile
                                expanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = Color.Black,
                                disabledTextColor = Color.Gray
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            selectedProfile?.let { profile ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Szczegóły profilu:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Zmiana ciężaru: ${profile.weightChance}kg",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Zmiana powtórzeń: ${profile.repsChance}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Zmiana serii: ${profile.setsChance}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    selectedProfile?.let { profile ->
                        coroutineScope.launch {
                            viewModel.setProfileForUser(profile.id)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Zapisz")
            }
        }
    }
}
