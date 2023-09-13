package net.minecraft.world.level.entity;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import java.util.Objects;
import java.util.Spliterators;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.phys.AxisAlignedBB;

public class EntitySectionStorage<T extends EntityAccess> {

    private final Class<T> entityClass;
    private final Long2ObjectFunction<Visibility> intialSectionVisibility;
    private final Long2ObjectMap<EntitySection<T>> sections = new Long2ObjectOpenHashMap();
    private final LongSortedSet sectionIds = new LongAVLTreeSet();

    public EntitySectionStorage(Class<T> oclass, Long2ObjectFunction<Visibility> long2objectfunction) {
        this.entityClass = oclass;
        this.intialSectionVisibility = long2objectfunction;
    }

    public void forEachAccessibleNonEmptySection(AxisAlignedBB axisalignedbb, AbortableIterationConsumer<EntitySection<T>> abortableiterationconsumer) {
        boolean flag = true;
        int i = SectionPosition.posToSectionCoord(axisalignedbb.minX - 2.0D);
        int j = SectionPosition.posToSectionCoord(axisalignedbb.minY - 4.0D);
        int k = SectionPosition.posToSectionCoord(axisalignedbb.minZ - 2.0D);
        int l = SectionPosition.posToSectionCoord(axisalignedbb.maxX + 2.0D);
        int i1 = SectionPosition.posToSectionCoord(axisalignedbb.maxY + 0.0D);
        int j1 = SectionPosition.posToSectionCoord(axisalignedbb.maxZ + 2.0D);

        for (int k1 = i; k1 <= l; ++k1) {
            long l1 = SectionPosition.asLong(k1, 0, 0);
            long i2 = SectionPosition.asLong(k1, -1, -1);
            LongBidirectionalIterator longbidirectionaliterator = this.sectionIds.subSet(l1, i2 + 1L).iterator();

            while (longbidirectionaliterator.hasNext()) {
                long j2 = longbidirectionaliterator.nextLong();
                int k2 = SectionPosition.y(j2);
                int l2 = SectionPosition.z(j2);

                if (k2 >= j && k2 <= i1 && l2 >= k && l2 <= j1) {
                    EntitySection<T> entitysection = (EntitySection) this.sections.get(j2);

                    if (entitysection != null && !entitysection.isEmpty() && entitysection.getStatus().isAccessible() && abortableiterationconsumer.accept(entitysection).shouldAbort()) {
                        return;
                    }
                }
            }
        }

    }

    public LongStream getExistingSectionPositionsInChunk(long i) {
        int j = ChunkCoordIntPair.getX(i);
        int k = ChunkCoordIntPair.getZ(i);
        LongSortedSet longsortedset = this.getChunkSections(j, k);

        if (longsortedset.isEmpty()) {
            return LongStream.empty();
        } else {
            LongBidirectionalIterator longbidirectionaliterator = longsortedset.iterator();

            return StreamSupport.longStream(Spliterators.spliteratorUnknownSize(longbidirectionaliterator, 1301), false);
        }
    }

    private LongSortedSet getChunkSections(int i, int j) {
        long k = SectionPosition.asLong(i, 0, j);
        long l = SectionPosition.asLong(i, -1, j);

        return this.sectionIds.subSet(k, l + 1L);
    }

    public Stream<EntitySection<T>> getExistingSectionsInChunk(long i) {
        LongStream longstream = this.getExistingSectionPositionsInChunk(i);
        Long2ObjectMap long2objectmap = this.sections;

        Objects.requireNonNull(this.sections);
        return longstream.mapToObj(long2objectmap::get).filter(Objects::nonNull);
    }

    private static long getChunkKeyFromSectionKey(long i) {
        return ChunkCoordIntPair.asLong(SectionPosition.x(i), SectionPosition.z(i));
    }

    public EntitySection<T> getOrCreateSection(long i) {
        return (EntitySection) this.sections.computeIfAbsent(i, this::createSection);
    }

    @Nullable
    public EntitySection<T> getSection(long i) {
        return (EntitySection) this.sections.get(i);
    }

    private EntitySection<T> createSection(long i) {
        long j = getChunkKeyFromSectionKey(i);
        Visibility visibility = (Visibility) this.intialSectionVisibility.get(j);

        this.sectionIds.add(i);
        return new EntitySection<>(this.entityClass, visibility);
    }

    public LongSet getAllChunksWithExistingSections() {
        LongOpenHashSet longopenhashset = new LongOpenHashSet();

        this.sections.keySet().forEach((i) -> {
            longopenhashset.add(getChunkKeyFromSectionKey(i));
        });
        return longopenhashset;
    }

    public void getEntities(AxisAlignedBB axisalignedbb, AbortableIterationConsumer<T> abortableiterationconsumer) {
        this.forEachAccessibleNonEmptySection(axisalignedbb, (entitysection) -> {
            return entitysection.getEntities(axisalignedbb, abortableiterationconsumer);
        });
    }

    public <U extends T> void getEntities(EntityTypeTest<T, U> entitytypetest, AxisAlignedBB axisalignedbb, AbortableIterationConsumer<U> abortableiterationconsumer) {
        this.forEachAccessibleNonEmptySection(axisalignedbb, (entitysection) -> {
            return entitysection.getEntities(entitytypetest, axisalignedbb, abortableiterationconsumer);
        });
    }

    public void remove(long i) {
        this.sections.remove(i);
        this.sectionIds.remove(i);
    }

    @VisibleForDebug
    public int count() {
        return this.sectionIds.size();
    }
}
