import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.named
import ru.astrainteractive.gradleplugin.property.extension.ModelPropertyValueExt.requireProjectInfo

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.klibs.gradle.java.version)
    alias(libs.plugins.klibs.minecraft.resource.processor)
    alias(libs.plugins.gradle.shadow)
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.serialization.json)

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

    val projectInfo = requireProjectInfo
    isReproducibleFileOrder = true
    mergeServiceFiles()
    dependsOn(configurations)
    archiveClassifier.set(null as String?)

    minimize {
        exclude(dependency(libs.exposed.jdbc.get()))
        exclude(dependency(libs.exposed.dao.get()))
    }
    archiveVersion.set(projectInfo.versionString)
    archiveBaseName.set("${projectInfo.name}-bukkit")
    destinationDirectory = rootDir.resolve("build")
        .resolve("bukkit")
        .resolve("plugins")
        .takeIf(File::exists)
        ?: File(rootDir, "jars").also(File::mkdirs)

    relocate("org.bstats", projectInfo.group)
    listOf(
        "co.touchlab",
        "com.mysql",
        "google.protobuf",
        "io.github.reactivecircus",
        "ch.qos.logback",
        "com.charleskorn.kaml",
        "com.ibm.icu",
        "it.krzeminski.snakeyaml",
        "net.thauvin.erik",
        "okio",
        "org.apache",
        "org.intellij",
        "org.slf4j",
        "org.jetbrains.annotations",
        "ru.astrainteractive.klibs",
        "ru.astrainteractive.astralibs"
    ).forEach { pattern -> relocate(pattern, "${projectInfo.group}.$pattern") }
    listOf(
        "org.jetbrains.exposed",
        "kotlinx",
    ).forEach { pattern ->
        relocate(pattern, "${projectInfo.group}.$pattern") {
            exclude("kotlin/kotlin.kotlin_builtins")
        }
    }
}
