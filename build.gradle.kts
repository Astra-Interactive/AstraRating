buildscript {
    dependencies {
        classpath("ru.astrainteractive.gradleplugin:convention:0.0.10")
        classpath("ru.astrainteractive.gradleplugin:minecraft:0.0.10")
    }
}

plugins {
    java
    `java-library`
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false

    // klibs - core
    alias(libs.plugins.klibs.gradle.detekt) apply false
    alias(libs.plugins.klibs.gradle.detekt.compose) apply false
    alias(libs.plugins.klibs.gradle.dokka.root) apply false
    alias(libs.plugins.klibs.gradle.dokka.module) apply false
    alias(libs.plugins.klibs.gradle.java.core) apply false
    alias(libs.plugins.klibs.gradle.stub.javadoc) apply false
    alias(libs.plugins.klibs.gradle.publication) apply false
    alias(libs.plugins.klibs.gradle.rootinfo) apply false
    // klibs - minecraft
    alias(libs.plugins.klibs.gradle.minecraft.empty) apply false
    alias(libs.plugins.klibs.gradle.minecraft.multiplatform) apply false
}

apply(plugin = "ru.astrainteractive.gradleplugin.dokka.root")
apply(plugin = "ru.astrainteractive.gradleplugin.detekt")
apply(plugin = "ru.astrainteractive.gradleplugin.root.info")
