@file:JvmName("MessageExt")

package com.labs2160.vertx.rxjava.ext

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.eventbus.Message

/**
 * Reply to this message with an arbitrary [message] as a JsonObject.
 */
fun <T> Message<T>.replyAsJsonObject(message: Any) = this.replyAsJsonObject(message, null)

/**
 * Reply to this message with an arbitrary [message] as a JsonObject. [options]
 * can be supplied to configure the reply delivery.
 */
fun <T> Message<T>.replyAsJsonObject(message: Any, options: DeliveryOptions?) {
    if(options == null) {
        this.reply(JsonObject.mapFrom(message))
    } else {
        this.reply(JsonObject.mapFrom(message), options)
    }
}

/**
 * Deserialize this message's JsonObject body as an instance of the specified
 * [type]
 */
fun <T> Message<JsonObject>.fromJsonObject(type: Class<T>): T = this.body().mapTo(type)