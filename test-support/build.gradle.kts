plugins {
    id("java-library")
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
    api(platform(libs.testcontainers.bom))
    api(libs.testcontainers.core)
    api(libs.testcontainers.postgresql)
    api(libs.testcontainers.junit.jupiter)

    api(libs.spring.boot.starter.test)
    api(libs.spring.boot.starter.data.jpa)
    api(libs.spring.boot.starter.data.redis)
    api("org.springframework.boot:spring-boot-testcontainers")

    api(libs.junit.jupiter)
    api(libs.assertj.core)
    api(libs.mockito.core)
    api(libs.mockito.junit.jupiter)

    api(project(":domain"))
}
