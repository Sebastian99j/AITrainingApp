import SwiftUI

struct ProfileScreen: View {
    @ObservedObject var viewModel: ProfileViewModelWrapper
    @State private var selectedProfile: ProfileEntity?
    @State private var avatarUrl: String = ""
    
    var body: some View {
        ProfileContentView(
            viewModel: viewModel,
            selectedProfile: $selectedProfile,
            avatarUrl: avatarUrl,
            onAppear: {
                viewModel.loadData()
                let seed = Int.random(in: 1...1000)
                avatarUrl = "https://api.dicebear.com/7.x/personas/png?seed=user\(seed)"
            },
            onChangeProfile: { newValue in

            },
            onSave: {
                if let profile = selectedProfile {
                    viewModel.setProfile(profileId: profile.id)
                }
            }
        )
    }
}

struct ProfileContentView: View {
    let viewModel: ProfileViewModelWrapper
    @Binding var selectedProfile: ProfileEntity?
    let avatarUrl: String
    let onAppear: () -> Void
    let onChangeProfile: (ProfileEntity?) -> Void
    let onSave: () -> Void
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                headerSection
                avatarSection
                userInfoSection
                Divider()
                profileSelectionSection
                profileDetailsSection
                saveButton
            }
            .padding()
        }
        .onAppear(perform: onAppear)
        .onChange(of: viewModel.selectedProfileId) { id in
            selectedProfile = viewModel.profiles.first(where: { $0.id == id })
        }
    }
    
    private var headerSection: some View {
        Text("Profil użytkownika")
            .font(.title)
            .bold()
    }
    
    private var avatarSection: some View {
        AsyncImage(url: URL(string: avatarUrl)) { image in
            image.resizable()
        } placeholder: {
            ProgressView()
        }
        .frame(width: 100, height: 100)
        .clipShape(Circle())
    }
    
    private var userInfoSection: some View {
        Group {
            Text(viewModel.username)
                .font(.title2)
            
            Text(viewModel.aiIdentifier)
                .foregroundColor(.gray)
                .font(.subheadline)
        }
    }
    
    private var profileSelectionSection: some View {
        VStack {
            Text("Wybierz profil")
                .font(.headline)
            
            Picker("Profil", selection: $selectedProfile) {
                ForEach(viewModel.profiles, id: \.id) { profile in
                    Text(profile.name).tag(Optional(profile))
                }
            }
            .pickerStyle(MenuPickerStyle())
            .onChange(of: selectedProfile, perform: onChangeProfile)
        }
    }
    
    private var profileDetailsSection: some View {
        Group {
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
        }
    }
    
    private var saveButton: some View {
        Button(action: onSave) {
            Text("Zapisz")
                .frame(maxWidth: .infinity)
        }
        .padding()
        .background(Color.accentColor)
        .foregroundColor(.white)
        .cornerRadius(10)
    }
}
