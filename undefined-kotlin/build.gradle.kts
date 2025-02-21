plugins {
    id("common.library-conventions")
    alias(libs.plugins.kotlin)
}

dependencies {
    api(project(":undefined"))

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.jackson.parameter.names)
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("jackson-undefined-kt") {
            groupId = "de.cmdjulian"
            artifactId = "jackson-undefined-kotlin"
            version = project.version.toString()

            from(components["java"])
            artifact(tasks["sourcesJar"])

            pom {
                packaging = "jar"
                name = "jackson-undefined"
                description = "undefined wrapper for Jackson"
                url = "https://github.com/cmdjulian/jackson-undefined"
                scm {
                    url = "https://github.com/cmdjulian/jackson-undefined"
                }
                issueManagement {
                    url = "https://github.com/cmdjulian/jackson-undefined/issues"
                }
                developers {
                    developer {
                        id = "cmdjulian"
                        name = "Julian Goede"
                    }
                }
            }
        }
    }
}
