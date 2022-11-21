import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "me.m4l15"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.poi:poi-ooxml:3.9")
    implementation("org.apache.commons:commons-csv:1.5")
    implementation("com.google.code.gson:gson:2.8.2")
    implementation("com.grapecity.documents:gcexcel:2.1.0")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.6.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}