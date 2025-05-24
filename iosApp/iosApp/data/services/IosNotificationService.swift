import Foundation
import UserNotifications

class IosNotificationService {

    func scheduleDailyReminder() {
        let center = UNUserNotificationCenter.current()

        // Prośba o uprawnienia
        center.requestAuthorization(options: [.alert, .sound]) { granted, error in
            print("Permission granted: \(granted)")
        }

        // Treść powiadomienia
        let content = UNMutableNotificationContent()
        content.title = "Przypomnienie"
        content.body = "Czas zarejestrować trening!"

        // Codzienna godzina 12:00
        var dateComponents = DateComponents()
        dateComponents.hour = 12
        dateComponents.minute = 0

        let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)

        let request = UNNotificationRequest(
            identifier: "daily_training_reminder",
            content: content,
            trigger: trigger
        )

        center.add(request) { error in
            if let error = error {
                print("Notification error: \(error.localizedDescription)")
            }
        }
    }

    func cancelReminder() {
        let center = UNUserNotificationCenter.current()
        center.removePendingNotificationRequests(withIdentifiers: ["daily_training_reminder"])
    }
}
