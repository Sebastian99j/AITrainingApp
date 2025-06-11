import SwiftUI
import AVFoundation

struct SettingsScreen: View {
    @ObservedObject var viewModel: UserSettingsViewModelWrapper

    @State private var username: String = ""
    @State private var volumeLevel: Float = 0.5
    @State private var brightness: CGFloat = UIScreen.main.brightness

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                Text("Ustawienia")
                    .font(.title)
                    .bold()

                // 🔤 Nazwa użytkownika
                TextField("Nazwa użytkownika", text: $username)
                    .textFieldStyle(.roundedBorder)

                Button("Zapisz nazwę") {
                    viewModel.updateUsername(username)
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.accentColor)
                .foregroundColor(.white)
                .cornerRadius(8)

                Divider()

                // 🌙 Tryb ciemny
                Toggle("Tryb ciemny", isOn: $viewModel.darkMode)
                    .tint(.blue)

                // 🔔 Powiadomienia
                Toggle("Przypomnienie o treningu", isOn: $viewModel.notificationsOn)
                    .tint(.green)
                    .onChange(of: viewModel.notificationsOn) { enabled in
                        viewModel.toggleNotification(enabled: enabled)
                    }

                // 🔊 Głośność
                VStack(alignment: .leading) {
                    Text("Głośność")
                    Slider(value: $volumeLevel, in: 0...1) { _ in
                        viewModel.setVolume(volume: volumeLevel)
                    }
                }

                // ☀️ Jasność
                VStack(alignment: .leading) {
                    Text("Jasność")
                    Slider(value: $brightness, in: 0...1) { _ in
                        UIScreen.main.brightness = brightness
                    }
                }
            }
            .padding()
        }
        .onAppear {
            username = viewModel.username
        }
    }
}
