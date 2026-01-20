plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// Common module source will be compiled by loader-specific modules
// Disable compilation here - it will be compiled with proper mappings by Fabric/NeoForge
tasks.withType<JavaCompile>().configureEach {
    enabled = false
}
