package com.labs2160.vertx.rxjava.ext;

import com.labs2160.vertx.rxjava.ext.test.VertxTestBase;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ArrayBlockingQueue;

@RunWith(VertxUnitRunner.class)
public class EventBusExtensionsTestJ extends VertxTestBase {

  static class Ping {
    private final String value = "Ping?";

    @SuppressWarnings("unused")
    public String getValue() {
      return value;
    }
  }

  static class Pong {
    private final String value = "Pong!";

    @SuppressWarnings("unused")
    public String getValue() {
      return value;
    }
  }

  @Override @Before
  public void setUp(@NotNull TestContext context) {
    super.setUp(context);
  }

  @Override @After
  public void tearDown(@NotNull TestContext context) {
    super.tearDown(context);
  }

  @Test
  public void publishJsonObject_publish_address_is_message_class_fqn(@NotNull TestContext context) throws Exception {
    final Async async = context.async();

    final ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<>(1);

    final MessageConsumer<JsonObject> consumer = eventBus.consumer(Ping.class.getName(), message -> {
      queue.add(new Object());
    });

    final Ping message = new Ping();
    final DeliveryOptions options = null;

    EventBusExt.publishJsonObject(eventBus, message, options);

    queue.take();

    consumer.unregister();

    async.complete();
  }

  @Test
  public void publishJsonObject_publishes_message_as_json_object(@NotNull TestContext context) throws Exception {
    final Async async = context.async();

    final ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<>(1);

    final MessageConsumer<JsonObject> consumer = eventBus.consumer(Ping.class.getName(), message -> {
      final Ping received = MessageExt.fromJsonObject(message, Ping.class);

      context.assertEquals(new Ping().value, received.value);

      queue.add(new Object());
    });

    final Ping message = new Ping();
    final DeliveryOptions options = null;

    EventBusExt.publishJsonObject(eventBus, message, options);

    queue.take();

    consumer.unregister();

    async.complete();
  }

  @Test
  public void rxSendJsonObject_send_address_is_message_class_fqn(@NotNull TestContext context) {
    final Async async = context.async();

    final MessageConsumer<JsonObject> consumer = eventBus.consumer(Ping.class.getName(), message -> {
      MessageExt.replyAsJsonObject(message, new Pong());
    });

    final Ping message = new Ping();
    final DeliveryOptions options = null;

    EventBusExt.rxSendJsonObject(eventBus, message, options).subscribe(
        result -> {
          consumer.unregister();

          async.complete();
        },
        error -> {
          context.fail(error);
        }
    );
  }

  @Test
  public void rxSendJsonObject_sends_message_as_json_object(@NotNull TestContext context) {
    final Async async = context.async();

    final MessageConsumer<JsonObject> consumer = eventBus.consumer(Ping.class.getName(), message -> {
      MessageExt.replyAsJsonObject(message, new Pong());
    });

    final Ping message = new Ping();
    final DeliveryOptions options = null;

    EventBusExt.rxSendJsonObject(eventBus, message, options).subscribe(
        result -> {
          final Pong reply = MessageExt.fromJsonObject(result, Pong.class);

          context.assertEquals(new Pong().value, reply.value);

          consumer.unregister();

          async.complete();
        },
        error -> {
          context.fail(error);
        }
    );
  }

}
