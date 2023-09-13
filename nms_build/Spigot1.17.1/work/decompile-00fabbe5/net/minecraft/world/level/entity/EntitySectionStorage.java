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
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPosition;
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

    public void a(AxisAlignedBB axisalignedbb, Consumer<EntitySection<T>> consumer) {
        int i = SectionPosition.a(axisalignedbb.minX - 2.0D);
        int j = SectionPosition.a(axisalignedbb.minY - 2.0D);
        int k = SectionPosition.a(axisalignedbb.minZ - 2.0D);
        int l = SectionPosition.a(axisalignedbb.maxX + 2.0D);
        int i1 = SectionPosition.a(axisalignedbb.maxY + 2.0D);
        int j1 = SectionPosition.a(axisalignedbb.maxZ + 2.0D);

        for (int k1 = i; k1 <= l; ++k1) {
            long l1 = SectionPosition.b(k1, 0, 0);
            long i2 = SectionPosition.b(k1, -1, -1);
            LongBidirectionalIterator longbidirectionaliterator = this.sectionIds.subSet(l1, i2 + 1L).iterator();

            while (longbidirectionaliterator.hasNext()) {
                long j2 = longbidirectionaliterator.nextLong();
                int k2 = SectionPosition.c(j2);
                int l2 = SectionPosition.d(j2);

                if (k2 >= j && k2 <= i1 && l2 >= k && l2 <= j1) {
                    EntitySection<T> entitysection = (EntitySection) this.sections.get(j2);

                    if (entitysection != null && entitysection.c().b()) {
                        consumer.accept(entitysection);
                    }
                }
            }
        }

    }

    public LongStream a(long i) {
        int j = ChunkCoordIntPair.getX(i);
        int k = ChunkCoordIntPair.getZ(i);
        LongSortedSet longsortedset = this.a(j, k);

        if (longsortedset.isEmpty()) {
            return LongStream.empty();
        } else {
            LongBidirectionalIterator longbidirectionaliterator = longsortedset.iterator();

            return StreamSupport.longStream(Spliterators.spliteratorUnknownSize(longbidirectionaliterator, 1301), false);
        }
    }

    private LongSortedSet a(int i, int j) {
        long k = SectionPosition.b(i, 0, j);
        long l = SectionPosition.b(i, -1, j);

        return this.sectionIds.subSet(k, l + 1L);
    }

    public Stream<EntitySection<T>> b(long i) {
        LongStream longstream = this.a(i);
        Long2ObjectMap long2objectmap = this.sections;

        Objects.requireNonNull(this.sections);
        return longstream.mapToObj(long2objectmap::get).filter(Objects::nonNull);
    }

    private static long f(long i) {
        return ChunkCoordIntPair.pair(SectionPosition.b(i), SectionPosition.d(i));
    }

    public EntitySection<T> c(long i) {
        return (EntitySection) this.sections.computeIfAbsent(i, this::g);
    }

    @Nullable
    public EntitySection<T> d(long i) {
        return (EntitySection) this.sections.get(i);
    }

    private EntitySection<T> g(long i) {
        long j = f(i);
        Visibility visibility = (Visibility) this.intialSectionVisibility.get(j);

        this.sectionIds.add(i);
        return new EntitySection<>(this.entityClass, visibility);
    }

    public LongSet a() {
        LongOpenHashSet longopenhashset = new LongOpenHashSet();

        this.sections.keySet().forEach((i) -> {
            longopenhashset.add(f(i));
        });
        return longopenhashset;
    }

    private static <T extends EntityAccess> Predicate<T> a(AxisAlignedBB axisalignedbb) {
        return (entityaccess) -> {
            return entityaccess.getBoundingBox().c(axisalignedbb);
        };
    }

    public void b(AxisAlignedBB axisalignedbb, Consumer<T> consumer) {
        this.a(axisalignedbb, (entitysection) -> {
            entitysection.a(a(axisalignedbb), consumer);
        });
    }

    public <U extends T> void a(EntityTypeTest<T, U> entitytypetest, AxisAlignedBB axisalignedbb, Consumer<U> consumer) {
        this.a(axisalignedbb, (entitysection) -> {
            entitysection.a(entitytypetest, a(axisalignedbb), consumer);
        });
    }

    public void e(long i) {
        this.sections.remove(i);
        this.sectionIds.remove(i);
    }

    @VisibleForDebug
    public int b() {
        return this.sectionIds.size();
    }
}
