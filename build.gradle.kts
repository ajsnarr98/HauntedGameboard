plugins {
    application
    kotlin("jvm") version "1.3.70"
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
