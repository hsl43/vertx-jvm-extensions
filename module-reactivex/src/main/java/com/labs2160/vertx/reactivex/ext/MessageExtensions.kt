@file:JvmName("MessageExt")

package com.labs2160.vertx.reactivex.ext

import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.eventbus.Message

fun <T> Message<T>.replyAsJsonObject(instance: Any) = this.reply(JsonObject.mapFrom(instance))

fun <T> Message<JsonObject>.fromJsonObject(type: Class<T>): T = this.body().mapTo(type)