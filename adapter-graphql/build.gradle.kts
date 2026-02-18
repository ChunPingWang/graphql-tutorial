plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(libs.spring.boot.starter.graphql)
    implementation(libs.spring.boot.starter.web)
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))
}
