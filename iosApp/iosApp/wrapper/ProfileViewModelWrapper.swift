import Shared
import Combine

struct ProfileEntity: Identifiable, Equatable {
    let id: Int32
    let name: String
    let weightChance: Float
    let repsChance: Int32
    let setsChance: Int32
}

class ProfileViewModelWrapper: ObservableObject {
    private let viewModel: ProfileViewModel

    @Published var profiles: [ProfileEntity] = []
    @Published var username: String = ""
    @Published var aiIdentifier: String = ""
    @Published var selectedProfileId: Int32?

    init(profileQueries: ProfileQueries, userQueries: UserQueries) {
        self.viewModel = ProfileViewModel(profileQueries: profileQueries, userQueries: userQueries)
        observe()
    }

    private func observe() {
        viewModel.profiles.watch { [weak self] list in
            guard let list = list as? [Shared.ProfileEntity] else { return }
            DispatchQueue.main.async {
                self?.profiles = list.map {
                    ProfileEntity(
                        id: Int32($0.id),
                        name: $0.name,
                        weightChance: $0.weightChance,
                        repsChance: Int32($0.repsChance),
                        setsChance: Int32($0.setsChance)
                    )
                }
            }
        }

        viewModel.username.watch { [weak self] value in
            DispatchQueue.main.async {
                self?.username = value ?? ""
            }
        }

        viewModel.aiIdentifier.watch { [weak self] value in
            DispatchQueue.main.async {
                self?.aiIdentifier = value ?? ""
            }
        }

        viewModel.selectedProfileId.watch { [weak self] value in
            DispatchQueue.main.async {
                self?.selectedProfileId = value?.int32Value
            }
        }
    }

    func loadData() {
        viewModel.loadData()
    }

    func setProfile(profileId: Int32) {
        viewModel.setProfileForUser(profileId: Int(profileId))
    }
}
