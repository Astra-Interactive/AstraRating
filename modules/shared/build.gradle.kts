plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.core)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    // klibs
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.kstorage)
    // Bukkit
    compileOnly(libs.minecraft.paper.api)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    // Local
    implementation(projects.modules.apiRating)
    implementation(projects.modules.dbRating)
    implementation(projects.modules.core)
}
