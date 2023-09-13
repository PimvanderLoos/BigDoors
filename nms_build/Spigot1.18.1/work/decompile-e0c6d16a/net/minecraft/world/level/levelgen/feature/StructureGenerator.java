package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.feature.configurations.RangeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenMineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureNetherFossil;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureOceanRuin;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureGenerator<C extends WorldGenFeatureConfiguration> {

    public static final BiMap<String, StructureGenerator<?>> STRUCTURES_REGISTRY = HashBiMap.create();
    private static final Map<StructureGenerator<?>, WorldGenStage.Decoration> STEP = Maps.newHashMap();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final StructureGenerator<WorldGenFeatureVillageConfiguration> PILLAGER_OUTPOST = register("Pillager_Outpost", new WorldGenFeaturePillagerOutpost(WorldGenFeatureVillageConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenMineshaftConfiguration> MINESHAFT = register("Mineshaft", new WorldGenMineshaft(WorldGenMineshaftConfiguration.CODEC), WorldGenStage.Decoration.UNDERGROUND_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> WOODLAND_MANSION = register("Mansion", new WorldGenWoodlandMansion(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> JUNGLE_TEMPLE = register("Jungle_Pyramid", new WorldGenFeatureJunglePyramid(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> DESERT_PYRAMID = register("Desert_Pyramid", new WorldGenFeatureDesertPyramid(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> IGLOO = register("Igloo", new WorldGenFeatureIgloo(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureRuinedPortalConfiguration> RUINED_PORTAL = register("Ruined_Portal", new WorldGenFeatureRuinedPortal(WorldGenFeatureRuinedPortalConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureShipwreckConfiguration> SHIPWRECK = register("Shipwreck", new WorldGenFeatureShipwreck(WorldGenFeatureShipwreckConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> SWAMP_HUT = register("Swamp_Hut", new WorldGenFeatureSwampHut(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> STRONGHOLD = register("Stronghold", new WorldGenStronghold(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.STRONGHOLDS);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> OCEAN_MONUMENT = register("Monument", new WorldGenMonument(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureOceanRuinConfiguration> OCEAN_RUIN = register("Ocean_Ruin", new WorldGenFeatureOceanRuin(WorldGenFeatureOceanRuinConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> NETHER_BRIDGE = register("Fortress", new WorldGenNether(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.UNDERGROUND_DECORATION);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> END_CITY = register("EndCity", new WorldGenEndCity(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureConfigurationChance> BURIED_TREASURE = register("Buried_Treasure", new WorldGenBuriedTreasure(WorldGenFeatureConfigurationChance.CODEC), WorldGenStage.Decoration.UNDERGROUND_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureVillageConfiguration> VILLAGE = register("Village", new WorldGenVillage(WorldGenFeatureVillageConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<RangeConfiguration> NETHER_FOSSIL = register("Nether_Fossil", new WorldGenFeatureNetherFossil(RangeConfiguration.CODEC), WorldGenStage.Decoration.UNDERGROUND_DECORATION);
    public static final StructureGenerator<WorldGenFeatureVillageConfiguration> BASTION_REMNANT = register("Bastion_Remnant", new WorldGenFeatureBastionRemnant(WorldGenFeatureVillageConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final List<StructureGenerator<?>> NOISE_AFFECTING_FEATURES = ImmutableList.of(StructureGenerator.PILLAGER_OUTPOST, StructureGenerator.VILLAGE, StructureGenerator.NETHER_FOSSIL, StructureGenerator.STRONGHOLD);
    public static final int MAX_STRUCTURE_RANGE = 8;
    private final Codec<StructureFeature<C, StructureGenerator<C>>> configuredStructureCodec;
    private final PieceGeneratorSupplier<C> pieceGenerator;
    private final PostPlacementProcessor postPlacementProcessor;

    private static <F extends StructureGenerator<?>> F register(String s, F f0, WorldGenStage.Decoration worldgenstage_decoration) {
        StructureGenerator.STRUCTURES_REGISTRY.put(s.toLowerCase(Locale.ROOT), f0);
        StructureGenerator.STEP.put(f0, worldgenstage_decoration);
        return (StructureGenerator) IRegistry.register(IRegistry.STRUCTURE_FEATURE, s.toLowerCase(Locale.ROOT), f0);
    }

    public StructureGenerator(Codec<C> codec, PieceGeneratorSupplier<C> piecegeneratorsupplier) {
        this(codec, piecegeneratorsupplier, PostPlacementProcessor.NONE);
    }

    public StructureGenerator(Codec<C> codec, PieceGeneratorSupplier<C> piecegeneratorsupplier, PostPlacementProcessor postplacementprocessor) {
        this.configuredStructureCodec = codec.fieldOf("config").xmap((worldgenfeatureconfiguration) -> {
            return new StructureFeature<>(this, worldgenfeatureconfiguration);
        }, (structurefeature) -> {
            return structurefeature.config;
        }).codec();
        this.pieceGenerator = piecegeneratorsupplier;
        this.postPlacementProcessor = postplacementprocessor;
    }

    public WorldGenStage.Decoration step() {
        return (WorldGenStage.Decoration) StructureGenerator.STEP.get(this);
    }

    public static void bootstrap() {}

    @Nullable
    public static StructureStart<?> loadStaticStart(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound, long i) {
        String s = nbttagcompound.getString("id");

        if ("INVALID".equals(s)) {
            return StructureStart.INVALID_START;
        } else {
            StructureGenerator<?> structuregenerator = (StructureGenerator) IRegistry.STRUCTURE_FEATURE.get(new MinecraftKey(s.toLowerCase(Locale.ROOT)));

            if (structuregenerator == null) {
                StructureGenerator.LOGGER.error("Unknown feature id: {}", s);
                return null;
            } else {
                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(nbttagcompound.getInt("ChunkX"), nbttagcompound.getInt("ChunkZ"));
                int j = nbttagcompound.getInt("references");
                NBTTagList nbttaglist = nbttagcompound.getList("Children", 10);

                try {
                    PiecesContainer piecescontainer = PiecesContainer.load(nbttaglist, structurepieceserializationcontext);

                    if (structuregenerator == StructureGenerator.OCEAN_MONUMENT) {
                        piecescontainer = WorldGenMonument.regeneratePiecesAfterLoad(chunkcoordintpair, i, piecescontainer);
                    }

                    return new StructureStart<>(structuregenerator, chunkcoordintpair, j, piecescontainer);
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

    public StructureFeature<C, ? extends StructureGenerator<C>> configured(C c0) {
        return new StructureFeature<>(this, c0);
    }

    public BlockPosition getLocatePos(ChunkCoordIntPair chunkcoordintpair) {
        return new BlockPosition(chunkcoordintpair.getMinBlockX(), 0, chunkcoordintpair.getMinBlockZ());
    }

    @Nullable
    public BlockPosition getNearestGeneratedFeature(IWorldReader iworldreader, StructureManager structuremanager, BlockPosition blockposition, int i, boolean flag, long j, StructureSettingsFeature structuresettingsfeature) {
        int k = structuresettingsfeature.spacing();
        int l = SectionPosition.blockToSectionCoord(blockposition.getX());
        int i1 = SectionPosition.blockToSectionCoord(blockposition.getZ());
        int j1 = 0;

        while (j1 <= i) {
            int k1 = -j1;

            while (true) {
                if (k1 <= j1) {
                    boolean flag1 = k1 == -j1 || k1 == j1;

                    for (int l1 = -j1; l1 <= j1; ++l1) {
                        boolean flag2 = l1 == -j1 || l1 == j1;

                        if (flag1 || flag2) {
                            int i2 = l + k * k1;
                            int j2 = i1 + k * l1;
                            ChunkCoordIntPair chunkcoordintpair = this.getPotentialFeatureChunk(structuresettingsfeature, j, i2, j2);
                            StructureCheckResult structurecheckresult = structuremanager.checkStructurePresence(chunkcoordintpair, this, flag);

                            if (structurecheckresult != StructureCheckResult.START_NOT_PRESENT) {
                                if (!flag && structurecheckresult == StructureCheckResult.START_PRESENT) {
                                    return this.getLocatePos(chunkcoordintpair);
                                }

                                IChunkAccess ichunkaccess = iworldreader.getChunk(chunkcoordintpair.x, chunkcoordintpair.z, ChunkStatus.STRUCTURE_STARTS);
                                StructureStart<?> structurestart = structuremanager.getStartForFeature(SectionPosition.bottomOf(ichunkaccess), this, ichunkaccess);

                                if (structurestart != null && structurestart.isValid()) {
                                    if (flag && structurestart.canBeReferenced()) {
                                        structuremanager.addReference(structurestart);
                                        return this.getLocatePos(structurestart.getChunkPos());
                                    }

                                    if (!flag) {
                                        return this.getLocatePos(structurestart.getChunkPos());
                                    }
                                }

                                if (j1 == 0) {
                                    break;
                                }
                            }
                        }
                    }

                    if (j1 != 0) {
                        ++k1;
                        continue;
                    }
                }

                ++j1;
                break;
            }
        }

        return null;
    }

    protected boolean linearSeparation() {
        return true;
    }

    public final ChunkCoordIntPair getPotentialFeatureChunk(StructureSettingsFeature structuresettingsfeature, long i, int j, int k) {
        int l = structuresettingsfeature.spacing();
        int i1 = structuresettingsfeature.separation();
        int j1 = Math.floorDiv(j, l);
        int k1 = Math.floorDiv(k, l);
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setLargeFeatureWithSalt(i, j1, k1, structuresettingsfeature.salt());
        int l1;
        int i2;

        if (this.linearSeparation()) {
            l1 = seededrandom.nextInt(l - i1);
            i2 = seededrandom.nextInt(l - i1);
        } else {
            l1 = (seededrandom.nextInt(l - i1) + seededrandom.nextInt(l - i1)) / 2;
            i2 = (seededrandom.nextInt(l - i1) + seededrandom.nextInt(l - i1)) / 2;
        }

        return new ChunkCoordIntPair(j1 * l + l1, k1 * l + i2);
    }

    public StructureStart<?> generate(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, DefinedStructureManager definedstructuremanager, long i, ChunkCoordIntPair chunkcoordintpair, int j, StructureSettingsFeature structuresettingsfeature, C c0, LevelHeightAccessor levelheightaccessor, Predicate<BiomeBase> predicate) {
        ChunkCoordIntPair chunkcoordintpair1 = this.getPotentialFeatureChunk(structuresettingsfeature, i, chunkcoordintpair.x, chunkcoordintpair.z);

        if (chunkcoordintpair.x == chunkcoordintpair1.x && chunkcoordintpair.z == chunkcoordintpair1.z) {
            Optional<PieceGenerator<C>> optional = this.pieceGenerator.createGenerator(new PieceGeneratorSupplier.a<>(chunkgenerator, worldchunkmanager, i, chunkcoordintpair, c0, levelheightaccessor, predicate, definedstructuremanager, iregistrycustom));

            if (optional.isPresent()) {
                StructurePiecesBuilder structurepiecesbuilder = new StructurePiecesBuilder();
                SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

                seededrandom.setLargeFeatureSeed(i, chunkcoordintpair.x, chunkcoordintpair.z);
                ((PieceGenerator) optional.get()).generatePieces(structurepiecesbuilder, new PieceGenerator.a<>(c0, chunkgenerator, definedstructuremanager, chunkcoordintpair, levelheightaccessor, seededrandom, i));
                StructureStart<C> structurestart = new StructureStart<>(this, chunkcoordintpair, j, structurepiecesbuilder.build());

                if (structurestart.isValid()) {
                    return structurestart;
                }
            }
        }

        return StructureStart.INVALID_START;
    }

    public boolean canGenerate(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, DefinedStructureManager definedstructuremanager, long i, ChunkCoordIntPair chunkcoordintpair, C c0, LevelHeightAccessor levelheightaccessor, Predicate<BiomeBase> predicate) {
        return this.pieceGenerator.createGenerator(new PieceGeneratorSupplier.a<>(chunkgenerator, worldchunkmanager, i, chunkcoordintpair, c0, levelheightaccessor, predicate, definedstructuremanager, iregistrycustom)).isPresent();
    }

    public PostPlacementProcessor getPostPlacementProcessor() {
        return this.postPlacementProcessor;
    }

    public String getFeatureName() {
        return (String) StructureGenerator.STRUCTURES_REGISTRY.inverse().get(this);
    }

    public StructureBoundingBox adjustBoundingBox(StructureBoundingBox structureboundingbox) {
        return structureboundingbox;
    }
}
