@file:JvmName("RoutingContextExt")

package com.labs2160.vertx.core.ext

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.RequestParameters

/**
 * Extracts the validation results produced by 'Vert.x API Contract' module
 * components from this routing context.
 */
fun RoutingContext.params(): RequestParameters = this.get<Any>("parsedParameters") as RequestParameters

/**
 * Composes an HTTP response with specified [status] code.
 */
fun RoutingContext.respond(status: HttpResponseStatus) {
    this.respond(status = status, body = null, pretty = false)
}

/**
 * Composes an HTTP response with specified [status] code. [body] will be
 * serialized as JSON.
 */
fun RoutingContext.respond(status: HttpResponseStatus, body: Any) {
    this.respond(status = status, body = body, pretty = false)
}

/**
 * Composes an HTTP response with specified [status] code. [body] will be
 * serialized as pretty JSON.
 */
fun RoutingContext.respondPretty(status: HttpResponseStatus, body: Any) {
    this.respond(status, body, true)
}

/**
 * Composes an HTTP response with specified [status] code. If supplied, [body]
 * will be serialized as JSON with conditionally [pretty] formatting.
 */
fun RoutingContext.respond(status: HttpResponseStatus, body: Any? = null, pretty: Boolean = false) {
    val response = this.response()

    response.statusCode = status.code()

    if(body == null) {
        response.end()
    } else {
        response.end(if(pretty) Json.encodePrettily(body) else Json.encode(body))
    }
}