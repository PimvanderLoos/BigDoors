package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChanceDecoratorRangeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenMineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureNetherFossil;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureOceanRuin;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureGenerator<C extends WorldGenFeatureConfiguration> {

    public static final BiMap<String, StructureGenerator<?>> STRUCTURES_REGISTRY = HashBiMap.create();
    private static final Map<StructureGenerator<?>, WorldGenStage.Decoration> STEP = Maps.newHashMap();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final StructureGenerator<WorldGenFeatureVillageConfiguration> PILLAGER_OUTPOST = a("Pillager_Outpost", new WorldGenFeaturePillagerOutpost(WorldGenFeatureVillageConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenMineshaftConfiguration> MINESHAFT = a("Mineshaft", new WorldGenMineshaft(WorldGenMineshaftConfiguration.CODEC), WorldGenStage.Decoration.UNDERGROUND_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> WOODLAND_MANSION = a("Mansion", new WorldGenWoodlandMansion(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> JUNGLE_TEMPLE = a("Jungle_Pyramid", new WorldGenFeatureJunglePyramid(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> DESERT_PYRAMID = a("Desert_Pyramid", new WorldGenFeatureDesertPyramid(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> IGLOO = a("Igloo", new WorldGenFeatureIgloo(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureRuinedPortalConfiguration> RUINED_PORTAL = a("Ruined_Portal", new WorldGenFeatureRuinedPortal(WorldGenFeatureRuinedPortalConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureShipwreckConfiguration> SHIPWRECK = a("Shipwreck", new WorldGenFeatureShipwreck(WorldGenFeatureShipwreckConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final WorldGenFeatureSwampHut SWAMP_HUT = (WorldGenFeatureSwampHut) a("Swamp_Hut", new WorldGenFeatureSwampHut(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> STRONGHOLD = a("Stronghold", new WorldGenStronghold(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.STRONGHOLDS);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> OCEAN_MONUMENT = a("Monument", new WorldGenMonument(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureOceanRuinConfiguration> OCEAN_RUIN = a("Ocean_Ruin", new WorldGenFeatureOceanRuin(WorldGenFeatureOceanRuinConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> NETHER_BRIDGE = a("Fortress", new WorldGenNether(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.UNDERGROUND_DECORATION);
    public static final StructureGenerator<WorldGenFeatureEmptyConfiguration> END_CITY = a("EndCity", new WorldGenEndCity(WorldGenFeatureEmptyConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureConfigurationChance> BURIED_TREASURE = a("Buried_Treasure", new WorldGenBuriedTreasure(WorldGenFeatureConfigurationChance.CODEC), WorldGenStage.Decoration.UNDERGROUND_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureVillageConfiguration> VILLAGE = a("Village", new WorldGenVillage(WorldGenFeatureVillageConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final StructureGenerator<WorldGenFeatureChanceDecoratorRangeConfiguration> NETHER_FOSSIL = a("Nether_Fossil", new WorldGenFeatureNetherFossil(WorldGenFeatureChanceDecoratorRangeConfiguration.CODEC), WorldGenStage.Decoration.UNDERGROUND_DECORATION);
    public static final StructureGenerator<WorldGenFeatureVillageConfiguration> BASTION_REMNANT = a("Bastion_Remnant", new WorldGenFeatureBastionRemnant(WorldGenFeatureVillageConfiguration.CODEC), WorldGenStage.Decoration.SURFACE_STRUCTURES);
    public static final List<StructureGenerator<?>> NOISE_AFFECTING_FEATURES = ImmutableList.of(StructureGenerator.PILLAGER_OUTPOST, StructureGenerator.VILLAGE, StructureGenerator.NETHER_FOSSIL, StructureGenerator.STRONGHOLD);
    private static final MinecraftKey JIGSAW_RENAME = new MinecraftKey("jigsaw");
    private static final Map<MinecraftKey, MinecraftKey> RENAMES = ImmutableMap.builder().put(new MinecraftKey("nvi"), StructureGenerator.JIGSAW_RENAME).put(new MinecraftKey("pcp"), StructureGenerator.JIGSAW_RENAME).put(new MinecraftKey("bastionremnant"), StructureGenerator.JIGSAW_RENAME).put(new MinecraftKey("runtime"), StructureGenerator.JIGSAW_RENAME).build();
    public static final int MAX_STRUCTURE_RANGE = 8;
    private final Codec<StructureFeature<C, StructureGenerator<C>>> configuredStructureCodec;

    private static <F extends StructureGenerator<?>> F a(String s, F f0, WorldGenStage.Decoration worldgenstage_decoration) {
        StructureGenerator.STRUCTURES_REGISTRY.put(s.toLowerCase(Locale.ROOT), f0);
        StructureGenerator.STEP.put(f0, worldgenstage_decoration);
        return (StructureGenerator) IRegistry.a(IRegistry.STRUCTURE_FEATURE, s.toLowerCase(Locale.ROOT), (Object) f0);
    }

    public StructureGenerator(Codec<C> codec) {
        this.configuredStructureCodec = codec.fieldOf("config").xmap((worldgenfeatureconfiguration) -> {
            return new StructureFeature<>(this, worldgenfeatureconfiguration);
        }, (structurefeature) -> {
            return structurefeature.config;
        }).codec();
    }

    public WorldGenStage.Decoration d() {
        return (WorldGenStage.Decoration) StructureGenerator.STEP.get(this);
    }

    public static void e() {}

    @Nullable
    public static StructureStart<?> a(WorldServer worldserver, NBTTagCompound nbttagcompound, long i) {
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
                    StructureStart<?> structurestart = structuregenerator.a(chunkcoordintpair, j, i);

                    for (int k = 0; k < nbttaglist.size(); ++k) {
                        NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(k);
                        String s1 = nbttagcompound1.getString("id").toLowerCase(Locale.ROOT);
                        MinecraftKey minecraftkey = new MinecraftKey(s1);
                        MinecraftKey minecraftkey1 = (MinecraftKey) StructureGenerator.RENAMES.getOrDefault(minecraftkey, minecraftkey);
                        WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype = (WorldGenFeatureStructurePieceType) IRegistry.STRUCTURE_PIECE.get(minecraftkey1);

                        if (worldgenfeaturestructurepiecetype == null) {
                            StructureGenerator.LOGGER.error("Unknown structure piece id: {}", minecraftkey1);
                        } else {
                            try {
                                StructurePiece structurepiece = worldgenfeaturestructurepiecetype.load(worldserver, nbttagcompound1);

                                structurestart.a(structurepiece);
                            } catch (Exception exception) {
                                StructureGenerator.LOGGER.error("Exception loading structure piece with id {}", minecraftkey1, exception);
                            }
                        }
                    }

                    return structurestart;
                } catch (Exception exception1) {
                    StructureGenerator.LOGGER.error("Failed Start with id {}", s, exception1);
                    return null;
                }
            }
        }
    }

    public Codec<StructureFeature<C, StructureGenerator<C>>> f() {
        return this.configuredStructureCodec;
    }

    public StructureFeature<C, ? extends StructureGenerator<C>> a(C c0) {
        return new StructureFeature<>(this, c0);
    }

    @Nullable
    public BlockPosition getNearestGeneratedFeature(IWorldReader iworldreader, StructureManager structuremanager, BlockPosition blockposition, int i, boolean flag, long j, StructureSettingsFeature structuresettingsfeature) {
        int k = structuresettingsfeature.a();
        int l = SectionPosition.a(blockposition.getX());
        int i1 = SectionPosition.a(blockposition.getZ());
        int j1 = 0;
        SeededRandom seededrandom = new SeededRandom();

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
                            ChunkCoordIntPair chunkcoordintpair = this.a(structuresettingsfeature, j, seededrandom, i2, j2);
                            boolean flag3 = iworldreader.r_().a(chunkcoordintpair).e().a(this);

                            if (flag3) {
                                IChunkAccess ichunkaccess = iworldreader.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z, ChunkStatus.STRUCTURE_STARTS);
                                StructureStart<?> structurestart = structuremanager.a(SectionPosition.a(ichunkaccess), this, ichunkaccess);

                                if (structurestart != null && structurestart.e()) {
                                    if (flag && structurestart.g()) {
                                        structurestart.h();
                                        return structurestart.a();
                                    }

                                    if (!flag) {
                                        return structurestart.a();
                                    }
                                }
                            }

                            if (j1 == 0) {
                                break;
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

    protected boolean b() {
        return true;
    }

    public final ChunkCoordIntPair a(StructureSettingsFeature structuresettingsfeature, long i, SeededRandom seededrandom, int j, int k) {
        int l = structuresettingsfeature.a();
        int i1 = structuresettingsfeature.b();
        int j1 = Math.floorDiv(j, l);
        int k1 = Math.floorDiv(k, l);

        seededrandom.b(i, j1, k1, structuresettingsfeature.c());
        int l1;
        int i2;

        if (this.b()) {
            l1 = seededrandom.nextInt(l - i1);
            i2 = seededrandom.nextInt(l - i1);
        } else {
            l1 = (seededrandom.nextInt(l - i1) + seededrandom.nextInt(l - i1)) / 2;
            i2 = (seededrandom.nextInt(l - i1) + seededrandom.nextInt(l - i1)) / 2;
        }

        return new ChunkCoordIntPair(j1 * l + l1, k1 * l + i2);
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair1, C c0, LevelHeightAccessor levelheightaccessor) {
        return true;
    }

    private StructureStart<C> a(ChunkCoordIntPair chunkcoordintpair, int i, long j) {
        return this.a().create(this, chunkcoordintpair, i, j);
    }

    public StructureStart<?> a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, DefinedStructureManager definedstructuremanager, long i, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, int j, SeededRandom seededrandom, StructureSettingsFeature structuresettingsfeature, C c0, LevelHeightAccessor levelheightaccessor) {
        ChunkCoordIntPair chunkcoordintpair1 = this.a(structuresettingsfeature, i, seededrandom, chunkcoordintpair.x, chunkcoordintpair.z);

        if (chunkcoordintpair.x == chunkcoordintpair1.x && chunkcoordintpair.z == chunkcoordintpair1.z && this.a(chunkgenerator, worldchunkmanager, i, seededrandom, chunkcoordintpair, biomebase, chunkcoordintpair1, c0, levelheightaccessor)) {
            StructureStart<C> structurestart = this.a(chunkcoordintpair, j, i);

            structurestart.a(iregistrycustom, chunkgenerator, definedstructuremanager, chunkcoordintpair, biomebase, c0, levelheightaccessor);
            if (structurestart.e()) {
                return structurestart;
            }
        }

        return StructureStart.INVALID_START;
    }

    public abstract StructureGenerator.a<C> a();

    public String g() {
        return (String) StructureGenerator.STRUCTURES_REGISTRY.inverse().get(this);
    }

    public WeightedRandomList<BiomeSettingsMobs.c> c() {
        return BiomeSettingsMobs.EMPTY_MOB_LIST;
    }

    public WeightedRandomList<BiomeSettingsMobs.c> h() {
        return BiomeSettingsMobs.EMPTY_MOB_LIST;
    }

    public WeightedRandomList<BiomeSettingsMobs.c> i() {
        return BiomeSettingsMobs.EMPTY_MOB_LIST;
    }

    public interface a<C extends WorldGenFeatureConfiguration> {

        StructureStart<C> create(StructureGenerator<C> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j);
    }
}
