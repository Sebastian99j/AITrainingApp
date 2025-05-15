package com.aitrainingapp.android.ui.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aitrainingapp.android.viewmodel.ExerciseViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterExerciseScreen(viewModel: ExerciseViewModel) {
    val isRunning by viewModel.timerRunning
    val series by viewModel.seriesList.collectAsState()
    val exercises by viewModel.exercises.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("⏱️ Czas serii: ${if (isRunning) "Trwa..." else "Zatrzymany"}")

        Button(onClick = { viewModel.toggleTimer() }) {
            Text(if (isRunning) "Stop" else "Start")
        }

        Spacer(Modifier.height(8.dp))

        var exerciseName by remember { mutableStateOf("") }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = exerciseName,
                onValueChange = { exerciseName = it },
                readOnly = true,
                label = { Text("Nazwa ćwiczenia") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black,
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
                exercises.forEach { exercise ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                exercise,
                                color = Color.Black
                            )
                        },
                        onClick = {
                            exerciseName = exercise
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

        var weight by remember { mutableStateOf("") }
        var reps by remember { mutableStateOf("") }
        var sets by remember { mutableStateOf("") }
        var rpe by remember { mutableStateOf("") }

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Waga (kg)") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            OutlinedTextField(
                value = reps,
                onValueChange = { reps = it },
                label = { Text("Powtórzenia") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = sets,
                onValueChange = { sets = it },
                label = { Text("Serie") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            OutlinedTextField(
                value = rpe,
                onValueChange = { rpe = it },
                label = { Text("RPE") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }

        Button(
            onClick = {
                viewModel.addSeries(
                    weight = weight.toFloatOrNull() ?: 0f,
                    reps = reps.toIntOrNull() ?: 0,
                    sets = sets.toIntOrNull() ?: 0,
                    rpe = rpe.toIntOrNull() ?: 0
                )
                weight = ""; reps = ""; sets = ""; rpe = ""
            }
        ) {
            Text("➕ Dodaj serię")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(series) { s ->
                Text("🔹 ${s.weight}kg × ${s.reps} × ${s.sets}, ${s.rpe} RPE, ${s.durationSeconds}s")
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("📊 Podsumowanie", style = MaterialTheme.typography.titleMedium)
        Text("Łącznie powtórzeń: ${viewModel.totalReps()}")
        Text("Łącznie serii: ${viewModel.totalSets()}")
        Text("Łączny ciężar: ${viewModel.totalWeight()} kg")
        Text("Średni czas serii: ${viewModel.averageDuration()} s")

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            if (exerciseName.isNotBlank()) {
                viewModel.saveAll(exerciseName)
            }
        }) {
            Text("💾 Zapisz wszystkie")
        }
    }
}
