plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.core)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.bstats)
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.kstorage)
    implementation("io.github.reactivecircus.cache4k:cache4k-jvm:0.14.0")
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
}
