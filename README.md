# Online Gradle

Archives containing JAR files are available as [releases](https://github.com/Blizzity/intisy/online-gradle/releases).

## What is Online Gradle?

Online Gradle allows you to use build.gradle files which are not located on your system, so you can share and reuse your code snippets

## Usage

You can add the plugin like this and add as many codeartifact repositories, as you want:



Or to add repositories to the Publishing plugin:



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
```
