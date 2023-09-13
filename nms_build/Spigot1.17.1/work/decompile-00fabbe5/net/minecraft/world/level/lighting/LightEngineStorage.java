package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.LightEngineGraphSection;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.chunk.ILightAccess;
import net.minecraft.world.level.chunk.NibbleArray;

public abstract class LightEngineStorage<M extends LightEngineStorageArray<M>> extends LightEngineGraphSection {

    protected static final int LIGHT_AND_DATA = 0;
    protected static final int LIGHT_ONLY = 1;
    protected static final int EMPTY = 2;
    protected static final NibbleArray EMPTY_DATA = new NibbleArray();
    private static final EnumDirection[] DIRECTIONS = EnumDirection.values();
    private final EnumSkyBlock layer;
    private final ILightAccess chunkSource;
    protected final LongSet dataSectionSet = new LongOpenHashSet();
    protected final LongSet toMarkNoData = new LongOpenHashSet();
    protected final LongSet toMarkData = new LongOpenHashSet();
    protected volatile M visibleSectionData;
    protected final M updatingSectionData;
    protected final LongSet changedSections = new LongOpenHashSet();
    protected final LongSet sectionsAffectedByLightUpdates = new LongOpenHashSet();
    protected final Long2ObjectMap<NibbleArray> queuedSections = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap());
    private final LongSet untrustedSections = new LongOpenHashSet();
    private final LongSet columnsToRetainQueuedDataFor = new LongOpenHashSet();
    private final LongSet toRemove = new LongOpenHashSet();
    protected volatile boolean hasToRemove;

    protected LightEngineStorage(EnumSkyBlock enumskyblock, ILightAccess ilightaccess, M m0) {
        super(3, 16, 256);
        this.layer = enumskyblock;
        this.chunkSource = ilightaccess;
        this.updatingSectionData = m0;
        this.visibleSectionData = m0.b();
        this.visibleSectionData.d();
    }

    protected boolean g(long i) {
        return this.a(i, true) != null;
    }

    @Nullable
    protected NibbleArray a(long i, boolean flag) {
        return this.a(flag ? this.updatingSectionData : this.visibleSectionData, i);
    }

    @Nullable
    protected NibbleArray a(M m0, long i) {
        return m0.c(i);
    }

    @Nullable
    public NibbleArray h(long i) {
        NibbleArray nibblearray = (NibbleArray) this.queuedSections.get(i);

        return nibblearray != null ? nibblearray : this.a(i, false);
    }

    protected abstract int d(long i);

    protected int i(long i) {
        long j = SectionPosition.e(i);
        NibbleArray nibblearray = this.a(j, true);

        return nibblearray.a(SectionPosition.b(BlockPosition.a(i)), SectionPosition.b(BlockPosition.b(i)), SectionPosition.b(BlockPosition.c(i)));
    }

    protected void b(long i, int j) {
        long k = SectionPosition.e(i);

        if (this.changedSections.add(k)) {
            this.updatingSectionData.a(k);
        }

        NibbleArray nibblearray = this.a(k, true);

        nibblearray.a(SectionPosition.b(BlockPosition.a(i)), SectionPosition.b(BlockPosition.b(i)), SectionPosition.b(BlockPosition.c(i)), j);

        for (int l = -1; l <= 1; ++l) {
            for (int i1 = -1; i1 <= 1; ++i1) {
                for (int j1 = -1; j1 <= 1; ++j1) {
                    this.sectionsAffectedByLightUpdates.add(SectionPosition.e(BlockPosition.a(i, i1, j1, l)));
                }
            }
        }

    }

    @Override
    protected int c(long i) {
        return i == Long.MAX_VALUE ? 2 : (this.dataSectionSet.contains(i) ? 0 : (!this.toRemove.contains(i) && this.updatingSectionData.b(i) ? 1 : 2));
    }

    @Override
    protected int b(long i) {
        return this.toMarkNoData.contains(i) ? 2 : (!this.dataSectionSet.contains(i) && !this.toMarkData.contains(i) ? 2 : 0);
    }

    @Override
    protected void a(long i, int j) {
        int k = this.c(i);

        if (k != 0 && j == 0) {
            this.dataSectionSet.add(i);
            this.toMarkData.remove(i);
        }

        if (k == 0 && j != 0) {
            this.dataSectionSet.remove(i);
            this.toMarkNoData.remove(i);
        }

        if (k >= 2 && j != 2) {
            if (this.toRemove.contains(i)) {
                this.toRemove.remove(i);
            } else {
                this.updatingSectionData.a(i, this.j(i));
                this.changedSections.add(i);
                this.k(i);

                for (int l = -1; l <= 1; ++l) {
                    for (int i1 = -1; i1 <= 1; ++i1) {
                        for (int j1 = -1; j1 <= 1; ++j1) {
                            this.sectionsAffectedByLightUpdates.add(SectionPosition.e(BlockPosition.a(i, i1, j1, l)));
                        }
                    }
                }
            }
        }

        if (k != 2 && j >= 2) {
            this.toRemove.add(i);
        }

        this.hasToRemove = !this.toRemove.isEmpty();
    }

    protected NibbleArray j(long i) {
        NibbleArray nibblearray = (NibbleArray) this.queuedSections.get(i);

        return nibblearray != null ? nibblearray : new NibbleArray();
    }

    protected void a(LightEngineLayer<?, ?> lightenginelayer, long i) {
        if (lightenginelayer.c() < 8192) {
            lightenginelayer.a((j) -> {
                return SectionPosition.e(j) == i;
            });
        } else {
            int j = SectionPosition.c(SectionPosition.b(i));
            int k = SectionPosition.c(SectionPosition.c(i));
            int l = SectionPosition.c(SectionPosition.d(i));

            for (int i1 = 0; i1 < 16; ++i1) {
                for (int j1 = 0; j1 < 16; ++j1) {
                    for (int k1 = 0; k1 < 16; ++k1) {
                        long l1 = BlockPosition.a(j + i1, k + j1, l + k1);

                        lightenginelayer.e(l1);
                    }
                }
            }

        }
    }

    protected boolean a() {
        return this.hasToRemove;
    }

    protected void a(LightEngineLayer<M, ?> lightenginelayer, boolean flag, boolean flag1) {
        if (this.a() || !this.queuedSections.isEmpty()) {
            LongIterator longiterator = this.toRemove.iterator();

            long i;
            NibbleArray nibblearray;

            while (longiterator.hasNext()) {
                i = (Long) longiterator.next();
                this.a(lightenginelayer, i);
                NibbleArray nibblearray1 = (NibbleArray) this.queuedSections.remove(i);

                nibblearray = this.updatingSectionData.d(i);
                if (this.columnsToRetainQueuedDataFor.contains(SectionPosition.f(i))) {
                    if (nibblearray1 != null) {
                        this.queuedSections.put(i, nibblearray1);
                    } else if (nibblearray != null) {
                        this.queuedSections.put(i, nibblearray);
                    }
                }
            }

            this.updatingSectionData.c();
            longiterator = this.toRemove.iterator();

            while (longiterator.hasNext()) {
                i = (Long) longiterator.next();
                this.l(i);
            }

            this.toRemove.clear();
            this.hasToRemove = false;
            ObjectIterator objectiterator = this.queuedSections.long2ObjectEntrySet().iterator();

            Entry entry;
            long j;

            while (objectiterator.hasNext()) {
                entry = (Entry) objectiterator.next();
                j = entry.getLongKey();
                if (this.g(j)) {
                    nibblearray = (NibbleArray) entry.getValue();
                    if (this.updatingSectionData.c(j) != nibblearray) {
                        this.a(lightenginelayer, j);
                        this.updatingSectionData.a(j, nibblearray);
                        this.changedSections.add(j);
                    }
                }
            }

            this.updatingSectionData.c();
            if (!flag1) {
                longiterator = this.queuedSections.keySet().iterator();

                while (longiterator.hasNext()) {
                    i = (Long) longiterator.next();
                    this.b(lightenginelayer, i);
                }
            } else {
                longiterator = this.untrustedSections.iterator();

                while (longiterator.hasNext()) {
                    i = (Long) longiterator.next();
                    this.b(lightenginelayer, i);
                }
            }

            this.untrustedSections.clear();
            objectiterator = this.queuedSections.long2ObjectEntrySet().iterator();

            while (objectiterator.hasNext()) {
                entry = (Entry) objectiterator.next();
                j = entry.getLongKey();
                if (this.g(j)) {
                    objectiterator.remove();
                }
            }

        }
    }

    private void b(LightEngineLayer<M, ?> lightenginelayer, long i) {
        if (this.g(i)) {
            int j = SectionPosition.c(SectionPosition.b(i));
            int k = SectionPosition.c(SectionPosition.c(i));
            int l = SectionPosition.c(SectionPosition.d(i));
            EnumDirection[] aenumdirection = LightEngineStorage.DIRECTIONS;
            int i1 = aenumdirection.length;

            for (int j1 = 0; j1 < i1; ++j1) {
                EnumDirection enumdirection = aenumdirection[j1];
                long k1 = SectionPosition.a(i, enumdirection);

                if (!this.queuedSections.containsKey(k1) && this.g(k1)) {
                    for (int l1 = 0; l1 < 16; ++l1) {
                        for (int i2 = 0; i2 < 16; ++i2) {
                            long j2;
                            long k2;

                            switch (enumdirection) {
                                case DOWN:
                                    j2 = BlockPosition.a(j + i2, k, l + l1);
                                    k2 = BlockPosition.a(j + i2, k - 1, l + l1);
                                    break;
                                case UP:
                                    j2 = BlockPosition.a(j + i2, k + 16 - 1, l + l1);
                                    k2 = BlockPosition.a(j + i2, k + 16, l + l1);
                                    break;
                                case NORTH:
                                    j2 = BlockPosition.a(j + l1, k + i2, l);
                                    k2 = BlockPosition.a(j + l1, k + i2, l - 1);
                                    break;
                                case SOUTH:
                                    j2 = BlockPosition.a(j + l1, k + i2, l + 16 - 1);
                                    k2 = BlockPosition.a(j + l1, k + i2, l + 16);
                                    break;
                                case WEST:
                                    j2 = BlockPosition.a(j, k + l1, l + i2);
                                    k2 = BlockPosition.a(j - 1, k + l1, l + i2);
                                    break;
                                default:
                                    j2 = BlockPosition.a(j + 16 - 1, k + l1, l + i2);
                                    k2 = BlockPosition.a(j + 16, k + l1, l + i2);
                            }

                            lightenginelayer.a(j2, k2, lightenginelayer.b(j2, k2, lightenginelayer.c(j2)), false);
                            lightenginelayer.a(k2, j2, lightenginelayer.b(k2, j2, lightenginelayer.c(k2)), false);
                        }
                    }
                }
            }

        }
    }

    protected void k(long i) {}

    protected void l(long i) {}

    protected void b(long i, boolean flag) {}

    public void c(long i, boolean flag) {
        if (flag) {
            this.columnsToRetainQueuedDataFor.add(i);
        } else {
            this.columnsToRetainQueuedDataFor.remove(i);
        }

    }

    protected void a(long i, @Nullable NibbleArray nibblearray, boolean flag) {
        if (nibblearray != null) {
            this.queuedSections.put(i, nibblearray);
            if (!flag) {
                this.untrustedSections.add(i);
            }
        } else {
            this.queuedSections.remove(i);
        }

    }

    protected void d(long i, boolean flag) {
        boolean flag1 = this.dataSectionSet.contains(i);

        if (!flag1 && !flag) {
            this.toMarkData.add(i);
            this.a(Long.MAX_VALUE, i, 0, true);
        }

        if (flag1 && flag) {
            this.toMarkNoData.add(i);
            this.a(Long.MAX_VALUE, i, 2, false);
        }

    }

    protected void d() {
        if (this.b()) {
            this.b(Integer.MAX_VALUE);
        }

    }

    protected void e() {
        if (!this.changedSections.isEmpty()) {
            M m0 = this.updatingSectionData.b();

            m0.d();
            this.visibleSectionData = m0;
            this.changedSections.clear();
        }

        if (!this.sectionsAffectedByLightUpdates.isEmpty()) {
            LongIterator longiterator = this.sectionsAffectedByLightUpdates.iterator();

            while (longiterator.hasNext()) {
                long i = longiterator.nextLong();

                this.chunkSource.a(this.layer, SectionPosition.a(i));
            }

            this.sectionsAffectedByLightUpdates.clear();
        }

    }
}
