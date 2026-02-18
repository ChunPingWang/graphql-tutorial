plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation("net.devh:grpc-spring-boot-starter:3.1.0")
    implementation("io.grpc:grpc-protobuf:1.69.0")
    implementation("io.grpc:grpc-stub:1.69.0")
    implementation("io.grpc:grpc-api:1.69.0")
    implementation(project(":application"))
    implementation(project(":infrastructure"))
}
