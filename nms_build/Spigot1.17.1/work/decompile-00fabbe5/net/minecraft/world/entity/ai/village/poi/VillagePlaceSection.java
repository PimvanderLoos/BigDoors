package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.VisibleForDebug;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class VillagePlaceSection {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Short2ObjectMap<VillagePlaceRecord> records;
    private final Map<VillagePlaceType, Set<VillagePlaceRecord>> byType;
    private final Runnable setDirty;
    private boolean isValid;

    public static Codec<VillagePlaceSection> a(Runnable runnable) {
        Codec codec = RecordCodecBuilder.create((instance) -> {
            return instance.group(RecordCodecBuilder.point(runnable), Codec.BOOL.optionalFieldOf("Valid", false).forGetter((villageplacesection) -> {
                return villageplacesection.isValid;
            }), VillagePlaceRecord.a(runnable).listOf().fieldOf("Records").forGetter((villageplacesection) -> {
                return ImmutableList.copyOf(villageplacesection.records.values());
            })).apply(instance, VillagePlaceSection::new);
        });
        Logger logger = VillagePlaceSection.LOGGER;

        Objects.requireNonNull(logger);
        return codec.orElseGet(SystemUtils.a("Failed to read POI section: ", logger::error), () -> {
            return new VillagePlaceSection(runnable, false, ImmutableList.of());
        });
    }

    public VillagePlaceSection(Runnable runnable) {
        this(runnable, true, ImmutableList.of());
    }

    private VillagePlaceSection(Runnable runnable, boolean flag, List<VillagePlaceRecord> list) {
        this.records = new Short2ObjectOpenHashMap();
        this.byType = Maps.newHashMap();
        this.setDirty = runnable;
        this.isValid = flag;
        list.forEach(this::a);
    }

    public Stream<VillagePlaceRecord> a(Predicate<VillagePlaceType> predicate, VillagePlace.Occupancy villageplace_occupancy) {
        return this.byType.entrySet().stream().filter((entry) -> {
            return predicate.test((VillagePlaceType) entry.getKey());
        }).flatMap((entry) -> {
            return ((Set) entry.getValue()).stream();
        }).filter(villageplace_occupancy.a());
    }

    public void a(BlockPosition blockposition, VillagePlaceType villageplacetype) {
        if (this.a(new VillagePlaceRecord(blockposition, villageplacetype, this.setDirty))) {
            VillagePlaceSection.LOGGER.debug("Added POI of type {} @ {}", new Supplier[]{() -> {
                        return villageplacetype;
                    }, () -> {
                        return blockposition;
                    }});
            this.setDirty.run();
        }

    }

    private boolean a(VillagePlaceRecord villageplacerecord) {
        BlockPosition blockposition = villageplacerecord.f();
        VillagePlaceType villageplacetype = villageplacerecord.g();
        short short0 = SectionPosition.b(blockposition);
        VillagePlaceRecord villageplacerecord1 = (VillagePlaceRecord) this.records.get(short0);

        if (villageplacerecord1 != null) {
            if (villageplacetype.equals(villageplacerecord1.g())) {
                return false;
            }

            SystemUtils.a("POI data mismatch: already registered at " + blockposition);
        }

        this.records.put(short0, villageplacerecord);
        ((Set) this.byType.computeIfAbsent(villageplacetype, (villageplacetype1) -> {
            return Sets.newHashSet();
        })).add(villageplacerecord);
        return true;
    }

    public void a(BlockPosition blockposition) {
        VillagePlaceRecord villageplacerecord = (VillagePlaceRecord) this.records.remove(SectionPosition.b(blockposition));

        if (villageplacerecord == null) {
            VillagePlaceSection.LOGGER.error("POI data mismatch: never registered at {}", blockposition);
        } else {
            ((Set) this.byType.get(villageplacerecord.g())).remove(villageplacerecord);
            Logger logger = VillagePlaceSection.LOGGER;
            Supplier[] asupplier = new Supplier[2];

            Objects.requireNonNull(villageplacerecord);
            asupplier[0] = villageplacerecord::g;
            Objects.requireNonNull(villageplacerecord);
            asupplier[1] = villageplacerecord::f;
            logger.debug("Removed POI of type {} @ {}", asupplier);
            this.setDirty.run();
        }
    }

    @Deprecated
    @VisibleForDebug
    public int b(BlockPosition blockposition) {
        return (Integer) this.e(blockposition).map(VillagePlaceRecord::a).orElse(0);
    }

    public boolean c(BlockPosition blockposition) {
        VillagePlaceRecord villageplacerecord = (VillagePlaceRecord) this.records.get(SectionPosition.b(blockposition));

        if (villageplacerecord == null) {
            throw (IllegalStateException) SystemUtils.c((Throwable) (new IllegalStateException("POI never registered at " + blockposition)));
        } else {
            boolean flag = villageplacerecord.c();

            this.setDirty.run();
            return flag;
        }
    }

    public boolean a(BlockPosition blockposition, Predicate<VillagePlaceType> predicate) {
        return this.d(blockposition).filter(predicate).isPresent();
    }

    public Optional<VillagePlaceType> d(BlockPosition blockposition) {
        return this.e(blockposition).map(VillagePlaceRecord::g);
    }

    private Optional<VillagePlaceRecord> e(BlockPosition blockposition) {
        return Optional.ofNullable((VillagePlaceRecord) this.records.get(SectionPosition.b(blockposition)));
    }

    public void a(Consumer<BiConsumer<BlockPosition, VillagePlaceType>> consumer) {
        if (!this.isValid) {
            Short2ObjectMap<VillagePlaceRecord> short2objectmap = new Short2ObjectOpenHashMap(this.records);

            this.b();
            consumer.accept((blockposition, villageplacetype) -> {
                short short0 = SectionPosition.b(blockposition);
                VillagePlaceRecord villageplacerecord = (VillagePlaceRecord) short2objectmap.computeIfAbsent(short0, (i) -> {
                    return new VillagePlaceRecord(blockposition, villageplacetype, this.setDirty);
                });

                this.a(villageplacerecord);
            });
            this.isValid = true;
            this.setDirty.run();
        }

    }

    private void b() {
        this.records.clear();
        this.byType.clear();
    }

    boolean a() {
        return this.isValid;
    }
}
