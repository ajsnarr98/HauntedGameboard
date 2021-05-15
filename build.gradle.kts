plugins {
    application
    kotlin("jvm") version "1.5.0"
    java
}

application {
    mainClassName = "com.ajsnarr.hauntedgameboard.MainKt"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("uk.co.caprica:picam:2.0.2")
    implementation("opencv:opencv:4.0.0-0")

}

repositories {
    mavenCentral()
    maven { url = uri("https://clojars.org/repo/") }
}

sourceSets {
    main {
        java {
            srcDirs(
                "src/main/kotlin/",
                "src/main/java/"
            )
        }
    }
}

tasks.jar {

    manifest {
        attributes(
            "Main-Class" to "com.ajsnarr.hauntedgameboard.MainKt"
        )
    }

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    // add lib folder
    from("src/lib/") {
        into("lib/")
    }
}
