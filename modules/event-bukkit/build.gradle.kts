plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.version)
    alias(libs.plugins.klibs.gradle.linter)
}

dependencies {
    compileOnly(libs.minecraft.paper.api)

    implementation(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.command.bukkit)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.astralibs.menu.bukkit)

    implementation(projects.modules.core.api)
    implementation(projects.modules.core.bukkit)
    implementation(projects.modules.data.dao)

    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.tests.kotlin.test)
    testImplementation(testFixtures(projects.modules.data.dao))
}
