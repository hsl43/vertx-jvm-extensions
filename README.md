# vertx-jvm-extensions

A library of convenient helper functions for developing Vert.x applications on the JVM.


## Releases

**Gradle** 
```groovy
buildscript {
    repositories {
        jcenter()
    }
}

dependencies {
    // for applications built with Vert.x "Core" API 
    implementation 'com.labs2160:vertx-ext-core:0.1.0'

    // for applications built with Vert.x "RxJava 2" API
    implementation 'com.labs2160:vertx-ext-reactivex:0.1.0'

    // for applications built with Vert.x "RxJava 1" API
    implementation 'com.labs2160:vertx-ext-rxjava:0.1.0'
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

The EventBus extensions are designed to promote canonical usage of JSON
as a messaging medium, while also reducing the amount of code needed for both 
multicast and point-to-point messaging scenarios. This is achieved in two 
steps: 

1. Overloaded variations of `publishJsonObject()` and `sendJsonObject()` 
have been introduced, with each variant accepting an arbitrary message object 
as input. This message is serialized into a `JsonObject` before being 
transmitted over the wire. Provided this type can be (de)serialized, this 
alleviates the need for writing and registering custom `MessageCodec`s.
1. The fully-qualified name of the class of the message supplied to 
`publishJsonObject()` and `sendJsonObject()` is used as the destination 
address. This alleviates the need for maintaining a separate registry of 
addresses, while also promoting an event-driven approach to messaging.
 
**Java**
```java
final MyEvent event = new MyEvent(...);

EventBusExt.rxSendJsonObject(eventBus, event).subscribe(...);
```
**Kotlin**
```kotlin
val event = MyEvent(...)

eventBus.rxSendJsonObject(event).subscribe(...)
```


### Message

The Message extensions are designed to further promote the canonical usage of 
JSON for messaging purposes. 

**Java**
```java
final Message<JsonObject> message...;

final TheirBody theirBody = MessageExt.fromJsonObject(message, TheirBody.class);

final MyReply reply = new MyReply(...);

MessageExt.replyAsJsonObject(message, reply);
```

**Kotlin**
```kotlin
val message: Message<JsonObject>...

val theirBody = message.fromJsonObject(TheirBody::class.java)

val reply = MyReply(...)

message.replyAsJsonObject(reply)
```

### RoutingContext
The RoutingContext extensions introduce more concise ways of composing 
responses to HTTP requests.

**Java**
```java
final RoutingContext routingContext...;
    
final MyBody body = new MyBody();
    
// With JSON body
RoutingContextExt.respond(routingContext, OK, body);

// With JSON body prettily formatted
RoutingContextExt.respondPretty(routingContext, OK, body);

// No body
RoutingContextExt.respond(routingContext, OK);
```

**Kotlin**
```kotlin
val routingContext: RoutingContext...
    
val body = MyBody()
    
// With JSON body
routingContext.respond(OK, body);

// With JSON body prettily formatted
routingContext.respondPretty(OK, body);

// No body
routingContext.respond(OK);
```