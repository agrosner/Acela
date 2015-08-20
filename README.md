# Acela

Acela is a powerful and blazing-fast JSON parser and serializer with __annotation processing__ backed by [Jackson Streaming API](https://github.com/FasterXML/jackson-core).

This library is built on speed, performance, and approachability. Not only does it eliminate most boilerplate code for parsing JSON data into model classes and back to JSON, but also provides a very flexible and simple API to manage how data is parsed.

What sets this library apart:
1. Built on speed, its faster than nearly anything out there.

// insert graph here

2. Support for custom conversions from JSON
3. Perform custom logic by listening for parse/serialize events using `SerializeListener`, `ParseListener` or the more specific `ParseKeyListener` and `SerializeKeyListener`
4. Support for merging different JSON sources together using the `@Mergeable`annotation.
5. Support for subclassing non-Translatable classes and parsing data into them using `@InheritedField`

## Getting Started
Will be up soon.

```groovy
  buildscript {
    repositories {
        jcenter()
    }
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
  }

  allprojects {
    repositories {
        jcenter()
    }
  }


```

Add the library to the project-level build.gradle, and use the [apt plugin](https://bitbucket.org/hvisser/android-apt):

```groovy

  apply plugin: 'com.neenbedankt.android-apt'

  dependencies {
    compile "com.andrewgrosner.acela:library:1.0.0"
  }

```

## Changelog

## Usage Docs

For more detailed usage, check out these sections:

[Getting Started](https://github.com/agrosner/Acela/blob/master/usage/GettingStarted.md)

[Merging Objects](https://github.com/agrosner/Acela/blob/master/usage/Mergeable.md)

[Inheriting Fields](https://github.com/agrosner/Acela/blob/master/usage/InheritedField.md)

[Listening For Parse/Serialize Events](https://github.com/agrosner/Acela/blob/master/usage/EventListening.md)

[Custom Type Converters](https://github.com/agrosner/Acela/blob/master/usage/TypeConverters.md)

[Customizing Serialization and Parsing](https://github.com/agrosner/Acela/blob/master/usage/CustomTranslatable.md)
