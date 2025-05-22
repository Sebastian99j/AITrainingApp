package com.aitrainingapp.android.ui.analysis

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.aitrainingapp.android.viewmodel.ProgressionViewModel
import androidx.compose.runtime.Composable
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressionAnalysisScreen(viewModel: ProgressionViewModel) {
    val data by viewModel.regressionData
    val forecast by viewModel.forecastMap
    var expanded by remember { mutableStateOf(false) }
    val exercises by viewModel.exercises.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(26.dp))

            Text(
                "Analiza progresji ciężaru",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                                viewModel.analyzeProgression(exercise)
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

            Spacer(modifier = Modifier.height(16.dp))

            if (data.isNotEmpty()) {
                LineChart(data)
                Spacer(modifier = Modifier.height(24.dp))

                Text("Prognozy ciężaru:", color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))

                forecast.forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = MaterialTheme.colorScheme.onBackground)
                        Text("${"%.1f".format(value)} kg",
                            color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            } else {
                Text("Brak danych lub analiza w toku...", color = Color.Gray)
            }
        }
    }
}

@Composable
fun LineChart(data: List<Pair<String, Double>>) {
    val points = data.mapIndexed { index, entry ->
        Entry(index.toFloat(), entry.second.toFloat())
    }

    val dataSet = LineDataSet(points, "Waga (kg)").apply {
        color = android.graphics.Color.CYAN
        valueTextColor = android.graphics.Color.CYAN
        lineWidth = 3f
        circleRadius = 5f
        setCircleColor(android.graphics.Color.CYAN)
    }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                this.data = LineData(dataSet)

                setBackgroundColor(android.graphics.Color.BLACK)
                setGridBackgroundColor(android.graphics.Color.DKGRAY)
                setDrawGridBackground(true)

                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(data.map { it.first })
                    granularity = 1f
                    textColor = android.graphics.Color.WHITE
                    setDrawGridLines(false)
                    labelRotationAngle = -45f
                }

                axisLeft.textColor = android.graphics.Color.WHITE
                axisLeft.setDrawGridLines(true)
                axisRight.isEnabled = false

                legend.textColor = android.graphics.Color.WHITE
                description.isEnabled = false
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
    )
}
