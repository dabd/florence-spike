package florence_spike.rendererjs

import florence_spike.core.*
import florence_spike.core.model.ChartSpec.LineChartSpec
import florence_spike.core.model.{AxisSpec, LineData}
import florence_spike.core.rendering.Renderable
import org.scalajs.dom.CanvasRenderingContext2D

object RenderableInstances:

  given Renderable[LineChartSpec[?], CanvasRenderingContext2D] with

    def render(spec: LineChartSpec[?], ctx: CanvasRenderingContext2D): Unit =
      ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height)

      val margin     = 50
      val plotWidth  = ctx.canvas.width - 2 * margin
      val plotHeight = ctx.canvas.height - 2 * margin

      val xMin = spec.xAxis match
        case axis: AxisSpec.LinearScale => axis.min.getOrElse(0.0)
        case _                          => 0.0

      val xMax = spec.xAxis match
        case axis: AxisSpec.LinearScale => axis.max.getOrElse(600.0)
        case _                          => 600.0

      val yMin = spec.yAxis match
        case axis: AxisSpec.LinearScale => axis.min.getOrElse(0.0)
        case _                          => 0.0

      val yMax = spec.yAxis match
        case axis: AxisSpec.LinearScale => axis.max.getOrElse(400.0)
        case _                          => 400.0

      def transformX(x: Double): Double =
        margin + (x - xMin) * plotWidth / (xMax - xMin)

      def transformY(y: Double): Double =
        ctx.canvas.height - margin - (y - yMin) * plotHeight / (yMax - yMin)

      ctx.strokeStyle = "#e0e0e0"
      ctx.lineWidth = 0.5

      for i <- 0 to 10 do
        val x = margin + i * plotWidth / 10
        ctx.beginPath()
        ctx.moveTo(x, margin)
        ctx.lineTo(x, ctx.canvas.height - margin)
        ctx.stroke()

      for i <- 0 to 10 do
        val y = margin + i * plotHeight / 10
        ctx.beginPath()
        ctx.moveTo(margin, y)
        ctx.lineTo(ctx.canvas.width - margin, y)
        ctx.stroke()

      spec.title.foreach { t =>
        ctx.font = "bold 16px sans-serif"
        ctx.fillStyle = "black"
        ctx.fillText(t, margin, 20)
      }

      ctx.strokeStyle = "#000000"
      ctx.lineWidth = 2

      ctx.beginPath()
      ctx.moveTo(margin, ctx.canvas.height - margin)
      ctx.lineTo(ctx.canvas.width - margin, ctx.canvas.height - margin)
      ctx.stroke()

      ctx.beginPath()
      ctx.moveTo(margin, margin)
      ctx.lineTo(margin, ctx.canvas.height - margin)
      ctx.stroke()

      ctx.font = "12px sans-serif"
      ctx.fillStyle = "black"
      ctx.textAlign = "center"

      for i <- 0 to 6 do
        val value = xMin + i * (xMax - xMin) / 6
        val x     = transformX(value)

        ctx.beginPath()
        ctx.moveTo(x, ctx.canvas.height - margin)
        ctx.lineTo(x, ctx.canvas.height - margin + 5)
        ctx.stroke()

        ctx.fillText(f"$value%.0f", x, ctx.canvas.height - margin + 20)

      ctx.textAlign = "right"

      for i <- 0 to 5 do
        val value = yMin + i * (yMax - yMin) / 5
        val y     = transformY(value)

        ctx.beginPath()
        ctx.moveTo(margin - 5, y)
        ctx.lineTo(margin, y)
        ctx.stroke()

        ctx.fillText(f"$value%.0f", margin - 10, y + 4)

      ctx.textAlign = "center"
      val xLabel = spec.xAxis match
        case axis: AxisSpec.LinearScale   => axis.label
        case axis: AxisSpec.CategoryScale => axis.label
      ctx.fillText(xLabel, ctx.canvas.width / 2, ctx.canvas.height - 10)

      ctx.save()
      ctx.translate(10, ctx.canvas.height / 2)
      ctx.rotate(-Math.PI / 2)
      val yLabel = spec.yAxis match
        case axis: AxisSpec.LinearScale   => axis.label
        case axis: AxisSpec.CategoryScale => axis.label
      ctx.fillText(yLabel, 0, 0)
      ctx.restore()

      val colours     = List("red", "blue", "green", "orange", "purple")
      var colourIndex = 0

      spec.series.foreach { series =>
        val points: Vector[(Double, Double)] = series.lineData match
          case LineData.Points(data) =>
            data
          case LineData.FunctionPlot(f, start, end, sampleSize) =>
            val step = (end - start) / (sampleSize - 1)
            (0 until sampleSize).toVector.map { i =>
              val x = start + i * step
              (x, f(x))
            }
          case LineData.GenericData(data, xFn, yFn) =>
            data.map(el => (xFn(el), yFn(el)))

        ctx.strokeStyle = colours(colourIndex % colours.size)
        ctx.lineWidth = 2
        colourIndex += 1

        if points.nonEmpty then
          ctx.beginPath()

          val (x0, y0) = points.head
          val canvasX0 = transformX(x0)
          val canvasY0 = transformY(y0)
          ctx.moveTo(canvasX0, canvasY0)

          points.tail.foreach { case (x, y) =>
            val canvasX = transformX(x)
            val canvasY = transformY(y)
            ctx.lineTo(canvasX, canvasY)
          }

          ctx.stroke()
      }

      ctx.textAlign = "left"
      ctx.font = "12px sans-serif"

      colourIndex = 0
      spec.series.foreach { series =>
        ctx.fillStyle = colours(colourIndex % colours.size)

        ctx.fillRect(ctx.canvas.width - margin - 100, margin + 20 * colourIndex - 10, 10, 10)

        ctx.fillText(series.label, ctx.canvas.width - margin - 85, margin + 20 * colourIndex)

        colourIndex += 1
      }
    end render
  end given
end RenderableInstances
