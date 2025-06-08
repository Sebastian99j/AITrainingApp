import SwiftUI

struct DashboardItem: Identifiable {
    let id = UUID()
    let label: String
    let systemIcon: String
    let action: () -> Void
}

struct DashboardScreen: View {
    let dashboardItems: [DashboardItem]

    var body: some View {
        ZStack {
            Image("gym_background")
                .resizable()
                .scaledToFill()
                .ignoresSafeArea()

            ScrollView {
                VStack(spacing: 16) {
                    Spacer().frame(height: 40)

                    ForEach(dashboardItems) { item in
                        Button(action: item.action) {
                            HStack {
                                Image(systemName: item.systemIcon)
                                    .foregroundColor(.white)
                                    .font(.system(size: 20))
                                    .frame(width: 28)

                                Text(item.label)
                                    .foregroundColor(.white)
                                    .font(.system(size: 18, weight: .medium))
                                    .padding(.leading, 8)

                                Spacer()
                            }
                            .padding(.horizontal, 16)
                            .frame(height: 60)
                            .background(Color.black.opacity(0.7))
                            .cornerRadius(16)
                            .shadow(radius: 4)
                        }
                        .buttonStyle(PlainButtonStyle())
                    }

                    Spacer()
                }
                .padding(24)
            }
        }
    }
}
