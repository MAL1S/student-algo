import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("maven-publish")
    application
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.apache.poi:poi-ooxml:5.2.2")
    implementation("org.apache.commons:commons-csv:1.9.0")
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.grapecity.documents:gcexcel:5.0.3")
    testImplementation(kotlin("test"))

    implementation("ru.malis:test:1.2")
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "ru.student.distribution"
            artifactId = "StudentDistributionAlgorithm"
            version = "1.0.0"

            from(components["java"])
        }
    }
}
