
import ru.astrainteractive.gradleplugin.property.extension.ModelPropertyValueExt.requireProjectInfo
import ru.astrainteractive.gradleplugin.setupSpigotProcessor
import ru.astrainteractive.gradleplugin.setupSpigotShadow

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.core)
    id("ru.astrainteractive.gradleplugin.minecraft.multiplatform")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.command.bukkit)
    // klibs
    implementation(libs.klibs.kdi)
    implementation(libs.klibs.mikro.core)
    // Exposed
    implementation(libs.exposed.core)
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
    implementation(projects.modules.integrationPapi)
    implementation(projects.modules.shared)
    implementation(projects.modules.core)
}
minecraftMultiplatform {
    dependencies {
        implementation(projects.modules.shared.bukkitMain)
    }
}
val localFolder = File("D:\\Minecraft Servers\\Servers\\esmp-configuration\\smp\\plugins")
    .takeIf { it.exists() }
    ?: File(rootDir, "jars")

setupSpigotProcessor()

setupSpigotShadow {
    relocate("org.bstats", requireProjectInfo.group)
    isReproducibleFileOrder = true
    mergeServiceFiles()
    dependsOn(configurations)
    archiveClassifier.set(null as String?)
    from(sourceSets.main.get().output)
    from(project.configurations.runtimeClasspath)
    minimize {
        exclude(dependency("org.jetbrains.exposed:exposed-jdbc:${libs.versions.exposed.get()}"))
        exclude(dependency("org.jetbrains.exposed:exposed-dao:${libs.versions.exposed.get()}"))
    }
    archiveVersion.set(requireProjectInfo.versionString)
    archiveBaseName.set(requireProjectInfo.name)
    localFolder.apply { if (!exists()) parentFile.mkdirs() }
    localFolder.also(destinationDirectory::set)
}
