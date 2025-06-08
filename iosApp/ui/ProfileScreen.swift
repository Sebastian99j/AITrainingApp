import SwiftUI

struct ProfileScreen: View {
    @ObservedObject var viewModel: ProfileViewModelWrapper

    @State private var selectedProfile: ProfileEntity?
    @State private var avatarUrl: String = ""

    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                Text("Profil użytkownika")
                    .font(.title)
                    .bold()

                // Avatar (losowy z DiceBear)
                AsyncImage(url: URL(string: avatarUrl)) { image in
                    image.resizable()
                } placeholder: {
                    ProgressView()
                }
                .frame(width: 100, height: 100)
                .clipShape(Circle())

                Text(viewModel.username)
                    .font(.title2)

                Text(viewModel.aiIdentifier)
                    .foregroundColor(.gray)
                    .font(.subheadline)

                Divider()

                Text("Wybierz profil")
                    .font(.headline)

                Picker("Profil", selection: $selectedProfile) {
                    ForEach(viewModel.profiles, id: \.id) { profile in
                        Text(profile.name).tag(Optional(profile))
                    }
                }
                .pickerStyle(MenuPickerStyle())
                .onChange(of: selectedProfile) { newValue in
                    // optional additional logic
                }

                if let profile = selectedProfile {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Szczegóły profilu:")
                            .font(.headline)
                            .foregroundColor(.accentColor)

                        Text("Zmiana ciężaru: \(profile.weightChance, specifier: "%.1f") kg")
                        Text("Zmiana powtórzeń: \(profile.repsChance)")
                        Text("Zmiana serii: \(profile.setsChance)")
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.top)
                }

                Button("Zapisz") {
                    if let profile = selectedProfile {
                        viewModel.setProfile(profileId: profile.id)
                    }
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.accentColor)
                .foregroundColor(.white)
                .cornerRadius(10)
            }
            .padding()
        }
        .onAppear {
            viewModel.loadData()

            // losowy avatar (dla efektu z Androida)
            let seed = Int.random(in: 1...1000)
            avatarUrl = "https://api.dicebear.com/7.x/personas/png?seed=user\(seed)"
        }
        .onChange(of: viewModel.selectedProfileId) { id in
            selectedProfile = viewModel.profiles.first(where: { $0.id == id })
        }
    }
}
