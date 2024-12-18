val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.0"
}

group = "com.kot"
version = "0.0.1"

repositories {
    mavenCentral()
}
