# square-biomes Client Pack

This folder contains the files intended for client-side distribution.

## Contents

- `square-biomes-1.0.0.jar`
- `square-biomes.json`
- `modrinth-description.md`
- `curseforge-description.md`

## Install

1. Install Fabric Loader for Minecraft `1.21.11`.
2. Install Fabric API.
3. Copy `square-biomes-1.0.0.jar` into your client's `mods` folder.
4. Start the game once.
5. Edit `config/square-biomes.json` if you want to change generation behavior.

## Build Helper

Run this from the project root to copy the built `square-biomes` jar into `distribution/client/mods` and `distribution/server/mods`:

```powershell
.\gradlew.bat prepareDistribution
```

## Notes

- This mod changes world generation.
- New worlds are recommended.
- Existing worlds can show visible borders between old and newly generated chunks.
- This mod only affects worlds using `MultiNoiseBiomeSource`.
