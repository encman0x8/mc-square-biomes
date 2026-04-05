# square-biomes

`square-biomes` is a Fabric mod for Minecraft `1.21.11` that forces biome generation into a strict square grid.

Each biome is assigned per grid cell, so biome borders become clean square lines instead of natural curves. The mod can also randomize biome selection heavily, making overworld maps feel more like a biome checkerboard while still keeping square boundaries.

## Requirements

- Minecraft `1.21.11`
- Fabric Loader
- Fabric API
- Java `21`

## Installation

1. Install Fabric Loader for Minecraft `1.21.11`.
2. Install Fabric API.
3. Copy the built `square-biomes-1.0.0.jar` into your Minecraft `mods` folder.
4. Start the game once to generate `config/square-biomes.json`.

## Configuration

The config file is created automatically at:

- `config/square-biomes.json`

Available options:

- `gridSizeBlocks`: Size of one biome square in blocks. Default: `128`
- `cellJitter`: How much the biome sampling point moves around inside each square. Default: `0.85`
- `randomBiomeChance`: Chance to ignore vanilla climate and pick an overworld biome uniformly at random per square. Default: `0.8`

Example:

```json
{
  "gridSizeBlocks": 128,
  "cellJitter": 0.85,
  "randomBiomeChance": 0.8
}
```

## Behavior

- Overworld biome borders become square.
- Random biome selection applies only to the overworld.
- Nether and End are excluded from the extra random biome selection.
- Overworld random selection uses unique biome entries, so duplicated vanilla climate weights do not bias the random result.

## World Compatibility

- New worlds are strongly recommended.
- Adding or removing this mod in an existing world can create visible biome border changes in newly generated chunks.
- This mod only intercepts `MultiNoiseBiomeSource`.
- Mods that replace biome generation with a different biome source or a fully custom world preset may bypass square-biomes entirely.

## Compatibility Notes

- Overworld biome mods that extend vanilla `MultiNoiseBiomeSource` usually work reasonably well.
- Mods with custom biome sources are not guaranteed to be affected.
- At runtime, square-biomes writes log messages describing which `MultiNoiseBiomeSource` preset path it detected.

## Distribution

Recommended release contents:

- `square-biomes-1.0.0.jar`
- `README.md`
- optional sample `square-biomes.json`

## License

See [LICENSE](LICENSE).
