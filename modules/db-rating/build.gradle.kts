plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(klibs.plugins.klibs.gradle.java.core)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // klibs
    implementation(klibs.klibs.kdi)
    implementation(klibs.klibs.mikro.core)
    // AstraLibs
    implementation(libs.minecraft.astralibs.ktxcore)
    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation("mysql:mysql-connector-java:8.0.30")
    // Local
    implementation(projects.modules.core)
}
