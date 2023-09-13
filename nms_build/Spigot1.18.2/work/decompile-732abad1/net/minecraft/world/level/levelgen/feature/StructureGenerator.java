package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.INamable;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.feature.configurations.RangeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenMineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureNetherFossil;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureOceanRuin;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import org.slf4j.Logger;

public class StructureGenerator<C extends WorldGenFeatureConfiguration> {

    private static final Map<StructureGenerator<?>, WorldGenStage.Decoration> STEP = Maps.newHashMap();
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final StructureGenerator<WorldGenFeatureVillageConfiguration> PILLAGER_OUTPOST = register("pillager_outpost", new WorldGenFeaturePillagerOutpost(WorldGenFeatureVillageConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenMineshaftConfiguration> MINESHAFT = register("mineshaft", new WorldGenMineshaft(WorldGenMineshaftConfiguration.CODEC), WorldGenStage.Decoration.UNDERGROUND_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> WOODLAND_MANSION = register("mansion", new WorldGenWoodlandMansion(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> JUNGLE_TEMPLE = register("jungle_pyramid", new WorldGenFeatureJunglePyramid(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> DESERT_PYRAMID = register("desert_pyramid", new WorldGenFeatureDesertPyramid(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> IGLOO = register("igloo", new WorldGenFeatureIgloo(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureRuinedPortalConfiguration> RUINED_PORTAL = register("ruined_portal", new WorldGenFeatureRuinedPortal(WorldGenFeatureRuinedPortalConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureShipwreckConfiguration> SHIPWRECK = register("shipwreck", new WorldGenFeatureShipwreck(WorldGenFeatureShipwreckConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> SWAMP_HUT = register("swamp_hut", new WorldGenFeatureSwampHut(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> STRONGHOLD = register("stronghold", new WorldGenStronghold(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.STRONGHOLDS);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> OCEAN_MONUMENT = register("monument", new WorldGenMonument(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureOceanRuinConfiguration> OCEAN_RUIN = register("ocean_ruin", new WorldGenFeatureOceanRuin(WorldGenFeatureOceanRuinConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> FORTRESS = register("fortress", new WorldGenNether(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.UNDERGROUND_DECORATION);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> END_CITY = register("endcity", new WorldGenEndCity(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureConfigurationChance> BURIED_TREASURE = register("buried_treasure", new WorldGenBuriedTreasure(WorldGenFeatureConfigurationChance.CODEC), WorldGenStage.Decoration.UNDERGROUND_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureVillageConfiguration> VILLAGE = register("village", new WorldGenVillage(WorldGenFeatureVillageConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<RangeConfiguration> NETHER_FOSSIL = register("nether_fossil", new WorldGenFeatureNetherFossil(RangeConfiguration.CODEC), WorldGenStage.Decoration.UNDERGROUND_DECORATION);
    public static final StructureGenerator<WorldGenFeatureVillageConfiguration> BASTION_REMNANT = register("bastion_remnant", new WorldGenFeatureBastionRemnant(WorldGenFeatureVillageConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final int MAX_STRUCTURE_RANGE = 8;
    private final Codec<StructureFeature<C, StructureGenerator<C>>> configuredStructureCodec;
    private final PieceGeneratorSupplier<C> pieceGenerator;
    private final PostPlacementProcessor postPlacementProcessor;

    private static <F extends StructureGenerator<?>> F register(String s, F f0, WorldGenStage.Decoration worldgenstage_decoration) {
        StructureGenerator.STEP.put(f0, worldgenstage_decoration);
        return (StructureGenerator) IRegistry.register(IRegistry.STRUCTURE_FEATURE, s, f0);
    }

    public StructureGenerator(Codec<C> codec, PieceGeneratorSupplier<C> piecegeneratorsupplier) {
        this(codec, piecegeneratorsupplier, PostPlacementProcessor.NONE);
    }

    public StructureGenerator(Codec<C> codec, PieceGeneratorSupplier<C> piecegeneratorsupplier, PostPlacementProcessor postplacementprocessor) {
        this.configuredStructureCodec = RecordCodecBuilder.create((instance) -> {
            return instance.group(codec.fieldOf("config").forGetter((structurefeature) -> {
                return structurefeature.config;
            }), RegistryCodecs.homogeneousList(IRegistry.BIOME_REGISTRY).fieldOf("biomes").forGetter(StructureFeature::biomes), Codec.BOOL.optionalFieldOf("adapt_noise", false).forGetter((structurefeature) -> {
                return structurefeature.adaptNoise;
            }), Codec.simpleMap(EnumCreatureType.CODEC, StructureSpawnOverride.CODEC, INamable.keys(EnumCreatureType.values())).fieldOf("spawn_overrides").forGetter((structurefeature) -> {
                return structurefeature.spawnOverrides;
            })).apply(instance, (worldgenfeatureconfiguration, holderset, obool, map) -> {
                return new StructureFeature<>(this, worldgenfeatureconfiguration, holderset, obool, map);
            });
        });
        this.pieceGenerator = piecegeneratorsupplier;
        this.postPlacementProcessor = postplacementprocessor;
    }

    public WorldGenStage.Decoration step() {
        return (WorldGenStage.Decoration) StructureGenerator.STEP.get(this);
    }

    public static void bootstrap() {}

    @Nullable
    public static StructureStart loadStaticStart(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound, long i) {
        String s = nbttagcompound.getString("id");

        if ("INVALID".equals(s)) {
            return StructureStart.INVALID_START;
        } else {
            IRegistry<StructureFeature<?, ?>> iregistry = structurepieceserializationcontext.registryAccess().registryOrThrow(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
            StructureFeature<?, ?> structurefeature = (StructureFeature) iregistry.get(new MinecraftKey(s));

            if (structurefeature == null) {
                StructureGenerator.LOGGER.error("Unknown feature id: {}", s);
                return null;
            } else {
                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(nbttagcompound.getInt("ChunkX"), nbttagcompound.getInt("ChunkZ"));
                int j = nbttagcompound.getInt("references");
                NBTTagList nbttaglist = nbttagcompound.getList("Children", 10);

                try {
                    PiecesContainer piecescontainer = PiecesContainer.load(nbttaglist, structurepieceserializationcontext);

                    if (structurefeature.feature == StructureGenerator.OCEAN_MONUMENT) {
                        piecescontainer = WorldGenMonument.regeneratePiecesAfterLoad(chunkcoordintpair, i, piecescontainer);
                    }

                    return new StructureStart(structurefeature, chunkcoordintpair, j, piecescontainer);
                } catch (Exception exception) {
                    StructureGenerator.LOGGER.error("Failed Start with id {}", s, exception);
                    return null;
                }
            }
        }
    }

    public Codec<StructureFeature<C, StructureGenerator<C>>> configuredStructureCodec() {
        return this.configuredStructureCodec;
    }

    public StructureFeature<C, ? extends StructureGenerator<C>> configured(C c0, TagKey<BiomeBase> tagkey) {
        return this.configured(c0, tagkey, false);
    }

    public StructureFeature<C, ? extends StructureGenerator<C>> configured(C c0, TagKey<BiomeBase> tagkey, boolean flag) {
        return new StructureFeature<>(this, c0, RegistryGeneration.BIOME.getOrCreateTag(tagkey), flag, Map.of());
    }

    public StructureFeature<C, ? extends StructureGenerator<C>> configured(C c0, TagKey<BiomeBase> tagkey, Map<EnumCreatureType, StructureSpawnOverride> map) {
        return new StructureFeature<>(this, c0, RegistryGeneration.BIOME.getOrCreateTag(tagkey), false, map);
    }

    public StructureFeature<C, ? extends StructureGenerator<C>> configured(C c0, TagKey<BiomeBase> tagkey, boolean flag, Map<EnumCreatureType, StructureSpawnOverride> map) {
        return new StructureFeature<>(this, c0, RegistryGeneration.BIOME.getOrCreateTag(tagkey), flag, map);
    }

    public static BlockPosition getLocatePos(RandomSpreadStructurePlacement randomspreadstructureplacement, ChunkCoordIntPair chunkcoordintpair) {
        return (new BlockPosition(chunkcoordintpair.getMinBlockX(), 0, chunkcoordintpair.getMinBlockZ())).offset(randomspreadstructureplacement.locateOffset());
    }

    public boolean canGenerate(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, DefinedStructureManager definedstructuremanager, long i, ChunkCoordIntPair chunkcoordintpair, C c0, LevelHeightAccessor levelheightaccessor, Predicate<Holder<BiomeBase>> predicate) {
        return this.pieceGenerator.createGenerator(new PieceGeneratorSupplier.a<>(chunkgenerator, worldchunkmanager, i, chunkcoordintpair, c0, levelheightaccessor, predicate, definedstructuremanager, iregistrycustom)).isPresent();
    }

    public PieceGeneratorSupplier<C> pieceGeneratorSupplier() {
        return this.pieceGenerator;
    }

    public PostPlacementProcessor getPostPlacementProcessor() {
        return this.postPlacementProcessor;
    }
}
