import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// variable definitions use extra delegate property.
val krotoPlusVersion: String by extra
val coroutinesVersion: String by extra
val grpcVersion: String by extra
val protobufVersion: String by extra

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
    // delegate property to register the variable to extra.
    @Suppress("UNUSED_VARIABLE") val krotoPlusVersion by extra { "0.6.1" }
    @Suppress("UNUSED_VARIABLE") val coroutinesVersion by extra { "1.6.1" }
    @Suppress("UNUSED_VARIABLE") val grpcVersion by extra { "1.45.1" }
    @Suppress("UNUSED_VARIABLE") val protobufVersion by extra { "3.20.0" }

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
        java {
            srcDir("${buildDir}/generated/source/proto/main/java")
            srcDir("${buildDir}/generated/source/proto/main/kotlin")
        }
        kotlin {

        }
    }

}


repositories {
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/public")
    maven("https://dl.bintray.com/marcoferrer/kroto-plus/")
    mavenCentral()

    maven("https://plugins.gradle.org/m2/")
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    implementation("io.grpc:grpc-netty:${grpcVersion}")
    implementation("com.google.protobuf:protobuf-java:${protobufVersion}")
    implementation("io.grpc:grpc-stub:${grpcVersion}")
    implementation("io.grpc:grpc-protobuf:${grpcVersion}")
    implementation("net.devh:grpc-spring-boot-starter:2.13.1.RELEASE") {
        exclude("io.grpc:grpc-netty-shaded")
    }
    implementation("com.github.marcoferrer.krotoplus:kroto-plus-coroutines:${krotoPlusVersion}")
    implementation("com.github.marcoferrer.krotoplus:kroto-plus-message:${krotoPlusVersion}")

    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.0")
    implementation("org.mybatis:mybatis:3.5.7")


    runtimeOnly("mysql:mysql-connector-java")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xjsr305=strict",
//            "-Xuse-experimental=kotlinx.coroutines.ObsoleteCoroutimesApi"
        )
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
        artifact = "com.google.protobuf:protoc:${protobufVersion}"
    }
    plugins {
        // Optional: an artifact spec for a protoc plugin, with "grpc" as
        // the identifier, which can be referred to in the "plugins"
        // container of the "generateProtoTasks" closure.
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
        }
        id("coroutines") {
            artifact = "com.github.marcoferrer.krotoplus:protoc-gen-grpc-coroutines:${krotoPlusVersion}:jvm8@jar"
        }
        id("kroto") {
            artifact = "com.github.marcoferrer.krotoplus:protoc-gen-kroto-plus:$krotoPlusVersion:jvm8@jar"
        }
    }
    generateProtoTasks {
        // use configFile krotoPlusConfig
        val krotoConfig = file("src/main/resources/krotoPlusConfig.json")

        ofSourceSet("main").forEach {
            it.inputs.files(krotoConfig)
            it.plugins {
                // Apply the "grpc" plugin whose spec is defined above, without options.
                id("grpc") {}
                id("coroutines") {}
                id("kroto") {
                    // warning: must allocate java.
                    outputSubDir = "java"
                    // warning: must use path string with it. FIXME: use relative path instead. @cht 2022/4/23
                    option("ConfigPath=src/main/resources/krotoPlusConfig.json")
                }
            }
        }
    }


}

