import Shared
import Combine

class UserSettingsViewModel: ObservableObject {
    let controller: SharedUserSettingsController
    private var cancellables = Set<AnyCancellable>()

    @Published var username: String = ""
    @Published var darkMode: Bool = true
    @Published var notificationOn: Bool = false

    init(userQueries: UserQueries) {
        controller = SharedUserSettingsController(userQueries: userQueries, scope: MainScope())

        controller.username.watch { [weak self] name in
            self?.username = name ?? ""
        }.store(in: &cancellables)

        controller.darkMode.watch { [weak self] mode in
            self?.darkMode = mode ?? true
        }.store(in: &cancellables)

        controller.notificationOn.watch { [weak self] enabled in
            self?.notificationOn = enabled ?? false
        }.store(in: &cancellables)
    }

    func toggleNotification(_ value: Bool) {
        controller.updateNotificationFlag(enabled: value)
    }

    func updateUsername(_ name: String) {
        controller.updateUsername(newName: name)
    }

    func toggleDarkMode(_ value: Bool) {
        controller.toggleDarkMode(enabled: value)
    }
}
