package com.labs2160.vertx.rxjava.ext.test

import io.vertx.core.http.HttpMethod
import io.vertx.ext.unit.TestContext
import io.vertx.rxjava.core.Vertx
import io.vertx.rxjava.core.buffer.Buffer
import io.vertx.rxjava.core.eventbus.EventBus
import io.vertx.rxjava.core.http.HttpServer
import io.vertx.rxjava.ext.web.Router
import io.vertx.rxjava.ext.web.client.HttpResponse
import io.vertx.rxjava.ext.web.client.WebClient
import io.vertx.rxjava.ext.web.handler.BodyHandler
import rx.Single
import java.net.ServerSocket

abstract class VertxTestBase {
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

        server.requestHandler(router::accept).rxListen(listenPort).subscribe(
                { async.complete() },
                { error -> context.fail(error) }
        )
    }

    open fun tearDown(context: TestContext) {
        client.close()

        vertx.close(context.asyncAssertSuccess())
    }

    fun testRequest(
            method: HttpMethod,
            path: String,
            body: Any? = null,
            headers: Map<String,String>? = null

    ): Single<HttpResponse<Buffer>> {

        val request = when(method) {
            HttpMethod.GET    -> client.get    (listenPort, "localhost", path)
            HttpMethod.HEAD   -> client.head   (listenPort, "localhost", path)
            HttpMethod.POST   -> client.post   (listenPort, "localhost", path)
            HttpMethod.PUT    -> client.put    (listenPort, "localhost", path)
            HttpMethod.DELETE -> client.delete (listenPort, "localhost", path)
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