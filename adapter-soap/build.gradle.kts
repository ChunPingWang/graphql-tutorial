plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(libs.spring.boot.starter.web.services)
    implementation(libs.spring.ws.core)
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))
}
