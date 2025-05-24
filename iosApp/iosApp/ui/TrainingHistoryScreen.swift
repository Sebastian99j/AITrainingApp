import SwiftUI
import Shared

struct TrainingHistoryScreen: View {
    @ObservedObject var viewModel: TrainingHistoryViewModelWrapper

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Historia treningów")
                    .font(.title)
                    .bold()

                ForEach(viewModel.history.reversed().indices, id: \.self) { index in
                    let item = viewModel.history.reversed()[index]
                    let volume = item.weight * Double(item.reps * item.sets)

                    let previousVolume: Double = {
                        if index < viewModel.history.count - 1 {
                            let prev = viewModel.history.reversed()[index + 1]
                            return prev.weight * Double(prev.reps * prev.sets)
                        } else {
                            return volume
                        }
                    }()

                    let delta = volume - previousVolume
                    let arrowIcon: String? = delta > 0 ? "arrow.up" :
                                             delta < 0 ? "arrow.down" : nil
                    let deltaColor: Color = delta > 0 ? .green :
                                            delta < 0 ? .red : .gray

                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            VStack {
                                Text("\(Int(volume)) kg")
                                    .font(.headline)
                                if let arrow = arrowIcon {
                                    Image(systemName: arrow)
                                        .foregroundColor(deltaColor)
                                    Text("\(abs(Int(delta)))")
                                        .foregroundColor(deltaColor)
                                        .font(.subheadline)
                                }
                            }
                            .frame(width: 80)

                            VStack(alignment: .leading, spacing: 4) {
                                Text("Typ: \(item.trainingType ?? "Nieznany")")
                                Text("Waga: \(item.weight, specifier: "%.1f") kg")
                                Text("Powtórzenia: \(item.reps)")
                                Text("Serie: \(item.sets)")
                                Text("RPE: \(item.rpe)")
                                Text("Data: \(formatDate(item.dateTime))")
                            }
                            .font(.subheadline)
                        }
                        .padding()
                        .background(Color(UIColor.secondarySystemBackground))
                        .cornerRadius(10)
                    }
                }
            }
            .padding()
        }
        .onAppear {
            viewModel.load()
        }
    }
}
