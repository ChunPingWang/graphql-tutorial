plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(libs.spring.boot.starter.websocket)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))
}
