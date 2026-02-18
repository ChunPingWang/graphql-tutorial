plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))
}
