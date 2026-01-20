plugins {
    id("java")
}

allprojects {
    group = "com.chippedgraver"
    version = project.findProperty("mod_version") as String? ?: "1.0.0"
}

subprojects {
    apply(plugin = "java")
    
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
            vendor = JvmVendorSpec.ADOPTIUM
        }
    }
    
    repositories {
        mavenCentral()
        maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
        maven(url = "https://maven.fabricmc.net/")
        maven(url = "https://maven.neoforged.net/releases/")
    }
}

// Common module should not compile on its own - it's compiled by loader modules
project(":common").tasks.withType<JavaCompile>().configureEach {
    enabled = false
}
