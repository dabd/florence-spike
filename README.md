# Florence Chart Library (Spike)

## Design Choices

The design of Florence is based on these principles:

1. **Separation of specification and rendering**: Charts are defined as immutable data structures separate from rendering logic.

2. **Type-safe DSL**: Smart constructors and extension methods create chart specifications with compile-time safety.

3. **Platform independence**: Chart definitions function on both JVM and JavaScript through cross-compilation.

4. **Multiple data sources**: Supports point data, mathematical functions, and domain objects with mapping functions (extractors for plotting values).

5. **Extensibility through type Classes**: Renderer implementations can be added without modifying core specifications.

## Architecture

```
┌─────────────┐     ┌──────────────┐     ┌───────────────────┐
│ Chart Model │ ──▶ │ Generic      │ ──▶ │ Platform-Specific │
│ + DSL       │     │ Renderer API │     │ Renderers         │
└─────────────┘     └──────────────┘     └───────────────────┘
```

- **Core**: Contains model definitions and DSL (cross-platform)
- **Rendering API**: Type class-based abstraction for rendering
- **Renderers**: Platform-specific implementations (JS Canvas, JVM, etc.)

## Core Components

### Chart Model

```scala
enum ChartSpec:
  case LineChartSpec[A](
      title: Option[String],
      series: Vector[LineSeries[A]],
      xAxis: AxisSpec,
      yAxis: AxisSpec
  )

enum LineData[A]:
  case Points(data: Vector[(Double, Double)])
  case FunctionPlot(f: Double => Double, start: Double, end: Double, sampleSize: Int)
  case GenericData(data: Vector[A], x: A => Double, y: A => Double)
```

### Chart DSL

The DSL enables chart creation through a fluent API:

```scala
val chart = lineChart("Population Growth",
  pointsSeries("Historical", (1900, 1.6), (1950, 2.5), (2000, 6.1)),
  functionPlotSeries("Projection", t => 6.1 * Math.exp(0.011 * (t - 2000)), 2000, 2100, 50)
)
.withAxis(AxisSpec.LinearScale("Year", Some(1900), Some(2100)))
.withYAxis(AxisSpec.LinearScale("Billions", Some(0), Some(16)))
```

## Rendering Abstraction

The rendering abstraction is defined through a type class:

```scala
trait Renderable[T, C]:
  def render(spec: T, ctx: C): Unit

// Usage
GenericRenderer.render(chart, context)(using rendererInstance)
```

Current implementations:
- JavaScript: Canvas-based renderer
- JVM: Not yet implemented

## Usage Example

```scala
// Create chart with the DSL
val chart = lineChart("Demo Chart",
  pointsSeries("Data", (0, 0), (100, 100), (200, 50)),
  functionPlotSeries("Model", x => 100 * Math.sin(x/100), 0, 300, 50)
)
.withAxis(AxisSpec.LinearScale("x", Some(0), Some(300)))
.withYAxis(AxisSpec.LinearScale("y", Some(-100), Some(100)))

// Render with appropriate (summoned) renderer
GenericRenderer.render(chart, canvasContext)
```


## Demo (MacOS)

```bash
sbt clean rendererJS/fastOptJS && python -m http.server 8000 & sleep 2 && open http://localhost:8000/index.html
```

## Future Directions

- Rendering is currently hard-coded to the rendering engine, but it can be made
more abstract by introducing a `DrawingContext` hierarchy that provides capabilities to draw 
different types of shapes. For instance, a renderer for line charts would utilise different capabilities
than a renderer for bar charts.
- Additional chart types (bar, scatter, area charts, rose diagrams :)
- Interactive elements (tooltips, animations)
- Styling customisation
- Additional renderers (SVG, etc.)
