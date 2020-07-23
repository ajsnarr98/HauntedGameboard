plugins {
  application
  kotlin("jvm") version "1.3.70"
  java
  c
}

application {
  mainClassName = "com.ajsnarr.main.MainKt"
}

dependencies {
  compile(kotlin("stdlib"))
}

repositories {
  jcenter()
  maven { url = uri("https://jitpack.io") }
}

sourceSets {
  main {
    java {
      srcDirs(
        "src/main/kotlin/"
      )
    }
  }
}

tasks.jar {
  manifest {
    attributes("Main-Class" to "com.ajsnarr.main.MainKt")
  }

  // To add all of the dependencies
  from(sourceSets.main.get().output)

  dependsOn(configurations.runtimeClasspath)
  from({
    configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
  })
}
