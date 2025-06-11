import Combine
import shared

struct ProfileEntity: Identifiable, Equatable, Hashable {
    let id: Int32
    let name: String
    let weightChance: Float
    let repsChance: Int32
    let setsChance: Int32
}

class ProfileViewModelWrapper: ObservableObject {
    private let viewModel: ProfileViewModel
    private var cancellables = Set<AnyCancellable>()

    @Published var profiles: [ProfileEntity] = []
    @Published var username: String = ""
    @Published var aiIdentifier: String = ""
    @Published var selectedProfileId: Int32?

    init(profileQueries: ProfileQueries, userQueries: UserQueries) {
        self.viewModel = ProfileViewModel(
            profileQueries: profileQueries,
            userQueries: userQueries
        )
        observe()
    }

    private func observe() {
        viewModel.$profiles
            .receive(on: DispatchQueue.main)
            .sink { [weak self] list in
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
            .store(in: &cancellables)

        viewModel.$username
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in self?.username = $0 }
            .store(in: &cancellables)

        viewModel.$aiIdentifier
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in self?.aiIdentifier = $0 }
            .store(in: &cancellables)

        viewModel.$selectedProfileId
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in self?.selectedProfileId = $0 }
            .store(in: &cancellables)
    }

    func loadData() {
        viewModel.loadData()
    }

    func setProfile(profileId: Int32) {
        viewModel.setProfile(profileId: profileId)
    }
}
