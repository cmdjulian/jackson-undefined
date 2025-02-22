plugins {
    `java-library`
    `maven-publish`
    id("com.javiersc.semver")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier = "sources"
    from(sourceSets.main.get().allSource)
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

semver {
    tagPrefix = "v"
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/cmdjulian/jackson-undefined")
            credentials {
                username = project.findProperty("gpr.user") as? String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as? String? ?: System.getenv("TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>(project.name) {
            groupId = "de.cmdjulian"
            artifactId = "jackson-${project.name}"
            version = project.version.toString()

            from(components["java"])
            artifact(tasks["sourcesJar"])

            pom {
                packaging = "jar"
                name = "jackson-${project.name}"
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
