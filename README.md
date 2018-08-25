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
    // Vert.x "core" API 
    implementation 'com.labs2160:vertx-ext-core:0.1.0'

    // Vert.x "RxJava 2" API 
    implementation 'com.labs2160:vertx-ext-reactivex:0.1.0'

    // Vert.x "RxJava 1" API
    implementation 'com.labs2160:vertx-ext-rxjava:0.1.0'
}
```