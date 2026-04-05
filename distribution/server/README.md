# square-biomes Server Pack

This folder contains a simple dedicated server skeleton for `square-biomes`.

## Required Files

Place these files into your dedicated server directory:

- `mods/square-biomes-1.0.0.jar`
- `config/square-biomes.json`
- `server.properties`
- `eula.txt`
- `run.sh` or `run.bat`
- `install.sh`

## Additional Dependencies

You still need:

- Java `21`

## Setup

1. Copy this folder to your server.
2. Put `square-biomes-1.0.0.jar` in `mods/` if it is not already there.
3. Run `./install.sh`.
4. Review `server.properties`.
5. Set `eula=true` in `eula.txt` after accepting Mojang's EULA.
6. Start the server with `run.sh` on Linux or `run.bat` on Windows.

## Build Helper

Run this from the project root to copy the built `square-biomes` jar into the distribution pack:

```powershell
.\gradlew.bat prepareDistribution
```

## What install.sh Does

- Downloads the Fabric installer from Fabric's official metadata endpoint
- Installs the Fabric server for Minecraft `1.21.11`
- Downloads the official Minecraft server jar via the installer
- Downloads Fabric API into `mods/`

## Notes

- World generation mods should be enabled before creating the world.
- Changing settings later can create hard borders in newly generated chunks.
- The provided `server.properties` is only a skeleton and should be reviewed before production use.
- This mod only affects worlds using `MultiNoiseBiomeSource`.
