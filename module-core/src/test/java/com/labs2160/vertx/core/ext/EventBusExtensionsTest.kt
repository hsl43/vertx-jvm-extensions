package com.labs2160.vertx.core.ext

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.ArrayBlockingQueue

@RunWith(VertxUnitRunner::class)
class EventBusExtensionsTest : VertxTestBase() {
    private data class Ping(val value: String = "Ping?")
    private data class Pong(val value: String = "Pong!")

    @Before override fun setUp(context: TestContext) {
        super.setUp(context)
    }

    @After override fun tearDown(context: TestContext) {
        super.tearDown(context)
    }

    @Test fun publishJsonObject_publish_address_is_message_class_fqn(context: TestContext) {
        val async = context.async()

        val queue = ArrayBlockingQueue<Any>(1)

        val consumer = eventBus.consumer<JsonObject>(Ping::class.java.name) {
            queue.put(Any())
        }

        eventBus.publishJsonObject(Ping())

        queue.take()

        consumer.unregister()

        async.complete()
    }

    @Test fun publishJsonObject_publishes_message_as_json_object(context: TestContext) {
        val async = context.async()

        val queue = ArrayBlockingQueue<Any>(1)

        val consumer = eventBus.consumer<JsonObject>(Ping::class.java.name) { message ->
            val event = message.fromJsonObject(Ping::class.java)

            context.assertEquals(Ping().value, event.value)

            queue.put(Any())
        }

        eventBus.publishJsonObject(Ping())

        queue.take()

        consumer.unregister()

        async.complete()
    }

    @Test fun sendJsonObject_send_address_is_message_class_fqn(context: TestContext) {
        val async = context.async()

        val consumer = eventBus.consumer<JsonObject>(Ping::class.java.name) { message ->
            message.replyAsJsonObject(Pong())
        }

        val handler = Handler<AsyncResult<Message<JsonObject>>> { result ->
            if(result.succeeded()) {
                consumer.unregister()

                async.complete()

            } else {
                context.fail(result.cause())
            }
        }

        eventBus.sendJsonObject(Ping(), handler = handler)
    }

    @Test fun rxSendJsonObject_sends_message_as_json_object(context: TestContext) {
        val async = context.async()

        val consumer = eventBus.consumer<JsonObject>(Ping::class.java.name) { message ->
            message.replyAsJsonObject(Pong())
        }

        val handler = Handler<AsyncResult<Message<JsonObject>>> { result ->
            if(result.succeeded()) {
                val event = result.result().fromJsonObject(Pong::class.java)

                context.assertEquals(Pong().value, event.value)

                consumer.unregister()

                async.complete()

            } else {
                context.fail(result.cause())
            }
        }

        eventBus.sendJsonObject(Ping(), handler = handler)
    }
}