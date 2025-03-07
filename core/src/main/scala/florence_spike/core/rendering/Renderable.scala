package florence_spike.core.rendering

trait Renderable[T, C]:
  def render(spec: T, ctx: C): Unit
