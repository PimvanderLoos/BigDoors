package net.minecraft.server;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PersistentStructureLegacy {

    private static final Logger a = LogManager.getLogger();
    private static final Map<String, String> b = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
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
    private static final Map<String, String> c = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put("Iglu", "Igloo");
        hashmap.put("TeDP", "Desert_Pyramid");
        hashmap.put("TeJP", "Jungle_Pyramid");
        hashmap.put("TeSH", "Swamp_Hut");
    });
    private final boolean d;
    private final Map<String, Long2ObjectMap<NBTTagCompound>> e = Maps.newHashMap();
    private final Map<String, PersistentIndexed> f = Maps.newHashMap();

    public PersistentStructureLegacy(@Nullable PersistentCollection persistentcollection) {
        this.a(persistentcollection);
        boolean flag = false;
        String[] astring = this.b();
        int i = astring.length;

        for (int j = 0; j < i; ++j) {
            String s = astring[j];

            flag |= this.e.get(s) != null;
        }

        this.d = flag;
    }

    public void a(long i) {
        String[] astring = this.a();
        int j = astring.length;

        for (int k = 0; k < j; ++k) {
            String s = astring[k];
            PersistentIndexed persistentindexed = (PersistentIndexed) this.f.get(s);

            if (persistentindexed != null && persistentindexed.c(i)) {
                persistentindexed.d(i);
                persistentindexed.c();
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
        String[] astring = this.b();
        int i = astring.length;

        for (int j = 0; j < i; ++j) {
            String s = astring[j];
            StructureGenerator<?> structuregenerator = (StructureGenerator) WorldGenerator.aF.get(s.toLowerCase(Locale.ROOT));

            if (!nbttagcompound3.hasKeyOfType(s, 12) && structuregenerator != null) {
                int k = structuregenerator.b();
                LongArrayList longarraylist = new LongArrayList();

                for (int l = chunkcoordintpair.x - k; l <= chunkcoordintpair.x + k; ++l) {
                    for (int i1 = chunkcoordintpair.z - k; i1 <= chunkcoordintpair.z + k; ++i1) {
                        if (this.a(l, i1, s)) {
                            longarraylist.add(ChunkCoordIntPair.a(l, i1));
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

    protected abstract String[] a();

    protected abstract String[] b();

    private boolean a(int i, int j, String s) {
        return !this.d ? false : this.e.get(s) != null && ((PersistentIndexed) this.f.get(PersistentStructureLegacy.b.get(s))).b(ChunkCoordIntPair.a(i, j));
    }

    private boolean a(int i, int j) {
        if (!this.d) {
            return false;
        } else {
            String[] astring = this.b();
            int k = astring.length;

            for (int l = 0; l < k; ++l) {
                String s = astring[l];

                if (this.e.get(s) != null && ((PersistentIndexed) this.f.get(PersistentStructureLegacy.b.get(s))).c(ChunkCoordIntPair.a(i, j))) {
                    return true;
                }
            }

            return false;
        }
    }

    private NBTTagCompound a(NBTTagCompound nbttagcompound, ChunkCoordIntPair chunkcoordintpair) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");
        NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("Structures");
        NBTTagCompound nbttagcompound3 = nbttagcompound2.getCompound("Starts");
        String[] astring = this.b();
        int i = astring.length;

        for (int j = 0; j < i; ++j) {
            String s = astring[j];
            Long2ObjectMap<NBTTagCompound> long2objectmap = (Long2ObjectMap) this.e.get(s);

            if (long2objectmap != null) {
                long k = chunkcoordintpair.a();

                if (((PersistentIndexed) this.f.get(PersistentStructureLegacy.b.get(s))).c(k)) {
                    NBTTagCompound nbttagcompound4 = (NBTTagCompound) long2objectmap.get(k);

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

    private void a(@Nullable PersistentCollection persistentcollection) {
        if (persistentcollection != null) {
            String[] astring = this.a();
            int i = astring.length;

            for (int j = 0; j < i; ++j) {
                String s = astring[j];
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                try {
                    nbttagcompound = persistentcollection.a(s, 1493).getCompound("data").getCompound("Features");
                    if (nbttagcompound.isEmpty()) {
                        continue;
                    }
                } catch (IOException ioexception) {
                    ;
                }

                Iterator iterator = nbttagcompound.getKeys().iterator();

                while (iterator.hasNext()) {
                    String s1 = (String) iterator.next();
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound(s1);
                    long k = ChunkCoordIntPair.a(nbttagcompound1.getInt("ChunkX"), nbttagcompound1.getInt("ChunkZ"));
                    NBTTagList nbttaglist = nbttagcompound1.getList("Children", 10);
                    String s2;

                    if (!nbttaglist.isEmpty()) {
                        s2 = nbttaglist.getCompound(0).getString("id");
                        String s3 = (String) PersistentStructureLegacy.c.get(s2);

                        if (s3 != null) {
                            nbttagcompound1.setString("id", s3);
                        }
                    }

                    s2 = nbttagcompound1.getString("id");
                    ((Long2ObjectMap) this.e.computeIfAbsent(s2, (s4) -> {
                        return new Long2ObjectOpenHashMap();
                    })).put(k, nbttagcompound1);
                }

                String s4 = s + "_index";
                PersistentIndexed persistentindexed = (PersistentIndexed) persistentcollection.get(DimensionManager.OVERWORLD, PersistentIndexed::new, s4);

                if (persistentindexed != null && !persistentindexed.a().isEmpty()) {
                    this.f.put(s, persistentindexed);
                } else {
                    PersistentIndexed persistentindexed1 = new PersistentIndexed(s4);

                    this.f.put(s, persistentindexed1);
                    Iterator iterator1 = nbttagcompound.getKeys().iterator();

                    while (iterator1.hasNext()) {
                        String s5 = (String) iterator1.next();
                        NBTTagCompound nbttagcompound2 = nbttagcompound.getCompound(s5);

                        persistentindexed1.a(ChunkCoordIntPair.a(nbttagcompound2.getInt("ChunkX"), nbttagcompound2.getInt("ChunkZ")));
                    }

                    persistentcollection.a(DimensionManager.OVERWORLD, s4, persistentindexed1);
                    persistentindexed1.c();
                }
            }

        }
    }

    public static PersistentStructureLegacy a(DimensionManager dimensionmanager, @Nullable PersistentCollection persistentcollection) {
        if (dimensionmanager == DimensionManager.OVERWORLD) {
            return new PersistentStructureLegacy.b(persistentcollection);
        } else if (dimensionmanager == DimensionManager.NETHER) {
            return new PersistentStructureLegacy.a(persistentcollection);
        } else if (dimensionmanager == DimensionManager.THE_END) {
            return new PersistentStructureLegacy.c(persistentcollection);
        } else {
            throw new RuntimeException(String.format("Unknown dimension type : %s", dimensionmanager));
        }
    }

    public static class c extends PersistentStructureLegacy {

        private static final String[] a = new String[] { "EndCity"};

        public c(@Nullable PersistentCollection persistentcollection) {
            super(persistentcollection);
        }

        protected String[] a() {
            return PersistentStructureLegacy.c.a;
        }

        protected String[] b() {
            return PersistentStructureLegacy.c.a;
        }
    }

    public static class a extends PersistentStructureLegacy {

        private static final String[] a = new String[] { "Fortress"};

        public a(@Nullable PersistentCollection persistentcollection) {
            super(persistentcollection);
        }

        protected String[] a() {
            return PersistentStructureLegacy.a.a;
        }

        protected String[] b() {
            return PersistentStructureLegacy.a.a;
        }
    }

    public static class b extends PersistentStructureLegacy {

        private static final String[] a = new String[] { "Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"};
        private static final String[] b = new String[] { "Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"};

        public b(@Nullable PersistentCollection persistentcollection) {
            super(persistentcollection);
        }

        protected String[] a() {
            return PersistentStructureLegacy.b.a;
        }

        protected String[] b() {
            return PersistentStructureLegacy.b.b;
        }
    }
}
