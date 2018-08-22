package io.labs2160.vertx.reactivex

import io.reactivex.Single
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.eventbus.EventBus
import io.vertx.reactivex.core.eventbus.Message

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