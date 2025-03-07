package florence_spike.core.model

enum ChartSpec:

  case LineChartSpec[A](
      title: Option[String],
      series: Vector[LineSeries[A]],
      xAxis: AxisSpec,
      yAxis: AxisSpec
  )

final case class LineSeries[A](
    label: String,
    lineData: LineData[A]
)

enum LineData[A]:
  case Points(data: Vector[(Double, Double)])

  case FunctionPlot(
      f: Double => Double,
      start: Double,
      end: Double,
      sampleSize: Int
  )

  case GenericData(
      data: Vector[A],
      x: A => Double,
      y: A => Double
  )

enum AxisSpec:
  case LinearScale(label: String, min: Option[Double], max: Option[Double])
  case CategoryScale(label: String)
