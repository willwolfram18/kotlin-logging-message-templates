plugins {
    kotlin("jvm") version "2.3.0"
}

group = "com.willwolfram18.extensions.kotlinlogging"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val jacksonVersion = "2.15.2"

    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core:6.0.5")
    testImplementation("org.junit.jupiter:junit-jupiter")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}