import Combine
import shared

class ProfileViewModel: ObservableObject {
    let controller: ProfileController
    private var cancellables = Set<AnyCancellable>()

    @Published var profiles: [ProfileUiModel] = []
    @Published var username: String = ""
    @Published var aiIdentifier: String = ""
    @Published var selectedProfileId: Int32? = nil

    init(profileQueries: ProfileQueries, userQueries: UserQueries) {
        self.controller = ProfileController(
            profileQueries: profileQueries,
            userQueries: userQueries, scope: <#any Kotlinx_coroutines_coreCoroutineScope#>
        )

        observeFlows()
    }

    private func observeFlows() {
        controller.profiles.watch { [weak self] data in
            self?.profiles = data as? [ProfileUiModel] ?? []
        }.store(in: &cancellables)

        controller.username.watch { [weak self] name in
            self?.username = name ?? ""
        }.store(in: &cancellables)

        controller.aiIdentifier.watch { [weak self] id in
            self?.aiIdentifier = id ?? ""
        }.store(in: &cancellables)

        controller.selectedProfileId.watch { [weak self] id in
            self?.selectedProfileId = id as? Int32
        }.store(in: &cancellables)
    }

    func loadData() {
        controller.loadData()
    }

    func setProfile(profileId: Int32) {
        controller.setProfileForUser(profileId: profileId)
    }
}
