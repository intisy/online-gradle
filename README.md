# Online Gradle

Archives containing JAR files are available as [releases](https://github.com/intisy/online-gradle/releases).

## What is Online Gradle?

Online Gradle allows you to use build.gradle files which are not located on your system, so you can share and reuse your code snippets

## Usage

Using the plugins DSL:

```groovy
plugins {
    id "io.github.intisy.online-gradle" version "1.4.5.1"
}
```

Using legacy plugin application:

```groovy
buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "io.github.intisy.online-gradle:1.4.5.1"
    }
}

apply plugin: "io.github.intisy.online-gradle"
```

Once you have the plugin installed you can use it like so:

```groovy
ext {
    main = "${group}.Main"
}

online {
    urls = [
            'https://raw.githubusercontent.com/intisy/gradle-clips/main/shadowJar.gradle'
    ]
}
```

## License

[![Apache License 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
