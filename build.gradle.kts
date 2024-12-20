plugins {
    id("java")
    id("org.springframework.boot") version "3.1.2"
}

group = "edjon.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    runtimeOnly("com.h2database:h2:2.2.220")

    implementation("org.modelmapper:modelmapper:3.1.1")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.bootRun {
    args("--spring.profiles.active=local")
}