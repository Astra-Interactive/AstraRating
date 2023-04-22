
import gradle.kotlin.dsl.accessors._64acc05bf1a66f2c855e386526b4bcff.main
import gradle.kotlin.dsl.accessors._64acc05bf1a66f2c855e386526b4bcff.sourceSets
import gradle.kotlin.dsl.accessors._64acc05bf1a66f2c855e386526b4bcff.test
import org.gradle.ide.visualstudio.tasks.internal.RelativeFileNameTransformer

plugins {
    java
    `java-library`
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = libs.versions.group.get()
version = libs.versions.plugin.get()
description = libs.versions.description.get()

java {
    withSourcesJar()
    withJavadocJar()
    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_17
}


tasks {
    withType<JavaCompile>() {
        options.encoding = "UTF-8"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
    withType<Jar> {
        archiveClassifier.set("min")
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            this.showStandardStreams = true
            ignoreFailures = true
        }
    }
}