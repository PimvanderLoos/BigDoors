package net.minecraft.world.level.levelgen.presets;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.biome.WorldChunkManagerHell;
import net.minecraft.world.level.biome.WorldChunkManagerMultiNoise;
import net.minecraft.world.level.biome.WorldChunkManagerTheEnd;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.dimension.WorldDimension;
import net.minecraft.world.level.levelgen.ChunkGeneratorAbstract;
import net.minecraft.world.level.levelgen.ChunkProviderDebug;
import net.minecraft.world.level.levelgen.ChunkProviderFlat;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;
import net.minecraft.world.level.levelgen.GeneratorSettings;
import net.minecraft.world.level.levelgen.flat.GeneratorSettingsFlat;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public class WorldPresets {

    public static final ResourceKey<WorldPreset> NORMAL = register("normal");
    public static final ResourceKey<WorldPreset> FLAT = register("flat");
    public static final ResourceKey<WorldPreset> LARGE_BIOMES = register("large_biomes");
    public static final ResourceKey<WorldPreset> AMPLIFIED = register("amplified");
    public static final ResourceKey<WorldPreset> SINGLE_BIOME_SURFACE = register("single_biome_surface");
    public static final ResourceKey<WorldPreset> DEBUG = register("debug_all_block_states");

    public WorldPresets() {}

    public static Holder<WorldPreset> bootstrap(IRegistry<WorldPreset> iregistry) {
        return (new WorldPresets.a(iregistry)).run();
    }

    private static ResourceKey<WorldPreset> register(String s) {
        return ResourceKey.create(IRegistry.WORLD_PRESET_REGISTRY, new MinecraftKey(s));
    }

    public static Optional<ResourceKey<WorldPreset>> fromSettings(GeneratorSettings generatorsettings) {
        ChunkGenerator chunkgenerator = generatorsettings.overworld();

        return chunkgenerator instanceof ChunkProviderFlat ? Optional.of(WorldPresets.FLAT) : (chunkgenerator instanceof ChunkProviderDebug ? Optional.of(WorldPresets.DEBUG) : Optional.empty());
    }

    public static GeneratorSettings createNormalWorldFromPreset(IRegistryCustom iregistrycustom, long i, boolean flag, boolean flag1) {
        return ((WorldPreset) iregistrycustom.registryOrThrow(IRegistry.WORLD_PRESET_REGISTRY).getHolderOrThrow(WorldPresets.NORMAL).value()).createWorldGenSettings(i, flag, flag1);
    }

    public static GeneratorSettings createNormalWorldFromPreset(IRegistryCustom iregistrycustom, long i) {
        return createNormalWorldFromPreset(iregistrycustom, i, true, false);
    }

    public static GeneratorSettings createNormalWorldFromPreset(IRegistryCustom iregistrycustom) {
        return createNormalWorldFromPreset(iregistrycustom, RandomSource.create().nextLong());
    }

    public static GeneratorSettings demoSettings(IRegistryCustom iregistrycustom) {
        return createNormalWorldFromPreset(iregistrycustom, (long) "North Carolina".hashCode(), true, true);
    }

    public static WorldDimension getNormalOverworld(IRegistryCustom iregistrycustom) {
        return ((WorldPreset) iregistrycustom.registryOrThrow(IRegistry.WORLD_PRESET_REGISTRY).getHolderOrThrow(WorldPresets.NORMAL).value()).overworldOrThrow();
    }

    private static class a {

        private final IRegistry<WorldPreset> presets;
        private final IRegistry<DimensionManager> dimensionTypes;
        private final IRegistry<BiomeBase> biomes;
        private final IRegistry<StructureSet> structureSets;
        private final IRegistry<GeneratorSettingBase> noiseSettings;
        private final IRegistry<NoiseGeneratorNormal.a> noises;
        private final Holder<DimensionManager> overworldDimensionType;
        private final Holder<DimensionManager> netherDimensionType;
        private final Holder<GeneratorSettingBase> netherNoiseSettings;
        private final WorldDimension netherStem;
        private final Holder<DimensionManager> endDimensionType;
        private final Holder<GeneratorSettingBase> endNoiseSettings;
        private final WorldDimension endStem;

        a(IRegistry<WorldPreset> iregistry) {
            this.dimensionTypes = RegistryGeneration.DIMENSION_TYPE;
            this.biomes = RegistryGeneration.BIOME;
            this.structureSets = RegistryGeneration.STRUCTURE_SETS;
            this.noiseSettings = RegistryGeneration.NOISE_GENERATOR_SETTINGS;
            this.noises = RegistryGeneration.NOISE;
            this.overworldDimensionType = this.dimensionTypes.getOrCreateHolderOrThrow(BuiltinDimensionTypes.OVERWORLD);
            this.netherDimensionType = this.dimensionTypes.getOrCreateHolderOrThrow(BuiltinDimensionTypes.NETHER);
            this.netherNoiseSettings = this.noiseSettings.getOrCreateHolderOrThrow(GeneratorSettingBase.NETHER);
            this.netherStem = new WorldDimension(this.netherDimensionType, new ChunkGeneratorAbstract(this.structureSets, this.noises, WorldChunkManagerMultiNoise.a.NETHER.biomeSource(this.biomes), this.netherNoiseSettings));
            this.endDimensionType = this.dimensionTypes.getOrCreateHolderOrThrow(BuiltinDimensionTypes.END);
            this.endNoiseSettings = this.noiseSettings.getOrCreateHolderOrThrow(GeneratorSettingBase.END);
            this.endStem = new WorldDimension(this.endDimensionType, new ChunkGeneratorAbstract(this.structureSets, this.noises, new WorldChunkManagerTheEnd(this.biomes), this.endNoiseSettings));
            this.presets = iregistry;
        }

        private WorldDimension makeOverworld(ChunkGenerator chunkgenerator) {
            return new WorldDimension(this.overworldDimensionType, chunkgenerator);
        }

        private WorldDimension makeNoiseBasedOverworld(WorldChunkManager worldchunkmanager, Holder<GeneratorSettingBase> holder) {
            return this.makeOverworld(new ChunkGeneratorAbstract(this.structureSets, this.noises, worldchunkmanager, holder));
        }

        private WorldPreset createPresetWithCustomOverworld(WorldDimension worlddimension) {
            return new WorldPreset(Map.of(WorldDimension.OVERWORLD, worlddimension, WorldDimension.NETHER, this.netherStem, WorldDimension.END, this.endStem));
        }

        private Holder<WorldPreset> registerCustomOverworldPreset(ResourceKey<WorldPreset> resourcekey, WorldDimension worlddimension) {
            return RegistryGeneration.register(this.presets, resourcekey, this.createPresetWithCustomOverworld(worlddimension));
        }

        public Holder<WorldPreset> run() {
            WorldChunkManagerMultiNoise worldchunkmanagermultinoise = WorldChunkManagerMultiNoise.a.OVERWORLD.biomeSource(this.biomes);
            Holder<GeneratorSettingBase> holder = this.noiseSettings.getOrCreateHolderOrThrow(GeneratorSettingBase.OVERWORLD);

            this.registerCustomOverworldPreset(WorldPresets.NORMAL, this.makeNoiseBasedOverworld(worldchunkmanagermultinoise, holder));
            Holder<GeneratorSettingBase> holder1 = this.noiseSettings.getOrCreateHolderOrThrow(GeneratorSettingBase.LARGE_BIOMES);

            this.registerCustomOverworldPreset(WorldPresets.LARGE_BIOMES, this.makeNoiseBasedOverworld(worldchunkmanagermultinoise, holder1));
            Holder<GeneratorSettingBase> holder2 = this.noiseSettings.getOrCreateHolderOrThrow(GeneratorSettingBase.AMPLIFIED);

            this.registerCustomOverworldPreset(WorldPresets.AMPLIFIED, this.makeNoiseBasedOverworld(worldchunkmanagermultinoise, holder2));
            this.registerCustomOverworldPreset(WorldPresets.SINGLE_BIOME_SURFACE, this.makeNoiseBasedOverworld(new WorldChunkManagerHell(this.biomes.getOrCreateHolderOrThrow(Biomes.PLAINS)), holder));
            this.registerCustomOverworldPreset(WorldPresets.FLAT, this.makeOverworld(new ChunkProviderFlat(this.structureSets, GeneratorSettingsFlat.getDefault(this.biomes, this.structureSets))));
            return this.registerCustomOverworldPreset(WorldPresets.DEBUG, this.makeOverworld(new ChunkProviderDebug(this.structureSets, this.biomes)));
        }
    }
}
