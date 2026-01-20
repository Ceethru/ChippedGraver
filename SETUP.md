# Setup Guide

## Prerequisites

- Java 21 or higher
- Gradle (will be downloaded automatically via wrapper)

## Initial Setup

1. Clone or navigate to this repository
2. Run the Gradle wrapper to set up the project:
   ```bash
   ./gradlew build
   ```

## Building the Mod

### For Fabric:
```bash
./gradlew :fabric:build
```
The JAR will be in `fabric/build/libs/`

### For NeoForge:
```bash
./gradlew :neoforge:build
```
The JAR will be in `neoforge/build/libs/`

## Running in Development

### Fabric:
```bash
./gradlew :fabric:runClient  # For client
./gradlew :fabric:runServer  # For server
```

### NeoForge:
```bash
./gradlew :neoforge:runClient  # For client
./gradlew :neoforge:runServer  # For server
```

## Adding a Texture

You need to create a texture for the randomizer tool:
- Location: `common/src/main/resources/assets/chippedgraver/textures/item/randomizer_tool.png`
- Size: 16x16 or 32x32 pixels
- Format: PNG with transparency

## Notes

- The mod requires the Chipped mod to be installed
- Make sure you have the correct Minecraft version (1.21.1)
- The tool has 256 durability by default
