plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(klibs.plugins.klibs.gradle.java.core)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.ktxcore)
    // klibs
    implementation(klibs.klibs.mikro.core)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    // Local
    implementation(projects.modules.apiRating)
    implementation(projects.modules.dto)
}
