package io.labs2160.vertx.core;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler;
import io.vertx.ext.web.api.validation.ParameterType;
import io.vertx.ext.web.client.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@RunWith(VertxUnitRunner.class)
public class RoutingContextExtensionsTestJ extends VertxTestBase {

  static class FakeBody {
    private final String name;
    private final int value;

    FakeBody(String name, int value) {
      this.name = name;
      this.value = value;
    }

    @SuppressWarnings("unused")
    public String getName() {
      return name;
    }

    @SuppressWarnings("unused")
    public int getValue() {
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
  public void params_captures_request_parameters(@NotNull TestContext context) {
    final Async async = context.async();

    final Route route = router.get("/:path")
        .handler(HTTPRequestValidationHandler.create()
            .addHeaderParam("_header", ParameterType.GENERIC_STRING, false)
            .addQueryParam("_query", ParameterType.GENERIC_STRING, false)
            .addPathParam("path", ParameterType.GENERIC_STRING)
        )
        .handler(routingContext -> {
          final RequestParameters params = RoutingContextExt.params(routingContext);

          context.assertEquals("HEADER_VALUE", params.headerParameter("_header").getString());
          context.assertEquals("QUERY_VALUE", params.queryParameter("_query").getString());
          context.assertEquals("PATH_VALUE", params.pathParameter("path").getString());

          final Object responseBody = null;
          final boolean prettyJson = false;

          RoutingContextExt.respond(routingContext, OK, responseBody, prettyJson);
        });

    final String path = "/PATH_VALUE?_query=QUERY_VALUE";

    final Object requestBody = null;

    final Map<String,String> headers = new HashMap<>();
    headers.put("_header", "HEADER_VALUE");

    final Handler<AsyncResult<HttpResponse<Buffer>>> handler = result -> {
      route.remove();

      if(result.succeeded()) {
        async.complete();
      } else {
        context.fail(result.cause());
      }

    };

    testRequest(HttpMethod.GET, path, requestBody, headers, handler);
  }

  @Test
  public void respond_has_status_code(@NotNull TestContext context) {
    final Async async = context.async();

    final Route route = router.get("/").handler(routingContext -> {
      final Object responseBody = null;
      final boolean prettyJson = false;

      RoutingContextExt.respond(routingContext, OK, responseBody, prettyJson);
    });

    final String path = "/";
    final Object requestBody = null;
    final Map<String,String> headers = null;

    final Handler<AsyncResult<HttpResponse<Buffer>>> handler = result -> {
      route.remove();

      if(result.succeeded()) {
        context.assertEquals(OK.code(), result.result().statusCode());

        async.complete();

      } else {
        context.fail(result.cause());
      }
    };

    testRequest(HttpMethod.GET, path, requestBody, headers, handler);
  }

  @Test
  public void respond_has_uglified_body(@NotNull TestContext context) {
    final Async async = context.async();

    final Route route = router.get("/").handler(routingContext -> {
      final Object responseBody = new FakeBody("dummy-name-value", Integer.MIN_VALUE);
      final boolean pretty = false;

      RoutingContextExt.respond(routingContext, OK, responseBody, pretty);
    });

    final String path = "/";
    final Object requestBody = null;
    final Map<String,String> headers = null;

    final Handler<AsyncResult<HttpResponse<Buffer>>> handler = result -> {
      route.remove();

      if(result.succeeded()) {
        final String expected = "{\"name\":\"dummy-name-value\",\"value\":-2147483648}";

        context.assertEquals(expected, result.result().bodyAsString());

        async.complete();

      } else {
        context.fail(result.cause());
      }
    };

    testRequest(HttpMethod.GET, path, requestBody, headers, handler);
  }

  @Test
  public void respond_has_prettified_body(@NotNull TestContext context) {
    final Async async = context.async();

    final Route route = router.get("/").handler(routingContext -> {
      final Object responseBody = new FakeBody("dummy-name-value", Integer.MIN_VALUE);
      final boolean prettyJson = true;

      RoutingContextExt.respond(routingContext, OK, responseBody, prettyJson);
    });

    final String path = "/";
    final Object requestBody = null;
    final Map<String,String> headers = null;

    final Handler<AsyncResult<HttpResponse<Buffer>>> handler = result -> {
      route.remove();

      if(result.succeeded()) {
        final String expected = "" +
            "{\n" +
            "  \"name\" : \"dummy-name-value\",\n" +
            "  \"value\" : -2147483648\n" +
            "}";

        context.assertEquals(expected, result.result().bodyAsString());

        async.complete();

      } else {
        context.fail(result.cause());
      }
    };

    testRequest(HttpMethod.GET, path, requestBody, headers, handler);
  }

  @Test
  public void respondPretty_has_pretty_body(@NotNull TestContext context) {
    final Async async = context.async();

    final Route route = router.get("/").handler(routingContext -> {
      final Object responseBody = new FakeBody("dummy-name-value", Integer.MIN_VALUE);

      RoutingContextExt.respondPretty(routingContext, OK, responseBody);
    });

    final String path = "/";
    final Object requestBody = null;
    final Map<String,String> headers = null;

    final Handler<AsyncResult<HttpResponse<Buffer>>> handler = result -> {
      route.remove();

      if(result.succeeded()) {
        final String expected = "" +
            "{\n" +
            "  \"name\" : \"dummy-name-value\",\n" +
            "  \"value\" : -2147483648\n" +
            "}";

        context.assertEquals(expected, result.result().bodyAsString());

        async.complete();

      } else {
        context.fail(result.cause());
      }
    };

    testRequest(HttpMethod.GET, path, requestBody, headers, handler);
  }

}
