package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.ChunkProviderServer;
import net.minecraft.server.level.LightEngineThreaded;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkConverter;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.DataPaletteBlock;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.NibbleArray;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.ProtoChunkExtension;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.ProtoChunkTickList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRegionLoader {

    public static final Codec<DataPaletteBlock<IBlockData>> BLOCK_STATE_CODEC = DataPaletteBlock.codec(Block.BLOCK_STATE_REGISTRY, IBlockData.CODEC, DataPaletteBlock.e.SECTION_STATES, Blocks.AIR.defaultBlockState());
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String TAG_UPGRADE_DATA = "UpgradeData";
    private static final String BLOCK_TICKS_TAG = "block_ticks";
    private static final String FLUID_TICKS_TAG = "fluid_ticks";

    public ChunkRegionLoader() {}

    public static ProtoChunk read(WorldServer worldserver, VillagePlace villageplace, ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) {
        ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(nbttagcompound.getInt("xPos"), nbttagcompound.getInt("zPos"));

        if (!Objects.equals(chunkcoordintpair, chunkcoordintpair1)) {
            ChunkRegionLoader.LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", chunkcoordintpair, chunkcoordintpair, chunkcoordintpair1);
        }

        ChunkConverter chunkconverter = nbttagcompound.contains("UpgradeData", 10) ? new ChunkConverter(nbttagcompound.getCompound("UpgradeData"), worldserver) : ChunkConverter.EMPTY;
        boolean flag = nbttagcompound.getBoolean("isLightOn");
        NBTTagList nbttaglist = nbttagcompound.getList("sections", 10);
        int i = worldserver.getSectionsCount();
        ChunkSection[] achunksection = new ChunkSection[i];
        boolean flag1 = worldserver.dimensionType().hasSkyLight();
        ChunkProviderServer chunkproviderserver = worldserver.getChunkSource();
        LightEngine lightengine = chunkproviderserver.getLightEngine();

        if (flag) {
            lightengine.retainData(chunkcoordintpair, true);
        }

        IRegistry<BiomeBase> iregistry = worldserver.registryAccess().registryOrThrow(IRegistry.BIOME_REGISTRY);
        Codec<DataPaletteBlock<BiomeBase>> codec = makeBiomeCodec(iregistry);

        DataResult dataresult;

        for (int j = 0; j < nbttaglist.size(); ++j) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(j);
            byte b0 = nbttagcompound1.getByte("Y");
            int k = worldserver.getSectionIndexFromSectionY(b0);

            if (k >= 0 && k < achunksection.length) {
                Logger logger;
                DataPaletteBlock datapaletteblock;

                if (nbttagcompound1.contains("block_states", 10)) {
                    dataresult = ChunkRegionLoader.BLOCK_STATE_CODEC.parse(DynamicOpsNBT.INSTANCE, nbttagcompound1.getCompound("block_states")).promotePartial((s) -> {
                        logErrors(chunkcoordintpair, b0, s);
                    });
                    logger = ChunkRegionLoader.LOGGER;
                    Objects.requireNonNull(logger);
                    datapaletteblock = (DataPaletteBlock) dataresult.getOrThrow(false, logger::error);
                } else {
                    datapaletteblock = new DataPaletteBlock<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), DataPaletteBlock.e.SECTION_STATES);
                }

                DataPaletteBlock datapaletteblock1;

                if (nbttagcompound1.contains("biomes", 10)) {
                    dataresult = codec.parse(DynamicOpsNBT.INSTANCE, nbttagcompound1.getCompound("biomes")).promotePartial((s) -> {
                        logErrors(chunkcoordintpair, b0, s);
                    });
                    logger = ChunkRegionLoader.LOGGER;
                    Objects.requireNonNull(logger);
                    datapaletteblock1 = (DataPaletteBlock) dataresult.getOrThrow(false, logger::error);
                } else {
                    datapaletteblock1 = new DataPaletteBlock<>(iregistry, (BiomeBase) iregistry.getOrThrow(Biomes.PLAINS), DataPaletteBlock.e.SECTION_BIOMES);
                }

                ChunkSection chunksection = new ChunkSection(b0, datapaletteblock, datapaletteblock1);

                achunksection[k] = chunksection;
                villageplace.checkConsistencyWithBlocks(chunkcoordintpair, chunksection);
            }

            if (flag) {
                if (nbttagcompound1.contains("BlockLight", 7)) {
                    lightengine.queueSectionData(EnumSkyBlock.BLOCK, SectionPosition.of(chunkcoordintpair, b0), new NibbleArray(nbttagcompound1.getByteArray("BlockLight")), true);
                }

                if (flag1 && nbttagcompound1.contains("SkyLight", 7)) {
                    lightengine.queueSectionData(EnumSkyBlock.SKY, SectionPosition.of(chunkcoordintpair, b0), new NibbleArray(nbttagcompound1.getByteArray("SkyLight")), true);
                }
            }
        }

        long l = nbttagcompound.getLong("InhabitedTime");
        ChunkStatus.Type chunkstatus_type = getChunkTypeFromTag(nbttagcompound);
        Logger logger1;
        BlendingData blendingdata;

        if (nbttagcompound.contains("blending_data", 10)) {
            dataresult = BlendingData.CODEC.parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.getCompound("blending_data")));
            logger1 = ChunkRegionLoader.LOGGER;
            Objects.requireNonNull(logger1);
            blendingdata = (BlendingData) dataresult.resultOrPartial(logger1::error).orElse((Object) null);
        } else {
            blendingdata = null;
        }

        Object object;

        if (chunkstatus_type == ChunkStatus.Type.LEVELCHUNK) {
            LevelChunkTicks<Block> levelchunkticks = LevelChunkTicks.load(nbttagcompound.getList("block_ticks", 10), (s) -> {
                return IRegistry.BLOCK.getOptional(MinecraftKey.tryParse(s));
            }, chunkcoordintpair);
            LevelChunkTicks<FluidType> levelchunkticks1 = LevelChunkTicks.load(nbttagcompound.getList("fluid_ticks", 10), (s) -> {
                return IRegistry.FLUID.getOptional(MinecraftKey.tryParse(s));
            }, chunkcoordintpair);

            object = new Chunk(worldserver.getLevel(), chunkcoordintpair, chunkconverter, levelchunkticks, levelchunkticks1, l, achunksection, postLoadChunk(worldserver, nbttagcompound), blendingdata);
        } else {
            ProtoChunkTickList<Block> protochunkticklist = ProtoChunkTickList.load(nbttagcompound.getList("block_ticks", 10), (s) -> {
                return IRegistry.BLOCK.getOptional(MinecraftKey.tryParse(s));
            }, chunkcoordintpair);
            ProtoChunkTickList<FluidType> protochunkticklist1 = ProtoChunkTickList.load(nbttagcompound.getList("fluid_ticks", 10), (s) -> {
                return IRegistry.FLUID.getOptional(MinecraftKey.tryParse(s));
            }, chunkcoordintpair);
            ProtoChunk protochunk = new ProtoChunk(chunkcoordintpair, chunkconverter, achunksection, protochunkticklist, protochunkticklist1, worldserver, iregistry, blendingdata);

            object = protochunk;
            protochunk.setInhabitedTime(l);
            if (nbttagcompound.contains("below_zero_retrogen", 10)) {
                dataresult = BelowZeroRetrogen.CODEC.parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.getCompound("below_zero_retrogen")));
                logger1 = ChunkRegionLoader.LOGGER;
                Objects.requireNonNull(logger1);
                Optional optional = dataresult.resultOrPartial(logger1::error);

                Objects.requireNonNull(protochunk);
                optional.ifPresent(protochunk::setBelowZeroRetrogen);
            }

            ChunkStatus chunkstatus = ChunkStatus.byName(nbttagcompound.getString("Status"));

            protochunk.setStatus(chunkstatus);
            if (chunkstatus.isOrAfter(ChunkStatus.FEATURES)) {
                protochunk.setLightEngine(lightengine);
            }

            BelowZeroRetrogen belowzeroretrogen = protochunk.getBelowZeroRetrogen();
            boolean flag2 = chunkstatus.isOrAfter(ChunkStatus.LIGHT) || belowzeroretrogen != null && belowzeroretrogen.targetStatus().isOrAfter(ChunkStatus.LIGHT);

            if (!flag && flag2) {
                Iterator iterator = BlockPosition.betweenClosed(chunkcoordintpair.getMinBlockX(), worldserver.getMinBuildHeight(), chunkcoordintpair.getMinBlockZ(), chunkcoordintpair.getMaxBlockX(), worldserver.getMaxBuildHeight() - 1, chunkcoordintpair.getMaxBlockZ()).iterator();

                while (iterator.hasNext()) {
                    BlockPosition blockposition = (BlockPosition) iterator.next();

                    if (((IChunkAccess) object).getBlockState(blockposition).getLightEmission() != 0) {
                        protochunk.addLight(blockposition);
                    }
                }
            }
        }

        ((IChunkAccess) object).setLightCorrect(flag);
        NBTTagCompound nbttagcompound2 = nbttagcompound.getCompound("Heightmaps");
        EnumSet<HeightMap.Type> enumset = EnumSet.noneOf(HeightMap.Type.class);
        Iterator iterator1 = ((IChunkAccess) object).getStatus().heightmapsAfter().iterator();

        while (iterator1.hasNext()) {
            HeightMap.Type heightmap_type = (HeightMap.Type) iterator1.next();
            String s = heightmap_type.getSerializationKey();

            if (nbttagcompound2.contains(s, 12)) {
                ((IChunkAccess) object).setHeightmap(heightmap_type, nbttagcompound2.getLongArray(s));
            } else {
                enumset.add(heightmap_type);
            }
        }

        HeightMap.primeHeightmaps((IChunkAccess) object, enumset);
        NBTTagCompound nbttagcompound3 = nbttagcompound.getCompound("structures");

        ((IChunkAccess) object).setAllStarts(unpackStructureStart(StructurePieceSerializationContext.fromLevel(worldserver), nbttagcompound3, worldserver.getSeed()));
        ((IChunkAccess) object).setAllReferences(unpackStructureReferences(chunkcoordintpair, nbttagcompound3));
        if (nbttagcompound.getBoolean("shouldSave")) {
            ((IChunkAccess) object).setUnsaved(true);
        }

        NBTTagList nbttaglist1 = nbttagcompound.getList("PostProcessing", 9);

        NBTTagList nbttaglist2;
        int i1;

        for (int j1 = 0; j1 < nbttaglist1.size(); ++j1) {
            nbttaglist2 = nbttaglist1.getList(j1);

            for (i1 = 0; i1 < nbttaglist2.size(); ++i1) {
                ((IChunkAccess) object).addPackedPostProcess(nbttaglist2.getShort(i1), j1);
            }
        }

        if (chunkstatus_type == ChunkStatus.Type.LEVELCHUNK) {
            return new ProtoChunkExtension((Chunk) object, false);
        } else {
            ProtoChunk protochunk1 = (ProtoChunk) object;

            nbttaglist2 = nbttagcompound.getList("entities", 10);

            for (i1 = 0; i1 < nbttaglist2.size(); ++i1) {
                protochunk1.addEntity(nbttaglist2.getCompound(i1));
            }

            NBTTagList nbttaglist3 = nbttagcompound.getList("block_entities", 10);

            NBTTagCompound nbttagcompound4;

            for (int k1 = 0; k1 < nbttaglist3.size(); ++k1) {
                nbttagcompound4 = nbttaglist3.getCompound(k1);
                ((IChunkAccess) object).setBlockEntityNbt(nbttagcompound4);
            }

            NBTTagList nbttaglist4 = nbttagcompound.getList("Lights", 9);

            for (int l1 = 0; l1 < nbttaglist4.size(); ++l1) {
                NBTTagList nbttaglist5 = nbttaglist4.getList(l1);

                for (int i2 = 0; i2 < nbttaglist5.size(); ++i2) {
                    protochunk1.addLight(nbttaglist5.getShort(i2), l1);
                }
            }

            nbttagcompound4 = nbttagcompound.getCompound("CarvingMasks");
            Iterator iterator2 = nbttagcompound4.getAllKeys().iterator();

            while (iterator2.hasNext()) {
                String s1 = (String) iterator2.next();
                WorldGenStage.Features worldgenstage_features = WorldGenStage.Features.valueOf(s1);

                protochunk1.setCarvingMask(worldgenstage_features, new CarvingMask(nbttagcompound4.getLongArray(s1), ((IChunkAccess) object).getMinBuildHeight()));
            }

            return protochunk1;
        }
    }

    private static void logErrors(ChunkCoordIntPair chunkcoordintpair, int i, String s) {
        ChunkRegionLoader.LOGGER.error("Recoverable errors when loading section [" + chunkcoordintpair.x + ", " + i + ", " + chunkcoordintpair.z + "]: " + s);
    }

    private static Codec<DataPaletteBlock<BiomeBase>> makeBiomeCodec(IRegistry<BiomeBase> iregistry) {
        return DataPaletteBlock.codec(iregistry, iregistry.byNameCodec(), DataPaletteBlock.e.SECTION_BIOMES, (BiomeBase) iregistry.getOrThrow(Biomes.PLAINS));
    }

    public static NBTTagCompound write(WorldServer worldserver, IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        nbttagcompound.putInt("xPos", chunkcoordintpair.x);
        nbttagcompound.putInt("yPos", ichunkaccess.getMinSection());
        nbttagcompound.putInt("zPos", chunkcoordintpair.z);
        nbttagcompound.putLong("LastUpdate", worldserver.getGameTime());
        nbttagcompound.putLong("InhabitedTime", ichunkaccess.getInhabitedTime());
        nbttagcompound.putString("Status", ichunkaccess.getStatus().getName());
        BlendingData blendingdata = ichunkaccess.getBlendingData();
        DataResult dataresult;
        Logger logger;

        if (blendingdata != null) {
            dataresult = BlendingData.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, blendingdata);
            logger = ChunkRegionLoader.LOGGER;
            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
                nbttagcompound.put("blending_data", nbtbase);
            });
        }

        BelowZeroRetrogen belowzeroretrogen = ichunkaccess.getBelowZeroRetrogen();

        if (belowzeroretrogen != null) {
            dataresult = BelowZeroRetrogen.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, belowzeroretrogen);
            logger = ChunkRegionLoader.LOGGER;
            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
                nbttagcompound.put("below_zero_retrogen", nbtbase);
            });
        }

        ChunkConverter chunkconverter = ichunkaccess.getUpgradeData();

        if (!chunkconverter.isEmpty()) {
            nbttagcompound.put("UpgradeData", chunkconverter.write());
        }

        ChunkSection[] achunksection = ichunkaccess.getSections();
        NBTTagList nbttaglist = new NBTTagList();
        LightEngineThreaded lightenginethreaded = worldserver.getChunkSource().getLightEngine();
        IRegistry<BiomeBase> iregistry = worldserver.registryAccess().registryOrThrow(IRegistry.BIOME_REGISTRY);
        Codec<DataPaletteBlock<BiomeBase>> codec = makeBiomeCodec(iregistry);
        boolean flag = ichunkaccess.isLightCorrect();

        for (int i = lightenginethreaded.getMinLightSection(); i < lightenginethreaded.getMaxLightSection(); ++i) {
            int j = ichunkaccess.getSectionIndexFromSectionY(i);
            boolean flag1 = j >= 0 && j < achunksection.length;
            NibbleArray nibblearray = lightenginethreaded.getLayerListener(EnumSkyBlock.BLOCK).getDataLayerData(SectionPosition.of(chunkcoordintpair, i));
            NibbleArray nibblearray1 = lightenginethreaded.getLayerListener(EnumSkyBlock.SKY).getDataLayerData(SectionPosition.of(chunkcoordintpair, i));

            if (flag1 || nibblearray != null || nibblearray1 != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                if (flag1) {
                    ChunkSection chunksection = achunksection[j];
                    DataResult dataresult1 = ChunkRegionLoader.BLOCK_STATE_CODEC.encodeStart(DynamicOpsNBT.INSTANCE, chunksection.getStates());
                    Logger logger1 = ChunkRegionLoader.LOGGER;

                    Objects.requireNonNull(logger1);
                    nbttagcompound1.put("block_states", (NBTBase) dataresult1.getOrThrow(false, logger1::error));
                    dataresult1 = codec.encodeStart(DynamicOpsNBT.INSTANCE, chunksection.getBiomes());
                    logger1 = ChunkRegionLoader.LOGGER;
                    Objects.requireNonNull(logger1);
                    nbttagcompound1.put("biomes", (NBTBase) dataresult1.getOrThrow(false, logger1::error));
                }

                if (nibblearray != null && !nibblearray.isEmpty()) {
                    nbttagcompound1.putByteArray("BlockLight", nibblearray.getData());
                }

                if (nibblearray1 != null && !nibblearray1.isEmpty()) {
                    nbttagcompound1.putByteArray("SkyLight", nibblearray1.getData());
                }

                if (!nbttagcompound1.isEmpty()) {
                    nbttagcompound1.putByte("Y", (byte) i);
                    nbttaglist.add(nbttagcompound1);
                }
            }
        }

        nbttagcompound.put("sections", nbttaglist);
        if (flag) {
            nbttagcompound.putBoolean("isLightOn", true);
        }

        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator = ichunkaccess.getBlockEntitiesPos().iterator();

        NBTTagCompound nbttagcompound2;

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            nbttagcompound2 = ichunkaccess.getBlockEntityNbtForSaving(blockposition);
            if (nbttagcompound2 != null) {
                nbttaglist1.add(nbttagcompound2);
            }
        }

        nbttagcompound.put("block_entities", nbttaglist1);
        if (ichunkaccess.getStatus().getChunkType() == ChunkStatus.Type.PROTOCHUNK) {
            ProtoChunk protochunk = (ProtoChunk) ichunkaccess;
            NBTTagList nbttaglist2 = new NBTTagList();

            nbttaglist2.addAll(protochunk.getEntities());
            nbttagcompound.put("entities", nbttaglist2);
            nbttagcompound.put("Lights", packOffsets(protochunk.getPackedLights()));
            nbttagcompound2 = new NBTTagCompound();
            WorldGenStage.Features[] aworldgenstage_features = WorldGenStage.Features.values();
            int k = aworldgenstage_features.length;

            for (int l = 0; l < k; ++l) {
                WorldGenStage.Features worldgenstage_features = aworldgenstage_features[l];
                CarvingMask carvingmask = protochunk.getCarvingMask(worldgenstage_features);

                if (carvingmask != null) {
                    nbttagcompound2.putLongArray(worldgenstage_features.toString(), carvingmask.toArray());
                }
            }

            nbttagcompound.put("CarvingMasks", nbttagcompound2);
        }

        saveTicks(worldserver, nbttagcompound, ichunkaccess.getTicksForSerialization());
        nbttagcompound.put("PostProcessing", packOffsets(ichunkaccess.getPostProcessing()));
        NBTTagCompound nbttagcompound3 = new NBTTagCompound();
        Iterator iterator1 = ichunkaccess.getHeightmaps().iterator();

        while (iterator1.hasNext()) {
            Entry<HeightMap.Type, HeightMap> entry = (Entry) iterator1.next();

            if (ichunkaccess.getStatus().heightmapsAfter().contains(entry.getKey())) {
                nbttagcompound3.put(((HeightMap.Type) entry.getKey()).getSerializationKey(), new NBTTagLongArray(((HeightMap) entry.getValue()).getRawData()));
            }
        }

        nbttagcompound.put("Heightmaps", nbttagcompound3);
        nbttagcompound.put("structures", packStructureData(StructurePieceSerializationContext.fromLevel(worldserver), chunkcoordintpair, ichunkaccess.getAllStarts(), ichunkaccess.getAllReferences()));
        return nbttagcompound;
    }

    private static void saveTicks(WorldServer worldserver, NBTTagCompound nbttagcompound, IChunkAccess.a ichunkaccess_a) {
        long i = worldserver.getLevelData().getGameTime();

        nbttagcompound.put("block_ticks", ichunkaccess_a.blocks().save(i, (block) -> {
            return IRegistry.BLOCK.getKey(block).toString();
        }));
        nbttagcompound.put("fluid_ticks", ichunkaccess_a.fluids().save(i, (fluidtype) -> {
            return IRegistry.FLUID.getKey(fluidtype).toString();
        }));
    }

    public static ChunkStatus.Type getChunkTypeFromTag(@Nullable NBTTagCompound nbttagcompound) {
        return nbttagcompound != null ? ChunkStatus.byName(nbttagcompound.getString("Status")).getChunkType() : ChunkStatus.Type.PROTOCHUNK;
    }

    @Nullable
    private static Chunk.c postLoadChunk(WorldServer worldserver, NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist = getListOfCompoundsOrNull(nbttagcompound, "entities");
        NBTTagList nbttaglist1 = getListOfCompoundsOrNull(nbttagcompound, "block_entities");

        return nbttaglist == null && nbttaglist1 == null ? null : (chunk) -> {
            if (nbttaglist != null) {
                worldserver.addLegacyChunkEntities(EntityTypes.loadEntitiesRecursive(nbttaglist, worldserver));
            }

            if (nbttaglist1 != null) {
                for (int i = 0; i < nbttaglist1.size(); ++i) {
                    NBTTagCompound nbttagcompound1 = nbttaglist1.getCompound(i);
                    boolean flag = nbttagcompound1.getBoolean("keepPacked");

                    if (flag) {
                        chunk.setBlockEntityNbt(nbttagcompound1);
                    } else {
                        BlockPosition blockposition = TileEntity.getPosFromTag(nbttagcompound1);
                        TileEntity tileentity = TileEntity.loadStatic(blockposition, chunk.getBlockState(blockposition), nbttagcompound1);

                        if (tileentity != null) {
                            chunk.setBlockEntity(tileentity);
                        }
                    }
                }
            }

        };
    }

    @Nullable
    private static NBTTagList getListOfCompoundsOrNull(NBTTagCompound nbttagcompound, String s) {
        NBTTagList nbttaglist = nbttagcompound.getList(s, 10);

        return nbttaglist.isEmpty() ? null : nbttaglist;
    }

    private static NBTTagCompound packStructureData(StructurePieceSerializationContext structurepieceserializationcontext, ChunkCoordIntPair chunkcoordintpair, Map<StructureGenerator<?>, StructureStart<?>> map, Map<StructureGenerator<?>, LongSet> map1) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<StructureGenerator<?>, StructureStart<?>> entry = (Entry) iterator.next();

            nbttagcompound1.put(((StructureGenerator) entry.getKey()).getFeatureName(), ((StructureStart) entry.getValue()).createTag(structurepieceserializationcontext, chunkcoordintpair));
        }

        nbttagcompound.put("starts", nbttagcompound1);
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();
        Iterator iterator1 = map1.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry<StructureGenerator<?>, LongSet> entry1 = (Entry) iterator1.next();

            nbttagcompound2.put(((StructureGenerator) entry1.getKey()).getFeatureName(), new NBTTagLongArray((LongSet) entry1.getValue()));
        }

        nbttagcompound.put("References", nbttagcompound2);
        return nbttagcompound;
    }

    private static Map<StructureGenerator<?>, StructureStart<?>> unpackStructureStart(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound, long i) {
        Map<StructureGenerator<?>, StructureStart<?>> map = Maps.newHashMap();
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("starts");
        Iterator iterator = nbttagcompound1.getAllKeys().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            String s1 = s.toLowerCase(Locale.ROOT);
            StructureGenerator<?> structuregenerator = (StructureGenerator) StructureGenerator.STRUCTURES_REGISTRY.get(s1);

            if (structuregenerator == null) {
                ChunkRegionLoader.LOGGER.error("Unknown structure start: {}", s1);
            } else {
                StructureStart<?> structurestart = StructureGenerator.loadStaticStart(structurepieceserializationcontext, nbttagcompound1.getCompound(s), i);

                if (structurestart != null) {
                    map.put(structuregenerator, structurestart);
                }
            }
        }

        return map;
    }

    private static Map<StructureGenerator<?>, LongSet> unpackStructureReferences(ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) {
        Map<StructureGenerator<?>, LongSet> map = Maps.newHashMap();
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("References");
        Iterator iterator = nbttagcompound1.getAllKeys().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            String s1 = s.toLowerCase(Locale.ROOT);
            StructureGenerator<?> structuregenerator = (StructureGenerator) StructureGenerator.STRUCTURES_REGISTRY.get(s1);

            if (structuregenerator == null) {
                ChunkRegionLoader.LOGGER.warn("Found reference to unknown structure '{}' in chunk {}, discarding", s1, chunkcoordintpair);
            } else {
                map.put(structuregenerator, new LongOpenHashSet(Arrays.stream(nbttagcompound1.getLongArray(s)).filter((i) -> {
                    ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(i);

                    if (chunkcoordintpair1.getChessboardDistance(chunkcoordintpair) > 8) {
                        ChunkRegionLoader.LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", s1, chunkcoordintpair1, chunkcoordintpair);
                        return false;
                    } else {
                        return true;
                    }
                }).toArray()));
            }
        }

        return map;
    }

    public static NBTTagList packOffsets(ShortList[] ashortlist) {
        NBTTagList nbttaglist = new NBTTagList();
        ShortList[] ashortlist1 = ashortlist;
        int i = ashortlist.length;

        for (int j = 0; j < i; ++j) {
            ShortList shortlist = ashortlist1[j];
            NBTTagList nbttaglist1 = new NBTTagList();

            if (shortlist != null) {
                ShortListIterator shortlistiterator = shortlist.iterator();

                while (shortlistiterator.hasNext()) {
                    Short oshort = (Short) shortlistiterator.next();

                    nbttaglist1.add(NBTTagShort.valueOf(oshort));
                }
            }

            nbttaglist.add(nbttaglist1);
        }

        return nbttaglist;
    }
}
