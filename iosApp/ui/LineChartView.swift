import SwiftUI
import Charts

struct LineChartView: View {
    let data: [Pair<String, Double>]

    var body: some View {
        Chart {
            ForEach(data.indices, id: \.self) { index in
                let item = data[index]
                LineMark(
                    x: .value("Data", item.first),
                    y: .value("Ciężar", item.second)
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
