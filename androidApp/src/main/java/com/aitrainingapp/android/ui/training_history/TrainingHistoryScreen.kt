package com.aitrainingapp.android.ui.training_history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aitrainingapp.android.viewmodel.TrainingHistoryViewModel
import com.aitrainingapp.util.formatDate

@Composable
fun TrainingHistoryScreen(viewModel: TrainingHistoryViewModel) {
    val history by viewModel.history.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Historia treningów",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val reversedHistory = history.reversed()
                items(reversedHistory.size) { index ->
                    val item = reversedHistory[index]
                    val volume = item.weight * item.reps * item.sets
                    val previousVolume = if (index < reversedHistory.lastIndex) {
                        val prev = reversedHistory[index + 1]
                        prev.weight * prev.reps * prev.sets
                    } else volume

                    val delta = volume - previousVolume
                    val (arrow, deltaColor) = when {
                        delta > 0 -> Icons.Default.ArrowDropUp to Color.Green
                        delta < 0 -> Icons.Default.ArrowDropDown to Color.Red
                        else -> null to Color.Gray
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.width(80.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${volume.toInt()} kg",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                arrow?.let {
                                    Icon(
                                        imageVector = it,
                                        contentDescription = null,
                                        tint = deltaColor,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "${kotlin.math.abs(delta).toInt()}",
                                        color = deltaColor,
                                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text("Typ: ${item.trainingType ?: "Nieznany"}", color = Color.Black)
                                Text("Waga: ${item.weight} kg", color = Color.Black)
                                Text("Powtórzenia: ${item.reps}", color = Color.Black)
                                Text("Serie: ${item.sets}", color = Color.Black)
                                Text("RPE: ${item.rpe}", color = Color.Black)
                                Text("Data: ${formatDate(item.dateTime)}", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}
