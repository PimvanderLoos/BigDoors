package net.minecraft.world.entity.ai.village.poi;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.LightEngineGraphSection;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.storage.RegionFileSection;

public class VillagePlace extends RegionFileSection<VillagePlaceSection> {

    public static final int MAX_VILLAGE_DISTANCE = 6;
    public static final int VILLAGE_SECTION_SIZE = 1;
    private final VillagePlace.a distanceTracker = new VillagePlace.a();
    private final LongSet loadedChunks = new LongOpenHashSet();

    public VillagePlace(File file, DataFixer datafixer, boolean flag, LevelHeightAccessor levelheightaccessor) {
        super(file, VillagePlaceSection::a, VillagePlaceSection::new, datafixer, DataFixTypes.POI_CHUNK, flag, levelheightaccessor);
    }

    public void a(BlockPosition blockposition, VillagePlaceType villageplacetype) {
        ((VillagePlaceSection) this.f(SectionPosition.c(blockposition))).a(blockposition, villageplacetype);
    }

    public void a(BlockPosition blockposition) {
        this.d(SectionPosition.c(blockposition)).ifPresent((villageplacesection) -> {
            villageplacesection.a(blockposition);
        });
    }

    public long a(Predicate<VillagePlaceType> predicate, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        return this.c(predicate, blockposition, i, villageplace_occupancy).count();
    }

    public boolean a(VillagePlaceType villageplacetype, BlockPosition blockposition) {
        Objects.requireNonNull(villageplacetype);
        return this.a(blockposition, villageplacetype::equals);
    }

    public Stream<VillagePlaceRecord> b(Predicate<VillagePlaceType> predicate, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        int j = Math.floorDiv(i, 16) + 1;

        return ChunkCoordIntPair.a(new ChunkCoordIntPair(blockposition), j).flatMap((chunkcoordintpair) -> {
            return this.a(predicate, chunkcoordintpair, villageplace_occupancy);
        }).filter((villageplacerecord) -> {
            BlockPosition blockposition1 = villageplacerecord.f();

            return Math.abs(blockposition1.getX() - blockposition.getX()) <= i && Math.abs(blockposition1.getZ() - blockposition.getZ()) <= i;
        });
    }

    public Stream<VillagePlaceRecord> c(Predicate<VillagePlaceType> predicate, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        int j = i * i;

        return this.b(predicate, blockposition, i, villageplace_occupancy).filter((villageplacerecord) -> {
            return villageplacerecord.f().j(blockposition) <= (double) j;
        });
    }

    @VisibleForDebug
    public Stream<VillagePlaceRecord> a(Predicate<VillagePlaceType> predicate, ChunkCoordIntPair chunkcoordintpair, VillagePlace.Occupancy villageplace_occupancy) {
        return IntStream.range(this.levelHeightAccessor.getMinSection(), this.levelHeightAccessor.getMaxSection()).boxed().map((integer) -> {
            return this.d(SectionPosition.a(chunkcoordintpair, integer).s());
        }).filter(Optional::isPresent).flatMap((optional) -> {
            return ((VillagePlaceSection) optional.get()).a(predicate, villageplace_occupancy);
        });
    }

    public Stream<BlockPosition> a(Predicate<VillagePlaceType> predicate, Predicate<BlockPosition> predicate1, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        return this.c(predicate, blockposition, i, villageplace_occupancy).map(VillagePlaceRecord::f).filter(predicate1);
    }

    public Stream<BlockPosition> b(Predicate<VillagePlaceType> predicate, Predicate<BlockPosition> predicate1, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        return this.a(predicate, predicate1, blockposition, i, villageplace_occupancy).sorted(Comparator.comparingDouble((blockposition1) -> {
            return blockposition1.j(blockposition);
        }));
    }

    public Optional<BlockPosition> c(Predicate<VillagePlaceType> predicate, Predicate<BlockPosition> predicate1, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        return this.a(predicate, predicate1, blockposition, i, villageplace_occupancy).findFirst();
    }

    public Optional<BlockPosition> d(Predicate<VillagePlaceType> predicate, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        return this.c(predicate, blockposition, i, villageplace_occupancy).map(VillagePlaceRecord::f).min(Comparator.comparingDouble((blockposition1) -> {
            return blockposition1.j(blockposition);
        }));
    }

    public Optional<BlockPosition> d(Predicate<VillagePlaceType> predicate, Predicate<BlockPosition> predicate1, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        return this.c(predicate, blockposition, i, villageplace_occupancy).map(VillagePlaceRecord::f).filter(predicate1).min(Comparator.comparingDouble((blockposition1) -> {
            return blockposition1.j(blockposition);
        }));
    }

    public Optional<BlockPosition> a(Predicate<VillagePlaceType> predicate, Predicate<BlockPosition> predicate1, BlockPosition blockposition, int i) {
        return this.c(predicate, blockposition, i, VillagePlace.Occupancy.HAS_SPACE).filter((villageplacerecord) -> {
            return predicate1.test(villageplacerecord.f());
        }).findFirst().map((villageplacerecord) -> {
            villageplacerecord.b();
            return villageplacerecord.f();
        });
    }

    public Optional<BlockPosition> a(Predicate<VillagePlaceType> predicate, Predicate<BlockPosition> predicate1, VillagePlace.Occupancy villageplace_occupancy, BlockPosition blockposition, int i, Random random) {
        List<VillagePlaceRecord> list = (List) this.c(predicate, blockposition, i, villageplace_occupancy).collect(Collectors.toList());

        Collections.shuffle(list, random);
        return list.stream().filter((villageplacerecord) -> {
            return predicate1.test(villageplacerecord.f());
        }).findFirst().map(VillagePlaceRecord::f);
    }

