import Combine
import shared

class UserSettingsViewModel: ObservableObject {
    let controller: UserSettingsController
    private var cancellables = Set<AnyCancellable>()

    @Published var username: String = ""
    @Published var darkMode: Bool = true
    @Published var notificationOn: Bool = false

    init(userQueries: UserQueries) {
        controller = UserSettingsController(userQueries: userQueries, scope: IOSScope.shared.scope)

        // ✅ ZAMIANA .username → .username()
        FlowWatcher.shared.watch(flow: controller.username()) { [weak self] value in
            self?.username = value as? String ?? ""
        }

        FlowWatcher.shared.watch(flow: controller.darkMode()) { [weak self] value in
            self?.darkMode = value as? Bool ?? true
        }

        FlowWatcher.shared.watch(flow: controller.notificationOn()) { [weak self] value in
            self?.notificationOn = value as? Bool ?? false
        }
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
