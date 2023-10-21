plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // klibs
    implementation(libs.klibs.mikro)
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
