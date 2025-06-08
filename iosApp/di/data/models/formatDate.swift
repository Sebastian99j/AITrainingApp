func formatDate(_ dateString: String?) -> String {
    guard let str = dateString else { return "-" }

    let formatter = DateFormatter()
    formatter.dateFormat = "yyyy-MM-dd"

    if let date = formatter.date(from: str) {
        let output = DateFormatter()
        output.dateStyle = .medium
        return output.string(from: date)
    } else {
        return str
    }
}
