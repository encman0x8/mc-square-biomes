package work.encman0x404.squarebiomes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SquareBiomesConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int DEFAULT_GRID_SIZE_BLOCKS = 128;
    private static final int MIN_GRID_SIZE_BLOCKS = 4;
    private static final double DEFAULT_CELL_JITTER = 0.85D;
    private static final double DEFAULT_RANDOM_BIOME_CHANCE = 0.8D;
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("square-biomes.json");

    private static volatile Data data = new Data(DEFAULT_GRID_SIZE_BLOCKS, DEFAULT_CELL_JITTER, DEFAULT_RANDOM_BIOME_CHANCE);

    private SquareBiomesConfig() {
    }

    public static void load() {
        Data loaded = readOrCreate();
        int normalizedGridSize = normalizeGridSize(loaded.gridSizeBlocks);
        double normalizedCellJitter = normalizeCellJitter(loaded.cellJitter);
        double normalizedRandomBiomeChance = normalizeUnitInterval(loaded.randomBiomeChance, DEFAULT_RANDOM_BIOME_CHANCE);
        boolean changed = normalizedGridSize != loaded.gridSizeBlocks
            || Double.compare(normalizedCellJitter, loaded.cellJitter) != 0
            || Double.compare(normalizedRandomBiomeChance, loaded.randomBiomeChance) != 0;
        if (normalizedGridSize != loaded.gridSizeBlocks) {
            SquareBiomesMod.LOGGER.warn("Invalid grid_size_blocks {} in {}. Using {} instead.", loaded.gridSizeBlocks, CONFIG_PATH, normalizedGridSize);
        }
        if (Double.compare(normalizedCellJitter, loaded.cellJitter) != 0) {
            SquareBiomesMod.LOGGER.warn("Invalid cell_jitter {} in {}. Using {} instead.", loaded.cellJitter, CONFIG_PATH, normalizedCellJitter);
        }
        if (Double.compare(normalizedRandomBiomeChance, loaded.randomBiomeChance) != 0) {
            SquareBiomesMod.LOGGER.warn(
                "Invalid random_biome_chance {} in {}. Using {} instead.",
                loaded.randomBiomeChance,
                CONFIG_PATH,
                normalizedRandomBiomeChance
            );
        }
        if (changed) {
            loaded = new Data(normalizedGridSize, normalizedCellJitter, normalizedRandomBiomeChance);
            write(loaded);
        }

        data = loaded;
    }

    public static int gridSizeBlocks() {
        return data.gridSizeBlocks;
    }

    public static double cellJitter() {
        return data.cellJitter;
    }

    public static double randomBiomeChance() {
        return data.randomBiomeChance;
    }

    private static Data readOrCreate() {
        if (Files.notExists(CONFIG_PATH)) {
            Data defaults = new Data(DEFAULT_GRID_SIZE_BLOCKS, DEFAULT_CELL_JITTER, DEFAULT_RANDOM_BIOME_CHANCE);
            write(defaults);
            return defaults;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            Data loaded = GSON.fromJson(reader, Data.class);
            return loaded == null ? new Data(DEFAULT_GRID_SIZE_BLOCKS, DEFAULT_CELL_JITTER, DEFAULT_RANDOM_BIOME_CHANCE) : loaded;
        } catch (IOException | JsonParseException exception) {
            SquareBiomesMod.LOGGER.error("Failed to read {}. Falling back to defaults.", CONFIG_PATH, exception);
            Data defaults = new Data(DEFAULT_GRID_SIZE_BLOCKS, DEFAULT_CELL_JITTER, DEFAULT_RANDOM_BIOME_CHANCE);
            write(defaults);
            return defaults;
        }
    }

    private static void write(Data data) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException exception) {
            SquareBiomesMod.LOGGER.error("Failed to write {}", CONFIG_PATH, exception);
        }
    }

    private static int normalizeGridSize(int gridSizeBlocks) {
        return Math.max(MIN_GRID_SIZE_BLOCKS, gridSizeBlocks);
    }

    private static double normalizeCellJitter(double cellJitter) {
        return normalizeUnitInterval(cellJitter, DEFAULT_CELL_JITTER);
    }

    private static double normalizeUnitInterval(double value, double fallback) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return fallback;
        }

        return Math.max(0.0D, Math.min(1.0D, value));
    }

    private static final class Data {
        private int gridSizeBlocks;
        private double cellJitter;
        private double randomBiomeChance;

        private Data(int gridSizeBlocks, double cellJitter, double randomBiomeChance) {
            this.gridSizeBlocks = gridSizeBlocks;
            this.cellJitter = cellJitter;
            this.randomBiomeChance = randomBiomeChance;
        }
    }
}
