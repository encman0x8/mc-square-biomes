# square-biomes

`square-biomes` turns Minecraft biome generation into a strict square grid on Fabric for Minecraft `1.21.11`.

Instead of organic biome borders, the overworld is divided into clean square cells. Each cell resolves to a single biome, giving the world a grid-like, map-art style layout. The mod also supports strong biome randomization, so the overworld can feel much less climate-driven than vanilla.

## Features

- Square biome borders
- Configurable biome square size
- Configurable sampling jitter inside each square
- Configurable random biome chance
- Extra randomization applies only to the overworld
- Nether and End excluded from random overworld selection
- Uniform random selection across unique overworld biomes

## Default Settings

- `gridSizeBlocks`: `128`
- `cellJitter`: `0.85`
- `randomBiomeChance`: `0.8`

## Requirements

- Minecraft `1.21.11`
- Fabric Loader
- Fabric API
- Java `21`

## Notes

- Best used on new worlds
- Existing worlds may show hard transitions between old and newly generated chunks
