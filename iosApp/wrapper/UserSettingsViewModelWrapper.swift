import Combine
import AVFoundation

class UserSettingsViewModelWrapper: ObservableObject {
    private let viewModel: UserSettingsViewModel

    @Published var username: String = ""
    @Published var darkMode: Bool = false
    @Published var notificationsOn: Bool = false

    init(userQueries: UserQueries) {
        self.viewModel = UserSettingsViewModel(userQueries: userQueries)
        observe()
    }

    private func observe() {
        viewModel.username.watch { [weak self] name in
            DispatchQueue.main.async {
                self?.username = name ?? ""
            }
        }

        viewModel.darkMode.watch { [weak self] dark in
            DispatchQueue.main.async {
                self?.darkMode = dark as? Bool ?? false
            }
        }

        viewModel.notificationOn.watch { [weak self] enabled in
            DispatchQueue.main.async {
                self?.notificationsOn = enabled as? Bool ?? false
            }
        }
    }

    func updateUsername(newName: String) {
        viewModel.updateUsername(newName: newName)
    }

    func toggleNotification(enabled: Bool) {
        viewModel.toggleNotification(context: nil, enabled: enabled)
    }

    func setVolume(volume: Float) {
        let session = AVAudioSession.sharedInstance()
        do {
            try session.setActive(true)
            let volumeView = MPVolumeView(frame: .zero)
            if let slider = volumeView.subviews.first(where: { $0 is UISlider }) as? UISlider {
                slider.value = volume
            }
        } catch {
            print("⚠️ Nie można ustawić głośności: \(error)")
        }
    }
}
