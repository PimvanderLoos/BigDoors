package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Objects;
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
        this.visibleSectionData = m0.copy();
        this.visibleSectionData.disableCache();
    }

    protected boolean storingLightForSection(long i) {
        return this.getDataLayer(i, true) != null;
    }

    @Nullable
    protected NibbleArray getDataLayer(long i, boolean flag) {
        return this.getDataLayer(flag ? this.updatingSectionData : this.visibleSectionData, i);
    }

    @Nullable
    protected NibbleArray getDataLayer(M m0, long i) {
        return m0.getLayer(i);
    }

    @Nullable
    public NibbleArray getDataLayerData(long i) {
        NibbleArray nibblearray = (NibbleArray) this.queuedSections.get(i);

        return nibblearray != null ? nibblearray : this.getDataLayer(i, false);
    }

    protected abstract int getLightValue(long i);

    protected int getStoredLevel(long i) {
        long j = SectionPosition.blockToSection(i);
        NibbleArray nibblearray = this.getDataLayer(j, true);

        return nibblearray.get(SectionPosition.sectionRelative(BlockPosition.getX(i)), SectionPosition.sectionRelative(BlockPosition.getY(i)), SectionPosition.sectionRelative(BlockPosition.getZ(i)));
    }

    protected void setStoredLevel(long i, int j) {
        long k = SectionPosition.blockToSection(i);

        if (this.changedSections.add(k)) {
            this.updatingSectionData.copyDataLayer(k);
        }

        NibbleArray nibblearray = this.getDataLayer(k, true);

        nibblearray.set(SectionPosition.sectionRelative(BlockPosition.getX(i)), SectionPosition.sectionRelative(BlockPosition.getY(i)), SectionPosition.sectionRelative(BlockPosition.getZ(i)), j);
        LongSet longset = this.sectionsAffectedByLightUpdates;

        Objects.requireNonNull(this.sectionsAffectedByLightUpdates);
        SectionPosition.aroundAndAtBlockPos(i, longset::add);
    }

    @Override
    protected int getLevel(long i) {
        return i == Long.MAX_VALUE ? 2 : (this.dataSectionSet.contains(i) ? 0 : (!this.toRemove.contains(i) && this.updatingSectionData.hasLayer(i) ? 1 : 2));
    }

    @Override
    protected int getLevelFromSource(long i) {
        return this.toMarkNoData.contains(i) ? 2 : (!this.dataSectionSet.contains(i) && !this.toMarkData.contains(i) ? 2 : 0);
    }

    @Override
    protected void setLevel(long i, int j) {
        int k = this.getLevel(i);

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
                this.updatingSectionData.setLayer(i, this.createDataLayer(i));
                this.changedSections.add(i);
                this.onNodeAdded(i);
                int l = SectionPosition.x(i);
                int i1 = SectionPosition.y(i);
                int j1 = SectionPosition.z(i);

                for (int k1 = -1; k1 <= 1; ++k1) {
                    for (int l1 = -1; l1 <= 1; ++l1) {
                        for (int i2 = -1; i2 <= 1; ++i2) {
                            this.sectionsAffectedByLightUpdates.add(SectionPosition.asLong(l + l1, i1 + i2, j1 + k1));
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

    protected NibbleArray createDataLayer(long i) {
        NibbleArray nibblearray = (NibbleArray) this.queuedSections.get(i);

        return nibblearray != null ? nibblearray : new NibbleArray();
    }

    protected void clearQueuedSectionBlocks(LightEngineLayer<?, ?> lightenginelayer, long i) {
        if (lightenginelayer.getQueueSize() != 0) {
            if (lightenginelayer.getQueueSize() < 8192) {
                lightenginelayer.removeIf((j) -> {
                    return SectionPosition.blockToSection(j) == i;
                });
            } else {
                int j = SectionPosition.sectionToBlockCoord(SectionPosition.x(i));
                int k = SectionPosition.sectionToBlockCoord(SectionPosition.y(i));
                int l = SectionPosition.sectionToBlockCoord(SectionPosition.z(i));

                for (int i1 = 0; i1 < 16; ++i1) {
                    for (int j1 = 0; j1 < 16; ++j1) {
                        for (int k1 = 0; k1 < 16; ++k1) {
                            long l1 = BlockPosition.asLong(j + i1, k + j1, l + k1);

                            lightenginelayer.removeFromQueue(l1);
                        }
                    }
                }

            }
        }
    }

    protected boolean hasInconsistencies() {
        return this.hasToRemove;
    }

    protected void markNewInconsistencies(LightEngineLayer<M, ?> lightenginelayer, boolean flag, boolean flag1) {
        if (this.hasInconsistencies() || !this.queuedSections.isEmpty()) {
            LongIterator longiterator = this.toRemove.iterator();

            long i;
            NibbleArray nibblearray;

            while (longiterator.hasNext()) {
                i = (Long) longiterator.next();
                this.clearQueuedSectionBlocks(lightenginelayer, i);
                NibbleArray nibblearray1 = (NibbleArray) this.queuedSections.remove(i);

                nibblearray = this.updatingSectionData.removeLayer(i);
                if (this.columnsToRetainQueuedDataFor.contains(SectionPosition.getZeroNode(i))) {
                    if (nibblearray1 != null) {
                        this.queuedSections.put(i, nibblearray1);
                    } else if (nibblearray != null) {
                        this.queuedSections.put(i, nibblearray);
                    }
                }
            }

            this.updatingSectionData.clearCache();
            longiterator = this.toRemove.iterator();

            while (longiterator.hasNext()) {
                i = (Long) longiterator.next();
                this.onNodeRemoved(i);
            }

            this.toRemove.clear();
            this.hasToRemove = false;
            ObjectIterator objectiterator = this.queuedSections.long2ObjectEntrySet().iterator();

            Entry entry;
            long j;

            while (objectiterator.hasNext()) {
                entry = (Entry) objectiterator.next();
                j = entry.getLongKey();
                if (this.storingLightForSection(j)) {
                    nibblearray = (NibbleArray) entry.getValue();
                    if (this.updatingSectionData.getLayer(j) != nibblearray) {
                        this.clearQueuedSectionBlocks(lightenginelayer, j);
                        this.updatingSectionData.setLayer(j, nibblearray);
                        this.changedSections.add(j);
                    }
                }
            }

            this.updatingSectionData.clearCache();
            if (!flag1) {
                longiterator = this.queuedSections.keySet().iterator();

                while (longiterator.hasNext()) {
                    i = (Long) longiterator.next();
                    this.checkEdgesForSection(lightenginelayer, i);
                }
            } else {
                longiterator = this.untrustedSections.iterator();

                while (longiterator.hasNext()) {
                    i = (Long) longiterator.next();
                    this.checkEdgesForSection(lightenginelayer, i);
                }
            }

            this.untrustedSections.clear();
            objectiterator = this.queuedSections.long2ObjectEntrySet().iterator();

            while (objectiterator.hasNext()) {
                entry = (Entry) objectiterator.next();
                j = entry.getLongKey();
                if (this.storingLightForSection(j)) {
                    objectiterator.remove();
                }
            }

        }
    }

    private void checkEdgesForSection(LightEngineLayer<M, ?> lightenginelayer, long i) {
        if (this.storingLightForSection(i)) {
            int j = SectionPosition.sectionToBlockCoord(SectionPosition.x(i));
            int k = SectionPosition.sectionToBlockCoord(SectionPosition.y(i));
            int l = SectionPosition.sectionToBlockCoord(SectionPosition.z(i));
            EnumDirection[] aenumdirection = LightEngineStorage.DIRECTIONS;
            int i1 = aenumdirection.length;

            for (int j1 = 0; j1 < i1; ++j1) {
                EnumDirection enumdirection = aenumdirection[j1];
                long k1 = SectionPosition.offset(i, enumdirection);

                if (!this.queuedSections.containsKey(k1) && this.storingLightForSection(k1)) {
                    for (int l1 = 0; l1 < 16; ++l1) {
                        for (int i2 = 0; i2 < 16; ++i2) {
                            long j2;
                            long k2;

                            switch (enumdirection) {
                                case DOWN:
                                    j2 = BlockPosition.asLong(j + i2, k, l + l1);
                                    k2 = BlockPosition.asLong(j + i2, k - 1, l + l1);
                                    break;
                                case UP:
                                    j2 = BlockPosition.asLong(j + i2, k + 16 - 1, l + l1);
                                    k2 = BlockPosition.asLong(j + i2, k + 16, l + l1);
                                    break;
                                case NORTH:
                                    j2 = BlockPosition.asLong(j + l1, k + i2, l);
                                    k2 = BlockPosition.asLong(j + l1, k + i2, l - 1);
                                    break;
                                case SOUTH:
                                    j2 = BlockPosition.asLong(j + l1, k + i2, l + 16 - 1);
                                    k2 = BlockPosition.asLong(j + l1, k + i2, l + 16);
                                    break;
                                case WEST:
                                    j2 = BlockPosition.asLong(j, k + l1, l + i2);
                                    k2 = BlockPosition.asLong(j - 1, k + l1, l + i2);
                                    break;
                                default:
                                    j2 = BlockPosition.asLong(j + 16 - 1, k + l1, l + i2);
                                    k2 = BlockPosition.asLong(j + 16, k + l1, l + i2);
                            }

                            lightenginelayer.checkEdge(j2, k2, lightenginelayer.computeLevelFromNeighbor(j2, k2, lightenginelayer.getLevel(j2)), false);
                            lightenginelayer.checkEdge(k2, j2, lightenginelayer.computeLevelFromNeighbor(k2, j2, lightenginelayer.getLevel(k2)), false);
                        }
                    }
                }
            }

        }
    }

    protected void onNodeAdded(long i) {}

    protected void onNodeRemoved(long i) {}

    protected void enableLightSources(long i, boolean flag) {}

    public void retainData(long i, boolean flag) {
        if (flag) {
            this.columnsToRetainQueuedDataFor.add(i);
        } else {
            this.columnsToRetainQueuedDataFor.remove(i);
        }

    }

    protected void queueSectionData(long i, @Nullable NibbleArray nibblearray, boolean flag) {
        if (nibblearray != null) {
            this.queuedSections.put(i, nibblearray);
            if (!flag) {
                this.untrustedSections.add(i);
            }
        } else {
            this.queuedSections.remove(i);
        }

    }

    protected void updateSectionStatus(long i, boolean flag) {
        boolean flag1 = this.dataSectionSet.contains(i);

        if (!flag1 && !flag) {
            this.toMarkData.add(i);
            this.checkEdge(Long.MAX_VALUE, i, 0, true);
        }

        if (flag1 && flag) {
            this.toMarkNoData.add(i);
            this.checkEdge(Long.MAX_VALUE, i, 2, false);
        }

    }

    protected void runAllUpdates() {
        if (this.hasWork()) {
            this.runUpdates(Integer.MAX_VALUE);
        }

    }

    protected void swapSectionMap() {
        if (!this.changedSections.isEmpty()) {
            M m0 = this.updatingSectionData.copy();

            m0.disableCache();
            this.visibleSectionData = m0;
            this.changedSections.clear();
        }

        if (!this.sectionsAffectedByLightUpdates.isEmpty()) {
            LongIterator longiterator = this.sectionsAffectedByLightUpdates.iterator();

            while (longiterator.hasNext()) {
                long i = longiterator.nextLong();

                this.chunkSource.onLightUpdate(this.layer, SectionPosition.of(i));
            }

            this.sectionsAffectedByLightUpdates.clear();
        }

    }
}
