package io.labs2160.vertx.reactivex

import io.reactivex.Single
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.eventbus.EventBus
import io.vertx.reactivex.core.eventbus.Message

fun EventBus.publishJsonObject(message: Any, options: DeliveryOptions? = null): EventBus {
    return if(options == null) {
        this.publish(message::class.java.name, JsonObject.mapFrom(message))
    } else {
        this.publish(message::class.java.name, JsonObject.mapFrom(message), options)
    }
}

fun EventBus.rxSendJsonObject(message: Any, options: DeliveryOptions? = null): Single<Message<JsonObject>> {
    return if(options == null) {
        this.rxSend<JsonObject>(message::class.java.name, JsonObject.mapFrom(message))
    } else {
        this.rxSend<JsonObject>(message::class.java.name, JsonObject.mapFrom(message), options)
    }
}