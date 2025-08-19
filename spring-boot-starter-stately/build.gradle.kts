plugins { `java-library` }

dependencies {
    api(project(":stately-core"))
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.3.2")
    implementation("org.springframework:spring-tx:6.1.10")
    implementation("io.micrometer:micrometer-core:1.13.1")
    compileOnly("org.springframework.boot:spring-boot-configuration-processor:3.3.2")
}
