plugins { `java-library` }

dependencies {
    api("org.slf4j:slf4j-api:2.0.13")
    compileOnly("org.jetbrains:annotations:24.1.0")
    implementation("com.github.f4b6a3:uuid-creator:6.1.0")
}
