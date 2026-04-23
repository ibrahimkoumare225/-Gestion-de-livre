plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"

    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.7"

    // couverture
    jacoco

    // mutation testing
    id("info.solidsoft.pitest") version "1.15.0"

    // code quality
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

group = "fr.koumare"
version = "0.0.1-SNAPSHOT"
description = "Gestion de livre"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    sourceSets {
        create("testArchitecture") {
            kotlin.srcDir("src/testArchitecture/kotlin")
            resources.srcDir("src/testArchitecture/resources")
            compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        }
        create("testComponent") {
            kotlin.srcDir("src/testComponent/kotlin")
            resources.srcDir("src/testComponent/resources")
            compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        }
        create("testIntegration") {
            kotlin.srcDir("src/testIntegration/kotlin")
            resources.srcDir("src/testIntegration/resources")
            compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.liquibase:liquibase-core")
    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.pitest:pitest-junit5-plugin:1.2.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Test d'intégration
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-framework-datatest:5.9.1")
    testImplementation("io.kotest:kotest-property:5.9.1")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")

    // Architecture tests
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")

    // Component tests (Cucumber)
    testImplementation("io.cucumber:cucumber-java:7.18.0")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.18.0")
    testImplementation("io.cucumber:cucumber-spring:7.18.0")
    testImplementation("io.rest-assured:rest-assured:5.4.0")

    // Property-based testing
    testImplementation("net.jqwik:jqwik:1.8.1")
    testImplementation("net.jqwik:jqwik-kotlin:1.8.1")
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

tasks.register<Test>("testArchitecture") {
    description = "Runs architecture tests"
    group = "verification"
    useJUnitPlatform {
        includeTags("archunit")
    }
    testClassesDirs = sourceSets["testArchitecture"].output.classesDirs
    classpath = sourceSets["testArchitecture"].runtimeClasspath
    finalizedBy(tasks.jacocoTestReport)
}

tasks.register<Test>("testComponent") {
    description = "Runs component tests"
    group = "verification"
    useJUnitPlatform {
        includeTags("cucumber")
    }
    testClassesDirs = sourceSets["testComponent"].output.classesDirs
    classpath = sourceSets["testComponent"].runtimeClasspath
    finalizedBy(tasks.jacocoTestReport)
}

tasks.register<Test>("testIntegration") {
    description = "Runs integration tests"
    group = "verification"
    useJUnitPlatform {
        includeTags("integration")
    }
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    finalizedBy(tasks.jacocoTestReport)
}



jacoco {
    toolVersion = "0.8.10"
}

detekt {
    toolVersion = "1.23.5"
    ignoreFailures = true
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