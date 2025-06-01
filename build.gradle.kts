import proguard.gradle.ProGuardTask

plugins {
    `jvm-toolchains`
    kotlin("jvm") version "2.2.0-RC"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    kotlin("plugin.serialization") version "2.1.20"
    id("org.jetbrains.dokka") version "2.0.0"

}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.7.0")
    }
}

group = "com.sybsuper"
version = project.findProperty("version")?.toString() ?: "unknown"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.charleskorn.kaml:kaml:0.78.0")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

java {
    toolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks {
    runServer {
        minecraftVersion("1.21.5")
        jvmArgs("-XX:+AllowEnhancedClassRedefinition")
    }

    build {
        dependsOn("shadowJar", "proguardJar")
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        minimize()
    }

    register<ProGuardTask>("proguardJar") {
        dontwarn()
        dontoptimize()
        verbose()
        libraryjars(
            mapOf(
                "jarFilter" to "!**.jar",
                "filter" to "!module-info.class"
            ), "${System.getProperty("java.home")}/jmods/java.base.jmod"
        )
        libraryjars(configurations.compileClasspath.get().files)
        keepattributes("*Annotation*")
        keep("public class com.sybsuper.sybsafetyfirst.** { @** *; }")
        injars(shadowJar.get().archiveFile)
        outjars("${layout.buildDirectory.get()}/libs/${project.name}-${project.version}-min.jar")
    }
}
