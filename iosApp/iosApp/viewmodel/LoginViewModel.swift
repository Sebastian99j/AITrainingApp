import Shared
import Combine

class LoginViewModel: ObservableObject {
    let loginViewModel: SharedLoginViewModel
    @Published var state: LoginState = LoginState.companion.Empty

    init(
        loginUseCase: LoginUseCase,
        registerUseCase: RegisterUseCase,
        fetchUserUseCase: FetchAndStoreUserUseCase
    ) {
        loginViewModel = SharedLoginViewModel(
            loginUseCase: loginUseCase,
            registerUseCase: registerUseCase,
            coroutineScope: MainScope(),
            onUserRegistered: { user in
                Task {
                    _ = try? await fetchUserUseCase.execute(username: user.username)
                }
            }
        )

        loginViewModel.state.watch { [weak self] state in
            if let s = state as? LoginState {
                DispatchQueue.main.async {
                    self?.state = s
                }
            }
        }
    }

    func onEvent(event: LoginEvent) {
        loginViewModel.onEvent(event: event)
    }
}
