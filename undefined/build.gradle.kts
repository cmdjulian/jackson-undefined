plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.jspecify)
    implementation(libs.bundles.jackson)

    testImplementation(libs.assertj)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.jackson.parameter.names)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/OWNER/REPOSITORY")
            credentials {
                username = project.findProperty("gpr.user") as? String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as? String? ?: System.getenv("TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("jackson-undefined") {
            groupId = "de.cmdjulian"
            artifactId = "jackson-undefined"
            version = "1.0.0"

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
