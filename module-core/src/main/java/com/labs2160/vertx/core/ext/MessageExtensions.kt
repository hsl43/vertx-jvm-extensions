@file:JvmName("MessageExt")

package com.labs2160.vertx.core.ext

import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject

fun <T> Message<T>.replyAsJsonObject(instance: Any) = this.reply(JsonObject.mapFrom(instance))

fun <T> Message<JsonObject>.fromJsonObject(type: Class<T>): T = this.body().mapTo(type)