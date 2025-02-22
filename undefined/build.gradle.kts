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
