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
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.World;
import net.minecraft.world.level.storage.WorldPersistentData;

public class PersistentStructureLegacy {

    private static final Map<String, String> CURRENT_TO_LEGACY_MAP = (Map) SystemUtils.make(Maps.newHashMap(), (hashmap) -> {
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
    private static final Map<String, String> LEGACY_TO_CURRENT_MAP = (Map) SystemUtils.make(Maps.newHashMap(), (hashmap) -> {
        hashmap.put("Iglu", "Igloo");
        hashmap.put("TeDP", "Desert_Pyramid");
        hashmap.put("TeJP", "Jungle_Pyramid");
        hashmap.put("TeSH", "Swamp_Hut");
    });
    private static final Set<String> OLD_STRUCTURE_REGISTRY_KEYS = Set.of("pillager_outpost", "mineshaft", "mansion", "jungle_pyramid", "desert_pyramid", "igloo", "ruined_portal", "shipwreck", "swamp_hut", "stronghold", "monument", "ocean_ruin", "fortress", "endcity", "buried_treasure", "village", "nether_fossil", "bastion_remnant");
    private final boolean hasLegacyData;
    private final Map<String, Long2ObjectMap<NBTTagCompound>> dataMap = Maps.newHashMap();
    private final Map<String, PersistentIndexed> indexMap = Maps.newHashMap();
    private final List<String> legacyKeys;
    private final List<String> currentKeys;

    public PersistentStructureLegacy(@Nullable WorldPersistentData worldpersistentdata, List<String> list, List<String> list1) {
        this.legacyKeys = list;
        this.currentKeys = list1;
        this.populateCaches(worldpersistentdata);
        boolean flag = false;

        String s;

        for (Iterator iterator = this.currentKeys.iterator(); iterator.hasNext(); flag |= this.dataMap.get(s) != null) {
            s = (String) iterator.next();
        }

        this.hasLegacyData = flag;
    }

    public void removeIndex(long i) {
        Iterator iterator = this.legacyKeys.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            PersistentIndexed persistentindexed = (PersistentIndexed) this.indexMap.get(s);

            if (persistentindexed != null && persistentindexed.hasUnhandledIndex(i)) {
                persistentindexed.removeIndex(i);
                persistentindexed.setDirty();
            }
        }

    }

    public NBTTagCompound updateFromLegacy(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(nbttagcompound1.getInt("xPos"), nbttagcompound1.getInt("zPos"));

        if (this.isUnhandledStructureStart(chunkcoordintpair.x, chunkcoordintpair.z)) {
            nbttagcompound = this.updateStructureStart(nbttagcompound, chunkcoordintpair);
        }

        NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("Structures");
        NBTTagCompound nbttagcompound3 = nbttagcompound2.getCompound("References");
        Iterator iterator = this.currentKeys.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            boolean flag = PersistentStructureLegacy.OLD_STRUCTURE_REGISTRY_KEYS.contains(s.toLowerCase(Locale.ROOT));

            if (!nbttagcompound3.contains(s, 12) && flag) {
                boolean flag1 = true;
                LongArrayList longarraylist = new LongArrayList();

                for (int i = chunkcoordintpair.x - 8; i <= chunkcoordintpair.x + 8; ++i) {
                    for (int j = chunkcoordintpair.z - 8; j <= chunkcoordintpair.z + 8; ++j) {
                        if (this.hasLegacyStart(i, j, s)) {
                            longarraylist.add(ChunkCoordIntPair.asLong(i, j));
                        }
                    }
                }

                nbttagcompound3.putLongArray(s, (List) longarraylist);
            }
        }

        nbttagcompound2.put("References", nbttagcompound3);
        nbttagcompound1.put("Structures", nbttagcompound2);
        nbttagcompound.put("Level", nbttagcompound1);
        return nbttagcompound;
    }

    private boolean hasLegacyStart(int i, int j, String s) {
        return !this.hasLegacyData ? false : this.dataMap.get(s) != null && ((PersistentIndexed) this.indexMap.get(PersistentStructureLegacy.CURRENT_TO_LEGACY_MAP.get(s))).hasStartIndex(ChunkCoordIntPair.asLong(i, j));
    }

