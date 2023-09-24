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
    implementation(libs.klibs.kdi)
    implementation(libs.klibs.mikro)
    // Local
    implementation(projects.modules.dto)
}
