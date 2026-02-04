# Better Interaction

> A comprehensive Minecraft-style interaction system for Hytale, bringing familiar interaction mechanics to enhance player experience and server customization.

## Overview

**Better Interaction** is a Hytale server mod that implements a intuitive and familiar Minecraft-style interaction system. This mod allows players to interact with the game world in a natural way, leveraging established conventions that players already know and understand.

## Features

- 🎮 **Familiar Interaction Mechanics** - Implements well-known Minecraft interaction patterns
- 🎯 **Seamless Integration** - Works harmoniously with Hytale's game mechanics
- ⚙️ **Server Customization** - Easy configuration for server administrators
- 🎨 **Asset Pack Included** - Comes with optimized assets for visual consistency

## Requirements

- **Java Version**: 25 or higher
- **Hytale Server**: Version 2026.01.22 or compatible
- **Build Tool**: Apache Maven 3.6+

## Installation

### Prerequisites

Ensure you have the following installed:
- Java Development Kit (JDK) 25+
- Apache Maven 3.6+
- Hytale game server with mod support

### Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/Hytale-BetterInteraction.git
   cd Hytale-BetterInteraction
   ```

2. **Configure Maven**
   Update the Hytale mods folder path in `pom.xml` if necessary:
   ```xml
   <hytale.mods.folder>C:/Hytale/Hytale/release/package/game/latest/Client/UserData_sebold/Saves/New World/mods</hytale.mods.folder>
   ```

3. **Build the Mod**
   ```bash
   mvn clean package
   ```
   The compiled JAR will be automatically deployed to your Hytale mods folder.

4. **Launch Hytale Server**
   Start your Hytale server, and the mod will be loaded automatically.

## Project Structure

```
Hytale-BetterInteraction/
├── src/
│   └── main/
│       └── resources/
│           └── manifest.json        # Mod configuration and metadata
├── pom.xml                          # Maven build configuration
└── README.md                        # This file
```

## Configuration

### Manifest File

Configure the mod behavior through `src/main/resources/manifest.json`:

```json
{
  "Group": "vinisebold",
  "Name": "BetterInteraction",
  "Version": "1.0.0",
  "Description": "Brings familiar Minecraft-style interaction system to Hytale.",
  "IncludesAssetPack": true,
  "Main": "dev.vinisebold.Main"
}
```

## Development

### Building from Source

```bash
# Clean previous builds
mvn clean

# Compile and package
mvn package

```

### Dependencies

The project includes a direct dependency on the Hytale Server API:

```xml
<dependency>
    <groupId>com.hypixel.hytale</groupId>
    <artifactId>Server</artifactId>
    <version>2026.01.22-6f8bdbdc4</version>
    <scope>provided</scope>
</dependency>
```

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | n/a | Initial release |


## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

**Vini Sebold** - Initial development
- GitHub: [@vinisebold](https://github.com/vinisebold)

## Support

For bug reports, feature requests, or questions, please open an issue on the GitHub repository.

---
