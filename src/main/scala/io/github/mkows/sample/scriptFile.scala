
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