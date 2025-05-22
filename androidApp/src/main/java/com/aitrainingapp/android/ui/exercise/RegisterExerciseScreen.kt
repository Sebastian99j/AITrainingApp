package com.aitrainingapp.android.ui.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterExerciseScreen(viewModel: ExerciseViewModel) {
    val isRunning by viewModel.timerRunning
    val series by viewModel.seriesList.collectAsState()
    val exercises by viewModel.exercises.collectAsState()
    val elapsed by viewModel.elapsedSeconds.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val plan by viewModel.nextTrainingPlan
    val feedbackSent by viewModel.feedbackSent

    LaunchedEffect(Unit) {
        viewModel.loadData()
        viewModel.fetchRecommendation()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
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
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
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
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                exerciseName = exercise
                                viewModel.setSelectedExercise(exercise)
                                expanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.onSurface,
                                disabledTextColor = Color.Gray
                            )
                        )
                    }
                }
            }

            val recommendation by viewModel.recommendation

            Spacer(Modifier.height(8.dp))
            recommendation?.let {
                androidx.compose.material3.Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = Color(0xFFFFF4E1),
                    shadowElevation = 4.dp,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "📈 Rekomendacja: $it",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
            plan?.let {
                Spacer(Modifier.height(8.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    color = Color(0xFFe3f2fd), // jasnoniebieskie tło
                    shadowElevation = 2.dp,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }
            }

            if (!feedbackSent) {
                Text("Czy rekomendacja się sprawdziła?", color = MaterialTheme.colorScheme.onBackground)

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { viewModel.sendFeedback(successful = true) },
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    ) {
                        Text("✅ Tak")
                    }
                    Button(
                        onClick = { viewModel.sendFeedback(successful = false) },
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    ) {
                        Text("❌ Nie")
                    }
                }
            } else {
                Text("✅ Feedback został zapisany", color = Color.Green)
            }

            Text("⏱️ Czas przerwy: ${if (isRunning) "$elapsed s" else "Zatrzymany"}",
                color = MaterialTheme.colorScheme.onBackground)

            Button(onClick = { viewModel.toggleTimer() }) {
                Text(if (isRunning) "Stop" else "Start")
            }

            var weight by remember { mutableStateOf("") }
            var reps by remember { mutableStateOf("") }
            var rpe by remember { mutableStateOf("") }

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Waga (kg)", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.onBackground,
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Powtórzenia", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.onBackground,
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = "1",
                    onValueChange = {},
                    label = { Text("Serie", color = MaterialTheme.colorScheme.onBackground) },
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.onBackground,
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                OutlinedTextField(
                    value = rpe,
                    onValueChange = { rpe = it },
                    label = { Text("RPE", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.onBackground,
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            Button(
                onClick = {
                    viewModel.addSeries(
                        exercise = exerciseName,
                        weight = weight.toFloatOrNull() ?: 0f,
                        reps = reps.toIntOrNull() ?: 0,
                        sets = 1,
                        rpe = rpe.toIntOrNull() ?: 0
                    )
                    weight = ""; reps = ""; rpe = ""
                }
            ) {
                Text("➕ Dodaj serię")
            }

            Spacer(Modifier.height(16.dp))

            series.forEach { s ->
                Text("🔹 ${s.weight}kg × ${s.reps} × ${s.sets}, ${s.rpe} RPE, ${s.durationSeconds}s",
                    color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "📊 Podsumowanie",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "🔁 Powtórzenia: ${viewModel.totalReps()}",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "📦 Serie: ${viewModel.totalSets()}",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "🏋️ Ciężar: ${viewModel.totalWeight()} kg",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "⏱️ Śr. czas serii: ${viewModel.averageDuration()} s",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                viewModel.saveAll()
                exerciseName = ""
                weight = ""
                reps = ""
                rpe = ""
            }) {
                Text("💾 Zapisz wszystkie")
            }
        }
    }
}
