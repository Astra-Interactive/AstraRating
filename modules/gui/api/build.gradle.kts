plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.version)
    alias(libs.plugins.klibs.gradle.linter)
}

dependencies {
    implementation(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.core)

    implementation(projects.modules.core.api)
    implementation(projects.modules.data.dao)

    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.tests.kotlin.test)
    testImplementation(testFixtures(projects.modules.data.dao))
}
