plugins {
    id("java")
    id("maven-publish")
}

group = "nub.wi1helm.template"
version = "0.2.4.2"
description = "A simple template based Inventory and NPC library"

repositories {
    mavenCentral()
    maven("https://jitpack.io") // Include Jitpack repository
}

dependencies {
    implementation("net.minestom:minestom-snapshots:65f75bb059") // Minestom
    implementation("net.kyori:adventure-text-minimessage:4.17.0") // MiniMessage
    implementation("net.kyori:adventure-text-serializer-gson:4.17.0")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
    options.encoding = "UTF-8"
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// Maven Publish Configuration
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("Template Inventory / NPC Library")
                description.set("A simple template based inventory & npc library for Minestom.")
                url.set("https://github.com/wi1helm/Template") // Update with your repository URL

                licenses {
                    license {
                        name.set("GNU Affero General Public License Version 3")
                        url.set("https://www.gnu.org/licenses/agpl-3.0.txt")
                    }
                }
            }
        }
    }
}
