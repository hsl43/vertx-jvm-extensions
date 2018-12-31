package com.labs2160.vertx.reactivex.ext;

import com.labs2160.vertx.reactivex.ext.test.VertxIntegrationTest;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.ext.web.api.validation.ParameterType;
import io.vertx.reactivex.ext.web.Route;
import io.vertx.reactivex.ext.web.api.validation.HTTPRequestValidationHandler;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@RunWith(VertxUnitRunner.class)
public class RoutingContextExtensionsTestJ extends VertxIntegrationTest {

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

    testRequest(HttpMethod.GET, path, requestBody, headers)
        .doFinally(route::remove)
        .subscribe(
            value -> {
              async.complete();
            },
            error -> {
              context.fail(error);
            }
        );
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

    testRequest(HttpMethod.GET, path, requestBody, headers)
        .doFinally(route::remove)
        .subscribe(
            response -> {
              context.assertEquals(OK.code(), response.statusCode());

              async.complete();
            },
            error -> {
              context.fail(error);
            }
    );
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

    testRequest(HttpMethod.GET, path, requestBody, headers)
        .doFinally(route::remove)
        .subscribe(
            response -> {
              final String expected = "{\"name\":\"dummy-name-value\",\"value\":-2147483648}";

              context.assertEquals(expected, response.bodyAsString());

              async.complete();
            },
            error -> {
              context.fail(error);
            }
        );
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

    testRequest(HttpMethod.GET, path, requestBody, headers)
        .doFinally(route::remove)
        .subscribe(
            response -> {
              final String expected = "" +
                  "{\n" +
                  "  \"name\" : \"dummy-name-value\",\n" +
                  "  \"value\" : -2147483648\n" +
                  "}";

              context.assertEquals(expected, response.bodyAsString());

              async.complete();
            },
            error -> {
              context.fail(error);
            }
        );
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

    testRequest(HttpMethod.GET, path, requestBody, headers)
        .doFinally(route::remove)
        .subscribe(
            response -> {
              final String expected = "" +
                  "{\n" +
                  "  \"name\" : \"dummy-name-value\",\n" +
                  "  \"value\" : -2147483648\n" +
                  "}";

              context.assertEquals(expected, response.bodyAsString());

              async.complete();
            },
            error -> {
              context.fail(error);
            }
        );
  }

}
