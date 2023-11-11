import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import ru.astrainteractive.gradleplugin.setupSpigotProcessor
import ru.astrainteractive.gradleplugin.util.ProjectProperties.projectInfo

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
    alias(klibs.plugins.klibs.gradle.java.core)
    id("ru.astrainteractive.gradleplugin.minecraft.multiplatform")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.ktxcore)
    implementation(libs.minecraft.astralibs.spigot.gui)
    implementation(libs.minecraft.astralibs.spigot.core)
    // klibs
    implementation(klibs.klibs.kdi)
    implementation(klibs.klibs.mikro.core)
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
    implementation(projects.modules.dto)
    implementation(projects.modules.integrationPapi)
    implementation(projects.modules.shared)
}
minecraftMultiplatform {
    dependencies {
        implementation(projects.modules.shared.bukkitMain)
    }
}
val localFolder = File("D:\\Minecraft Servers\\Servers\\esmp-configuration\\anarchy\\plugins")
    .takeIf { it.exists() }
    ?: File(rootDir, "jars")

setupSpigotProcessor()

tasks.shadowJar {
    relocate("org.bstats", projectInfo.group)
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
    archiveVersion.set(projectInfo.versionString)
    archiveBaseName.set(projectInfo.name)
    localFolder.apply { if (!exists()) parentFile.mkdirs() }
    localFolder.also(destinationDirectory::set)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
    }
}
