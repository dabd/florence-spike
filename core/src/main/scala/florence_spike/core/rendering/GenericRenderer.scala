package florence_spike.core.rendering

object GenericRenderer:
  def render[T, C](spec: T, ctx: C)(using r: Renderable[T, C]): Unit =
    r.render(spec, ctx)
