plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.version)
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.coroutines.core)

    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    // klibs
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.kstorage)
    // Local
    implementation(projects.modules.data.dao)
    implementation(projects.modules.data.exposed)
    implementation(projects.modules.core.api)
}
