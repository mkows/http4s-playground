package io.github.mkows.sample

import cats.data.{EitherT, Kleisli, OptionT}
import cats.effect.{ConcurrentEffect, ContextShift, Effect, ExitCode, IO, IOApp, Sync, Timer}
import cats.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import fs2.Stream
import org.http4s.{HttpRoutes, Response, Status}

import scala.concurrent.ExecutionContext.global

object SampleServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {

    def middle1(routes: HttpRoutes[F]): HttpRoutes[F] = {
      println("Called middle1...")
      Kleisli { req =>
        println(s"Inside middle1 Kleisli... (${java.util.UUID.randomUUID()})")
        routes(req).map { resp =>
          println("After middle1...")
          resp
        }
      }
    }

    def middle2(routes: HttpRoutes[F]): HttpRoutes[F] = {
      println("Called middle2...")
      Kleisli { req =>
        println(s"Inside middle2 Kleisli... (${java.util.UUID.randomUUID()})")
        routes(req).map { resp =>
          println("After middle2...")
          resp
        }
      }
    }

    for {
      client <- BlazeClientBuilder[F](global).stream
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)


      //    # curl -i localhost:8080/hello/boom
      //    Inside middle2 Kleisli... (72d1e314-c67b-4b66-a79a-29d3dbe4d2d4)
      //    Inside middle1 Kleisli... (04ec1e7c-cefb-4af3-9225-ee50931f4c23)
      //    Running /hello/boom...
      //    After middle1...
      //    [scala-execution-context-global-129] INFO  org.http4s.server.middleware.Logger - HTTP/1.1 GET /hello/boom Headers(Host: localhost:8080, User-Agent: curl/7.54.0, Accept: */*) body=""
      //    [scala-execution-context-global-129] INFO  org.http4s.server.middleware.Logger - HTTP/1.1 200 OK Headers(Content-Type: application/json, Content-Length: 25) body="{"message":"Hello, boom"}"
      //
      //    # curl -i localhost:8080/joke
      //    Inside middle2 Kleisli... (bc9dd041-24b0-4eb4-a4eb-b1707aa365d5)
      //    Inside middle1 Kleisli... (f1d756ac-f1c0-4a31-8ed3-800b308f4067)
      //    Running /joke...
      //    After middle2...

      httpApp = (
        Logger.httpRoutes(true, true)(
          middle1(
            SampleRoutes.helloWorldRoutes[F](helloWorldAlg)
          )
        ) <+>
          middle2(
            SampleRoutes.jokeRoutes[F](jokeAlg)
          )
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = httpApp
//      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}