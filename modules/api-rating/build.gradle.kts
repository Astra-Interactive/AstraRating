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
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.klibs.kstorage)
    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    testImplementation(libs.minecraft.astralibs.exposed)
    // Local
    implementation(projects.modules.dbRating)
    implementation(projects.modules.core)
}
