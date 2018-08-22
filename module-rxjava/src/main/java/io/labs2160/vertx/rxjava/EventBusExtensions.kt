@file:JvmName("EventBusExt")

package io.labs2160.vertx.rxjava

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.eventbus.EventBus
import io.vertx.rxjava.core.eventbus.Message
import rx.Single

fun EventBus.publishJsonObject(message: Any, options: DeliveryOptions? = null): EventBus {
    val address = message::class.java.name

    val json = JsonObject.mapFrom(message)

    return if(options == null) {
        this.publish(address, json)
    } else {
        this.publish(address, json, options)
    }
}

fun EventBus.rxSendJsonObject(message: Any, options: DeliveryOptions? = null): Single<Message<JsonObject>> {
    val address = message::class.java.name

    val json = JsonObject.mapFrom(message)

    return if(options == null) {
        this.rxSend<JsonObject>(address, json)
    } else {
        this.rxSend<JsonObject>(address, json, options)
    }
}