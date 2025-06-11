import Combine
import shared

class LoginViewModel: ObservableObject {
    let kmpViewModel: shared.LoginViewModel
    @Published var state: LoginState = LoginState(isLoading: false, success: false, error: nil)
    private var job: Kotlinx_coroutines_coreJob?

    init(
        loginUseCase: LoginUseCase,
        registerUseCase: RegisterUseCase,
        fetchUserUseCase: FetchAndStoreUserUseCase
    ) {
        self.kmpViewModel = shared.LoginViewModel(
            loginUseCase: loginUseCase,
            registerUseCase: registerUseCase,
            coroutineScope: IOSScope.shared.scope,
            onUserRegistered: { user in
                Task {
                    _ = try? await fetchUserUseCase.invoke(username: user.username)
                }
            }
        )

        job = kmpViewModel.observeState { [weak self] value in
            let s = value // value już jest typu LoginState
            DispatchQueue.main.async {
                self?.state = s
            }
        }
    }

    func onEvent(event: LoginEvent) {
        kmpViewModel.onEvent(event: event)
    }

    deinit {
        job?.cancel(cause: nil)
    }
}
