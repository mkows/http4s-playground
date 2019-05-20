package io.github.mkows.sample

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]) =
    SampleServer.stream[IO].compile.drain.as(ExitCode.Success)
}