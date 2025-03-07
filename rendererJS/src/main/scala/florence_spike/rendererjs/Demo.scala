package florence_spike.rendererjs

import florence_spike.core.dsl.LineChartDsl.*
import florence_spike.core.model.AxisSpec
import florence_spike.core.rendering.GenericRenderer
import org.scalajs.dom
import org.scalajs.dom.{document, html}

object Demo:

  def main(args: Array[String]): Unit =
    dom.document.addEventListener(
      "DOMContentLoaded",
      { (_: dom.Event) =>
        setupDemo()
      }
    )

  def setupDemo(): Unit =
    val canvas = document.getElementById("chart-canvas").asInstanceOf[html.Canvas]

    canvas.width = 800
    canvas.height = 400

    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    val chart = lineChart(
      "Demo Line Chart",
      pointsSeries(
        "Data Points",
        (50, 50),
        (100, 120),
        (150, 80),
        (200, 150),
        (250, 100),
        (300, 180)
      ),
      functionPlotSeries("Sin Wave", x => 200 + 100 * Math.sin(x / 30), 0, 500, 100),
      functionPlotSeries("Quadratic", x => 300 - 0.003 * (x - 250) * (x - 250), 0, 500, 100)
    )

    val chartWithAxes = chart
      .withXAxis(AxisSpec.LinearScale("x", Some(0), Some(500)))
      .withYAxis(AxisSpec.LinearScale("y", Some(0), Some(400)))

    GenericRenderer.render(chartWithAxes, ctx)(using
      RenderableInstances.given_Renderable_LineChartSpec_CanvasRenderingContext2D
    )
  end setupDemo
end Demo
