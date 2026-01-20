plugins {
    id("fabric-loom") version "1.7-SNAPSHOT"
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
    minecraft("com.mojang:minecraft:1.21.1")
    mappings("net.fabricmc:yarn:1.21.1+build.1:v2")
    
    modImplementation("net.fabricmc:fabric-loader:0.16.9")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.109.0+1.21.1")
    
    // Chipped dependency
    modImplementation("earth.terrarium.chipped:chipped-fabric-1.21.1:4.0.2")
}
