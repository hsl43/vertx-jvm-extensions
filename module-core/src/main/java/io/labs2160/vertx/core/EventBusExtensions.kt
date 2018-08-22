@file:JvmName("EventBusExt")

package io.labs2160.vertx.core

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject

fun EventBus.publishJsonObject(message: Any, options: DeliveryOptions? = null): EventBus {
    val address = message::class.java.name

    val json = JsonObject.mapFrom(message)

    return if(options == null) {
        this.publish(address, json)
    } else {
        this.publish(address, json, options)
    }
}

fun <T> EventBus.sendJsonObject(message: Any, options: DeliveryOptions? = null): EventBus {
    val handler = Handler<AsyncResult<Message<Any>>> { /* do nothing... */ }

    return this.sendJsonObject(message, options, handler)
}

fun <T> EventBus.sendJsonObject(
        message: Any,
        options: DeliveryOptions? = null,
        handler: Handler<AsyncResult<Message<T>>>

): EventBus {

    val address = message::class.java.name

    val json = JsonObject.mapFrom(message)

    return if(options == null) {
        this.send(address, json, handler)
    } else {
        this.send(address, json, options, handler)
    }
}