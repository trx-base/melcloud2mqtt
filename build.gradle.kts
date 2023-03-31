buildscript {
    repositories {
        mavenCentral() // or gradlePluginPortal()
    }
    dependencies {
        classpath("com.dipien:semantic-version-gradle-plugin:1.3.0")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    id("org.jetbrains.kotlin.kapt") version "1.6.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.minimal.application") version "3.6.2"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

version = "0.1.0"
group = "melcloud2mqtt"

apply(plugin = "com.dipien.semantic-version")

val kotlinVersion = project.properties.get("kotlinVersion")
repositories {
    mavenCentral()
}

dependencies {
    kapt("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    runtimeOnly("ch.qos.logback:logback-classic")
    implementation("io.micronaut:micronaut-validation")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Micronaut Http Client
    implementation("io.micronaut:micronaut-http-client")

    // MQTT Eclipse Paho
    implementation("org.eclipse.paho:org.eclipse.paho.mqttv5.client:1.2.5")

    // Kotlin Test
    testImplementation("io.mockk:mockk:1.12.7")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.1.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // Serde
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")

    // Management
    implementation("io.micronaut:micronaut-management")

    // Kotlin Logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
}

application {
    mainClass.set("ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    withType<Test> {
        if (name == "testNativeImage") {
            extensions.configure(kotlinx.kover.api.KoverTaskExtension::class) {
                isDisabled.set(true)
            }
        }
    }
}
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("*")
    }
}

kover {
    isDisabled.set(false)
    filters {
        classes {
            excludes += listOf("ApplicationKt")
        }
    }
    verify {
        onCheck.set(true)
        rule {
            isEnabled = true
            name = "Code coverage minimum requirement"

            bound { // add rule bound
                minValue = 100
                counter =
                    kotlinx.kover.api.CounterType.LINE // change coverage metric to evaluate (LINE, INSTRUCTION, BRANCH)
                valueType =
                    kotlinx.kover.api.VerificationValueType.COVERED_PERCENTAGE // change counter value (COVERED_COUNT, MISSED_COUNT, COVERED_PERCENTAGE, MISSED_PERCENTAGE)
            }
            bound { // add rule bound
                minValue = 100
                counter =
                    kotlinx.kover.api.CounterType.BRANCH // change coverage metric to evaluate (LINE, INSTRUCTION, BRANCH)
                valueType =
                    kotlinx.kover.api.VerificationValueType.COVERED_PERCENTAGE // change counter value (COVERED_COUNT, MISSED_COUNT, COVERED_PERCENTAGE, MISSED_PERCENTAGE)
            }
        }
    }
}
