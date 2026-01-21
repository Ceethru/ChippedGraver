# Chipped Graver

A Minecraft mod that adds a tool to randomize Chipped mod blocks. Right-click on any Chipped block to randomly switch it to another variant from the same source block group. Includes an advanced filter system to control which blocks can be randomized to.

## Features

- **Randomizer Tool**: A tool that randomizes Chipped blocks within their variant group
- **Filter System**: Define custom palettes of blocks for randomization by dragging blocks from JEI
- **Grouped Display**: Filter UI automatically groups blocks by source type (e.g., all brick variants together)
- **Source Block Display**: Automatically shows the base Minecraft block for each Chipped variant group
- **Multi-Loader Support**: Works with both Fabric and NeoForge
- **JEI Integration**: Full drag-and-drop support from Just Enough Items
- **Property Preservation**: Maintains block properties like rotation and waterlogged state when randomizing

## Requirements

- Minecraft 1.21.1
- [Chipped Mod](https://github.com/terrarium-earth/Chipped) (version 4.0.2+)
- Fabric Loader 0.16.9+ (for Fabric version)
- NeoForge 21.1.88+ (for NeoForge version)
- [Just Enough Items (JEI)](https://www.curseforge.com/minecraft/mc-mods/jei) (optional, but recommended for filter UI)

## How to Use

### Crafting

Craft the Randomizer Tool using:
- 1x Chipped Chisel (`chipped:chisel`)
- 1x Observer (`minecraft:observer`)

Place both items anywhere in a crafting table (shapeless recipe).

### Basic Usage

1. **Right-click** on any Chipped block (e.g., any variant of oak planks or bricks)
   - The block will randomly change to another variant from the same group
   - Respects filter settings if filters are configured
   - Source blocks (original Minecraft blocks) are never included in randomization
2. **Shift + Right-click** on a Chipped block
   - Converts the block back to its original Minecraft source block
   - Useful for reverting changes or resetting blocks
3. The tool has infinite durability and does not take damage on use

### Filter System

1. Right-click in the air while holding the Randomizer Tool to open the filter menu
2. Drag blocks from JEI into the filter slots to add them to your palette
3. Right-click on filter slots to remove blocks
4. Click "Clear All" to remove all filters

**How Filters Work:**
- Each block type (e.g., bricks, oak planks) can have its own filter list
- If a block type has filters set, only blocks in that filter can be randomized to
- If a block type has no filters, all blocks in that Chipped tag group can be randomized to
- The filter UI automatically groups blocks by source type and shows the base Minecraft block first
- Source blocks (original Minecraft blocks) are displayed for reference but are never included in randomization
- Use Shift + Right-click to convert a block back to its source if needed

**Example:**
- Add "Massive Bricks Brick" to the filter → `minecraft:bricks` (source) appears automatically, then a spacer, then your filtered variant
- Add "Fine Oak Planks" → `minecraft:oak_planks` (source) appears on a new row, then a spacer, then your filtered variant

## Building

```bash
# Build for Fabric
./gradlew :fabric:build

# Build for NeoForge
./gradlew :neoforge:build
```

## Installation

1. Install Minecraft 1.21.1
2. Install the appropriate mod loader (Fabric or NeoForge)
3. Install the Chipped mod
4. Place the compiled mod JAR in your mods folder

## Version

Current Version: **1.0.1**

### Changelog

#### 1.0.1
- Added filter system with JEI drag-and-drop support
- Updated recipe to use Chipped Chisel + Observer
- Improved UI with grouped block display and source block indicators
- Added tooltips to filter slots
- Added Shift + Right-click to convert blocks back to their source block
- Removed "Include Source Blocks" checkbox (use Shift + Right-click instead)
- Fixed filter synchronization between client and server using network packets
- Removed durability system (tool has infinite uses)

#### 1.0.0
- Initial release
- Basic randomization functionality

## Author

**Ceethru**

## License

MIT License
