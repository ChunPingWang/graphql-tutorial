plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web-services")
    implementation("org.springframework.ws:spring-ws-core")
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))
}
