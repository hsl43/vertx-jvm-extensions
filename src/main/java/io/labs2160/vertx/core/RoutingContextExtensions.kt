package io.labs2160.vertx.core

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.json.Json
import io.vertx.ext.web.api.RequestParameters
import io.vertx.ext.web.RoutingContext

fun RoutingContext.params(): RequestParameters = this.get<Any>("parsedParameters") as RequestParameters

fun RoutingContext.respondPretty(status: HttpResponseStatus, body: Any? = null) {
    this.respond(status, body, true)
}

fun RoutingContext.respond(status: HttpResponseStatus, body: Any? = null, pretty: Boolean = false) {
    if(body == null) {
        this.response()
                .setStatusCode(status.code())
                .end()

    } else {
        val json = if(pretty) {
            Json.prettyMapper.writeValueAsString(body)
        } else {
            Json.mapper.writeValueAsString(body)
        }

        this.response()
                .setStatusCode(status.code())
                .end(json)
    }
}