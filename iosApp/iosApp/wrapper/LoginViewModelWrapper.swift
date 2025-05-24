import Shared
import Combine

class LoginViewModelWrapper: ObservableObject {
    @Published var state: LoginState = LoginState(isLoading: false, success: false, error: nil)

    private let viewModel: LoginViewModel

    init(
        loginUseCase: LoginUseCase,
        registerUseCase: RegisterUseCase,
        fetchAndStoreUser: FetchAndStoreUserUseCase
    ) {
        self.viewModel = LoginViewModel(
            loginUseCase: loginUseCase,
            registerUseCase: registerUseCase,
            coroutineScope: nil,
            onUserRegistered: { user in
                Task {
                    _ = try? await fetchAndStoreUser.invoke(username: user.username)
                }
            }
        )

        observe()
    }

    private func observe() {
        viewModel.state.watch { [weak self] newState in
            if let newState = newState as? LoginState {
                DispatchQueue.main.async {
                    self?.state = newState
                }
            }
        }
    }

    func login(username: String, password: String) {
        viewModel.onEvent(event: LoginEvent.Login(username: username, password: password))
    }

    func register(username: String, password: String) {
        viewModel.onEvent(event: LoginEvent.Register(username: username, password: password))
    }
}
