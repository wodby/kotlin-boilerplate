import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.logback.classic)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(kotlin("test"))
}

// Compile for Java 21 so the application runs on every JDK the Wodby Kotlin
// service offers, whichever JDK builds it.
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

application {
    mainClass = "com.example.ApplicationKt"
    applicationName = "app"
    // The JVM's default heap is a quarter of the container's memory limit;
    // a container that runs nothing but this application can give it more.
    applicationDefaultJvmArgs = listOf("-XX:MaxRAMPercentage=75")
}

// installDist writes the application to build/install/app with bin/app as its
// start script, which the Dockerfile copies into the image.
distributions {
    main {
        distributionBaseName = "app"
    }
}

tasks.test {
    useJUnitPlatform()
}
