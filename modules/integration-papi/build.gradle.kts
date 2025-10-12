plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.core)
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)

    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.bukkit)
    // klibs
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.kstorage)
    // Test
    testImplementation(libs.tests.kotlin.test)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
    compileOnly(libs.minecraft.papi)
    // Local
    implementation(projects.modules.data.dao)
    implementation(projects.modules.core.api)
}
