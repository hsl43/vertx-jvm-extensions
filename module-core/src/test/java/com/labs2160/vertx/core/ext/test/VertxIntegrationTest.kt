package com.labs2160.vertx.core.ext.test

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.EventBus
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.ext.unit.TestContext
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.handler.BodyHandler
import java.net.ServerSocket

abstract class VertxIntegrationTest {
  protected lateinit var vertx: Vertx
  protected lateinit var router: Router
  protected lateinit var eventBus: EventBus
  protected lateinit var server: HttpServer
  protected lateinit var client: WebClient

  protected val listenPort by lazy {
    val socket = ServerSocket(0)

    val port = socket.localPort

    socket.close()

    port
  }

  open fun setUp(context: TestContext) {
    vertx = Vertx.vertx().exceptionHandler(context.exceptionHandler())

    router = Router.router(vertx).apply { route().handler(BodyHandler.create()) }

    eventBus = vertx.eventBus()

    server = vertx.createHttpServer()

    client = WebClient.create(vertx)

    val async = context.async()

    server.requestHandler(router::accept).listen(listenPort) {
      if(it.succeeded()) {
        async.complete()
      } else {
        context.fail(it.cause())
      }
    }
  }

  open fun tearDown(context: TestContext) {
    client.close()

    vertx.close(context.asyncAssertSuccess())
  }

  fun testRequest(
      method: HttpMethod,
      path: String,
      body: Any? = null,
      headers: Map<String, String>? = null,
      handler: Handler<AsyncResult<HttpResponse<Buffer>>>
  ) {

    val request = when (method) {
         HttpMethod.GET -> client.get   (listenPort, "localhost", path)
        HttpMethod.HEAD -> client.head  (listenPort, "localhost", path)
        HttpMethod.POST -> client.post  (listenPort, "localhost", path)
         HttpMethod.PUT -> client.put   (listenPort, "localhost", path)
      HttpMethod.DELETE -> client.delete(listenPort, "localhost", path)
                   else -> throw IllegalArgumentException("Unexpected value for HttpMethod [$method]")
    }

    headers?.let { it.forEach { header -> request.putHeader(header.key, header.value) } }

    return if(body == null) {
      request.send(handler)
    } else {
      request.sendJson(body, handler)
    }
  }
}