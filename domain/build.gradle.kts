plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
    id("basic-plugin")
}
dependencies {
    // Kotlin
    implementation(libs.kotlinGradlePlugin)
    // Coroutines
    implementation(libs.coroutines.coreJvm)
    implementation(libs.coroutines.core)
    // Serialization
    implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.serializationJson)
    implementation(libs.kotlin.serializationKaml)
    // AstraLibs
    implementation(libs.astralibs.ktxCore)
    implementation(libs.astralibs.spigotCore)
    implementation(libs.astralibs.spigotGui)
    implementation(libs.astralibs.orm)
    implementation(libs.bstats.bukkit)
    // Test-Core
    testImplementation(kotlin("test-junit5"))
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // Test-libs
    testImplementation(libs.coroutines.core)
    testImplementation(libs.coroutines.coreJvm)
    testImplementation(libs.xerial.sqlite.jdbc)
}
