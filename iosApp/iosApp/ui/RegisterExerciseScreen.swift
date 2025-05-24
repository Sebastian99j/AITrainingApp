import SwiftUI
import Shared

struct RegisterExerciseScreen: View {
    @ObservedObject var viewModel: ExerciseViewModelWrapper

    @State private var selectedExercise: String = ""
    @State private var showDropdown = false

    @State private var weight = ""
    @State private var reps = ""
    @State private var rpe = ""

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                // Dropdown ćwiczenia
                Menu {
                    ForEach(viewModel.exercises, id: \.self) { exercise in
                        Button(exercise) {
                            selectedExercise = exercise
                            viewModel.setSelectedExercise(exercise)
                        }
                    }
                } label: {
                    HStack {
                        Text(selectedExercise.isEmpty ? "Wybierz ćwiczenie" : selectedExercise)
                        Spacer()
                        Image(systemName: "chevron.down")
                    }
                    .padding()
                    .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color.gray))
                }

                // Rekomendacja
                if let recommendation = viewModel.recommendation {
                    Text("📈 Rekomendacja: \(recommendation)")
                        .font(.system(size: 18, weight: .medium))
                        .padding()
                        .background(Color.yellow.opacity(0.2))
                        .cornerRadius(8)
                }

                // Plan
                if let plan = viewModel.plan {
                    Text(plan)
                        .padding()
                        .background(Color.blue.opacity(0.2))
                        .cornerRadius(8)
                }

                // Feedback
                if !viewModel.feedbackSent {
                    Text("Czy rekomendacja się sprawdziła?")
                    HStack {
                        Button("✅ Tak") {
                            viewModel.sendFeedback(successful: true)
                        }
                        .buttonStyle(.borderedProminent)

                        Button("❌ Nie") {
                            viewModel.sendFeedback(successful: false)
                        }
                        .buttonStyle(.bordered)
                    }
                } else {
                    Text("✅ Feedback został zapisany")
                        .foregroundColor(.green)
                }

                // Timer
                Text("⏱️ Czas przerwy: \(viewModel.elapsedSeconds) s")
                Button(viewModel.timerRunning ? "Stop" : "Start") {
                    viewModel.toggleTimer()
                }

                // Formularz serii
                HStack {
                    TextField("Waga (kg)", text: $weight)
                        .keyboardType(.decimalPad)
                        .textFieldStyle(.roundedBorder)
                    TextField("Powtórzenia", text: $reps)
                        .keyboardType(.numberPad)
                        .textFieldStyle(.roundedBorder)
                }

                HStack {
                    TextField("Serie", text: .constant("1"))
                        .disabled(true)
                        .textFieldStyle(.roundedBorder)
                    TextField("RPE", text: $rpe)
                        .keyboardType(.numberPad)
                        .textFieldStyle(.roundedBorder)
                }

                Button("➕ Dodaj serię") {
                    viewModel.addSeries(
                        exercise: selectedExercise,
                        weight: Float(weight) ?? 0,
                        reps: Int32(reps) ?? 0,
                        sets: 1,
                        rpe: Int32(rpe) ?? 0
                    )
                    weight = ""
                    reps = ""
                    rpe = ""
                }

                // Lista serii
                ForEach(viewModel.series, id: \.self) { s in
                    Text("🔹 \(s.weight)kg × \(s.reps) × \(s.sets), \(s.rpe) RPE, \(s.durationSeconds)s")
                }

                // Podsumowanie
                VStack(alignment: .leading) {
                    Text("📊 Podsumowanie").font(.headline)
                    Text("🔁 Powtórzenia: \(viewModel.totalReps())")
                    Text("📦 Serie: \(viewModel.totalSets())")
                    Text("🏋️ Ciężar: \(viewModel.totalWeight()) kg")
                    Text("⏱️ Śr. czas serii: \(viewModel.averageDuration()) s")
                }

                Button("💾 Zapisz wszystkie") {
                    viewModel.saveAll()
                    weight = ""
                    reps = ""
                    rpe = ""
                    selectedExercise = ""
                }
            }
            .padding()
        }
        .onAppear {
            viewModel.loadData()
            viewModel.fetchRecommendation()
        }
    }
}
