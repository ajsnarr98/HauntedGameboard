plugins {
    application
    kotlin("jvm") version "1.6.10"
    java
}

val glBasePackageName = "com.github.ajsnarr98.hauntedgameboard"
val glMainClassName = "$glBasePackageName.MainKt"

group = glBasePackageName
version = "alpha-1.0"

application {
    mainClassName = glMainClassName
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")

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

tasks.test {
    useJUnitPlatform()
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.jar {
    manifest {
        attributes(
                "Main-Class" to glMainClassName
        )
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // add all the dependencies
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
