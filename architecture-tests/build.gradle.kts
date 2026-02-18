plugins {
    id("java-test-fixtures")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    testImplementation(project(":domain"))
    testImplementation(project(":application"))
    testImplementation(project(":infrastructure"))
    testImplementation(project(":adapter-rest"))
    testImplementation(project(":adapter-graphql"))
    testImplementation(project(":adapter-websocket"))
    testImplementation(project(":adapter-soap"))
    testImplementation(project(":test-support"))

    testImplementation("com.tngtech.archunit:archunit:1.3.0")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
    testImplementation("jakarta.inject:jakarta.inject-api:2.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter")
}
