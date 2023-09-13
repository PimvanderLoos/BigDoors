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
    protected int getLightValue(long i) {
        return this.getLightValue(i, false);
    }

    protected int getLightValue(long i, boolean flag) {
        long j = SectionPosition.blockToSection(i);
        int k = SectionPosition.y(j);
        LightEngineStorageSky.a lightenginestoragesky_a = flag ? (LightEngineStorageSky.a) this.updatingSectionData : (LightEngineStorageSky.a) this.visibleSectionData;
        int l = lightenginestoragesky_a.topSections.get(SectionPosition.getZeroNode(j));

        if (l != lightenginestoragesky_a.currentLowestY && k < l) {
            NibbleArray nibblearray = this.getDataLayer(lightenginestoragesky_a, j);

            if (nibblearray == null) {
                for (i = BlockPosition.getFlatIndex(i); nibblearray == null; nibblearray = this.getDataLayer(lightenginestoragesky_a, j)) {
                    ++k;
                    if (k >= l) {
                        return 15;
                    }

                    i = BlockPosition.offset(i, 0, 16, 0);
                    j = SectionPosition.offset(j, EnumDirection.UP);
                }
            }

            return nibblearray.get(SectionPosition.sectionRelative(BlockPosition.getX(i)), SectionPosition.sectionRelative(BlockPosition.getY(i)), SectionPosition.sectionRelative(BlockPosition.getZ(i)));
        } else {
            return flag && !this.lightOnInSection(j) ? 0 : 15;
        }
    }

    @Override
    protected void onNodeAdded(long i) {
        int j = SectionPosition.y(i);

        if (((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY > j) {
            ((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY = j;
            ((LightEngineStorageSky.a) this.updatingSectionData).topSections.defaultReturnValue(((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY);
        }

        long k = SectionPosition.getZeroNode(i);
        int l = ((LightEngineStorageSky.a) this.updatingSectionData).topSections.get(k);

        if (l < j + 1) {
            ((LightEngineStorageSky.a) this.updatingSectionData).topSections.put(k, j + 1);
            if (this.columnsWithSkySources.contains(k)) {
                this.queueAddSource(i);
                if (l > ((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY) {
                    long i1 = SectionPosition.asLong(SectionPosition.x(i), l - 1, SectionPosition.z(i));

                    this.queueRemoveSource(i1);
                }

                this.recheckInconsistencyFlag();
            }
        }

    }

    private void queueRemoveSource(long i) {
        this.sectionsToRemoveSourcesFrom.add(i);
        this.sectionsToAddSourcesTo.remove(i);
    }

    private void queueAddSource(long i) {
        this.sectionsToAddSourcesTo.add(i);
        this.sectionsToRemoveSourcesFrom.remove(i);
    }

    private void recheckInconsistencyFlag() {
        this.hasSourceInconsistencies = !this.sectionsToAddSourcesTo.isEmpty() || !this.sectionsToRemoveSourcesFrom.isEmpty();
    }

    @Override
    protected void onNodeRemoved(long i) {
        long j = SectionPosition.getZeroNode(i);
        boolean flag = this.columnsWithSkySources.contains(j);

        if (flag) {
            this.queueRemoveSource(i);
        }

        int k = SectionPosition.y(i);

        if (((LightEngineStorageSky.a) this.updatingSectionData).topSections.get(j) == k + 1) {
            long l;

            for (l = i; !this.storingLightForSection(l) && this.hasSectionsBelow(k); l = SectionPosition.offset(l, EnumDirection.DOWN)) {
                --k;
            }

            if (this.storingLightForSection(l)) {
                ((LightEngineStorageSky.a) this.updatingSectionData).topSections.put(j, k + 1);
                if (flag) {
                    this.queueAddSource(l);
                }
            } else {
                ((LightEngineStorageSky.a) this.updatingSectionData).topSections.remove(j);
            }
        }

        if (flag) {
            this.recheckInconsistencyFlag();
        }

    }

    @Override
    protected void enableLightSources(long i, boolean flag) {
        this.runAllUpdates();
        if (flag && this.columnsWithSkySources.add(i)) {
            int j = ((LightEngineStorageSky.a) this.updatingSectionData).topSections.get(i);

            if (j != ((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY) {
                long k = SectionPosition.asLong(SectionPosition.x(i), j - 1, SectionPosition.z(i));

                this.queueAddSource(k);
                this.recheckInconsistencyFlag();
            }
        } else if (!flag) {
            this.columnsWithSkySources.remove(i);
        }

    }

    @Override
    protected boolean hasInconsistencies() {
        return super.hasInconsistencies() || this.hasSourceInconsistencies;
    }

    @Override
    protected NibbleArray createDataLayer(long i) {
        NibbleArray nibblearray = (NibbleArray) this.queuedSections.get(i);

        if (nibblearray != null) {
            return nibblearray;
        } else {
            long j = SectionPosition.offset(i, EnumDirection.UP);
            int k = ((LightEngineStorageSky.a) this.updatingSectionData).topSections.get(SectionPosition.getZeroNode(i));

            if (k != ((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY && SectionPosition.y(j) < k) {
                NibbleArray nibblearray1;

                while ((nibblearray1 = this.getDataLayer(j, true)) == null) {
                    j = SectionPosition.offset(j, EnumDirection.UP);
                }

                return repeatFirstLayer(nibblearray1);
            } else {
                return new NibbleArray();
            }
        }
    }

    private static NibbleArray repeatFirstLayer(NibbleArray nibblearray) {
        if (nibblearray.isEmpty()) {
            return new NibbleArray();
        } else {
            byte[] abyte = nibblearray.getData();
            byte[] abyte1 = new byte[2048];

            for (int i = 0; i < 16; ++i) {
                System.arraycopy(abyte, 0, abyte1, i * 128, 128);
            }

            return new NibbleArray(abyte1);
        }
    }

    @Override
    protected void markNewInconsistencies(LightEngineLayer<LightEngineStorageSky.a, ?> lightenginelayer, boolean flag, boolean flag1) {
        super.markNewInconsistencies(lightenginelayer, flag, flag1);
        if (flag) {
            LongIterator longiterator;
            long i;
            int j;
            int k;

            if (!this.sectionsToAddSourcesTo.isEmpty()) {
                longiterator = this.sectionsToAddSourcesTo.iterator();

                while (longiterator.hasNext()) {
                    i = (Long) longiterator.next();
                    j = this.getLevel(i);
                    if (j != 2 && !this.sectionsToRemoveSourcesFrom.contains(i) && this.sectionsWithSources.add(i)) {
                        int l;

                        if (j == 1) {
                            this.clearQueuedSectionBlocks(lightenginelayer, i);
                            if (this.changedSections.add(i)) {
                                ((LightEngineStorageSky.a) this.updatingSectionData).copyDataLayer(i);
                            }

                            Arrays.fill(this.getDataLayer(i, true).getData(), (byte) -1);
                            k = SectionPosition.sectionToBlockCoord(SectionPosition.x(i));
                            l = SectionPosition.sectionToBlockCoord(SectionPosition.y(i));
                            int i1 = SectionPosition.sectionToBlockCoord(SectionPosition.z(i));
                            EnumDirection[] aenumdirection = LightEngineStorageSky.HORIZONTALS;
                            int j1 = aenumdirection.length;

                            long k1;

                            for (int l1 = 0; l1 < j1; ++l1) {
                                EnumDirection enumdirection = aenumdirection[l1];

                                k1 = SectionPosition.offset(i, enumdirection);
                                if ((this.sectionsToRemoveSourcesFrom.contains(k1) || !this.sectionsWithSources.contains(k1) && !this.sectionsToAddSourcesTo.contains(k1)) && this.storingLightForSection(k1)) {
                                    for (int i2 = 0; i2 < 16; ++i2) {
                                        for (int j2 = 0; j2 < 16; ++j2) {
                                            long k2;
                                            long l2;

                                            switch (enumdirection) {
                                                case NORTH:
                                                    k2 = BlockPosition.asLong(k + i2, l + j2, i1);
                                                    l2 = BlockPosition.asLong(k + i2, l + j2, i1 - 1);
                                                    break;
                                                case SOUTH:
                                                    k2 = BlockPosition.asLong(k + i2, l + j2, i1 + 16 - 1);
                                                    l2 = BlockPosition.asLong(k + i2, l + j2, i1 + 16);
                                                    break;
                                                case WEST:
                                                    k2 = BlockPosition.asLong(k, l + i2, i1 + j2);
                                                    l2 = BlockPosition.asLong(k - 1, l + i2, i1 + j2);
                                                    break;
                                                default:
                                                    k2 = BlockPosition.asLong(k + 16 - 1, l + i2, i1 + j2);
                                                    l2 = BlockPosition.asLong(k + 16, l + i2, i1 + j2);
                                            }

                                            lightenginelayer.checkEdge(k2, l2, lightenginelayer.computeLevelFromNeighbor(k2, l2, 0), true);
                                        }
                                    }
                                }
                            }

                            for (int i3 = 0; i3 < 16; ++i3) {
                                for (j1 = 0; j1 < 16; ++j1) {
                                    long j3 = BlockPosition.asLong(SectionPosition.sectionToBlockCoord(SectionPosition.x(i), i3), SectionPosition.sectionToBlockCoord(SectionPosition.y(i)), SectionPosition.sectionToBlockCoord(SectionPosition.z(i), j1));

                                    k1 = BlockPosition.asLong(SectionPosition.sectionToBlockCoord(SectionPosition.x(i), i3), SectionPosition.sectionToBlockCoord(SectionPosition.y(i)) - 1, SectionPosition.sectionToBlockCoord(SectionPosition.z(i), j1));
                                    lightenginelayer.checkEdge(j3, k1, lightenginelayer.computeLevelFromNeighbor(j3, k1, 0), true);
                                }
                            }
                        } else {
                            for (k = 0; k < 16; ++k) {
                                for (l = 0; l < 16; ++l) {
                                    long k3 = BlockPosition.asLong(SectionPosition.sectionToBlockCoord(SectionPosition.x(i), k), SectionPosition.sectionToBlockCoord(SectionPosition.y(i), 15), SectionPosition.sectionToBlockCoord(SectionPosition.z(i), l));

                                    lightenginelayer.checkEdge(Long.MAX_VALUE, k3, 0, true);
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
                    if (this.sectionsWithSources.remove(i) && this.storingLightForSection(i)) {
                        for (j = 0; j < 16; ++j) {
                            for (k = 0; k < 16; ++k) {
                                long l3 = BlockPosition.asLong(SectionPosition.sectionToBlockCoord(SectionPosition.x(i), j), SectionPosition.sectionToBlockCoord(SectionPosition.y(i), 15), SectionPosition.sectionToBlockCoord(SectionPosition.z(i), k));

                                lightenginelayer.checkEdge(Long.MAX_VALUE, l3, 15, false);
                            }
                        }
                    }
                }
            }

            this.sectionsToRemoveSourcesFrom.clear();
            this.hasSourceInconsistencies = false;
        }
    }

    protected boolean hasSectionsBelow(int i) {
        return i >= ((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY;
    }

    protected boolean isAboveData(long i) {
        long j = SectionPosition.getZeroNode(i);
        int k = ((LightEngineStorageSky.a) this.updatingSectionData).topSections.get(j);

        return k == ((LightEngineStorageSky.a) this.updatingSectionData).currentLowestY || SectionPosition.y(i) >= k;
    }

    protected boolean lightOnInSection(long i) {
        long j = SectionPosition.getZeroNode(i);

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
        public LightEngineStorageSky.a copy() {
            return new LightEngineStorageSky.a(this.map.clone(), this.topSections.clone(), this.currentLowestY);
        }
    }
}
