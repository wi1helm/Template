plugins {
    id("java")
    id("maven-publish") // Add the Maven Publish plugin
}

group = "nub.wi1helm.template"
version = "0.1"
description = "A simple template inventory library"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
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

// Maven Publish Configuration
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"]) // Publish the Java component

            // Customize POM details if necessary
            pom {
                name.set("Template Inventory Library")
                description.set("A simple template inventory library for Minestom.")
                url.set("https://github.com/wi1helm/T") // Replace with your repo URL

                licenses {
                    license {
                        name = "The GNU Affero General Public License Version 3"
                        url = "https://www.gnu.org/licenses/agpl-3.0.txt"
                    }
                }
            }
        }
    }
}
