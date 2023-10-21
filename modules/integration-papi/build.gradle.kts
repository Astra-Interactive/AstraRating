plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.spigot.core)
    // klibs
    implementation(libs.klibs.mikro)
    implementation(libs.klibs.kdi)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
    compileOnly(libs.minecraft.papi)
    // Local
    implementation(projects.modules.apiRating)
    implementation(projects.modules.dto)
}
