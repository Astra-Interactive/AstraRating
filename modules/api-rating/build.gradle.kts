plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(klibs.plugins.klibs.gradle.java.core)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // klibs
    implementation(klibs.klibs.mikro.core)
    implementation(klibs.klibs.kdi)
    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    // Local
    implementation(projects.modules.dto)
    implementation(projects.modules.dbRating)
}
