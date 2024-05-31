plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.core)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // klibs
    implementation(libs.klibs.kdi)
    implementation(libs.klibs.mikro.core)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation("mysql:mysql-connector-java:8.0.33")
    // Local
    implementation(projects.modules.core)
}
