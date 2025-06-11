import SwiftUI
import shared

struct TrainingHistoryScreen: View {
    @ObservedObject var viewModel: TrainingHistoryViewModelWrapper
    
    private var reversedHistory: [TrainingSeries] {
        viewModel.history.reversed()
    }
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Historia treningów")
                    .font(.title)
                    .bold()
                    .padding(.bottom, 8)
                
                if reversedHistory.isEmpty {
                    Text("Brak historii treningów")
                        .foregroundColor(.secondary)
                        .padding()
                } else {
                    ForEach(Array(reversedHistory.enumerated()), id: \.element.id) { index, item in
                        TrainingHistoryItemView(
                            item: item,
                            index: index,
                            history: reversedHistory
                        )
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

struct TrainingHistoryItemView: View {
    let item: TrainingSeries
    let index: Int
    let history: [TrainingSeries]
    
    private var volume: Double {
        Double(item.weight) * Double(item.reps * item.sets)
    }
    
    private var previousVolume: Double {
        if index < history.count - 1 {
            let prev = history[index + 1]
            return Double(prev.weight) * Double(prev.reps * prev.sets)
        } else {
            return volume
        }
    }
    
    private var delta: Double {
        volume - previousVolume
    }
    
    private var arrowIcon: String? {
        delta > 0 ? "arrow.up" : delta < 0 ? "arrow.down" : nil
    }
    
    private var deltaColor: Color {
        delta > 0 ? .green : delta < 0 ? .red : .gray
    }
    
    var body: some View {
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
                    Text("Data: \(formatDateString(item.dateTime))")
                }
                .font(.subheadline)
            }
            .padding()
            .background(Color(UIColor.secondarySystemBackground))
            .cornerRadius(10)
        }
    }
    
    private func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        formatter.locale = Locale(identifier: "pl_PL")
        return formatter.string(from: date)
    }
    
    private func formatDateString(_ dateString: String) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss" // Adjust to match your string format
        guard let date = formatter.date(from: dateString) else { return dateString }
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}

struct TrainingHistoryItem: Identifiable {
    let id = UUID()
    let weight: Double
    let reps: Int
    let sets: Int
    let rpe: Int
    let trainingType: String?
    let dateTime: Date
}
