@file:JvmName("EventBusExt")

package com.labs2160.vertx.rxjava.ext

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.eventbus.EventBus
import io.vertx.rxjava.core.eventbus.Message
import rx.Single

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
fun EventBus.rxSendJsonObject(message: Any): Single<Message<JsonObject>> = this.rxSendJsonObject(message, null)

/**
 * Sends an arbitrary [message] as a JsonObject using the fully-qualified class
 * name of the [message] instance as the destination address. [options] can be
 * supplied to configure the delivery.
 */
fun EventBus.rxSendJsonObject(message: Any, options: DeliveryOptions? = null): Single<Message<JsonObject>> {
    val address = message::class.java.name

    val json = JsonObject.mapFrom(message)

    return if(options == null) {
        this.rxSend<JsonObject>(address, json)
    } else {
        this.rxSend<JsonObject>(address, json, options)
    }
}