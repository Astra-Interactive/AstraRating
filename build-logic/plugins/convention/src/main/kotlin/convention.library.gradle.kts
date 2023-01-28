plugins{
    java
    `java-library`
}

java {
    withJavadocJar()
    withSourcesJar()
}
sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/kotlin"))
        }

        resources {
            setSrcDirs(listOf("src/resources"))
        }
    }
}
sourceSets.named("main") {
    java.srcDir("src/main/kotlin")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        this.showStandardStreams = true
        ignoreFailures = true
    }
}
