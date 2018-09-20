# vertx-jvm-extensions

<img src='https://travis-ci.org/hsl43/vertx-jvm-extensions.svg?branch=master'/>

A library of helpers for developing Vert.x applications on the JVM.


## Releases

**Gradle** 
```groovy
buildscript {
    repositories {
        jcenter()
    }
}

dependencies {
    // Vert.x "Core" API 
    implementation 'com.labs2160:vertx-ext-core:1.0.0'

    // ...or, Vert.x "RxJava 2" API
    implementation 'com.labs2160:vertx-ext-reactivex:1.0.0'

    // ...or, Vert.x "RxJava 1" API
    implementation 'com.labs2160:vertx-ext-rxjava:1.0.0'
}
```

## Compatibility

* Java 8+
* Kotlin 1.2.x
* Vert.x 3.5.x

## Featured Extensions

  - [EventBus](#eventbus)
  - [Message](#message)
  - [RoutingContext](#routingcontext)

### EventBus
---
The `EventBus` extensions are designed to promote canonical usage of JSON
as a messaging medium, while also reducing the amount of code needed in both 
multicast and point-to-point messaging scenarios. This is achieved in two ways: 

1. Overloaded variations of **`publishJsonObject()`** and 
**`sendJsonObject()`**  have been introduced, with each variant accepting an 
arbitrary message object as input. This message is serialized as a `JsonObject` 
over the wire. Provided that the message can be (de)serialized as a 
`JsonObject`, this alleviates the need for writing and registering custom 
`MessageCodec`s.

1. The fully-qualified name of the class of the message supplied to 
**`publishJsonObject()`** and **`sendJsonObject()`** is used as the destination 
address. This alleviates the need for maintaining a separate registry of 
addresses, while also promoting an event-driven approach to messaging.

### Message
---
The `Message` extensions are designed to further promote the canonical usage of 
JSON as a messaging medium. 

Use **`fromJsonObject()`** to read the body of a `Message` as a `JsonObject`.

Use **`replyAsJsonObject()`** to reply to a `Message` with a body serialized as 
a `JsonObject`.

### RoutingContext
---
The `RoutingContext` extensions introduce more concise, typesafe ways of 
composing responses to HTTP requests.

Overloaded variations of **`respond()`** can be used to compose and end a
response with:
* [*required*] a status represented by `io.netty.handler.codec.http.HttpResponseStatus`
* [*optional*] an arbitrary body to be serialized as JSON 
 
 By default, bodies are serialized as JSON without any formatting. 
 
 **`respondPretty()`** is provided to write JSON bodies as in a human readable
 format. (No formatting is applied by default).
 
 If using the *Vert.x API Contract* to perform request validation, 
**`params()`** can be used to get the `RequestParameters` associated with the
current request.  