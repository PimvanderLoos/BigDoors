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
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

public class StructureCheck {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int NO_STRUCTURE = -1;
    private final ChunkScanAccess storageAccess;
    private final IRegistryCustom registryAccess;
    private final IRegistry<BiomeBase> biomes;
    private final IRegistry<Structure> structureConfigs;
    private final StructureTemplateManager structureTemplateManager;
    private final ResourceKey<World> dimension;
    private final ChunkGenerator chunkGenerator;
    private final RandomState randomState;
    private final LevelHeightAccessor heightAccessor;
    private final WorldChunkManager biomeSource;
    private final long seed;
    private final DataFixer fixerUpper;
    private final Long2ObjectMap<Object2IntMap<Structure>> loadedChunks = new Long2ObjectOpenHashMap();
    private final Map<Structure, Long2BooleanMap> featureChecks = new HashMap();

    public StructureCheck(ChunkScanAccess chunkscanaccess, IRegistryCustom iregistrycustom, StructureTemplateManager structuretemplatemanager, ResourceKey<World> resourcekey, ChunkGenerator chunkgenerator, RandomState randomstate, LevelHeightAccessor levelheightaccessor, WorldChunkManager worldchunkmanager, long i, DataFixer datafixer) {
        this.storageAccess = chunkscanaccess;
        this.registryAccess = iregistrycustom;
        this.structureTemplateManager = structuretemplatemanager;
        this.dimension = resourcekey;
        this.chunkGenerator = chunkgenerator;
        this.randomState = randomstate;
        this.heightAccessor = levelheightaccessor;
        this.biomeSource = worldchunkmanager;
        this.seed = i;
        this.fixerUpper = datafixer;
        this.biomes = iregistrycustom.registryOrThrow(Registries.BIOME);
        this.structureConfigs = iregistrycustom.registryOrThrow(Registries.STRUCTURE);
    }

    public StructureCheckResult checkStart(ChunkCoordIntPair chunkcoordintpair, Structure structure, boolean flag) {
        long i = chunkcoordintpair.toLong();
        Object2IntMap<Structure> object2intmap = (Object2IntMap) this.loadedChunks.get(i);

        if (object2intmap != null) {
            return this.checkStructureInfo(object2intmap, structure, flag);
        } else {
            StructureCheckResult structurecheckresult = this.tryLoadFromStorage(chunkcoordintpair, structure, flag, i);

            if (structurecheckresult != null) {
                return structurecheckresult;
            } else {
                boolean flag1 = ((Long2BooleanMap) this.featureChecks.computeIfAbsent(structure, (structure1) -> {
                    return new Long2BooleanOpenHashMap();
                })).computeIfAbsent(i, (j) -> {
                    return this.canCreateStructure(chunkcoordintpair, structure);
                });

                return !flag1 ? StructureCheckResult.START_NOT_PRESENT : StructureCheckResult.CHUNK_LOAD_NEEDED;
            }
        }
    }

    private boolean canCreateStructure(ChunkCoordIntPair chunkcoordintpair, Structure structure) {
        IRegistryCustom iregistrycustom = this.registryAccess;
        ChunkGenerator chunkgenerator = this.chunkGenerator;
        WorldChunkManager worldchunkmanager = this.biomeSource;
        RandomState randomstate = this.randomState;
        StructureTemplateManager structuretemplatemanager = this.structureTemplateManager;
        long i = this.seed;
        LevelHeightAccessor levelheightaccessor = this.heightAccessor;
        HolderSet holderset = structure.biomes();

        Objects.requireNonNull(holderset);
        return structure.findValidGenerationPoint(new Structure.a(iregistrycustom, chunkgenerator, worldchunkmanager, randomstate, structuretemplatemanager, i, chunkcoordintpair, levelheightaccessor, holderset::contains)).isPresent();
    }

    @Nullable
    private StructureCheckResult tryLoadFromStorage(ChunkCoordIntPair chunkcoordintpair, Structure structure, boolean flag, long i) {
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
                    nbttagcompound1 = DataFixTypes.CHUNK.updateToCurrentVersion(this.fixerUpper, nbttagcompound, j);
                } catch (Exception exception1) {
                    StructureCheck.LOGGER.warn("Failed to partially datafix chunk {}", chunkcoordintpair, exception1);
                    return StructureCheckResult.CHUNK_LOAD_NEEDED;
                }

                Object2IntMap<Structure> object2intmap = this.loadStructures(nbttagcompound1);

                if (object2intmap == null) {
                    return null;
                } else {
                    this.storeFullResults(i, object2intmap);
                    return this.checkStructureInfo(object2intmap, structure, flag);
                }
            }
        }
    }

    @Nullable
    private Object2IntMap<Structure> loadStructures(NBTTagCompound nbttagcompound) {
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
                    Object2IntMap<Structure> object2intmap = new Object2IntOpenHashMap();
                    IRegistry<Structure> iregistry = this.registryAccess.registryOrThrow(Registries.STRUCTURE);
                    Iterator iterator = nbttagcompound2.getAllKeys().iterator();

                    while (iterator.hasNext()) {
                        String s = (String) iterator.next();
                        MinecraftKey minecraftkey = MinecraftKey.tryParse(s);

                        if (minecraftkey != null) {
                            Structure structure = (Structure) iregistry.get(minecraftkey);

                            if (structure != null) {
                                NBTTagCompound nbttagcompound3 = nbttagcompound2.getCompound(s);

                                if (!nbttagcompound3.isEmpty()) {
                                    String s1 = nbttagcompound3.getString("id");

                                    if (!"INVALID".equals(s1)) {
                                        int i = nbttagcompound3.getInt("references");

                                        object2intmap.put(structure, i);
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

    private static Object2IntMap<Structure> deduplicateEmptyMap(Object2IntMap<Structure> object2intmap) {
        return object2intmap.isEmpty() ? Object2IntMaps.emptyMap() : object2intmap;
    }

    private StructureCheckResult checkStructureInfo(Object2IntMap<Structure> object2intmap, Structure structure, boolean flag) {
        int i = object2intmap.getOrDefault(structure, -1);

        return i != -1 && (!flag || i == 0) ? StructureCheckResult.START_PRESENT : StructureCheckResult.START_NOT_PRESENT;
    }

    public void onStructureLoad(ChunkCoordIntPair chunkcoordintpair, Map<Structure, StructureStart> map) {
        long i = chunkcoordintpair.toLong();
        Object2IntMap<Structure> object2intmap = new Object2IntOpenHashMap();

        map.forEach((structure, structurestart) -> {
            if (structurestart.isValid()) {
                object2intmap.put(structure, structurestart.getReferences());
            }

        });
        this.storeFullResults(i, object2intmap);
    }

    private void storeFullResults(long i, Object2IntMap<Structure> object2intmap) {
        this.loadedChunks.put(i, deduplicateEmptyMap(object2intmap));
        this.featureChecks.values().forEach((long2booleanmap) -> {
            long2booleanmap.remove(i);
        });
    }

    public void incrementReference(ChunkCoordIntPair chunkcoordintpair, Structure structure) {
        this.loadedChunks.compute(chunkcoordintpair.toLong(), (olong, object2intmap) -> {
            if (object2intmap == null || ((Object2IntMap) object2intmap).isEmpty()) {
                object2intmap = new Object2IntOpenHashMap();
            }

            ((Object2IntMap) object2intmap).computeInt(structure, (structure1, integer) -> {
                return integer == null ? 1 : integer + 1;
            });
            return (Object2IntMap) object2intmap;
        });
    }
}
