package io.labs2160.vertx.core

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.eventbus.EventBus
import io.vertx.rxjava.core.eventbus.Message

fun EventBus.publishJsonObject(message: Any, options: DeliveryOptions? = null): EventBus {
    return if(options == null) {
        this.publish(message::class.java.name, JsonObject.mapFrom(message))
    } else {
        this.publish(message::class.java.name, JsonObject.mapFrom(message), options)
    }
}

fun <T> EventBus.sendJsonObject(
        message: Any,
        options: DeliveryOptions? = null,
        handler: Handler<AsyncResult<Message<T>>>? = null

): EventBus {

    return if(handler == null) {
        if(options == null) {
            this.send(message::class.java.name, JsonObject.mapFrom(message))
        } else {
            this.send(message::class.java.name, JsonObject.mapFrom(message), options)
        }

    } else {
        if(options == null) {
            this.send(message::class.java.name, JsonObject.mapFrom(message), handler)
        } else {
            this.send(message::class.java.name, JsonObject.mapFrom(message), options, handler)
        }
    }
}