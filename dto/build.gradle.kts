plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("basic-java")
}
dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
}
