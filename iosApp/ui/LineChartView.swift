import SwiftUI
import Charts
import shared

struct RegressionPoint: Hashable {
    let date: String
    let value: Float
}

struct LineChartView: View {
    let data: [RegressionPoint]

    var body: some View {
        Chart {
            ForEach(data, id: \.self) { item in
                LineMark(
                    x: .value("Data", item.date),
                    y: .value("Ciężar", item.value)
                )
                .foregroundStyle(Color.cyan)
                .symbol(Circle())
            }
        }
        .chartXAxis {
            AxisMarks(values: .automatic) { _ in
                AxisGridLine()
                AxisTick()
                AxisValueLabel()
            }
        }
        .chartYAxis {
            AxisMarks()
        }
        .chartPlotStyle { plot in
            plot
                .background(Color.black.opacity(0.05))
                .border(Color.gray.opacity(0.4), width: 0.5)
        }
    }
}
