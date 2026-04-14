plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"

    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.7"

    // couverture
    jacoco

    // mutation testing
    id("info.solidsoft.pitest") version "1.15.0"
}

group = "fr.koumare"
version = "0.0.1-SNAPSHOT"
description = "Gestion de livre"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")


    testImplementation("org.pitest:pitest-junit5-plugin:1.2.1")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}



jacoco {
    toolVersion = "0.8.10"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}



pitest {
    targetClasses.set(listOf("fr.koumare.gestion_de_livre.*"))
    targetTests.set(listOf("fr.koumare.gestion_de_livre.*"))
    threads.set(2)
    outputFormats.set(listOf("HTML"))

    junit5PluginVersion.set("1.2.1")
}