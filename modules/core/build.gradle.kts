plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.core)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.klibs.kdi)
    implementation(libs.klibs.mikro.core)
}
