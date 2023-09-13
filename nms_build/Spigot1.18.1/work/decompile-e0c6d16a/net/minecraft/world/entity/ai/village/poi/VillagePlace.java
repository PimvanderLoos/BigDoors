package net.minecraft.world.entity.ai.village.poi;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.nio.file.Path;
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

    public VillagePlace(Path path, DataFixer datafixer, boolean flag, LevelHeightAccessor levelheightaccessor) {
        super(path, VillagePlaceSection::codec, VillagePlaceSection::new, datafixer, DataFixTypes.POI_CHUNK, flag, levelheightaccessor);
    }

    public void add(BlockPosition blockposition, VillagePlaceType villageplacetype) {
        ((VillagePlaceSection) this.getOrCreate(SectionPosition.asLong(blockposition))).add(blockposition, villageplacetype);
    }

    public void remove(BlockPosition blockposition) {
        this.getOrLoad(SectionPosition.asLong(blockposition)).ifPresent((villageplacesection) -> {
            villageplacesection.remove(blockposition);
        });
    }

    public long getCountInRange(Predicate<VillagePlaceType> predicate, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        return this.getInRange(predicate, blockposition, i, villageplace_occupancy).count();
    }

    public boolean existsAtPosition(VillagePlaceType villageplacetype, BlockPosition blockposition) {
        Objects.requireNonNull(villageplacetype);
        return this.exists(blockposition, villageplacetype::equals);
    }

    public Stream<VillagePlaceRecord> getInSquare(Predicate<VillagePlaceType> predicate, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        int j = Math.floorDiv(i, 16) + 1;

        return ChunkCoordIntPair.rangeClosed(new ChunkCoordIntPair(blockposition), j).flatMap((chunkcoordintpair) -> {
            return this.getInChunk(predicate, chunkcoordintpair, villageplace_occupancy);
        }).filter((villageplacerecord) -> {
            BlockPosition blockposition1 = villageplacerecord.getPos();

            return Math.abs(blockposition1.getX() - blockposition.getX()) <= i && Math.abs(blockposition1.getZ() - blockposition.getZ()) <= i;
        });
    }

    public Stream<VillagePlaceRecord> getInRange(Predicate<VillagePlaceType> predicate, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        int j = i * i;

        return this.getInSquare(predicate, blockposition, i, villageplace_occupancy).filter((villageplacerecord) -> {
            return villageplacerecord.getPos().distSqr(blockposition) <= (double) j;
        });
    }

    @VisibleForDebug
    public Stream<VillagePlaceRecord> getInChunk(Predicate<VillagePlaceType> predicate, ChunkCoordIntPair chunkcoordintpair, VillagePlace.Occupancy villageplace_occupancy) {
        return IntStream.range(this.levelHeightAccessor.getMinSection(), this.levelHeightAccessor.getMaxSection()).boxed().map((integer) -> {
            return this.getOrLoad(SectionPosition.of(chunkcoordintpair, integer).asLong());
        }).filter(Optional::isPresent).flatMap((optional) -> {
            return ((VillagePlaceSection) optional.get()).getRecords(predicate, villageplace_occupancy);
        });
    }

    public Stream<BlockPosition> findAll(Predicate<VillagePlaceType> predicate, Predicate<BlockPosition> predicate1, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        return this.getInRange(predicate, blockposition, i, villageplace_occupancy).map(VillagePlaceRecord::getPos).filter(predicate1);
    }

    public Stream<BlockPosition> findAllClosestFirst(Predicate<VillagePlaceType> predicate, Predicate<BlockPosition> predicate1, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        return this.findAll(predicate, predicate1, blockposition, i, villageplace_occupancy).sorted(Comparator.comparingDouble((blockposition1) -> {
            return blockposition1.distSqr(blockposition);
        }));
    }

    public Optional<BlockPosition> find(Predicate<VillagePlaceType> predicate, Predicate<BlockPosition> predicate1, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        return this.findAll(predicate, predicate1, blockposition, i, villageplace_occupancy).findFirst();
    }

    public Optional<BlockPosition> findClosest(Predicate<VillagePlaceType> predicate, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        return this.getInRange(predicate, blockposition, i, villageplace_occupancy).map(VillagePlaceRecord::getPos).min(Comparator.comparingDouble((blockposition1) -> {
            return blockposition1.distSqr(blockposition);
        }));
    }

    public Optional<BlockPosition> findClosest(Predicate<VillagePlaceType> predicate, Predicate<BlockPosition> predicate1, BlockPosition blockposition, int i, VillagePlace.Occupancy villageplace_occupancy) {
        return this.getInRange(predicate, blockposition, i, villageplace_occupancy).map(VillagePlaceRecord::getPos).filter(predicate1).min(Comparator.comparingDouble((blockposition1) -> {
            return blockposition1.distSqr(blockposition);
        }));
    }

    public Optional<BlockPosition> take(Predicate<VillagePlaceType> predicate, Predicate<BlockPosition> predicate1, BlockPosition blockposition, int i) {
        return this.getInRange(predicate, blockposition, i, VillagePlace.Occupancy.HAS_SPACE).filter((villageplacerecord) -> {
            return predicate1.test(villageplacerecord.getPos());
        }).findFirst().map((villageplacerecord) -> {
            villageplacerecord.acquireTicket();
            return villageplacerecord.getPos();
        });
    }

    public Optional<BlockPosition> getRandom(Predicate<VillagePlaceType> predicate, Predicate<BlockPosition> predicate1, VillagePlace.Occupancy villageplace_occupancy, BlockPosition blockposition, int i, Random random) {
        List<VillagePlaceRecord> list = (List) this.getInRange(predicate, blockposition, i, villageplace_occupancy).collect(Collectors.toList());

        Collections.shuffle(list, random);
        return list.stream().filter((villageplacerecord) -> {
            return predicate1.test(villageplacerecord.getPos());
        }).findFirst().map(VillagePlaceRecord::getPos);
    }

    public boolean release(BlockPosition blockposition) {
        return (Boolean) this.getOrLoad(SectionPosition.asLong(blockposition)).map((villageplacesection) -> {
            return villageplacesection.release(blockposition);
        }).orElseThrow(() -> {
            return (IllegalStateException) SystemUtils.pauseInIde(new IllegalStateException("POI never registered at " + blockposition));
        });
    }

    public boolean exists(BlockPosition blockposition, Predicate<VillagePlaceType> predicate) {
        return (Boolean) this.getOrLoad(SectionPosition.asLong(blockposition)).map((villageplacesection) -> {
            return villageplacesection.exists(blockposition, predicate);
        }).orElse(false);
    }

    public Optional<VillagePlaceType> getType(BlockPosition blockposition) {
        return this.getOrLoad(SectionPosition.asLong(blockposition)).flatMap((villageplacesection) -> {
            return villageplacesection.getType(blockposition);
        });
    }

    /** @deprecated */
    @Deprecated
    @VisibleForDebug
    public int getFreeTickets(BlockPosition blockposition) {
        return (Integer) this.getOrLoad(SectionPosition.asLong(blockposition)).map((villageplacesection) -> {
            return villageplacesection.getFreeTickets(blockposition);
        }).orElse(0);
    }

    public int sectionsToVillage(SectionPosition sectionposition) {
        this.distanceTracker.runAllUpdates();
        return this.distanceTracker.getLevel(sectionposition.asLong());
    }

    boolean isVillageCenter(long i) {
        Optional<VillagePlaceSection> optional = this.get(i);

        return optional == null ? false : (Boolean) optional.map((villageplacesection) -> {
            return villageplacesection.getRecords(VillagePlaceType.ALL, VillagePlace.Occupancy.IS_OCCUPIED).count() > 0L;
        }).orElse(false);
    }

    @Override
    public void tick(BooleanSupplier booleansupplier) {
        super.tick(booleansupplier);
        this.distanceTracker.runAllUpdates();
    }

    @Override
    protected void setDirty(long i) {
        super.setDirty(i);
        this.distanceTracker.update(i, this.distanceTracker.getLevelFromSource(i), false);
    }

    @Override
    protected void onSectionLoad(long i) {
        this.distanceTracker.update(i, this.distanceTracker.getLevelFromSource(i), false);
    }

    public void checkConsistencyWithBlocks(ChunkCoordIntPair chunkcoordintpair, ChunkSection chunksection) {
        SectionPosition sectionposition = SectionPosition.of(chunkcoordintpair, SectionPosition.blockToSectionCoord(chunksection.bottomBlockY()));

        SystemUtils.ifElse(this.getOrLoad(sectionposition.asLong()), (villageplacesection) -> {
            villageplacesection.refresh((biconsumer) -> {
                if (mayHavePoi(chunksection)) {
                    this.updateFromSection(chunksection, sectionposition, biconsumer);
                }

            });
        }, () -> {
            if (mayHavePoi(chunksection)) {
                VillagePlaceSection villageplacesection = (VillagePlaceSection) this.getOrCreate(sectionposition.asLong());

                Objects.requireNonNull(villageplacesection);
                this.updateFromSection(chunksection, sectionposition, villageplacesection::add);
            }

        });
    }

    private static boolean mayHavePoi(ChunkSection chunksection) {
        Set set = VillagePlaceType.ALL_STATES;

        Objects.requireNonNull(set);
        return chunksection.maybeHas(set::contains);
    }

    private void updateFromSection(ChunkSection chunksection, SectionPosition sectionposition, BiConsumer<BlockPosition, VillagePlaceType> biconsumer) {
        sectionposition.blocksInside().forEach((blockposition) -> {
            IBlockData iblockdata = chunksection.getBlockState(SectionPosition.sectionRelative(blockposition.getX()), SectionPosition.sectionRelative(blockposition.getY()), SectionPosition.sectionRelative(blockposition.getZ()));

            VillagePlaceType.forState(iblockdata).ifPresent((villageplacetype) -> {
                biconsumer.accept(blockposition, villageplacetype);
            });
        });
    }

    public void ensureLoadedAndValid(IWorldReader iworldreader, BlockPosition blockposition, int i) {
        SectionPosition.aroundChunk(new ChunkCoordIntPair(blockposition), Math.floorDiv(i, 16), this.levelHeightAccessor.getMinSection(), this.levelHeightAccessor.getMaxSection()).map((sectionposition) -> {
            return Pair.of(sectionposition, this.getOrLoad(sectionposition.asLong()));
        }).filter((pair) -> {
            return !(Boolean) ((Optional) pair.getSecond()).map(VillagePlaceSection::isValid).orElse(false);
        }).map((pair) -> {
            return ((SectionPosition) pair.getFirst()).chunk();
        }).filter((chunkcoordintpair) -> {
            return this.loadedChunks.add(chunkcoordintpair.toLong());
        }).forEach((chunkcoordintpair) -> {
            iworldreader.getChunk(chunkcoordintpair.x, chunkcoordintpair.z, ChunkStatus.EMPTY);
        });
    }

    private final class a extends LightEngineGraphSection {

        private final Long2ByteMap levels = new Long2ByteOpenHashMap();

        protected a() {
            super(7, 16, 256);
            this.levels.defaultReturnValue((byte) 7);
        }

        @Override
        protected int getLevelFromSource(long i) {
            return VillagePlace.this.isVillageCenter(i) ? 0 : 7;
        }

        @Override
        protected int getLevel(long i) {
            return this.levels.get(i);
        }

        @Override
        protected void setLevel(long i, int j) {
            if (j > 6) {
                this.levels.remove(i);
            } else {
                this.levels.put(i, (byte) j);
            }

        }

        public void runAllUpdates() {
            super.runUpdates(Integer.MAX_VALUE);
        }
    }

    public static enum Occupancy {

        HAS_SPACE(VillagePlaceRecord::hasSpace), IS_OCCUPIED(VillagePlaceRecord::isOccupied), ANY((villageplacerecord) -> {
            return true;
        });

        private final Predicate<? super VillagePlaceRecord> test;

        private Occupancy(Predicate predicate) {
            this.test = predicate;
        }

        public Predicate<? super VillagePlaceRecord> getTest() {
            return this.test;
        }
    }
}
