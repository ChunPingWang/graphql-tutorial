plugins {
    id("org.springframework.boot") version "3.4.1" apply false
    id("com.google.protobuf") version "0.9.4" apply false
}

allprojects {
    group = "com.poc.apistyles"
    version = "1.0.0-SNAPSHOT"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-test-fixtures")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        "compileOnly"("org.projectlombok:lombok:1.18.36")
        "annotationProcessor"("org.projectlombok:lombok:1.18.36")
        "testCompileOnly"("org.projectlombok:lombok:1.18.36")
        "testAnnotationProcessor"("org.projectlombok:lombok:1.18.36")
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        systemProperty("spring.main.allow-bean-definition-overriding", "true")
    }
}
