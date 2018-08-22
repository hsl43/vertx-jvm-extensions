package io.labs2160.vertx.core

import io.netty.handler.codec.http.HttpResponseStatus.OK
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler
import io.vertx.ext.web.api.validation.ParameterType
import io.vertx.ext.web.client.HttpResponse
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner::class)
class RoutingContextExtensionsTest : VertxTestBase() {
    private data class FakeBody(val name: String, val value: Int)

    @Before override fun setUp(context: TestContext) {
        super.setUp(context)
    }

    @After override fun tearDown(context: TestContext) {
        super.tearDown(context)
    }

    @Test fun params_captures_request_parameters(context: TestContext) {
        val async = context.async()

        val route = router.get("/:path")
                .handler(HTTPRequestValidationHandler.create()
                        .addHeaderParam("_header", ParameterType.GENERIC_STRING, false)
                        .addQueryParam("_query", ParameterType.GENERIC_STRING, false)
                        .addPathParam("path", ParameterType.GENERIC_STRING)
                )
                .handler { routingContext ->
                    val params = routingContext.params()

                    context.assertEquals("HEADER_VALUE", params.headerParameter("_header").string)
                    context.assertEquals("QUERY_VALUE", params.queryParameter("_query").string)
                    context.assertEquals("PATH_VALUE", params.pathParameter("path").string)

                    routingContext.respond(OK)
                }

        val headers = mapOf("_header" to "HEADER_VALUE")

        val handler = Handler<AsyncResult<HttpResponse<Buffer>>> { result ->
            route.remove()

            if(result.succeeded()) {
                async.complete()
            } else {
                context.fail(result.cause())
            }
        }

        testRequest(HttpMethod.GET, path = "/PATH_VALUE?_query=QUERY_VALUE", body = null, headers = headers, handler = handler)
    }

    @Test fun respond_has_status_code(context: TestContext) {
        val async = context.async()

        val route = router.get("/").handler { it.respond(OK) }

        val handler = Handler<AsyncResult<HttpResponse<Buffer>>> { result ->
            route.remove()

            if(result.succeeded()) {
                val response = result.result()

                context.assertEquals(OK.code(), response.statusCode())

                async.complete()

            } else {
                context.fail(result.cause())
            }
        }

        testRequest(HttpMethod.GET, path = "/", body = null, headers = null, handler = handler)
    }

    @Test fun respond_has_uglified_body(context: TestContext) {
        val async = context.async()

        val body = FakeBody(name = "dummy-name-value", value = Int.MIN_VALUE)

        val route = router.get("/").handler { it.respond(status = OK, body = body, pretty = false) }

        val handler = Handler<AsyncResult<HttpResponse<Buffer>>> { result ->
            route.remove()

            if(result.succeeded()) {
                val expected = "{\"name\":\"dummy-name-value\",\"value\":-2147483648}"

                val response = result.result()

                context.assertEquals(expected, response.bodyAsString())

                async.complete()

            } else {
                context.fail(result.cause())
            }
        }

        testRequest(HttpMethod.GET, path = "/", body = null, headers = null, handler = handler)
    }

    @Test fun respond_has_prettified_body(context: TestContext) {
        val async = context.async()

        val body = FakeBody(name = "dummy-name-value", value = Int.MIN_VALUE)

        val route = router.get("/").handler { it.respond(status = OK, body = body, pretty = true) }

        val handler = Handler<AsyncResult<HttpResponse<Buffer>>> { result ->
            route.remove()

            if(result.succeeded()) {
                val expected = """
                    {
                      "name" : "dummy-name-value",
                      "value" : -2147483648
                    }
                """.trimIndent()

                val response = result.result()

                context.assertEquals(expected, response.bodyAsString())

                async.complete()

            } else {
                context.fail(result.cause())
            }
        }

        testRequest(HttpMethod.GET, path = "/", body = null, headers = null, handler = handler)
    }

    @Test fun respondPretty_has_pretty_body(context: TestContext) {
        val async = context.async()

        val body = FakeBody(name = "dummy-name-value", value = Int.MIN_VALUE)

        val route = router.get("/").handler { it.respondPretty(status = OK, body = body) }

        val handler = Handler<AsyncResult<HttpResponse<Buffer>>> { result ->
            route.remove()

            if(result.succeeded()) {
                val expected = """
                    {
                      "name" : "dummy-name-value",
                      "value" : -2147483648
                    }
                """.trimIndent()

                val response = result.result()

                context.assertEquals(expected, response.bodyAsString())

                async.complete()

            } else {
                context.fail(result.cause())
            }
        }

        testRequest(HttpMethod.GET, path = "/", body = null, headers = null, handler = handler)
    }
}