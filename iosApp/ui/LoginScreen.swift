import SwiftUI

struct LoginScreen: View {
    @ObservedObject var viewModel: LoginViewModelWrapper
    var onLoginSuccess: () -> Void

    @State private var username = ""
    @State private var password = ""
    @State private var isLoginMode = true

    var body: some View {
        ZStack {
            Color(.black).ignoresSafeArea()

            VStack(spacing: 20) {
                Text(isLoginMode ? "Log in to AI Training App" : "Register New Account")
                    .font(.title)
                    .foregroundColor(.white)

                TextField("Username", text: $username)
                    .textFieldStyle(.roundedBorder)
                    .autocapitalization(.none)

                SecureField("Password", text: $password)
                    .textFieldStyle(.roundedBorder)

                Button(action: {
                    if isLoginMode {
                        viewModel.login(username: username, password: password)
                    } else {
                        viewModel.register(username: username, password: password)
                    }
                }) {
                    Text(isLoginMode ? "LOGIN" : "REGISTER")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.green)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }

                Button(action: {
                    isLoginMode.toggle()
                }) {
                    Text(isLoginMode ? "No account? Register" : "Already registered? Login")
                        .foregroundColor(.gray)
                        .font(.subheadline)
                }

                if viewModel.state.isLoading {
                    ProgressView().progressViewStyle(CircularProgressViewStyle(tint: .cyan))
                }

                if let error = viewModel.state.error {
                    Text("Błąd logowania: \(error)")
                        .foregroundColor(.red)
                        .font(.subheadline)
                }
            }
            .padding(24)
        }
        .onChange(of: viewModel.state.success) { success in
            if success {
                onLoginSuccess()
            }
        }
    }
}
