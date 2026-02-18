plugins {
    id("java-library")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    api("org.testcontainers:testcontainers:1.20.4")
    api("org.testcontainers:postgresql:1.20.4")
    api("org.testcontainers:junit-jupiter:1.20.4")

    api("org.springframework.boot:spring-boot-starter-test:3.4.1")
    api("org.springframework.boot:spring-boot-starter-data-jpa:3.4.1")
    api("org.springframework.boot:spring-boot-starter-data-redis:3.4.1")

    api("org.junit.jupiter:junit-jupiter:5.11.4")
    api("org.assertj:assertj-core:3.27.3")
    api("org.mockito:mockito-core:5.15.2")
    api("org.mockito:mockito-junit-jupiter:5.15.2")

    api(project(":domain"))

    implementation("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}
