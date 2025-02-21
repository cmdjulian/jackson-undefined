plugins {
    `java-library`
    `maven-publish`
    id("com.javiersc.semver")
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
}

semver {
    tagPrefix = "v"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
