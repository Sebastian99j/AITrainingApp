import SwiftUI
import shared

struct TrainingTypeScreen: View {
    @ObservedObject var viewModel: SharedTrainingTypeViewModel

    @State private var newName: String = ""

    var body: some View {
        VStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Typy ćwiczeń")
                        .font(.title)
                        .bold()
                        .padding(.bottom, 8)

                    ForEach(viewModel.types, id: \.self.name) { type in
                        RoundedRectangle(cornerRadius: 16)
                            .fill(Color(uiColor: .secondarySystemFill))
                            .frame(minHeight: 56)
                            .overlay(
                                HStack {
                                    Text(type.name)
                                        .foregroundColor(.primary)
                                        .padding(.horizontal)
                                    Spacer()
                                }
                            )
                    }
                }
                .padding()
            }

            Divider()
                .padding(.horizontal)

            HStack {
                TextField("Nowy typ ćwiczenia", text: $newName)
                    .textFieldStyle(.roundedBorder)

                Button(action: {
                    viewModel.addTrainingType(name: newName) {
                        newName = ""
                    }
                }) {
                    Text("Dodaj")
                        .padding(.horizontal)
                        .padding(.vertical, 10)
                        .background(Color.accentColor)
                        .foregroundColor(.white)
                        .cornerRadius(12)
                }
                .disabled(newName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
            }
            .padding(.horizontal)
            .padding(.bottom)
        }
        .onAppear {
            viewModel.loadTypes()
        }
    }
}
