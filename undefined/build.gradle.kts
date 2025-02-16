plugins {
    `java-library`
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
