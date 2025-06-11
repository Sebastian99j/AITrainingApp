import Combine
import shared

class ProfileViewModel: ObservableObject {
    let controller: ProfileController
    private var cancellables = Set<AnyCancellable>()

    @Published var profiles: [ProfileUiModel] = []
    @Published var username: String = ""
    @Published var aiIdentifier: String = ""
    @Published var selectedProfileId: Int32?

    init(profileQueries: ProfileQueries, userQueries: UserQueries) {
        self.controller = ProfileController(
            profileQueries: profileQueries,
            userQueries: userQueries,
            scope: IOSScope.shared.scope
        )

        // ✅ Watch profiles (as function)
        FlowWatcher.shared.watch(flow: controller.profiles()) { [weak self] value in
            self?.profiles = value as? [ProfileUiModel] ?? []
        }

        // ✅ Watch username
        FlowWatcher.shared.watch(flow: controller.username()) { [weak self] value in
            self?.username = value as? String ?? ""
        }

        // ✅ Watch aiIdentifier
        FlowWatcher.shared.watch(flow: controller.aiIdentifier()) { [weak self] value in
            self?.aiIdentifier = value as? String ?? ""
        }

        // ✅ Watch selectedProfileId
        FlowWatcher.shared.watch(flow: controller.selectedProfileId()) { [weak self] value in
            if let intVal = value as? KotlinInt {
                self?.selectedProfileId = intVal.int32Value
            } else if let intVal = value as? Int32 {
                self?.selectedProfileId = intVal
            } else if let intVal = value as? Int {
                self?.selectedProfileId = Int32(intVal)
            } else {
                self?.selectedProfileId = nil
            }
        }
    }

    func loadData() {
        controller.loadData()
    }

    func setProfile(profileId: Int32) {
        // ✅ Kotlin Multiplatform expects KotlinInt not Swift Int
        controller.setProfileForUser(profileId: Int32(KotlinInt(int: profileId)))
    }
}
