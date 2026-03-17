plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.13"
//    kotlin("benchmark") version "0.4.13"
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
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("net.bytebuddy:byte-buddy:1.17.5")
    testImplementation("org.slf4j:slf4j-simple:2.0.3")
}

kotlin {
    jvmToolchain(25)
    sourceSets {
        test {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.13")
            }
        }
    }
}

benchmark {
    targets {
        register("test")
    }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs = jvmArgs + "-Dnet.bytebuddy.experimental=true"
}