package work.encman0x404.squarebiomes.mixin;

import com.mojang.datafixers.util.Pair;
import work.encman0x404.squarebiomes.SquareBiomesConfig;
import work.encman0x404.squarebiomes.SquareBiomesMod;
import work.encman0x404.squarebiomes.SourceKind;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(MultiNoiseBiomeSource.class)
abstract class MultiNoiseBiomeSourceMixin {
    private static final long X_HASH = 0x9E3779B97F4A7C15L;
    private static final long Z_HASH = 0xC2B2AE3D27D4EB4FL;
    private static final AtomicBoolean OVERWORLD_LOGGED = new AtomicBoolean(false);
    private static final AtomicBoolean NETHER_LOGGED = new AtomicBoolean(false);
    private static final AtomicBoolean OTHER_LOGGED = new AtomicBoolean(false);

    @Invoker("getNoiseBiome")
    protected abstract Holder<Biome> squarebiomes$getNoiseBiome(Climate.TargetPoint targetPoint);

    @Invoker("parameters")
    protected abstract Climate.ParameterList<Holder<Biome>> squarebiomes$parameters();

    @Invoker("stable")
    protected abstract boolean squarebiomes$stable(ResourceKey<?> resourceKey);

    @Inject(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;", at = @At("HEAD"), cancellable = true)
    private void squarebiomes$snapBiomeSampling(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        SourceKind sourceKind = detectSourceKind();
        logSourceKindOnce(sourceKind);

        long cellHash = mixCell(x, z);
        if (sourceKind == SourceKind.OVERWORLD && shouldUseRandomBiome(cellHash)) {
            Holder<Biome> randomBiome = pickRandomBiome(cellHash);
            if (randomBiome != null) {
                cir.setReturnValue(randomBiome);
                return;
            }
        }

        int snappedX = snapQuartCoordinate(x, z, true);
        int snappedZ = snapQuartCoordinate(z, x, false);
        Climate.TargetPoint targetPoint = sampler.sample(snappedX, y, snappedZ);
        cir.setReturnValue(this.squarebiomes$getNoiseBiome(targetPoint));
    }

    private static int snapQuartCoordinate(int quartCoordinate, int otherQuartCoordinate, boolean isX) {
        int quartsPerCell = Math.max(1, SquareBiomesConfig.gridSizeBlocks() >> 2);
        int cellOrigin = Math.floorDiv(quartCoordinate, quartsPerCell) * quartsPerCell;
        int cellX = isX ? Math.floorDiv(quartCoordinate, quartsPerCell) : Math.floorDiv(otherQuartCoordinate, quartsPerCell);
        int cellZ = isX ? Math.floorDiv(otherQuartCoordinate, quartsPerCell) : Math.floorDiv(quartCoordinate, quartsPerCell);
        return cellOrigin + computeJitterOffset(quartsPerCell, cellX, cellZ);
    }

    private static int computeJitterOffset(int quartsPerCell, int cellX, int cellZ) {
        if (quartsPerCell <= 1) {
            return 0;
        }

        int halfSpan = quartsPerCell >> 1;
        int maxOffset = Math.max(0, (int) Math.floor(halfSpan * SquareBiomesConfig.cellJitter()));
        if (maxOffset <= 0) {
            return halfSpan;
        }

        long hash = mix(((long) cellX * X_HASH) ^ ((long) cellZ * Z_HASH));
        int range = (maxOffset * 2) + 1;
        int offset = (int) Math.floorMod(hash, range) - maxOffset;
        return clamp(halfSpan + offset, 0, quartsPerCell - 1);
    }

    private Holder<Biome> pickRandomBiome(long cellHash) {
        List<Pair<Climate.ParameterPoint, Holder<Biome>>> values = this.squarebiomes$parameters().values();
        if (values.isEmpty()) {
            return null;
        }

        List<Holder<Biome>> uniqueBiomes = collectUniqueBiomes(values);
        if (uniqueBiomes.isEmpty()) {
            return null;
        }

        int index = (int) Math.floorMod(cellHash >>> 1, uniqueBiomes.size());
        return uniqueBiomes.get(index);
    }

    private static List<Holder<Biome>> collectUniqueBiomes(List<Pair<Climate.ParameterPoint, Holder<Biome>>> values) {
        Set<Holder<Biome>> uniqueBiomes = new LinkedHashSet<>();
        for (Pair<Climate.ParameterPoint, Holder<Biome>> value : values) {
            uniqueBiomes.add(value.getSecond());
        }

        return new ArrayList<>(uniqueBiomes);
    }

    private static boolean shouldUseRandomBiome(long cellHash) {
        long threshold = (long) (SquareBiomesConfig.randomBiomeChance() * 10_000L);
        long value = Math.floorMod(cellHash, 10_000L);
        return value < threshold;
    }

    private static long mixCell(int x, int z) {
        int quartsPerCell = Math.max(1, SquareBiomesConfig.gridSizeBlocks() >> 2);
        int cellX = Math.floorDiv(x, quartsPerCell);
        int cellZ = Math.floorDiv(z, quartsPerCell);
        return mix(((long) cellX * X_HASH) ^ ((long) cellZ * Z_HASH));
    }

    private static long mix(long value) {
        value ^= value >>> 33;
        value *= 0xff51afd7ed558ccdl;
        value ^= value >>> 33;
        value *= 0xc4ceb9fe1a85ec53l;
        value ^= value >>> 33;
        return value;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private SourceKind detectSourceKind() {
        if (this.squarebiomes$stable(MultiNoiseBiomeSourceParameterLists.OVERWORLD)) {
            return SourceKind.OVERWORLD;
        }
        if (this.squarebiomes$stable(MultiNoiseBiomeSourceParameterLists.NETHER)) {
            return SourceKind.NETHER;
        }
        return SourceKind.OTHER;
    }

    private static void logSourceKindOnce(SourceKind sourceKind) {
        switch (sourceKind) {
            case OVERWORLD -> {
                if (OVERWORLD_LOGGED.compareAndSet(false, true)) {
                    SquareBiomesMod.LOGGER.info("square-biomes detected MultiNoiseBiomeSource preset: overworld. Square biome logic is active.");
                }
            }
            case NETHER -> {
                if (NETHER_LOGGED.compareAndSet(false, true)) {
                    SquareBiomesMod.LOGGER.info("square-biomes detected MultiNoiseBiomeSource preset: nether. Random overworld biome logic is skipped.");
                }
            }
            case OTHER -> {
                if (OTHER_LOGGED.compareAndSet(false, true)) {
                    SquareBiomesMod.LOGGER.info("square-biomes detected a non-overworld MultiNoiseBiomeSource preset. Only square sampling is applied.");
                }
            }
        }
    }
}
