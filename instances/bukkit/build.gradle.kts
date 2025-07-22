import ru.astrainteractive.gradleplugin.property.extension.ModelPropertyValueExt.requireProjectInfo

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.core)
    id("io.github.goooler.shadow")
    alias(libs.plugins.klibs.minecraft.shadow)
    alias(libs.plugins.klibs.minecraft.resource.processor)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.exposed)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.command.bukkit)
    // klibs
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.kstorage)
    // Exposed
    implementation(libs.exposed.core)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
    implementation(libs.minecraft.bstats)
    compileOnly(libs.minecraft.papi)
    compileOnly(libs.minecraft.vaultapi)
    implementation(libs.minecraft.bstats)
    // Local
    implementation(projects.modules.data.exposed)
    implementation(projects.modules.data.dao)
    implementation(projects.modules.integrationPapi)
    implementation(projects.modules.core.api)
    implementation(projects.modules.core.bukkit)
    implementation(projects.modules.guiCoreBukkit)
    implementation(projects.modules.commandBukkit)
    implementation(projects.modules.eventBukkit)
    implementation(projects.modules.ratingChange.api)
    implementation(projects.modules.ratingPlayer.api)
    implementation(projects.modules.ratingPlayers.api)
}

minecraftProcessResource {
    bukkit()
}

astraShadowJar {
    requireShadowJarTask {
        destination = File("/home/makeevrserg/Desktop/temp-server/build/bukkit/plugins")
            .takeIf { it.exists() }
            ?: File(rootDir, "jars")

        val projectInfo = requireProjectInfo
        isReproducibleFileOrder = true
        mergeServiceFiles()
        dependsOn(configurations)
        archiveClassifier.set(null as String?)
        relocate("org.bstats", projectInfo.group)

        minimize {
            exclude(dependency(libs.exposed.jdbc.get()))
            exclude(dependency(libs.exposed.dao.get()))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.version.get()}"))
        }
        archiveVersion.set(projectInfo.versionString)
        archiveBaseName.set("${projectInfo.name}-bukkit")
        destinationDirectory.set(destination.get())
    }
}
