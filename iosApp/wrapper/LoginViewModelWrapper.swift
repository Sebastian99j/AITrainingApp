import Combine
import shared

class LoginViewModelWrapper: ObservableObject {
    @Published var state: LoginState = LoginState(isLoading: false, success: false, error: nil)

    private let viewModel: LoginViewModel
    private var cancellables = Set<AnyCancellable>()

    init(
        loginUseCase: LoginUseCase,
        registerUseCase: RegisterUseCase,
        fetchAndStoreUser: FetchAndStoreUserUseCase
    ) {
        self.viewModel = LoginViewModel(
            loginUseCase: loginUseCase,
            registerUseCase: registerUseCase,
            fetchUserUseCase: fetchAndStoreUser
        )

        observe()
    }

    private func observe() {
        viewModel.$state
            .receive(on: DispatchQueue.main)
            .sink { [weak self] newState in
                self?.state = newState
            }
            .store(in: &cancellables)
    }

    func login(username: String, password: String) {
        viewModel.onEvent(event: LoginEvent.Login(username: username, password: password))
    }

    func register(username: String, password: String) {
        viewModel.onEvent(event: LoginEvent.Register(username: username, password: password))
    }
}
