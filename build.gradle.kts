plugins {
    java
}

allprojects {
    group = "io.jstatesman"
    version = "0.1.0-SNAPSHOT"

    repositories { mavenCentral() }
}

subprojects {
    apply(plugin = "java")
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = "21"
        targetCompatibility = "21"
        options.release.set(21)
    }
    tasks.test { useJUnitPlatform() }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    }
}
