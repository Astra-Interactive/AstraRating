plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.version)
}

dependencies {
    compileOnly(libs.minecraft.brigadier)

    implementation(libs.cache4k)
    implementation(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.serialization.kaml)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.core)

    testImplementation(libs.minecraft.kyori.api)
    testImplementation(libs.minecraft.kyori.legacy)
    testImplementation(libs.minecraft.kyori.minimessage)
    testImplementation(libs.minecraft.kyori.plain)
    testImplementation(libs.tests.kotlin.test)
}
