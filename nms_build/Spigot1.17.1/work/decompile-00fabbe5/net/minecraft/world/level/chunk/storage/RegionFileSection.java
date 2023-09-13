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
import java.io.File;
import java.io.IOException;
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

    public RegionFileSection(File file, Function<Runnable, Codec<R>> function, Function<Runnable, R> function1, DataFixer datafixer, DataFixTypes datafixtypes, boolean flag, LevelHeightAccessor levelheightaccessor) {
        this.codec = function;
        this.factory = function1;
        this.fixerUpper = datafixer;
        this.type = datafixtypes;
        this.levelHeightAccessor = levelheightaccessor;
        this.worker = new IOWorker(file, flag, file.getName());
    }

    protected void a(BooleanSupplier booleansupplier) {
        while (!this.dirty.isEmpty() && booleansupplier.getAsBoolean()) {
            ChunkCoordIntPair chunkcoordintpair = SectionPosition.a(this.dirty.firstLong()).r();

            this.d(chunkcoordintpair);
        }

    }

    @Nullable
    protected Optional<R> c(long i) {
        return (Optional) this.storage.get(i);
    }

    protected Optional<R> d(long i) {
        if (this.e(i)) {
            return Optional.empty();
        } else {
            Optional<R> optional = this.c(i);

            if (optional != null) {
                return optional;
            } else {
                this.b(SectionPosition.a(i).r());
                optional = this.c(i);
                if (optional == null) {
                    throw (IllegalStateException) SystemUtils.c((Throwable) (new IllegalStateException()));
                } else {
                    return optional;
                }
            }
        }
    }

    protected boolean e(long i) {
        int j = SectionPosition.c(SectionPosition.c(i));

        return this.levelHeightAccessor.d(j);
    }

    protected R f(long i) {
        if (this.e(i)) {
            throw (IllegalArgumentException) SystemUtils.c((Throwable) (new IllegalArgumentException("sectionPos out of bounds")));
        } else {
            Optional<R> optional = this.d(i);

            if (optional.isPresent()) {
                return optional.get();
            } else {
                R r0 = this.factory.apply(() -> {
                    this.a(i);
                });

                this.storage.put(i, Optional.of(r0));
                return r0;
            }
        }
    }

    private void b(ChunkCoordIntPair chunkcoordintpair) {
        this.a(chunkcoordintpair, DynamicOpsNBT.INSTANCE, this.c(chunkcoordintpair));
    }

    @Nullable
    private NBTTagCompound c(ChunkCoordIntPair chunkcoordintpair) {
        try {
            return this.worker.a(chunkcoordintpair);
        } catch (IOException ioexception) {
            RegionFileSection.LOGGER.error("Error reading chunk {} data from disk", chunkcoordintpair, ioexception);
            return null;
        }
    }

    private <T> void a(ChunkCoordIntPair chunkcoordintpair, DynamicOps<T> dynamicops, @Nullable T t0) {
        if (t0 == null) {
            for (int i = this.levelHeightAccessor.getMinSection(); i < this.levelHeightAccessor.getMaxSection(); ++i) {
                this.storage.put(a(chunkcoordintpair, i), Optional.empty());
            }
        } else {
            Dynamic<T> dynamic = new Dynamic(dynamicops, t0);
            int j = a(dynamic);
            int k = SharedConstants.getGameVersion().getWorldVersion();
            boolean flag = j != k;
            Dynamic<T> dynamic1 = this.fixerUpper.update(this.type.a(), dynamic, j, k);
            OptionalDynamic<T> optionaldynamic = dynamic1.get("Sections");

            for (int l = this.levelHeightAccessor.getMinSection(); l < this.levelHeightAccessor.getMaxSection(); ++l) {
                long i1 = a(chunkcoordintpair, l);
                Optional<R> optional = optionaldynamic.get(Integer.toString(l)).result().flatMap((dynamic2) -> {
                    DataResult dataresult = ((Codec) this.codec.apply(() -> {
                        this.a(i1);
                    })).parse(dynamic2);
                    Logger logger = RegionFileSection.LOGGER;

                    Objects.requireNonNull(logger);
                    return dataresult.resultOrPartial(logger::error);
                });

                this.storage.put(i1, optional);
                optional.ifPresent((object) -> {
                    this.b(i1);
                    if (flag) {
                        this.a(i1);
                    }

                });
            }
        }

    }

    private void d(ChunkCoordIntPair chunkcoordintpair) {
        Dynamic<NBTBase> dynamic = this.a(chunkcoordintpair, DynamicOpsNBT.INSTANCE);
        NBTBase nbtbase = (NBTBase) dynamic.getValue();

        if (nbtbase instanceof NBTTagCompound) {
            this.worker.a(chunkcoordintpair, (NBTTagCompound) nbtbase);
        } else {
            RegionFileSection.LOGGER.error("Expected compound tag, got {}", nbtbase);
        }

    }

    private <T> Dynamic<T> a(ChunkCoordIntPair chunkcoordintpair, DynamicOps<T> dynamicops) {
        Map<T, T> map = Maps.newHashMap();

        for (int i = this.levelHeightAccessor.getMinSection(); i < this.levelHeightAccessor.getMaxSection(); ++i) {
            long j = a(chunkcoordintpair, i);

            this.dirty.remove(j);
            Optional<R> optional = (Optional) this.storage.get(j);

            if (optional != null && optional.isPresent()) {
                DataResult<T> dataresult = ((Codec) this.codec.apply(() -> {
                    this.a(j);
                })).encodeStart(dynamicops, optional.get());
                String s = Integer.toString(i);
                Logger logger = RegionFileSection.LOGGER;

                Objects.requireNonNull(logger);
                dataresult.resultOrPartial(logger::error).ifPresent((object) -> {
                    map.put(dynamicops.createString(s), object);
                });
            }
        }

        return new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("Sections"), dynamicops.createMap(map), dynamicops.createString("DataVersion"), dynamicops.createInt(SharedConstants.getGameVersion().getWorldVersion()))));
    }

    private static long a(ChunkCoordIntPair chunkcoordintpair, int i) {
        return SectionPosition.b(chunkcoordintpair.x, i, chunkcoordintpair.z);
    }

    protected void b(long i) {}

    protected void a(long i) {
        Optional<R> optional = (Optional) this.storage.get(i);

        if (optional != null && optional.isPresent()) {
            this.dirty.add(i);
        } else {
            RegionFileSection.LOGGER.warn("No data for position: {}", SectionPosition.a(i));
        }
    }

    private static int a(Dynamic<?> dynamic) {
        return dynamic.get("DataVersion").asInt(1945);
    }

    public void a(ChunkCoordIntPair chunkcoordintpair) {
        if (!this.dirty.isEmpty()) {
            for (int i = this.levelHeightAccessor.getMinSection(); i < this.levelHeightAccessor.getMaxSection(); ++i) {
                long j = a(chunkcoordintpair, i);

                if (this.dirty.contains(j)) {
                    this.d(chunkcoordintpair);
                    return;
                }
            }
        }

    }

    public void close() throws IOException {
        this.worker.close();
    }
}
