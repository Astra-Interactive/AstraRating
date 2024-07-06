plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.core)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.command.bukkit)
    implementation(libs.klibs.kdi)
    implementation(libs.klibs.mikro.core)
    // Local
    implementation(projects.modules.core)
    implementation(projects.modules.coreBukkit)
    implementation(projects.modules.apiRating)
    implementation(projects.modules.dbRating)
    implementation(projects.modules.shared)
    implementation(projects.modules.guiCoreBukkit)
}
