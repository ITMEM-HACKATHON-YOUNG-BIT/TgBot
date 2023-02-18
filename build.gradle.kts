plugins {
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
    id("de.jensklingenberg.ktorfit") version "1.0.0"
    kotlin("plugin.serialization") version "1.8.0"
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.sniffers"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-logging-jvm:2.2.3")
    val ktorVersion = "2.2.3"
    val tgBotVersion = "5.2.0"
    val ktorfitVersion = "1.0.0-beta18"
    ksp("de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")
    implementation("dev.inmo:tgbotapi:$tgBotVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-properties:1.3.0")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    val slf4jVersion = "2.0.6"
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("io.github.oshai:kotlin-logging-jvm:4.0.0-beta-11")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}

tasks.jar {
    manifest.attributes["Main-Class"] = "MainKt"
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}