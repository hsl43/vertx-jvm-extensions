package com.labs2160.vertx.core.ext

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.ext.unit.TestContext

@Suppress("unused")
fun TestContext.assertEquals(expected: HttpResponseStatus, actual: Int): TestContext {
  return this.assertEquals(expected.code(), actual)
}

@Suppress("unused")
fun TestContext.assertEquals(expected: HttpResponseStatus, actual: Int, message: String): TestContext {
  return this.assertEquals(expected.code(), actual, message)
}

@Suppress("unused")
fun TestContext.assertNotEquals(expected: HttpResponseStatus, actual: Int): TestContext {
  return this.assertNotEquals(expected.code(), actual)
}

@Suppress("unused")
fun TestContext.assertNotEquals(expected: HttpResponseStatus, actual: Int, message: String): TestContext {
  return this.assertNotEquals(expected.code(), actual, message)
}