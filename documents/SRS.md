# Software Requirements Specification

## 1. Overview

### 1.1 Product Name

`square-biomes`

### 1.2 Purpose

`square-biomes` is a Fabric mod for Minecraft `1.21.11` that changes biome generation so biome borders form a strict square grid.

### 1.3 Scope

The mod modifies biome selection for worlds that use `MultiNoiseBiomeSource`, primarily targeting the overworld. It provides configurable square size and configurable randomization behavior, plus distribution assets for both client and dedicated server usage.

## 2. Target Environment

### 2.1 Minecraft Version

- Minecraft `1.21.11`

### 2.2 Loader and Dependencies

- Fabric Loader `0.18.2`
- Fabric API `0.139.4+1.21.11`
- Java `21`

### 2.3 Packaging Identity

- Mod ID: `square-biomes`
- Maven group: `work.encman0x404`
- License: `MIT`

## 3. Functional Requirements

### 3.1 Biome Grid Transformation

- The mod shall transform biome boundaries into square grid-aligned regions.
- The mod shall operate by intercepting biome selection in `MultiNoiseBiomeSource`.
- Each grid cell shall resolve to a single biome result for its covered area.

### 3.2 Square Size Configuration

- The mod shall support a configurable biome square size using `gridSizeBlocks`.
- The default value shall be `128`.
- The minimum allowed value shall be `4`.

### 3.3 Cell Sampling Jitter

- The mod shall support configurable sampling jitter inside each square cell using `cellJitter`.
- The default value shall be `0.85`.
- The valid range shall be `0.0` to `1.0`.
- Higher values shall increase per-cell sampling variation while preserving square borders.

### 3.4 Random Biome Selection

- The mod shall support a configurable chance to ignore vanilla climate-based biome selection per cell using `randomBiomeChance`.
- The default value shall be `0.8`.
- The valid range shall be `0.0` to `1.0`.
- At `0.0`, cells shall use climate-based square sampling only.
- At `1.0`, overworld cells shall always use random biome selection from the available overworld biome set.

### 3.5 Overworld-Only Randomization

- Additional random biome selection shall apply only to overworld `MultiNoiseBiomeSource`.
- Nether randomization shall be skipped.
- End randomization shall be skipped.

### 3.6 Uniform Random Biome Selection

- When random biome selection is used, the mod shall select from unique overworld biome entries.
- Duplicate climate-weighted entries shall not bias random selection.

### 3.7 Configuration File

- The mod shall create `config/square-biomes.json` automatically if it does not exist.
- The configuration file shall contain:
  - `gridSizeBlocks`
  - `cellJitter`
  - `randomBiomeChance`
- Invalid values shall be normalized and written back to the config file.

### 3.8 Runtime Logging

- The mod shall log initialization settings on startup.
- The mod shall log detected `MultiNoiseBiomeSource` behavior once per source category.
- The source categories shall include:
  - overworld
  - nether
  - other non-overworld `MultiNoiseBiomeSource`

## 4. Compatibility Requirements

### 4.1 Supported Worldgen Path

- The mod shall only target worlds using `MultiNoiseBiomeSource`.
- The mod shall not guarantee behavior for custom biome sources outside `MultiNoiseBiomeSource`.

### 4.2 Mod Compatibility

- Mods that extend vanilla-style overworld biome generation through `MultiNoiseBiomeSource` should generally remain compatible.
- Mods that replace biome generation with a custom biome source or custom world preset may bypass this mod.

### 4.3 World Compatibility

- New worlds are the recommended use case.
- Using the mod on an existing world may create visible borders between old and newly generated chunks.
- Changing configuration values after world creation may also create visible borders in newly generated chunks.

### 4.4 Loader Compatibility

- The mod is Fabric-only.
- Forge and other non-Fabric loaders are out of scope.

## 5. Non-Functional Requirements

### 5.1 Build System

- The project shall use Gradle with Fabric Loom.
- The standard build output jar shall be generated at `build/libs/square-biomes-1.0.0.jar`.

### 5.2 Source Organization

- Java packages shall use the namespace `work.encman0x404.squarebiomes`.

### 5.3 Git Hygiene

- Local runtime files, Gradle caches, generated build outputs, and copied distribution jars shall not be committed.

## 6. Distribution Requirements

### 6.1 Client Distribution

- The project shall provide a client distribution folder at `distribution/client`.
- The client distribution shall include:
  - client README
  - sample config
  - Modrinth description
  - CurseForge description
  - `mods/` folder for copied mod jar

### 6.2 Server Distribution

- The project shall provide a server distribution folder at `distribution/server`.
- The server distribution shall include:
  - server README
  - `config/square-biomes.json`
  - `server.properties` skeleton
  - `eula.txt` skeleton
  - `run.sh`
  - `run.bat`
  - `install.sh`
  - `mods/` folder for copied mod jar

### 6.3 Server Installation Script

- `distribution/server/install.sh` shall:
  - download the Fabric installer from Fabric official metadata
  - install a Fabric server for Minecraft `1.21.11`
  - download the official Minecraft server via the Fabric installer
  - download Fabric API into `mods/`
- The script shall require `java`, `curl`, and `python3` or `python`.

### 6.4 Distribution Build Tasks

- The project shall provide `prepareClientPack`.
- The project shall provide `prepareServerPack`.
- The project shall provide `prepareDistribution`.
- The project shall provide `zipServerPack`.

### 6.5 Server Zip Packaging

- `zipServerPack` shall output `build/distributions/server-1.21.11.zip`.

## 7. Operational Commands

### 7.1 Build

```powershell
.\gradlew.bat build
```

### 7.2 Prepare Distribution

```powershell
.\gradlew.bat prepareDistribution
```

### 7.3 Create Server Zip

```powershell
.\gradlew.bat zipServerPack
```

## 8. Out of Scope

- Forge or NeoForge compatibility
- Support for non-`MultiNoiseBiomeSource` biome generators
- Full world preset replacement
- Bundling third-party dependencies directly into repository-tracked distribution folders
