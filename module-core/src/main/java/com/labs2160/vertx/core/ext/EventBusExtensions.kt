@file:JvmName("EventBusExt")

package com.labs2160.vertx.core.ext

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject

/**
 * Publishes an arbitrary [message] as a JsonObject using the fully-qualified
 * class name of the [message] instance as the destination address.
 */
fun EventBus.publishJsonObject(message: Any): EventBus = this.publishJsonObject(message, null)

/**
 * Publishes an arbitrary [message] as a JsonObject using the fully-qualified
 * class name of the [message] instance as the destination address. [options]
 * can be supplied to configure the delivery.
 */
fun EventBus.publishJsonObject(message: Any, options: DeliveryOptions? = null): EventBus {
    val address = message::class.java.name

    val json = JsonObject.mapFrom(message)

    return if(options == null) {
        this.publish(address, json)
    } else {
        this.publish(address, json, options)
    }
}

/**
 * Sends an arbitrary [message] as a JsonObject using the fully-qualified class
 * name of the [message] instance as the destination address.
 */
fun <T> EventBus.sendJsonObject(message: Any): EventBus = this.sendJsonObject<Any>(message, null)

/**
 * Sends an arbitrary [message] as a JsonObject using the fully-qualified class
 * name of the [message] instance as the destination address. [options] can be
 * supplied to configure the delivery.
 */
fun <T> EventBus.sendJsonObject(message: Any, options: DeliveryOptions? = null): EventBus {
    val handler = Handler<AsyncResult<Message<Any>>> { /* do nothing... */ }

    return this.sendJsonObject(message, options, handler)
}

/**
 * Sends an arbitrary [message] as a JsonObject using the fully-qualified class
 * name of the [message] instance as the destination address. [options] can be
 * supplied to configure the delivery. A [handler] is supplied to handle any
 * forthcoming response to [message].
 */
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