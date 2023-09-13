package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.chunk.ILightAccess;
import net.minecraft.world.level.chunk.NibbleArray;

public class LightEngineStorageSky extends LightEngineStorage<LightEngineStorageSky.a> {

    private static final EnumDirection[] HORIZONTALS = new EnumDirection[]{EnumDirection.NORTH, EnumDirection.SOUTH, EnumDirection.WEST, EnumDirection.EAST};
    private final LongSet sectionsWithSources = new LongOpenHashSet();
    private final LongSet sectionsToAddSourcesTo = new LongOpenHashSet();
    private final LongSet sectionsToRemoveSourcesFrom = new LongOpenHashSet();
    private final LongSet columnsWithSkySources = new LongOpenHashSet();
    private volatile boolean hasSourceInconsistencies;

    protected LightEngineStorageSky(ILightAccess ilightaccess) {
        super(EnumSkyBlock.SKY, ilightaccess, new LightEngineStorageSky.a(new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
    }

    @Override
    protected int d(long i) {
        return this.e(i, false);
    }

    protected int e(long i, boolean flag) {
        long j = SectionPosition.e(i);
        int k = SectionPosition.c(j);
        LightEngineStorageSky.a lightenginestoragesky_a = flag ? (LightEngineStorageSky.a) this.updatingSectionData : (LightEngineStorageSky.a) this.visibleSectionData;
        int l = lightenginestoragesky_a.topSections.get(SectionPosition.f(j));

        if (l != lightenginestoragesky_a.currentLowestY && k < l) {
            NibbleArray nibblearray = this.a((LightEngineStorageArray) lightenginestoragesky_a, j);

            if (nibblearray == null) {
                for (i = BlockPosition.e(i); nibblearray == null; nibblearray = this.a((LightEngineStorageArray) lightenginestoragesky_a, j)) {
                    ++k;
                    if (k >= l) {
                        return 15;
                    }

                    i = BlockPosition.a(i, 0, 16, 0);
                    j = SectionPosition.a(j, EnumDirection.UP);
                }
            }

            return nibblearray.a(SectionPosition.b(BlockPosition.a(i)), SectionPosition.b(BlockPosition.b(i)), SectionPosition.b(BlockPosition.c(i)));
        } else {
            return flag && !this.n(j) ? 0 : 15;
        }
    }

    @Override
    protected void k(long i) {
        int j = SectionPosition.c(i);

        if (((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY > j) {
            ((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY = j;
            ((LightEngineStorageSky.a) this.updatingSectionData).topSections.defaultReturnValue(((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY);
        }

        long k = SectionPosition.f(i);
        int l = ((LightEngineStorageSky.a) this.updatingSectionData).topSections.get(k);

        if (l < j + 1) {
            ((LightEngineStorageSky.a) this.updatingSectionData).topSections.put(k, j + 1);
            if (this.columnsWithSkySources.contains(k)) {
                this.p(i);
                if (l > ((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY) {
                    long i1 = SectionPosition.b(SectionPosition.b(i), l - 1, SectionPosition.d(i));

                    this.o(i1);
                }

                this.f();
            }
        }

    }

    private void o(long i) {
        this.sectionsToRemoveSourcesFrom.add(i);
        this.sectionsToAddSourcesTo.remove(i);
    }

    private void p(long i) {
        this.sectionsToAddSourcesTo.add(i);
        this.sectionsToRemoveSourcesFrom.remove(i);
    }

    private void f() {
        this.hasSourceInconsistencies = !this.sectionsToAddSourcesTo.isEmpty() || !this.sectionsToRemoveSourcesFrom.isEmpty();
    }

    @Override
    protected void l(long i) {
        long j = SectionPosition.f(i);
        boolean flag = this.columnsWithSkySources.contains(j);

        if (flag) {
            this.o(i);
        }

        int k = SectionPosition.c(i);

        if (((LightEngineStorageSky.a) this.updatingSectionData).topSections.get(j) == k + 1) {
            long l;

            for (l = i; !this.g(l) && this.a(k); l = SectionPosition.a(l, EnumDirection.DOWN)) {
                --k;
            }

            if (this.g(l)) {
                ((LightEngineStorageSky.a) this.updatingSectionData).topSections.put(j, k + 1);
                if (flag) {
                    this.p(l);
                }
            } else {
                ((LightEngineStorageSky.a) this.updatingSectionData).topSections.remove(j);
            }
        }

        if (flag) {
            this.f();
        }

    }

    @Override
    protected void b(long i, boolean flag) {
        this.d();
        if (flag && this.columnsWithSkySources.add(i)) {
            int j = ((LightEngineStorageSky.a) this.updatingSectionData).topSections.get(i);

            if (j != ((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY) {
                long k = SectionPosition.b(SectionPosition.b(i), j - 1, SectionPosition.d(i));

                this.p(k);
                this.f();
            }
        } else if (!flag) {
            this.columnsWithSkySources.remove(i);
        }

    }

    @Override
    protected boolean a() {
        return super.a() || this.hasSourceInconsistencies;
    }

    @Override
    protected NibbleArray j(long i) {
        NibbleArray nibblearray = (NibbleArray) this.queuedSections.get(i);

        if (nibblearray != null) {
            return nibblearray;
        } else {
            long j = SectionPosition.a(i, EnumDirection.UP);
            int k = ((LightEngineStorageSky.a) this.updatingSectionData).topSections.get(SectionPosition.f(i));

            if (k != ((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY && SectionPosition.c(j) < k) {
                NibbleArray nibblearray1;

                while ((nibblearray1 = this.a(j, true)) == null) {
                    j = SectionPosition.a(j, EnumDirection.UP);
                }

                return a(nibblearray1);
            } else {
                return new NibbleArray();
            }
        }
    }

    private static NibbleArray a(NibbleArray nibblearray) {
        if (nibblearray.c()) {
            return new NibbleArray();
        } else {
            byte[] abyte = nibblearray.asBytes();
            byte[] abyte1 = new byte[2048];

            for (int i = 0; i < 16; ++i) {
                System.arraycopy(abyte, 0, abyte1, i * 128, 128);
            }

            return new NibbleArray(abyte1);
        }
    }

    @Override
    protected void a(LightEngineLayer<LightEngineStorageSky.a, ?> lightenginelayer, boolean flag, boolean flag1) {
        super.a(lightenginelayer, flag, flag1);
        if (flag) {
            LongIterator longiterator;
            long i;
            int j;
            int k;

            if (!this.sectionsToAddSourcesTo.isEmpty()) {
                longiterator = this.sectionsToAddSourcesTo.iterator();

                while (longiterator.hasNext()) {
                    i = (Long) longiterator.next();
                    j = this.c(i);
                    if (j != 2 && !this.sectionsToRemoveSourcesFrom.contains(i) && this.sectionsWithSources.add(i)) {
                        int l;

                        if (j == 1) {
                            this.a(lightenginelayer, i);
                            if (this.changedSections.add(i)) {
                                ((LightEngineStorageSky.a) this.updatingSectionData).a(i);
                            }

                            Arrays.fill(this.a(i, true).asBytes(), (byte) -1);
                            k = SectionPosition.c(SectionPosition.b(i));
                            l = SectionPosition.c(SectionPosition.c(i));
                            int i1 = SectionPosition.c(SectionPosition.d(i));
                            EnumDirection[] aenumdirection = LightEngineStorageSky.HORIZONTALS;
                            int j1 = aenumdirection.length;

                            long k1;

                            for (int l1 = 0; l1 < j1; ++l1) {
                                EnumDirection enumdirection = aenumdirection[l1];

                                k1 = SectionPosition.a(i, enumdirection);
                                if ((this.sectionsToRemoveSourcesFrom.contains(k1) || !this.sectionsWithSources.contains(k1) && !this.sectionsToAddSourcesTo.contains(k1)) && this.g(k1)) {
                                    for (int i2 = 0; i2 < 16; ++i2) {
                                        for (int j2 = 0; j2 < 16; ++j2) {
                                            long k2;
                                            long l2;

                                            switch (enumdirection) {
                                                case NORTH:
                                                    k2 = BlockPosition.a(k + i2, l + j2, i1);
                                                    l2 = BlockPosition.a(k + i2, l + j2, i1 - 1);
                                                    break;
                                                case SOUTH:
                                                    k2 = BlockPosition.a(k + i2, l + j2, i1 + 16 - 1);
                                                    l2 = BlockPosition.a(k + i2, l + j2, i1 + 16);
                                                    break;
                                                case WEST:
                                                    k2 = BlockPosition.a(k, l + i2, i1 + j2);
                                                    l2 = BlockPosition.a(k - 1, l + i2, i1 + j2);
                                                    break;
                                                default:
                                                    k2 = BlockPosition.a(k + 16 - 1, l + i2, i1 + j2);
                                                    l2 = BlockPosition.a(k + 16, l + i2, i1 + j2);
                                            }

                                            lightenginelayer.a(k2, l2, lightenginelayer.b(k2, l2, 0), true);
                                        }
                                    }
                                }
                            }

                            for (int i3 = 0; i3 < 16; ++i3) {
                                for (j1 = 0; j1 < 16; ++j1) {
                                    long j3 = BlockPosition.a(SectionPosition.a(SectionPosition.b(i), i3), SectionPosition.c(SectionPosition.c(i)), SectionPosition.a(SectionPosition.d(i), j1));

                                    k1 = BlockPosition.a(SectionPosition.a(SectionPosition.b(i), i3), SectionPosition.c(SectionPosition.c(i)) - 1, SectionPosition.a(SectionPosition.d(i), j1));
                                    lightenginelayer.a(j3, k1, lightenginelayer.b(j3, k1, 0), true);
                                }
                            }
                        } else {
                            for (k = 0; k < 16; ++k) {
                                for (l = 0; l < 16; ++l) {
                                    long k3 = BlockPosition.a(SectionPosition.a(SectionPosition.b(i), k), SectionPosition.a(SectionPosition.c(i), 15), SectionPosition.a(SectionPosition.d(i), l));

                                    lightenginelayer.a(Long.MAX_VALUE, k3, 0, true);
                                }
                            }
                        }
                    }
                }
            }

            this.sectionsToAddSourcesTo.clear();
            if (!this.sectionsToRemoveSourcesFrom.isEmpty()) {
                longiterator = this.sectionsToRemoveSourcesFrom.iterator();

                while (longiterator.hasNext()) {
                    i = (Long) longiterator.next();
                    if (this.sectionsWithSources.remove(i) && this.g(i)) {
                        for (j = 0; j < 16; ++j) {
                            for (k = 0; k < 16; ++k) {
                                long l3 = BlockPosition.a(SectionPosition.a(SectionPosition.b(i), j), SectionPosition.a(SectionPosition.c(i), 15), SectionPosition.a(SectionPosition.d(i), k));

                                lightenginelayer.a(Long.MAX_VALUE, l3, 15, false);
                            }
                        }
                    }
                }
            }

            this.sectionsToRemoveSourcesFrom.clear();
            this.hasSourceInconsistencies = false;
        }
    }

    protected boolean a(int i) {
        return i >= ((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY;
    }

    protected boolean m(long i) {
        long j = SectionPosition.f(i);
        int k = ((LightEngineStorageSky.a) this.updatingSectionData).topSections.get(j);

        return k == ((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY || SectionPosition.c(i) >= k;
    }

    protected boolean n(long i) {
        long j = SectionPosition.f(i);

        return this.columnsWithSkySources.contains(j);
    }

    protected static final class a extends LightEngineStorageArray<LightEngineStorageSky.a> {

        int currentLowestY;
        final Long2IntOpenHashMap topSections;

        public a(Long2ObjectOpenHashMap<NibbleArray> long2objectopenhashmap, Long2IntOpenHashMap long2intopenhashmap, int i) {
            super(long2objectopenhashmap);
            this.topSections = long2intopenhashmap;
            long2intopenhashmap.defaultReturnValue(i);
            this.currentLowestY = i;
        }

        @Override
        public LightEngineStorageSky.a b() {
            return new LightEngineStorageSky.a(this.map.clone(), this.topSections.clone(), this.currentLowestY);
        }
    }
}
