import SwiftUI
import shared
import Charts

struct ProgressionAnalysisScreen: View {
    @ObservedObject var viewModel: SharedProgressionViewModel
    @State private var selectedExercise: String = ""
    @State private var expanded: Bool = false

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Analiza progresji ciężaru")
                    .font(.title)
                    .bold()
                    .padding(.top, 20)

                // Dropdown ćwiczenia
                VStack(alignment: .leading) {
                    Text("Nazwa ćwiczenia")
                        .font(.subheadline)
                        .foregroundColor(.gray)

                    Menu {
                        ForEach(viewModel.exercises, id: \.self) { exercise in
                            Button(exercise) {
                                selectedExercise = exercise
                                viewModel.analyzeProgression(type: exercise)
                            }
                        }
                    } label: {
                        HStack {
                            Text(selectedExercise.isEmpty ? "Wybierz ćwiczenie" : selectedExercise)
                                .foregroundColor(.primary)
                            Spacer()
                            Image(systemName: "chevron.down")
                                .foregroundColor(.gray)
                        }
                        .padding()
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.gray.opacity(0.5), lineWidth: 1)
                        )
                    }
                }

                // Wykres
                if !viewModel.regressionData.isEmpty {
                    LineChartView(data: viewModel.regressionData)
                        .frame(height: 300)
                        .padding(.top)

                    Text("Prognozy ciężaru:")
                        .font(.headline)

                    ForEach(viewModel.forecastMap.sorted(by: { $0.key < $1.key }), id: \.key) { key, value in
                        HStack {
                            Text(key)
                            Spacer()
                            Text(String(format: "%.1f kg", value))
                                .bold()
                        }
                        .padding(.vertical, 4)
                    }
                } else {
                    Text("Brak danych lub analiza w toku...")
                        .foregroundColor(.gray)
                        .padding(.top, 20)
                }

                Spacer()
            }
            .padding()
        }
        .onAppear {
            viewModel.loadData()
        }
    }
}