    public boolean b(BlockPosition blockposition) {
        return (Boolean) this.d(SectionPosition.c(blockposition)).map((villageplacesection) -> {
            return villageplacesection.c(blockposition);
        }).orElseThrow(() -> {
            return (IllegalStateException) SystemUtils.c((Throwable) (new IllegalStateException("POI never registered at " + blockposition)));
        });
    }

    public boolean a(BlockPosition blockposition, Predicate<VillagePlaceType> predicate) {
        return (Boolean) this.d(SectionPosition.c(blockposition)).map((villageplacesection) -> {
            return villageplacesection.a(blockposition, predicate);
        }).orElse(false);
    }

    public Optional<VillagePlaceType> c(BlockPosition blockposition) {
        return this.d(SectionPosition.c(blockposition)).flatMap((villageplacesection) -> {
            return villageplacesection.d(blockposition);
        });
    }

    @Deprecated
    @VisibleForDebug
    public int d(BlockPosition blockposition) {
        return (Integer) this.d(SectionPosition.c(blockposition)).map((villageplacesection) -> {
            return villageplacesection.b(blockposition);
        }).orElse(0);
    }

    public int a(SectionPosition sectionposition) {
        this.distanceTracker.a();
        return this.distanceTracker.c(sectionposition.s());
    }

    boolean g(long i) {
        Optional<VillagePlaceSection> optional = this.c(i);

        return optional == null ? false : (Boolean) optional.map((villageplacesection) -> {
            return villageplacesection.a(VillagePlaceType.ALL, VillagePlace.Occupancy.IS_OCCUPIED).count() > 0L;
        }).orElse(false);
    }

    @Override
    public void a(BooleanSupplier booleansupplier) {
        super.a(booleansupplier);
        this.distanceTracker.a();
    }

    @Override
    protected void a(long i) {
        super.a(i);
        this.distanceTracker.b(i, this.distanceTracker.b(i), false);
    }

    @Override
    protected void b(long i) {
        this.distanceTracker.b(i, this.distanceTracker.b(i), false);
    }

    public void a(ChunkCoordIntPair chunkcoordintpair, ChunkSection chunksection) {
        SectionPosition sectionposition = SectionPosition.a(chunkcoordintpair, SectionPosition.a(chunksection.getYPosition()));

        SystemUtils.a(this.d(sectionposition.s()), (villageplacesection) -> {
            villageplacesection.a((biconsumer) -> {
                if (a(chunksection)) {
                    this.a(chunksection, sectionposition, biconsumer);
                }

            });
        }, () -> {
            if (a(chunksection)) {
                VillagePlaceSection villageplacesection = (VillagePlaceSection) this.f(sectionposition.s());

                Objects.requireNonNull(villageplacesection);
                this.a(chunksection, sectionposition, villageplacesection::a);
            }

        });
    }

    private static boolean a(ChunkSection chunksection) {
        Set set = VillagePlaceType.ALL_STATES;

        Objects.requireNonNull(set);
        return chunksection.a(set::contains);
    }

    private void a(ChunkSection chunksection, SectionPosition sectionposition, BiConsumer<BlockPosition, VillagePlaceType> biconsumer) {
        sectionposition.t().forEach((blockposition) -> {
            IBlockData iblockdata = chunksection.getType(SectionPosition.b(blockposition.getX()), SectionPosition.b(blockposition.getY()), SectionPosition.b(blockposition.getZ()));

            VillagePlaceType.b(iblockdata).ifPresent((villageplacetype) -> {
                biconsumer.accept(blockposition, villageplacetype);
            });
        });
    }

    public void a(IWorldReader iworldreader, BlockPosition blockposition, int i) {
        SectionPosition.a(new ChunkCoordIntPair(blockposition), Math.floorDiv(i, 16), this.levelHeightAccessor.getMinSection(), this.levelHeightAccessor.getMaxSection()).map((sectionposition) -> {
            return Pair.of(sectionposition, this.d(sectionposition.s()));
        }).filter((pair) -> {
            return !(Boolean) ((Optional) pair.getSecond()).map(VillagePlaceSection::a).orElse(false);
        }).map((pair) -> {
            return ((SectionPosition) pair.getFirst()).r();
        }).filter((chunkcoordintpair) -> {
            return this.loadedChunks.add(chunkcoordintpair.pair());
        }).forEach((chunkcoordintpair) -> {
            iworldreader.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z, ChunkStatus.EMPTY);
        });
    }

    private final class a extends LightEngineGraphSection {

        private final Long2ByteMap levels = new Long2ByteOpenHashMap();

        protected a() {
            super(7, 16, 256);
            this.levels.defaultReturnValue((byte) 7);
        }

        @Override
        protected int b(long i) {
            return VillagePlace.this.g(i) ? 0 : 7;
        }

        @Override
        protected int c(long i) {
            return this.levels.get(i);
        }

        @Override
        protected void a(long i, int j) {
            if (j > 6) {
                this.levels.remove(i);
            } else {
                this.levels.put(i, (byte) j);
            }

        }

        public void a() {
            super.b(Integer.MAX_VALUE);
        }
    }

    public static enum Occupancy {

        HAS_SPACE(VillagePlaceRecord::d), IS_OCCUPIED(VillagePlaceRecord::e), ANY((villageplacerecord) -> {
            return true;
        });

        private final Predicate<? super VillagePlaceRecord> test;

        private Occupancy(Predicate predicate) {
            this.test = predicate;
        }

        public Predicate<? super VillagePlaceRecord> a() {
            return this.test;
        }
    }
}
