package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

public enum ChunkStatus implements SchedulerTask<ChunkCoordIntPair, ChunkStatus> {

    EMPTY("empty", (ChunkTask) null, -1, false, ChunkStatus.Type.PROTOCHUNK), BASE("base", new ChunkTaskBase(), 0, false, ChunkStatus.Type.PROTOCHUNK), CARVED("carved", new ChunkTaskCarve(), 0, false, ChunkStatus.Type.PROTOCHUNK), LIQUID_CARVED("liquid_carved", new ChunkTaskLiquidCarve(), 1, false, ChunkStatus.Type.PROTOCHUNK), DECORATED("decorated", new ChunkTaskDecorate(), 1, true, ChunkStatus.Type.PROTOCHUNK) {
        public void a(ChunkCoordIntPair chunkcoordintpair, BiConsumer<ChunkCoordIntPair, ChunkStatus> biconsumer) {
            int i = chunkcoordintpair.x;
            int j = chunkcoordintpair.z;
            ChunkStatus chunkstatus = this.a();
            boolean flag = true;

            ChunkCoordIntPair chunkcoordintpair1;
            int k;
            int l;

            for (k = i - 8; k <= i + 8; ++k) {
                if (k < i - 1 || k > i + 1) {
                    for (l = j - 8; l <= j + 8; ++l) {
                        if (l < j - 1 || l > j + 1) {
                            chunkcoordintpair1 = new ChunkCoordIntPair(k, l);
                            biconsumer.accept(chunkcoordintpair1, null.EMPTY);
                        }
                    }
                }
            }

            for (k = i - 1; k <= i + 1; ++k) {
                for (l = j - 1; l <= j + 1; ++l) {
                    chunkcoordintpair1 = new ChunkCoordIntPair(k, l);
                    biconsumer.accept(chunkcoordintpair1, chunkstatus);
                }
            }

        }
    },
    LIGHTED("lighted", new ChunkTaskLight(), 1, true, ChunkStatus.Type.PROTOCHUNK), MOBS_SPAWNED("mobs_spawned", new ChunkTaskSpawnMobs(), 0, true, ChunkStatus.Type.PROTOCHUNK), FINALIZED("finalized", new ChunkTaskFinalize(), 0, true, ChunkStatus.Type.PROTOCHUNK), FULLCHUNK("fullchunk", new ChunkTaskNull(), 0, true, ChunkStatus.Type.LEVELCHUNK), POSTPROCESSED("postprocessed", new ChunkTaskNull(), 0, true, ChunkStatus.Type.LEVELCHUNK);

    private static final Map<String, ChunkStatus> k = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        ChunkStatus[] achunkstatus = values();
        int i = achunkstatus.length;

        for (int j = 0; j < i; ++j) {
            ChunkStatus chunkstatus = achunkstatus[j];

            hashmap.put(chunkstatus.b(), chunkstatus);
        }

    });
    private final String l;
    @Nullable
    private final ChunkTask m;
    private final int n;
    private final ChunkStatus.Type o;
    private final boolean p;

    private ChunkStatus(String s, ChunkTask chunktask, int i, @Nullable boolean flag, ChunkStatus.Type chunkstatus_type) {
        this.l = s;
        this.m = chunktask;
        this.n = i;
        this.o = chunkstatus_type;
        this.p = flag;
    }

    public String b() {
        return this.l;
    }

    public ProtoChunk a(World world, ChunkGenerator<?> chunkgenerator, Map<ChunkCoordIntPair, ProtoChunk> map, int i, int j) {
        return this.m.a(this, world, chunkgenerator, map, i, j);
    }

    public void a(ChunkCoordIntPair chunkcoordintpair, BiConsumer<ChunkCoordIntPair, ChunkStatus> biconsumer) {
        int i = chunkcoordintpair.x;
        int j = chunkcoordintpair.z;
        ChunkStatus chunkstatus = this.a();

        for (int k = i - this.n; k <= i + this.n; ++k) {
            for (int l = j - this.n; l <= j + this.n; ++l) {
                biconsumer.accept(new ChunkCoordIntPair(k, l), chunkstatus);
            }
        }

    }

    public int c() {
        return this.n;
    }

    public ChunkStatus.Type d() {
        return this.o;
    }

    @Nullable
    public static ChunkStatus a(String s) {
        return (ChunkStatus) ChunkStatus.k.get(s);
    }

    @Nullable
    public ChunkStatus a() {
        return this.ordinal() == 0 ? null : values()[this.ordinal() - 1];
    }

    public boolean f() {
        return this.p;
    }

    public boolean a(ChunkStatus chunkstatus) {
        return this.ordinal() >= chunkstatus.ordinal();
    }

    public static enum Type {

        PROTOCHUNK, LEVELCHUNK;

        private Type() {}
    }
}
