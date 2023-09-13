package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.storage.WorldPersistentData;

public class PersistentStructureLegacy {

    private static final Map<String, String> CURRENT_TO_LEGACY_MAP = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put("Village", "Village");
        hashmap.put("Mineshaft", "Mineshaft");
        hashmap.put("Mansion", "Mansion");
        hashmap.put("Igloo", "Temple");
        hashmap.put("Desert_Pyramid", "Temple");
        hashmap.put("Jungle_Pyramid", "Temple");
        hashmap.put("Swamp_Hut", "Temple");
        hashmap.put("Stronghold", "Stronghold");
        hashmap.put("Monument", "Monument");
        hashmap.put("Fortress", "Fortress");
        hashmap.put("EndCity", "EndCity");
    });
    private static final Map<String, String> LEGACY_TO_CURRENT_MAP = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put("Iglu", "Igloo");
        hashmap.put("TeDP", "Desert_Pyramid");
        hashmap.put("TeJP", "Jungle_Pyramid");
        hashmap.put("TeSH", "Swamp_Hut");
    });
    private final boolean hasLegacyData;
    private final Map<String, Long2ObjectMap<NBTTagCompound>> dataMap = Maps.newHashMap();
    private final Map<String, PersistentIndexed> indexMap = Maps.newHashMap();
    private final List<String> legacyKeys;
    private final List<String> currentKeys;

    public PersistentStructureLegacy(@Nullable WorldPersistentData worldpersistentdata, List<String> list, List<String> list1) {
        this.legacyKeys = list;
        this.currentKeys = list1;
        this.a(worldpersistentdata);
        boolean flag = false;

        String s;

        for (Iterator iterator = this.currentKeys.iterator(); iterator.hasNext(); flag |= this.dataMap.get(s) != null) {
            s = (String) iterator.next();
        }

        this.hasLegacyData = flag;
    }

    public void a(long i) {
        Iterator iterator = this.legacyKeys.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            PersistentIndexed persistentindexed = (PersistentIndexed) this.indexMap.get(s);

            if (persistentindexed != null && persistentindexed.c(i)) {
                persistentindexed.d(i);
                persistentindexed.b();
            }
        }

    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(nbttagcompound1.getInt("xPos"), nbttagcompound1.getInt("zPos"));

        if (this.a(chunkcoordintpair.x, chunkcoordintpair.z)) {
            nbttagcompound = this.a(nbttagcompound, chunkcoordintpair);
        }

        NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("Structures");
        NBTTagCompound nbttagcompound3 = nbttagcompound2.getCompound("References");
        Iterator iterator = this.currentKeys.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            StructureGenerator<?> structuregenerator = (StructureGenerator) StructureGenerator.STRUCTURES_REGISTRY.get(s.toLowerCase(Locale.ROOT));

            if (!nbttagcompound3.hasKeyOfType(s, 12) && structuregenerator != null) {
                boolean flag = true;
                LongArrayList longarraylist = new LongArrayList();

                for (int i = chunkcoordintpair.x - 8; i <= chunkcoordintpair.x + 8; ++i) {
                    for (int j = chunkcoordintpair.z - 8; j <= chunkcoordintpair.z + 8; ++j) {
                        if (this.a(i, j, s)) {
                            longarraylist.add(ChunkCoordIntPair.pair(i, j));
                        }
                    }
                }

                nbttagcompound3.c(s, longarraylist);
            }
        }

        nbttagcompound2.set("References", nbttagcompound3);
        nbttagcompound1.set("Structures", nbttagcompound2);
        nbttagcompound.set("Level", nbttagcompound1);
        return nbttagcompound;
    }

    private boolean a(int i, int j, String s) {
        return !this.hasLegacyData ? false : this.dataMap.get(s) != null && ((PersistentIndexed) this.indexMap.get(PersistentStructureLegacy.CURRENT_TO_LEGACY_MAP.get(s))).b(ChunkCoordIntPair.pair(i, j));
    }

    private boolean a(int i, int j) {
        if (!this.hasLegacyData) {
            return false;
        } else {
            Iterator iterator = this.currentKeys.iterator();

            String s;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                s = (String) iterator.next();
            } while (this.dataMap.get(s) == null || !((PersistentIndexed) this.indexMap.get(PersistentStructureLegacy.CURRENT_TO_LEGACY_MAP.get(s))).c(ChunkCoordIntPair.pair(i, j)));

            return true;
        }
    }

    private NBTTagCompound a(NBTTagCompound nbttagcompound, ChunkCoordIntPair chunkcoordintpair) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");
        NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("Structures");
        NBTTagCompound nbttagcompound3 = nbttagcompound2.getCompound("Starts");
        Iterator iterator = this.currentKeys.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            Long2ObjectMap<NBTTagCompound> long2objectmap = (Long2ObjectMap) this.dataMap.get(s);

            if (long2objectmap != null) {
                long i = chunkcoordintpair.pair();

                if (((PersistentIndexed) this.indexMap.get(PersistentStructureLegacy.CURRENT_TO_LEGACY_MAP.get(s))).c(i)) {
                    NBTTagCompound nbttagcompound4 = (NBTTagCompound) long2objectmap.get(i);

                    if (nbttagcompound4 != null) {
                        nbttagcompound3.set(s, nbttagcompound4);
                    }
                }
            }
        }

        nbttagcompound2.set("Starts", nbttagcompound3);
        nbttagcompound1.set("Structures", nbttagcompound2);
        nbttagcompound.set("Level", nbttagcompound1);
        return nbttagcompound;
    }

    private void a(@Nullable WorldPersistentData worldpersistentdata) {
        if (worldpersistentdata != null) {
            Iterator iterator = this.legacyKeys.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                try {
                    nbttagcompound = worldpersistentdata.a(s, 1493).getCompound("data").getCompound("Features");
                    if (nbttagcompound.isEmpty()) {
                        continue;
                    }
                } catch (IOException ioexception) {
                    ;
                }

                Iterator iterator1 = nbttagcompound.getKeys().iterator();

                while (iterator1.hasNext()) {
                    String s1 = (String) iterator1.next();
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound(s1);
                    long i = ChunkCoordIntPair.pair(nbttagcompound1.getInt("ChunkX"), nbttagcompound1.getInt("ChunkZ"));
                    NBTTagList nbttaglist = nbttagcompound1.getList("Children", 10);
                    String s2;

                    if (!nbttaglist.isEmpty()) {
                        s2 = nbttaglist.getCompound(0).getString("id");
                        String s3 = (String) PersistentStructureLegacy.LEGACY_TO_CURRENT_MAP.get(s2);

                        if (s3 != null) {
                            nbttagcompound1.setString("id", s3);
                        }
                    }

                    s2 = nbttagcompound1.getString("id");
                    ((Long2ObjectMap) this.dataMap.computeIfAbsent(s2, (s4) -> {
                        return new Long2ObjectOpenHashMap();
                    })).put(i, nbttagcompound1);
                }

                String s4 = s + "_index";
                PersistentIndexed persistentindexed = (PersistentIndexed) worldpersistentdata.a(PersistentIndexed::b, PersistentIndexed::new, s4);

                if (!persistentindexed.a().isEmpty()) {
                    this.indexMap.put(s, persistentindexed);
                } else {
                    PersistentIndexed persistentindexed1 = new PersistentIndexed();

                    this.indexMap.put(s, persistentindexed1);
                    Iterator iterator2 = nbttagcompound.getKeys().iterator();

                    while (iterator2.hasNext()) {
                        String s5 = (String) iterator2.next();
                        NBTTagCompound nbttagcompound2 = nbttagcompound.getCompound(s5);

                        persistentindexed1.a(ChunkCoordIntPair.pair(nbttagcompound2.getInt("ChunkX"), nbttagcompound2.getInt("ChunkZ")));
                    }

                    persistentindexed1.b();
                }
            }

        }
    }

    public static PersistentStructureLegacy a(ResourceKey<World> resourcekey, @Nullable WorldPersistentData worldpersistentdata) {
        if (resourcekey == World.OVERWORLD) {
            return new PersistentStructureLegacy(worldpersistentdata, ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"), ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"));
        } else {
            ImmutableList immutablelist;

            if (resourcekey == World.NETHER) {
                immutablelist = ImmutableList.of("Fortress");
                return new PersistentStructureLegacy(worldpersistentdata, immutablelist, immutablelist);
            } else if (resourcekey == World.END) {
                immutablelist = ImmutableList.of("EndCity");
                return new PersistentStructureLegacy(worldpersistentdata, immutablelist, immutablelist);
            } else {
                throw new RuntimeException(String.format("Unknown dimension type : %s", resourcekey));
            }
        }
    }
}
