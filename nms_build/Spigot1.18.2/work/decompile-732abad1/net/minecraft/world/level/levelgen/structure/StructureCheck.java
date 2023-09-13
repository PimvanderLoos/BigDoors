package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.chunk.storage.IChunkLoader;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import org.slf4j.Logger;

public class StructureCheck {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int NO_STRUCTURE = -1;
    private final ChunkScanAccess storageAccess;
    private final IRegistryCustom registryAccess;
    private final IRegistry<BiomeBase> biomes;
    private final IRegistry<StructureFeature<?, ?>> structureConfigs;
    private final DefinedStructureManager structureManager;
    private final ResourceKey<World> dimension;
    private final ChunkGenerator chunkGenerator;
    private final LevelHeightAccessor heightAccessor;
    private final WorldChunkManager biomeSource;
    private final long seed;
    private final DataFixer fixerUpper;
    private final Long2ObjectMap<Object2IntMap<StructureFeature<?, ?>>> loadedChunks = new Long2ObjectOpenHashMap();
    private final Map<StructureFeature<?, ?>, Long2BooleanMap> featureChecks = new HashMap();

    public StructureCheck(ChunkScanAccess chunkscanaccess, IRegistryCustom iregistrycustom, DefinedStructureManager definedstructuremanager, ResourceKey<World> resourcekey, ChunkGenerator chunkgenerator, LevelHeightAccessor levelheightaccessor, WorldChunkManager worldchunkmanager, long i, DataFixer datafixer) {
        this.storageAccess = chunkscanaccess;
        this.registryAccess = iregistrycustom;
        this.structureManager = definedstructuremanager;
        this.dimension = resourcekey;
        this.chunkGenerator = chunkgenerator;
        this.heightAccessor = levelheightaccessor;
        this.biomeSource = worldchunkmanager;
        this.seed = i;
        this.fixerUpper = datafixer;
        this.biomes = iregistrycustom.ownedRegistryOrThrow(IRegistry.BIOME_REGISTRY);
        this.structureConfigs = iregistrycustom.ownedRegistryOrThrow(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
    }

    public StructureCheckResult checkStart(ChunkCoordIntPair chunkcoordintpair, StructureFeature<?, ?> structurefeature, boolean flag) {
        long i = chunkcoordintpair.toLong();
        Object2IntMap<StructureFeature<?, ?>> object2intmap = (Object2IntMap) this.loadedChunks.get(i);

        if (object2intmap != null) {
            return this.checkStructureInfo(object2intmap, structurefeature, flag);
        } else {
            StructureCheckResult structurecheckresult = this.tryLoadFromStorage(chunkcoordintpair, structurefeature, flag, i);

            if (structurecheckresult != null) {
                return structurecheckresult;
            } else {
                boolean flag1 = ((Long2BooleanMap) this.featureChecks.computeIfAbsent(structurefeature, (structurefeature1) -> {
                    return new Long2BooleanOpenHashMap();
                })).computeIfAbsent(i, (j) -> {
                    return this.canCreateStructure(chunkcoordintpair, structurefeature);
                });

                return !flag1 ? StructureCheckResult.START_NOT_PRESENT : StructureCheckResult.CHUNK_LOAD_NEEDED;
            }
        }
    }

    private <FC extends WorldGenFeatureConfiguration, F extends StructureGenerator<FC>> boolean canCreateStructure(ChunkCoordIntPair chunkcoordintpair, StructureFeature<FC, F> structurefeature) {
        StructureGenerator structuregenerator = structurefeature.feature;
        IRegistryCustom iregistrycustom = this.registryAccess;
        ChunkGenerator chunkgenerator = this.chunkGenerator;
        WorldChunkManager worldchunkmanager = this.biomeSource;
        DefinedStructureManager definedstructuremanager = this.structureManager;
        long i = this.seed;
        WorldGenFeatureConfiguration worldgenfeatureconfiguration = structurefeature.config;
        LevelHeightAccessor levelheightaccessor = this.heightAccessor;
        HolderSet holderset = structurefeature.biomes();

        Objects.requireNonNull(holderset);
        return structuregenerator.canGenerate(iregistrycustom, chunkgenerator, worldchunkmanager, definedstructuremanager, i, chunkcoordintpair, worldgenfeatureconfiguration, levelheightaccessor, holderset::contains);
    }

    @Nullable
    private StructureCheckResult tryLoadFromStorage(ChunkCoordIntPair chunkcoordintpair, StructureFeature<?, ?> structurefeature, boolean flag, long i) {
        CollectFields collectfields = new CollectFields(new FieldSelector[]{new FieldSelector(NBTTagInt.TYPE, "DataVersion"), new FieldSelector("Level", "Structures", NBTTagCompound.TYPE, "Starts"), new FieldSelector("structures", NBTTagCompound.TYPE, "starts")});

        try {
            this.storageAccess.scanChunk(chunkcoordintpair, collectfields).join();
        } catch (Exception exception) {
            StructureCheck.LOGGER.warn("Failed to read chunk {}", chunkcoordintpair, exception);
            return StructureCheckResult.CHUNK_LOAD_NEEDED;
        }

        NBTBase nbtbase = collectfields.getResult();

        if (!(nbtbase instanceof NBTTagCompound)) {
            return null;
        } else {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
            int j = IChunkLoader.getVersion(nbttagcompound);

            if (j <= 1493) {
                return StructureCheckResult.CHUNK_LOAD_NEEDED;
            } else {
                IChunkLoader.injectDatafixingContext(nbttagcompound, this.dimension, this.chunkGenerator.getTypeNameForDataFixer());

                NBTTagCompound nbttagcompound1;

                try {
                    nbttagcompound1 = GameProfileSerializer.update(this.fixerUpper, DataFixTypes.CHUNK, nbttagcompound, j);
                } catch (Exception exception1) {
                    StructureCheck.LOGGER.warn("Failed to partially datafix chunk {}", chunkcoordintpair, exception1);
                    return StructureCheckResult.CHUNK_LOAD_NEEDED;
                }

                Object2IntMap<StructureFeature<?, ?>> object2intmap = this.loadStructures(nbttagcompound1);

                if (object2intmap == null) {
                    return null;
                } else {
                    this.storeFullResults(i, object2intmap);
                    return this.checkStructureInfo(object2intmap, structurefeature, flag);
                }
            }
        }
    }

    @Nullable
    private Object2IntMap<StructureFeature<?, ?>> loadStructures(NBTTagCompound nbttagcompound) {
        if (!nbttagcompound.contains("structures", 10)) {
            return null;
        } else {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("structures");

            if (!nbttagcompound1.contains("starts", 10)) {
                return null;
            } else {
                NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("starts");

                if (nbttagcompound2.isEmpty()) {
                    return Object2IntMaps.emptyMap();
                } else {
                    Object2IntMap<StructureFeature<?, ?>> object2intmap = new Object2IntOpenHashMap();
                    IRegistry<StructureFeature<?, ?>> iregistry = this.registryAccess.registryOrThrow(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
                    Iterator iterator = nbttagcompound2.getAllKeys().iterator();

                    while (iterator.hasNext()) {
                        String s = (String) iterator.next();
                        MinecraftKey minecraftkey = MinecraftKey.tryParse(s);

                        if (minecraftkey != null) {
                            StructureFeature<?, ?> structurefeature = (StructureFeature) iregistry.get(minecraftkey);

                            if (structurefeature != null) {
                                NBTTagCompound nbttagcompound3 = nbttagcompound2.getCompound(s);

                                if (!nbttagcompound3.isEmpty()) {
                                    String s1 = nbttagcompound3.getString("id");

                                    if (!"INVALID".equals(s1)) {
                                        int i = nbttagcompound3.getInt("references");

                                        object2intmap.put(structurefeature, i);
                                    }
                                }
                            }
                        }
                    }

                    return object2intmap;
                }
            }
        }
    }

    private static Object2IntMap<StructureFeature<?, ?>> deduplicateEmptyMap(Object2IntMap<StructureFeature<?, ?>> object2intmap) {
        return object2intmap.isEmpty() ? Object2IntMaps.emptyMap() : object2intmap;
    }

    private StructureCheckResult checkStructureInfo(Object2IntMap<StructureFeature<?, ?>> object2intmap, StructureFeature<?, ?> structurefeature, boolean flag) {
        int i = object2intmap.getOrDefault(structurefeature, -1);

        return i != -1 && (!flag || i == 0) ? StructureCheckResult.START_PRESENT : StructureCheckResult.START_NOT_PRESENT;
    }

    public void onStructureLoad(ChunkCoordIntPair chunkcoordintpair, Map<StructureFeature<?, ?>, StructureStart> map) {
        long i = chunkcoordintpair.toLong();
        Object2IntMap<StructureFeature<?, ?>> object2intmap = new Object2IntOpenHashMap();

        map.forEach((structurefeature, structurestart) -> {
            if (structurestart.isValid()) {
                object2intmap.put(structurefeature, structurestart.getReferences());
            }

        });
        this.storeFullResults(i, object2intmap);
    }

    private void storeFullResults(long i, Object2IntMap<StructureFeature<?, ?>> object2intmap) {
        this.loadedChunks.put(i, deduplicateEmptyMap(object2intmap));
        this.featureChecks.values().forEach((long2booleanmap) -> {
            long2booleanmap.remove(i);
        });
    }

    public void incrementReference(ChunkCoordIntPair chunkcoordintpair, StructureFeature<?, ?> structurefeature) {
        this.loadedChunks.compute(chunkcoordintpair.toLong(), (olong, object2intmap) -> {
            if (object2intmap == null || ((Object2IntMap) object2intmap).isEmpty()) {
                object2intmap = new Object2IntOpenHashMap();
            }

            ((Object2IntMap) object2intmap).computeInt(structurefeature, (structurefeature1, integer) -> {
                return integer == null ? 1 : integer + 1;
            });
            return (Object2IntMap) object2intmap;
        });
    }
}
