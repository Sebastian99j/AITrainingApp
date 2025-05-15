package com.aitrainingapp.android.ui.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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

@Composable
fun RegisterExerciseScreen(viewModel: ExerciseViewModel) {
    val isRunning by viewModel.timerRunning
    val series by viewModel.seriesList.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("⏱️ Czas serii: ${if (isRunning) "Trwa..." else "Zatrzymany"}")

        Button(onClick = { viewModel.toggleTimer() }) {
            Text(if (isRunning) "Stop" else "Start")
        }

        Spacer(Modifier.height(8.dp))

        // Pole na nazwę ćwiczenia
        var exerciseName by remember { mutableStateOf("") }
        OutlinedTextField(
            value = exerciseName,
            onValueChange = { exerciseName = it },
            label = { Text("Nazwa ćwiczenia") }
        )

        // Wprowadzenie danych serii
        var weight by remember { mutableStateOf("") }
        var reps by remember { mutableStateOf("") }
        var sets by remember { mutableStateOf("") }
        var rpe by remember { mutableStateOf("") }

        OutlinedTextField(weight, { weight = it }, label = { Text("Waga (kg)") })
        OutlinedTextField(reps, { reps = it }, label = { Text("Powtórzenia") })
        OutlinedTextField(sets, { sets = it }, label = { Text("Serie") })
        OutlinedTextField(rpe, { rpe = it }, label = { Text("RPE") })

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
