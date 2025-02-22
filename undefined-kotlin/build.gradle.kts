plugins {
    id("common.library-conventions")
    alias(libs.plugins.kotlin)
}

dependencies {
    api(project(":undefined"))

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.jackson.parameter.names)
}
