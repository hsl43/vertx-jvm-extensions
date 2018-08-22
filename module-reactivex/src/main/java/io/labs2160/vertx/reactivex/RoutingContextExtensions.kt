@file:JvmName("RoutingContextExt")

package io.labs2160.vertx.reactivex

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.json.Json
import io.vertx.ext.web.api.RequestParameters
import io.vertx.reactivex.ext.web.RoutingContext

fun RoutingContext.params(): RequestParameters = this.get<Any>("parsedParameters") as RequestParameters

fun RoutingContext.respondPretty(status: HttpResponseStatus, body: Any) {
    this.respond(status, body, true)
}

fun RoutingContext.respond(status: HttpResponseStatus, body: Any? = null, pretty: Boolean = false) {
    val response = this.response()

    response.statusCode = status.code()

    if(body == null) {
        response.end()
    } else {
        response.end(if(pretty) Json.encodePrettily(body) else Json.encode(body))
    }
}