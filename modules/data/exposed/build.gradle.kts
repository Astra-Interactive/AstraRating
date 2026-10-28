plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.version)
    alias(libs.plugins.klibs.rules.gradle.linter)
}

dependencies {
    implementation(libs.cache4k)
    implementation(libs.driver.mysql)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.mikro.extensions)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.minecraft.astralibs.core)

    implementation(projects.modules.core.api)
    implementation(projects.modules.data.dao)

    testImplementation(libs.driver.jdbc)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.minecraft.kyori.api)
    testImplementation(libs.tests.kotlin.test)
    testImplementation(testFixtures(projects.modules.data.dao))
}
