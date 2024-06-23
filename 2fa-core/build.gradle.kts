/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    java

    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.lielamar.java-conventions")
}

dependencies {
    implementation("org.json:json:20240303")

    annotationProcessor("io.micronaut:micronaut-inject-java:4.5.3")
    implementation("io.micronaut:micronaut-inject-java:4.5.3")

    compileOnly("commons-codec:commons-codec:1.17.0")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.h2database:h2:2.2.224")
    implementation("com.mysql:mysql-connector-j:8.4.0")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.0")
    implementation("org.mongodb:mongodb-driver-sync:5.1.1")

    implementation(project(":2fa-api"))
    implementation(project(":2fa-bukkit"))
}

description = "2fa-core"
