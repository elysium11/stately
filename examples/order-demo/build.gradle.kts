plugins {
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.5"
    java
}

val lombokVer = "1.18.38"
dependencies {
    compileOnly("org.projectlombok:lombok:$lombokVer")
    annotationProcessor("org.projectlombok:lombok:$lombokVer")
    implementation(project(":stately-core"))
    implementation(project(":spring-boot-starter-stately"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.flywaydb:flyway-core:11.11.0")
    implementation("org.flywaydb:flyway-database-postgresql:11.11.0")
    implementation("com.google.guava:guava:33.4.8-jre")

    implementation("org.postgresql:postgresql:42.7.4")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.testcontainers:postgresql:1.21.3")
    testImplementation("org.assertj:assertj-core:3.27.4")
    testCompileOnly("org.projectlombok:lombok:$lombokVer")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVer")
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "21"
    targetCompatibility = "21"
    options.release.set(21)
}
