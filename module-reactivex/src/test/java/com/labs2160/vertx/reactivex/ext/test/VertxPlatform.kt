package com.labs2160.vertx.reactivex.ext.test

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.ext.unit.TestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.core.eventbus.EventBus
import io.vertx.reactivex.core.http.HttpServer
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.reactivex.ext.web.handler.BodyHandler
import java.net.ServerSocket

abstract class VertxPlatform {
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
    listOf(Json.mapper, Json.prettyMapper).forEach { it.registerKotlinModule() }

    vertx = Vertx.vertx().exceptionHandler(context.exceptionHandler())

    router = Router.router(vertx).apply { route().handler(BodyHandler.create()) }

    eventBus = vertx.eventBus()

    server = vertx.createHttpServer()

    client = WebClient.create(vertx)

    val async = context.async()

    server.requestHandler(router::accept).rxListen(listenPort).subscribeBy(
        onSuccess = {
          async.complete()
        },
        onError = { error ->
          context.fail(error)
        }
    )
  }

  open fun tearDown(context: TestContext) {
    client.close()

    vertx.close(context.asyncAssertSuccess())
  }

  fun testRequest(
      method: HttpMethod,
      path: String,
      params: Map<String,Any>? = null,
      body: Any? = null,
      headers: Map<String, String>? = null

  ): Single<HttpResponse<Buffer>> {

    var pathWithBindValues = path

    if(params?.isNotEmpty() == true) {
      params.forEach { key, value -> pathWithBindValues = key.toRegex().replace(pathWithBindValues, "/$value") }
    }

    val request = when (method) {
      HttpMethod.DELETE -> client.delete(listenPort, "localhost", pathWithBindValues)
         HttpMethod.GET -> client.get   (listenPort, "localhost", pathWithBindValues)
        HttpMethod.HEAD -> client.head  (listenPort, "localhost", pathWithBindValues)
      HttpMethod. PATCH -> client.patch (listenPort, "localhost", pathWithBindValues)
        HttpMethod.POST -> client.post  (listenPort, "localhost", pathWithBindValues)
         HttpMethod.PUT -> client.put   (listenPort, "localhost", pathWithBindValues)
                   else -> throw IllegalArgumentException("Unexpected value for HttpMethod [$method]")
    }

    headers?.let { it.forEach { header -> request.putHeader(header.key, header.value) } }

    return if(body == null) {
      request.rxSend()
    } else {
      request.rxSendJson(body)
    }
  }
}