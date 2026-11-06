plugins {
    kotlin("jvm")
    alias(libs.plugins.klibs.gradle.java.version)
    alias(libs.plugins.klibs.rules.gradle.linter)
    `java-test-fixtures`
}

linter {
    unit(ru.astrainteractive.klibs.rules.core.unit.model.UnitRole.StorageContract())
    rootPackage.set("ru.astrainteractive.astrarating.data.dao")
}

dependencies {
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.tests.kotlin.test)
}
