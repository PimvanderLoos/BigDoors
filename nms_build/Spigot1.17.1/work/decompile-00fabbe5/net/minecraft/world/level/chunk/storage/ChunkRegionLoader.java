package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.server.level.ChunkProviderServer;
import net.minecraft.server.level.LightEngineThreaded;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickListChunk;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.chunk.BiomeStorage;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkConverter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.NibbleArray;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.ProtoChunkExtension;
import net.minecraft.world.level.chunk.ProtoChunkTickList;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRegionLoader {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String TAG_UPGRADE_DATA = "UpgradeData";

    public ChunkRegionLoader() {}

    public static ProtoChunk loadChunk(WorldServer worldserver, DefinedStructureManager definedstructuremanager, VillagePlace villageplace, ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) {
        ChunkGenerator chunkgenerator = worldserver.getChunkProvider().getChunkGenerator();
        WorldChunkManager worldchunkmanager = chunkgenerator.getWorldChunkManager();
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");
        ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(nbttagcompound1.getInt("xPos"), nbttagcompound1.getInt("zPos"));

        if (!Objects.equals(chunkcoordintpair, chunkcoordintpair1)) {
            ChunkRegionLoader.LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", chunkcoordintpair, chunkcoordintpair, chunkcoordintpair1);
        }

        BiomeStorage biomestorage = new BiomeStorage(worldserver.t().d(IRegistry.BIOME_REGISTRY), worldserver, chunkcoordintpair, worldchunkmanager, nbttagcompound1.hasKeyOfType("Biomes", 11) ? nbttagcompound1.getIntArray("Biomes") : null);
        ChunkConverter chunkconverter = nbttagcompound1.hasKeyOfType("UpgradeData", 10) ? new ChunkConverter(nbttagcompound1.getCompound("UpgradeData"), worldserver) : ChunkConverter.EMPTY;
        ProtoChunkTickList<Block> protochunkticklist = new ProtoChunkTickList<>((block) -> {
            return block == null || block.getBlockData().isAir();
        }, chunkcoordintpair, nbttagcompound1.getList("ToBeTicked", 9), worldserver);
        ProtoChunkTickList<FluidType> protochunkticklist1 = new ProtoChunkTickList<>((fluidtype) -> {
            return fluidtype == null || fluidtype == FluidTypes.EMPTY;
        }, chunkcoordintpair, nbttagcompound1.getList("LiquidsToBeTicked", 9), worldserver);
        boolean flag = nbttagcompound1.getBoolean("isLightOn");
        NBTTagList nbttaglist = nbttagcompound1.getList("Sections", 10);
        int i = worldserver.getSectionsCount();
        ChunkSection[] achunksection = new ChunkSection[i];
        boolean flag1 = worldserver.getDimensionManager().hasSkyLight();
        ChunkProviderServer chunkproviderserver = worldserver.getChunkProvider();
        LightEngine lightengine = chunkproviderserver.getLightEngine();

        if (flag) {
            lightengine.b(chunkcoordintpair, true);
        }

        for (int j = 0; j < nbttaglist.size(); ++j) {
            NBTTagCompound nbttagcompound2 = nbttaglist.getCompound(j);
            byte b0 = nbttagcompound2.getByte("Y");

            if (nbttagcompound2.hasKeyOfType("Palette", 9) && nbttagcompound2.hasKeyOfType("BlockStates", 12)) {
                ChunkSection chunksection = new ChunkSection(b0);

                chunksection.getBlocks().a(nbttagcompound2.getList("Palette", 10), nbttagcompound2.getLongArray("BlockStates"));
                chunksection.recalcBlockCounts();
                if (!chunksection.c()) {
                    achunksection[worldserver.getSectionIndexFromSectionY(b0)] = chunksection;
                }

                villageplace.a(chunkcoordintpair, chunksection);
            }

            if (flag) {
                if (nbttagcompound2.hasKeyOfType("BlockLight", 7)) {
                    lightengine.a(EnumSkyBlock.BLOCK, SectionPosition.a(chunkcoordintpair, b0), new NibbleArray(nbttagcompound2.getByteArray("BlockLight")), true);
                }

                if (flag1 && nbttagcompound2.hasKeyOfType("SkyLight", 7)) {
                    lightengine.a(EnumSkyBlock.SKY, SectionPosition.a(chunkcoordintpair, b0), new NibbleArray(nbttagcompound2.getByteArray("SkyLight")), true);
                }
            }
        }

        long k = nbttagcompound1.getLong("InhabitedTime");
        ChunkStatus.Type chunkstatus_type = a(nbttagcompound);
        Object object;

        if (chunkstatus_type == ChunkStatus.Type.LEVELCHUNK) {
            NBTTagList nbttaglist1;
            RegistryBlocks registryblocks;
            Function function;
            RegistryBlocks registryblocks1;
            Object object1;

            if (nbttagcompound1.hasKeyOfType("TileTicks", 9)) {
                nbttaglist1 = nbttagcompound1.getList("TileTicks", 10);
                registryblocks = IRegistry.BLOCK;
                Objects.requireNonNull(registryblocks);
                function = registryblocks::getKey;
                registryblocks1 = IRegistry.BLOCK;
                Objects.requireNonNull(registryblocks1);
                object1 = TickListChunk.a(nbttaglist1, function, registryblocks1::get);
            } else {
                object1 = protochunkticklist;
            }

            Object object2;

            if (nbttagcompound1.hasKeyOfType("LiquidTicks", 9)) {
                nbttaglist1 = nbttagcompound1.getList("LiquidTicks", 10);
                registryblocks = IRegistry.FLUID;
                Objects.requireNonNull(registryblocks);
                function = registryblocks::getKey;
                registryblocks1 = IRegistry.FLUID;
                Objects.requireNonNull(registryblocks1);
                object2 = TickListChunk.a(nbttaglist1, function, registryblocks1::get);
            } else {
                object2 = protochunkticklist1;
            }

            object = new Chunk(worldserver.getLevel(), chunkcoordintpair, biomestorage, chunkconverter, (TickList) object1, (TickList) object2, k, achunksection, (chunk) -> {
                loadEntities(worldserver, nbttagcompound1, chunk);
            });
        } else {
            ProtoChunk protochunk = new ProtoChunk(chunkcoordintpair, chunkconverter, achunksection, protochunkticklist, protochunkticklist1, worldserver);

            protochunk.a(biomestorage);
            object = protochunk;
            protochunk.setInhabitedTime(k);
            protochunk.a(ChunkStatus.a(nbttagcompound1.getString("Status")));
            if (protochunk.getChunkStatus().b(ChunkStatus.FEATURES)) {
                protochunk.a(lightengine);
            }

            if (!flag && protochunk.getChunkStatus().b(ChunkStatus.LIGHT)) {
                Iterator iterator = BlockPosition.b(chunkcoordintpair.d(), worldserver.getMinBuildHeight(), chunkcoordintpair.e(), chunkcoordintpair.f(), worldserver.getMaxBuildHeight() - 1, chunkcoordintpair.g()).iterator();

                while (iterator.hasNext()) {
                    BlockPosition blockposition = (BlockPosition) iterator.next();

                    if (((IChunkAccess) object).getType(blockposition).f() != 0) {
                        protochunk.j(blockposition);
                    }
                }
            }
        }

        ((IChunkAccess) object).b(flag);
        NBTTagCompound nbttagcompound3 = nbttagcompound1.getCompound("Heightmaps");
        EnumSet<HeightMap.Type> enumset = EnumSet.noneOf(HeightMap.Type.class);
        Iterator iterator1 = ((IChunkAccess) object).getChunkStatus().h().iterator();

        while (iterator1.hasNext()) {
            HeightMap.Type heightmap_type = (HeightMap.Type) iterator1.next();
            String s = heightmap_type.a();

            if (nbttagcompound3.hasKeyOfType(s, 12)) {
                ((IChunkAccess) object).a(heightmap_type, nbttagcompound3.getLongArray(s));
            } else {
                enumset.add(heightmap_type);
            }
        }

        HeightMap.a((IChunkAccess) object, enumset);
        NBTTagCompound nbttagcompound4 = nbttagcompound1.getCompound("Structures");

        ((IChunkAccess) object).a(a(worldserver, nbttagcompound4, worldserver.getSeed()));
        ((IChunkAccess) object).b(a(chunkcoordintpair, nbttagcompound4));
        if (nbttagcompound1.getBoolean("shouldSave")) {
            ((IChunkAccess) object).setNeedsSaving(true);
        }

        NBTTagList nbttaglist2 = nbttagcompound1.getList("PostProcessing", 9);

        NBTTagList nbttaglist3;
        int l;

        for (int i1 = 0; i1 < nbttaglist2.size(); ++i1) {
            nbttaglist3 = nbttaglist2.b(i1);

            for (l = 0; l < nbttaglist3.size(); ++l) {
                ((IChunkAccess) object).a(nbttaglist3.d(l), i1);
            }
        }

        if (chunkstatus_type == ChunkStatus.Type.LEVELCHUNK) {
            return new ProtoChunkExtension((Chunk) object);
        } else {
            ProtoChunk protochunk1 = (ProtoChunk) object;

            nbttaglist3 = nbttagcompound1.getList("Entities", 10);

            for (l = 0; l < nbttaglist3.size(); ++l) {
                protochunk1.b(nbttaglist3.getCompound(l));
            }

            NBTTagList nbttaglist4 = nbttagcompound1.getList("TileEntities", 10);

            NBTTagCompound nbttagcompound5;

            for (int j1 = 0; j1 < nbttaglist4.size(); ++j1) {
                nbttagcompound5 = nbttaglist4.getCompound(j1);
                ((IChunkAccess) object).a(nbttagcompound5);
            }

            NBTTagList nbttaglist5 = nbttagcompound1.getList("Lights", 9);

            for (int k1 = 0; k1 < nbttaglist5.size(); ++k1) {
                NBTTagList nbttaglist6 = nbttaglist5.b(k1);

                for (int l1 = 0; l1 < nbttaglist6.size(); ++l1) {
                    protochunk1.b(nbttaglist6.d(l1), k1);
                }
            }

            nbttagcompound5 = nbttagcompound1.getCompound("CarvingMasks");
            Iterator iterator2 = nbttagcompound5.getKeys().iterator();

            while (iterator2.hasNext()) {
                String s1 = (String) iterator2.next();
                WorldGenStage.Features worldgenstage_features = WorldGenStage.Features.valueOf(s1);

                protochunk1.a(worldgenstage_features, BitSet.valueOf(nbttagcompound5.getByteArray(s1)));
            }

            return protochunk1;
        }
    }

    public static NBTTagCompound saveChunk(WorldServer worldserver, IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        nbttagcompound.setInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        nbttagcompound.set("Level", nbttagcompound1);
        nbttagcompound1.setInt("xPos", chunkcoordintpair.x);
        nbttagcompound1.setInt("zPos", chunkcoordintpair.z);
        nbttagcompound1.setLong("LastUpdate", worldserver.getTime());
        nbttagcompound1.setLong("InhabitedTime", ichunkaccess.getInhabitedTime());
        nbttagcompound1.setString("Status", ichunkaccess.getChunkStatus().d());
        ChunkConverter chunkconverter = ichunkaccess.q();

        if (!chunkconverter.a()) {
            nbttagcompound1.set("UpgradeData", chunkconverter.b());
        }

        ChunkSection[] achunksection = ichunkaccess.getSections();
        NBTTagList nbttaglist = new NBTTagList();
        LightEngineThreaded lightenginethreaded = worldserver.getChunkProvider().getLightEngine();
        boolean flag = ichunkaccess.s();

        for (int i = lightenginethreaded.c(); i < lightenginethreaded.d(); ++i) {
            ChunkSection chunksection = (ChunkSection) Arrays.stream(achunksection).filter((chunksection1) -> {
                return chunksection1 != null && SectionPosition.a(chunksection1.getYPosition()) == i;
            }).findFirst().orElse(Chunk.EMPTY_SECTION);
            NibbleArray nibblearray = lightenginethreaded.a(EnumSkyBlock.BLOCK).a(SectionPosition.a(chunkcoordintpair, i));
            NibbleArray nibblearray1 = lightenginethreaded.a(EnumSkyBlock.SKY).a(SectionPosition.a(chunkcoordintpair, i));

            if (chunksection != Chunk.EMPTY_SECTION || nibblearray != null || nibblearray1 != null) {
                NBTTagCompound nbttagcompound2 = new NBTTagCompound();

                nbttagcompound2.setByte("Y", (byte) (i & 255));
                if (chunksection != Chunk.EMPTY_SECTION) {
                    chunksection.getBlocks().a(nbttagcompound2, "Palette", "BlockStates");
                }

                if (nibblearray != null && !nibblearray.c()) {
                    nbttagcompound2.setByteArray("BlockLight", nibblearray.asBytes());
                }

                if (nibblearray1 != null && !nibblearray1.c()) {
                    nbttagcompound2.setByteArray("SkyLight", nibblearray1.asBytes());
                }

                nbttaglist.add(nbttagcompound2);
            }
        }

        nbttagcompound1.set("Sections", nbttaglist);
        if (flag) {
            nbttagcompound1.setBoolean("isLightOn", true);
        }

        BiomeStorage biomestorage = ichunkaccess.getBiomeIndex();

        if (biomestorage != null) {
            nbttagcompound1.setIntArray("Biomes", biomestorage.a());
        }

        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator = ichunkaccess.c().iterator();

        NBTTagCompound nbttagcompound3;

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            nbttagcompound3 = ichunkaccess.g(blockposition);
            if (nbttagcompound3 != null) {
                nbttaglist1.add(nbttagcompound3);
            }
        }

        nbttagcompound1.set("TileEntities", nbttaglist1);
        if (ichunkaccess.getChunkStatus().getType() == ChunkStatus.Type.PROTOCHUNK) {
            ProtoChunk protochunk = (ProtoChunk) ichunkaccess;
            NBTTagList nbttaglist2 = new NBTTagList();

            nbttaglist2.addAll(protochunk.z());
            nbttagcompound1.set("Entities", nbttaglist2);
            nbttagcompound1.set("Lights", a(protochunk.x()));
            nbttagcompound3 = new NBTTagCompound();
            WorldGenStage.Features[] aworldgenstage_features = WorldGenStage.Features.values();
            int j = aworldgenstage_features.length;

            for (int k = 0; k < j; ++k) {
                WorldGenStage.Features worldgenstage_features = aworldgenstage_features[k];
                BitSet bitset = protochunk.a(worldgenstage_features);

                if (bitset != null) {
                    nbttagcompound3.setByteArray(worldgenstage_features.toString(), bitset.toByteArray());
                }
            }

            nbttagcompound1.set("CarvingMasks", nbttagcompound3);
        }

        TickList<Block> ticklist = ichunkaccess.o();

        if (ticklist instanceof ProtoChunkTickList) {
            nbttagcompound1.set("ToBeTicked", ((ProtoChunkTickList) ticklist).b());
        } else if (ticklist instanceof TickListChunk) {
            nbttagcompound1.set("TileTicks", ((TickListChunk) ticklist).b());
        } else {
            nbttagcompound1.set("TileTicks", worldserver.getBlockTickList().a(chunkcoordintpair));
        }

        TickList<FluidType> ticklist1 = ichunkaccess.p();

        if (ticklist1 instanceof ProtoChunkTickList) {
            nbttagcompound1.set("LiquidsToBeTicked", ((ProtoChunkTickList) ticklist1).b());
        } else if (ticklist1 instanceof TickListChunk) {
            nbttagcompound1.set("LiquidTicks", ((TickListChunk) ticklist1).b());
        } else {
            nbttagcompound1.set("LiquidTicks", worldserver.getFluidTickList().a(chunkcoordintpair));
        }

        nbttagcompound1.set("PostProcessing", a(ichunkaccess.k()));
        nbttagcompound3 = new NBTTagCompound();
        Iterator iterator1 = ichunkaccess.e().iterator();

        while (iterator1.hasNext()) {
            Entry<HeightMap.Type, HeightMap> entry = (Entry) iterator1.next();

            if (ichunkaccess.getChunkStatus().h().contains(entry.getKey())) {
                nbttagcompound3.set(((HeightMap.Type) entry.getKey()).a(), new NBTTagLongArray(((HeightMap) entry.getValue()).a()));
            }
        }

        nbttagcompound1.set("Heightmaps", nbttagcompound3);
        nbttagcompound1.set("Structures", a(worldserver, chunkcoordintpair, ichunkaccess.g(), ichunkaccess.w()));
        return nbttagcompound;
    }

    public static ChunkStatus.Type a(@Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound != null) {
            ChunkStatus chunkstatus = ChunkStatus.a(nbttagcompound.getCompound("Level").getString("Status"));

            if (chunkstatus != null) {
                return chunkstatus.getType();
            }
        }

        return ChunkStatus.Type.PROTOCHUNK;
    }

    private static void loadEntities(WorldServer worldserver, NBTTagCompound nbttagcompound, Chunk chunk) {
        NBTTagList nbttaglist;

        if (nbttagcompound.hasKeyOfType("Entities", 9)) {
            nbttaglist = nbttagcompound.getList("Entities", 10);
            if (!nbttaglist.isEmpty()) {
                worldserver.a(EntityTypes.a((List) nbttaglist, (World) worldserver));
            }
        }

        nbttaglist = nbttagcompound.getList("TileEntities", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
            boolean flag = nbttagcompound1.getBoolean("keepPacked");

            if (flag) {
                chunk.a(nbttagcompound1);
            } else {
                BlockPosition blockposition = new BlockPosition(nbttagcompound1.getInt("x"), nbttagcompound1.getInt("y"), nbttagcompound1.getInt("z"));
                TileEntity tileentity = TileEntity.create(blockposition, chunk.getType(blockposition), nbttagcompound1);

                if (tileentity != null) {
                    chunk.setTileEntity(tileentity);
                }
            }
        }

    }

    private static NBTTagCompound a(WorldServer worldserver, ChunkCoordIntPair chunkcoordintpair, Map<StructureGenerator<?>, StructureStart<?>> map, Map<StructureGenerator<?>, LongSet> map1) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<StructureGenerator<?>, StructureStart<?>> entry = (Entry) iterator.next();

            nbttagcompound1.set(((StructureGenerator) entry.getKey()).g(), ((StructureStart) entry.getValue()).a(worldserver, chunkcoordintpair));
        }

        nbttagcompound.set("Starts", nbttagcompound1);
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();
        Iterator iterator1 = map1.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry<StructureGenerator<?>, LongSet> entry1 = (Entry) iterator1.next();

            nbttagcompound2.set(((StructureGenerator) entry1.getKey()).g(), new NBTTagLongArray((LongSet) entry1.getValue()));
        }

        nbttagcompound.set("References", nbttagcompound2);
        return nbttagcompound;
    }

    private static Map<StructureGenerator<?>, StructureStart<?>> a(WorldServer worldserver, NBTTagCompound nbttagcompound, long i) {
        Map<StructureGenerator<?>, StructureStart<?>> map = Maps.newHashMap();
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Starts");
        Iterator iterator = nbttagcompound1.getKeys().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            String s1 = s.toLowerCase(Locale.ROOT);
            StructureGenerator<?> structuregenerator = (StructureGenerator) StructureGenerator.STRUCTURES_REGISTRY.get(s1);

            if (structuregenerator == null) {
                ChunkRegionLoader.LOGGER.error("Unknown structure start: {}", s1);
            } else {
                StructureStart<?> structurestart = StructureGenerator.a(worldserver, nbttagcompound1.getCompound(s), i);

                if (structurestart != null) {
                    map.put(structuregenerator, structurestart);
                }
            }
        }

        return map;
    }

    private static Map<StructureGenerator<?>, LongSet> a(ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) {
        Map<StructureGenerator<?>, LongSet> map = Maps.newHashMap();
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("References");
        Iterator iterator = nbttagcompound1.getKeys().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            String s1 = s.toLowerCase(Locale.ROOT);
            StructureGenerator<?> structuregenerator = (StructureGenerator) StructureGenerator.STRUCTURES_REGISTRY.get(s1);

            if (structuregenerator == null) {
                ChunkRegionLoader.LOGGER.warn("Found reference to unknown structure '{}' in chunk {}, discarding", s1, chunkcoordintpair);
            } else {
                map.put(structuregenerator, new LongOpenHashSet(Arrays.stream(nbttagcompound1.getLongArray(s)).filter((i) -> {
                    ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(i);

                    if (chunkcoordintpair1.a(chunkcoordintpair) > 8) {
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

    public static NBTTagList a(ShortList[] ashortlist) {
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

                    nbttaglist1.add(NBTTagShort.a(oshort));
                }
            }

            nbttaglist.add(nbttaglist1);
        }

        return nbttaglist;
    }
}
