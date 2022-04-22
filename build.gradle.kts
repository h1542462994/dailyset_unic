import com.google.protobuf.gradle.*

plugins {
    id("org.springframework.boot") version "2.6.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    java
    idea
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    id("com.google.protobuf") version "0.8.18"
}

group = "org.tty.dailyset"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_11

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.18")
    }
}

sourceSets {
    main {
        proto {
            srcDir("src/main/proto")
        }
    }
}


repositories {
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    implementation("io.grpc:grpc-netty:1.45.1")
    implementation("com.google.protobuf:protobuf-java:3.20.0")
    implementation("io.grpc:grpc-stub:1.45.1")
    implementation("io.grpc:grpc-protobuf:1.45.1")
    implementation("net.devh:grpc-spring-boot-starter:2.13.1.RELEASE") {
        exclude("io.grpc:grpc-netty-shaded")
    }

    runtimeOnly("mysql:mysql-connector-java")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.getByName("clean").apply {
    delete(protobuf.protobuf.generatedFilesBaseDir)
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.6.1"
    }
    plugins {
        // Optional: an artifact spec for a protoc plugin, with "grpc" as
        // the identifier, which can be referred to in the "plugins"
        // container of the "generateProtoTasks" closure.
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.15.1"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                // Apply the "grpc" plugin whose spec is defined above, without options.
                id("grpc")
            }
        }
    }


}

