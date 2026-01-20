plugins {
    id("net.neoforged.moddev") version "2.0.71"
}

neoForge {
    version = "21.1.88"
    // Disable parchment/mappings - we're using standard Java
    parchment {
        enabled = false
    }
}

// Configure JAR name
tasks.named<Jar>("jar") {
    archiveBaseName.set("chippedgraver-neoforge")
    archiveVersion.set("${project.version}")
}

// Include common source
sourceSets {
    main {
        java {
            srcDir("../common/src/main/java")
        }
        resources {
            srcDir("../common/src/main/resources")
        }
    }
}

dependencies {
    // NeoForge
    implementation("net.neoforged:neoforge:21.1.88")
    
    // Chipped dependency
    implementation("earth.terrarium.chipped:chipped-neoforge-1.21.1:4.0.2")
    
    // JEI - optional dependency for filter UI
    compileOnly("mezz.jei:jei-1.21.1-neoforge:19.27.0.340")
    runtimeOnly("mezz.jei:jei-1.21.1-neoforge:19.27.0.340")
}
