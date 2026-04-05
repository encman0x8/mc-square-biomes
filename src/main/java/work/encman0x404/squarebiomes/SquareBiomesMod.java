package work.encman0x404.squarebiomes;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SquareBiomesMod implements ModInitializer {
    public static final String MOD_ID = "square-biomes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        SquareBiomesConfig.load();
        LOGGER.info(
            "square-biomes initialized with biome grid size {} blocks, cell jitter {}, random biome chance {}",
            SquareBiomesConfig.gridSizeBlocks(),
            SquareBiomesConfig.cellJitter(),
            SquareBiomesConfig.randomBiomeChance()
        );
    }
}
