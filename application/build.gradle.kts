plugins {
    `java-library`
}

tasks.named<Jar>("jar") {
    enabled = true
}

dependencies {
    api(project(":domain"))

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
}
