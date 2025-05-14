package com.aitrainingapp.android.ui.training_history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                items(history) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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
