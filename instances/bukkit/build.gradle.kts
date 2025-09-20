import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.named
import ru.astrainteractive.gradleplugin.property.extension.ModelPropertyValueExt.requireProjectInfo

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.core)
    alias(libs.plugins.klibs.minecraft.resource.processor)
    alias(libs.plugins.gradle.shadow)
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
    implementation(projects.modules.core.guiBukkit)
    implementation(projects.modules.commandBukkit)
    implementation(projects.modules.eventBukkit)
    implementation(projects.modules.ratingChange.api)
    implementation(projects.modules.ratingPlayer.api)
    implementation(projects.modules.ratingPlayers.api)
}

minecraftProcessResource {
    bukkit(
        customProperties = mapOf(
            "libraries" to listOf(
                libs.driver.h2.get(),
                libs.driver.jdbc.get(),
                libs.driver.mysql.get(),
                libs.driver.mariadb.get()
            ).joinToString("\",\"", "[\"", "\"]")
        )
    )
}

val shadowJar = tasks.named<ShadowJar>("shadowJar")
shadowJar.configure {
    mergeServiceFiles()
    dependsOn(tasks.named<ProcessResources>("processResources"))
    isReproducibleFileOrder = true
    archiveClassifier = null as String?
    archiveVersion.set(requireProjectInfo.versionString)
    archiveBaseName.set("${requireProjectInfo.name}-bukkit")
    destinationDirectory = rootProject
        .layout.buildDirectory.asFile.get()
        .resolve("bukkit")
        .resolve("plugins")
        .takeIf(File::exists)
        ?: rootDir.resolve("jars").also(File::mkdirs)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    configurations = listOf(project.configurations.runtimeClasspath.get())
    listOf(
        "co.touchlab",
        "com.charleskorn",
        "com.mysql",
        "google.protobuf",
        "io.github",
        "it.krzeminski",
        "net.thauvin",
        "okio",
        "org.jetbrains",
        "org.intellij",
        "org.bstats",
        "org.slf4j",
        "ru.astrainteractive.klibs",
        "ru.astrainteractive.astralibs",
        "ch.qos.logback",
        "com.ibm.icu",
        "org.apache",
    ).forEach { pattern ->
        relocate(pattern, "${requireProjectInfo.group}.libs.$pattern")
    }
    minimize {
        exclude(dependency(libs.exposed.jdbc.get()))
        exclude(dependency(libs.exposed.dao.get()))
        exclude(dependency(libs.exposed.core.get()))
    }
}
