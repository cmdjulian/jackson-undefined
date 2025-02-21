plugins {
    id("common.library-conventions")
}

dependencies {
    api(libs.jspecify)
    implementation(libs.bundles.jackson)

    testImplementation(libs.assertj)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.jackson.parameter.names)
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("jackson-undefined") {
            groupId = "de.cmdjulian"
            artifactId = "jackson-undefined"
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
