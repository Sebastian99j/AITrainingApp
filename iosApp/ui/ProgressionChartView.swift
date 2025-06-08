import Charts
import shared
import SwiftUI

struct ProgressionChartView: View {
    let data: [(String, Double)]

    var body: some View {
        Chart {
            ForEach(data.indices, id: \.self) { index in
                let point = data[index]
                LineMark(
                    x: .value("Date", point.0),
                    y: .value("Weight", point.1)
                )
                .foregroundStyle(.cyan)
            }
        }
        .frame(height: 300)
        .padding()
    }
}
