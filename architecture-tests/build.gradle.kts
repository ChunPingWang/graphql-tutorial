plugins {
    id("java-test-fixtures")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}
tasks.named<Jar>("jar") {
    enabled = true
}

dependencies {
    testImplementation(project(":domain"))
    testImplementation(project(":application"))
    testImplementation(project(":infrastructure"))
    testImplementation(project(":adapter-rest"))
    testImplementation(project(":adapter-graphql"))
    testImplementation(project(":adapter-grpc"))
    testImplementation(project(":adapter-websocket"))
    testImplementation(project(":adapter-soap"))
    testImplementation(project(":test-support"))

    testImplementation(libs.archunit)
    testImplementation(libs.archunit.junit5)
    testImplementation(libs.jakarta.inject.api)
    testImplementation(libs.junit.jupiter)
}
