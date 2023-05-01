group = libs.versions.plugin.group.get()
version = libs.versions.plugin.version.get()

plugins {
    java
    `java-library`
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.gradle.shadow) apply false
    id("detekt-convention")
}