    private boolean isUnhandledStructureStart(int i, int j) {
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
            } while (this.dataMap.get(s) == null || !((PersistentIndexed) this.indexMap.get(PersistentStructureLegacy.CURRENT_TO_LEGACY_MAP.get(s))).hasUnhandledIndex(ChunkCoordIntPair.asLong(i, j)));

            return true;
        }
    }

    private NBTTagCompound updateStructureStart(NBTTagCompound nbttagcompound, ChunkCoordIntPair chunkcoordintpair) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");
        NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("Structures");
        NBTTagCompound nbttagcompound3 = nbttagcompound2.getCompound("Starts");
        Iterator iterator = this.currentKeys.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            Long2ObjectMap<NBTTagCompound> long2objectmap = (Long2ObjectMap) this.dataMap.get(s);

            if (long2objectmap != null) {
                long i = chunkcoordintpair.toLong();

                if (((PersistentIndexed) this.indexMap.get(PersistentStructureLegacy.CURRENT_TO_LEGACY_MAP.get(s))).hasUnhandledIndex(i)) {
                    NBTTagCompound nbttagcompound4 = (NBTTagCompound) long2objectmap.get(i);

                    if (nbttagcompound4 != null) {
                        nbttagcompound3.put(s, nbttagcompound4);
                    }
                }
            }
        }

        nbttagcompound2.put("Starts", nbttagcompound3);
        nbttagcompound1.put("Structures", nbttagcompound2);
        nbttagcompound.put("Level", nbttagcompound1);
        return nbttagcompound;
    }

    private void populateCaches(@Nullable WorldPersistentData worldpersistentdata) {
        if (worldpersistentdata != null) {
            Iterator iterator = this.legacyKeys.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                try {
                    nbttagcompound = worldpersistentdata.readTagFromDisk(s, 1493).getCompound("data").getCompound("Features");
                    if (nbttagcompound.isEmpty()) {
                        continue;
                    }
                } catch (IOException ioexception) {
                    ;
                }

                Iterator iterator1 = nbttagcompound.getAllKeys().iterator();

                while (iterator1.hasNext()) {
                    String s1 = (String) iterator1.next();
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound(s1);
                    long i = ChunkCoordIntPair.asLong(nbttagcompound1.getInt("ChunkX"), nbttagcompound1.getInt("ChunkZ"));
                    NBTTagList nbttaglist = nbttagcompound1.getList("Children", 10);
                    String s2;

                    if (!nbttaglist.isEmpty()) {
                        s2 = nbttaglist.getCompound(0).getString("id");
                        String s3 = (String) PersistentStructureLegacy.LEGACY_TO_CURRENT_MAP.get(s2);

                        if (s3 != null) {
                            nbttagcompound1.putString("id", s3);
                        }
                    }

                    s2 = nbttagcompound1.getString("id");
                    ((Long2ObjectMap) this.dataMap.computeIfAbsent(s2, (s4) -> {
                        return new Long2ObjectOpenHashMap();
                    })).put(i, nbttagcompound1);
                }

                String s4 = s + "_index";
                PersistentIndexed persistentindexed = (PersistentIndexed) worldpersistentdata.computeIfAbsent(PersistentIndexed::load, PersistentIndexed::new, s4);

                if (!persistentindexed.getAll().isEmpty()) {
                    this.indexMap.put(s, persistentindexed);
                } else {
                    PersistentIndexed persistentindexed1 = new PersistentIndexed();

                    this.indexMap.put(s, persistentindexed1);
                    Iterator iterator2 = nbttagcompound.getAllKeys().iterator();

                    while (iterator2.hasNext()) {
                        String s5 = (String) iterator2.next();
                        NBTTagCompound nbttagcompound2 = nbttagcompound.getCompound(s5);

                        persistentindexed1.addIndex(ChunkCoordIntPair.asLong(nbttagcompound2.getInt("ChunkX"), nbttagcompound2.getInt("ChunkZ")));
                    }

                    persistentindexed1.setDirty();
                }
            }

        }
    }

    public static PersistentStructureLegacy getLegacyStructureHandler(ResourceKey<World> resourcekey, @Nullable WorldPersistentData worldpersistentdata) {
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
                throw new RuntimeException(String.format(Locale.ROOT, "Unknown dimension type : %s", resourcekey));
            }
        }
    }
}
