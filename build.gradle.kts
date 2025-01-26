plugins {
    id("java")
}

group = "nub.wi1helm.template"
version = "0.1"
description = "A simple template inventory library"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:65f75bb059") // Minestom
    implementation("net.kyori:adventure-text-minimessage:4.17.0") // MiniMessage
    implementation("net.kyori:adventure-text-serializer-gson:4.17.0")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}