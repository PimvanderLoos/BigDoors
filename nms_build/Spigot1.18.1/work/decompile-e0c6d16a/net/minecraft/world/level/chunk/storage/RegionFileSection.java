package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionFileSection<R> implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SECTIONS_TAG = "Sections";
    private final IOWorker worker;
    private final Long2ObjectMap<Optional<R>> storage = new Long2ObjectOpenHashMap();
    private final LongLinkedOpenHashSet dirty = new LongLinkedOpenHashSet();
    private final Function<Runnable, Codec<R>> codec;
    private final Function<Runnable, R> factory;
    private final DataFixer fixerUpper;
    private final DataFixTypes type;
    protected final LevelHeightAccessor levelHeightAccessor;

    public RegionFileSection(Path path, Function<Runnable, Codec<R>> function, Function<Runnable, R> function1, DataFixer datafixer, DataFixTypes datafixtypes, boolean flag, LevelHeightAccessor levelheightaccessor) {
        this.codec = function;
        this.factory = function1;
        this.fixerUpper = datafixer;
        this.type = datafixtypes;
        this.levelHeightAccessor = levelheightaccessor;
        this.worker = new IOWorker(path, flag, path.getFileName().toString());
    }

    protected void tick(BooleanSupplier booleansupplier) {
        while (!this.dirty.isEmpty() && booleansupplier.getAsBoolean()) {
            ChunkCoordIntPair chunkcoordintpair = SectionPosition.of(this.dirty.firstLong()).chunk();

            this.writeColumn(chunkcoordintpair);
        }

    }

    @Nullable
    protected Optional<R> get(long i) {
        return (Optional) this.storage.get(i);
    }

    protected Optional<R> getOrLoad(long i) {
        if (this.outsideStoredRange(i)) {
            return Optional.empty();
        } else {
            Optional<R> optional = this.get(i);

            if (optional != null) {
                return optional;
            } else {
                this.readColumn(SectionPosition.of(i).chunk());
                optional = this.get(i);
                if (optional == null) {
                    throw (IllegalStateException) SystemUtils.pauseInIde(new IllegalStateException());
                } else {
                    return optional;
                }
            }
        }
    }

    protected boolean outsideStoredRange(long i) {
        int j = SectionPosition.sectionToBlockCoord(SectionPosition.y(i));

        return this.levelHeightAccessor.isOutsideBuildHeight(j);
    }

    protected R getOrCreate(long i) {
        if (this.outsideStoredRange(i)) {
            throw (IllegalArgumentException) SystemUtils.pauseInIde(new IllegalArgumentException("sectionPos out of bounds"));
        } else {
            Optional<R> optional = this.getOrLoad(i);

            if (optional.isPresent()) {
                return optional.get();
            } else {
                R r0 = this.factory.apply(() -> {
                    this.setDirty(i);
                });

                this.storage.put(i, Optional.of(r0));
                return r0;
            }
        }
    }

    private void readColumn(ChunkCoordIntPair chunkcoordintpair) {
        this.readColumn(chunkcoordintpair, DynamicOpsNBT.INSTANCE, this.tryRead(chunkcoordintpair));
    }

    @Nullable
    private NBTTagCompound tryRead(ChunkCoordIntPair chunkcoordintpair) {
        try {
            return this.worker.load(chunkcoordintpair);
        } catch (IOException ioexception) {
            RegionFileSection.LOGGER.error("Error reading chunk {} data from disk", chunkcoordintpair, ioexception);
            return null;
        }
    }

    private <T> void readColumn(ChunkCoordIntPair chunkcoordintpair, DynamicOps<T> dynamicops, @Nullable T t0) {
        if (t0 == null) {
            for (int i = this.levelHeightAccessor.getMinSection(); i < this.levelHeightAccessor.getMaxSection(); ++i) {
                this.storage.put(getKey(chunkcoordintpair, i), Optional.empty());
            }
        } else {
            Dynamic<T> dynamic = new Dynamic(dynamicops, t0);
            int j = getVersion(dynamic);
            int k = SharedConstants.getCurrentVersion().getWorldVersion();
            boolean flag = j != k;
            Dynamic<T> dynamic1 = this.fixerUpper.update(this.type.getType(), dynamic, j, k);
            OptionalDynamic<T> optionaldynamic = dynamic1.get("Sections");

            for (int l = this.levelHeightAccessor.getMinSection(); l < this.levelHeightAccessor.getMaxSection(); ++l) {
                long i1 = getKey(chunkcoordintpair, l);
                Optional<R> optional = optionaldynamic.get(Integer.toString(l)).result().flatMap((dynamic2) -> {
                    DataResult dataresult = ((Codec) this.codec.apply(() -> {
                        this.setDirty(i1);
                    })).parse(dynamic2);
                    Logger logger = RegionFileSection.LOGGER;

                    Objects.requireNonNull(logger);
                    return dataresult.resultOrPartial(logger::error);
                });

                this.storage.put(i1, optional);
                optional.ifPresent((object) -> {
                    this.onSectionLoad(i1);
                    if (flag) {
                        this.setDirty(i1);
                    }

                });
            }
        }

    }

    private void writeColumn(ChunkCoordIntPair chunkcoordintpair) {
        Dynamic<NBTBase> dynamic = this.writeColumn(chunkcoordintpair, DynamicOpsNBT.INSTANCE);
        NBTBase nbtbase = (NBTBase) dynamic.getValue();

        if (nbtbase instanceof NBTTagCompound) {
            this.worker.store(chunkcoordintpair, (NBTTagCompound) nbtbase);
        } else {
            RegionFileSection.LOGGER.error("Expected compound tag, got {}", nbtbase);
        }

    }

    private <T> Dynamic<T> writeColumn(ChunkCoordIntPair chunkcoordintpair, DynamicOps<T> dynamicops) {
        Map<T, T> map = Maps.newHashMap();

        for (int i = this.levelHeightAccessor.getMinSection(); i < this.levelHeightAccessor.getMaxSection(); ++i) {
            long j = getKey(chunkcoordintpair, i);

            this.dirty.remove(j);
            Optional<R> optional = (Optional) this.storage.get(j);

            if (optional != null && optional.isPresent()) {
                DataResult<T> dataresult = ((Codec) this.codec.apply(() -> {
                    this.setDirty(j);
                })).encodeStart(dynamicops, optional.get());
                String s = Integer.toString(i);
                Logger logger = RegionFileSection.LOGGER;

                Objects.requireNonNull(logger);
                dataresult.resultOrPartial(logger::error).ifPresent((object) -> {
                    map.put(dynamicops.createString(s), object);
                });
            }
        }

        return new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("Sections"), dynamicops.createMap(map), dynamicops.createString("DataVersion"), dynamicops.createInt(SharedConstants.getCurrentVersion().getWorldVersion()))));
    }

    private static long getKey(ChunkCoordIntPair chunkcoordintpair, int i) {
        return SectionPosition.asLong(chunkcoordintpair.x, i, chunkcoordintpair.z);
    }

    protected void onSectionLoad(long i) {}

    protected void setDirty(long i) {
        Optional<R> optional = (Optional) this.storage.get(i);

        if (optional != null && optional.isPresent()) {
            this.dirty.add(i);
        } else {
            RegionFileSection.LOGGER.warn("No data for position: {}", SectionPosition.of(i));
        }
    }

    private static int getVersion(Dynamic<?> dynamic) {
        return dynamic.get("DataVersion").asInt(1945);
    }

    public void flush(ChunkCoordIntPair chunkcoordintpair) {
        if (!this.dirty.isEmpty()) {
            for (int i = this.levelHeightAccessor.getMinSection(); i < this.levelHeightAccessor.getMaxSection(); ++i) {
                long j = getKey(chunkcoordintpair, i);

                if (this.dirty.contains(j)) {
                    this.writeColumn(chunkcoordintpair);
                    return;
                }
            }
        }

    }

    public void close() throws IOException {
        this.worker.close();
    }
}
