import Combine
import AVFoundation
import MediaPlayer // <- to jest potrzebne do MPVolumeView
import shared

class UserSettingsViewModelWrapper: ObservableObject {
    private let viewModel: UserSettingsViewModel
    private var cancellables = Set<AnyCancellable>()

    @Published var username: String = ""
    @Published var darkMode: Bool = false
    @Published var notificationsOn: Bool = false

    init(userQueries: UserQueries) {
        self.viewModel = UserSettingsViewModel(userQueries: userQueries)
        observe()
    }

    private func observe() {
        viewModel.$username
            .receive(on: DispatchQueue.main)
            .sink { [weak self] name in
                self?.username = name
            }
            .store(in: &cancellables)

        viewModel.$darkMode
            .receive(on: DispatchQueue.main)
            .sink { [weak self] dark in
                self?.darkMode = dark
            }
            .store(in: &cancellables)

        viewModel.$notificationOn
            .receive(on: DispatchQueue.main)
            .sink { [weak self] enabled in
                self?.notificationsOn = enabled
            }
            .store(in: &cancellables)
    }

    func updateUsername(_ newName: String) {
        viewModel.updateUsername(newName)
    }

    func toggleNotification(enabled: Bool) {
        viewModel.toggleNotification(enabled)
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
