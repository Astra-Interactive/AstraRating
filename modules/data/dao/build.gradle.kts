plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.core)
}

dependencies {
    // Kotlin

    // klibs
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.mikro.extensions)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.klibs.kstorage)
    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    // Test
    testImplementation(libs.tests.kotlin.test)
    // Local
    implementation(projects.modules.data.exposed)
    implementation(projects.modules.core.api)
}
