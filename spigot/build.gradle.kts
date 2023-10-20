import ru.astrainteractive.gradleplugin.setupSpigotProcessor
import ru.astrainteractive.gradleplugin.setupSpigotShadow

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.ktxcore)
    implementation(libs.minecraft.astralibs.orm)
    implementation(libs.minecraft.astralibs.spigot.gui)
    implementation(libs.minecraft.astralibs.spigot.core)
    // klibs
    implementation(libs.klibs.kdi)
    implementation(libs.klibs.mikro)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
    implementation(libs.minecraft.bstats)
    compileOnly(libs.minecraft.papi)
//    compileOnly(libs.discordsrv)
    compileOnly(libs.minecraft.vaultapi)
    implementation(libs.minecraft.bstats)
    // Local
    implementation(projects.modules.dbRating)
    implementation(projects.modules.apiRating)
    implementation(projects.modules.dto)
    implementation(projects.modules.integrationPapi)
    implementation(projects.modules.shared)
}
val localFolder = File("D:\\Minecraft Servers\\Servers\\esmp-configuration\\anarchy\\plugins")
if (localFolder.exists()) setupSpigotShadow(localFolder) else setupSpigotShadow()
setupSpigotProcessor()
