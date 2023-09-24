plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.orm)
    // klibs
    implementation(libs.klibs.mikro)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    // Local
    implementation(projects.modules.dto)
    implementation(projects.modules.dbRating)
}
