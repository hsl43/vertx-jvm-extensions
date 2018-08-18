package io.labs2160.vertx.rxjava

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.eventbus.EventBus
import io.vertx.rxjava.core.eventbus.Message
import rx.Single

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