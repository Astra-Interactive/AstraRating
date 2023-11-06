plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(klibs.plugins.klibs.gradle.java.core)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
}
