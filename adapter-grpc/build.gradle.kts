plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.protobuf")
}

dependencies {
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.api)
    implementation(libs.protobuf.java)
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))
}

dependencyManagement {
    imports {
        mavenBom(libs.grpc.bom.get().toString())
        mavenBom(libs.protobuf.bom.get().toString())
    }
}

protobuf {
    protoc {
        // Version managed in gradle/libs.versions.toml
        artifact = "com.google.protobuf:protoc:4.29.3"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.69.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }
}
