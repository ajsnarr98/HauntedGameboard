plugins {
    application
    kotlin("jvm") version "1.3.70"
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
