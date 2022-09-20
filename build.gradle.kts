plugins {
    kotlin("jvm") version "1.6.10"
    java
    id("org.jetbrains.compose") version "1.1.0"
//    application
}

val glBasePackageName = "com.github.ajsnarr98.hauntedgameboard"
val glMainClassName = "$glBasePackageName.MainKt"

group = glBasePackageName
version = "alpha-1.0"

//application {
//    mainClassName = glMainClassName
//}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation(compose.desktop.currentOs)

    // opencv
    implementation(files("lib/opencv-460.jar"))
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
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

compose.desktop {
    application {
        mainClass = glMainClassName
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

    // add native lib folder
    from("nativelib/") {
        into("nativelib/")
    }
}
