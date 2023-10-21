plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // klibs
    implementation(libs.klibs.kdi)
    implementation(libs.klibs.mikro)
    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation("mysql:mysql-connector-java:8.0.30")
    // Local
    implementation(projects.modules.dto)
}
