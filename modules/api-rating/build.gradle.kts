plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.core)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // klibs
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.kdi)
    implementation(libs.minecraft.astralibs.core)
    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    // Local
    implementation(projects.modules.dbRating)
    implementation(projects.modules.core)
}
