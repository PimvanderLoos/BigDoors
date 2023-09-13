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
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class VillagePlaceSection {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Short2ObjectMap<VillagePlaceRecord> b;
    private final Map<VillagePlaceType, Set<VillagePlaceRecord>> c;
    private final Runnable d;
    private boolean e;

    public static Codec<VillagePlaceSection> a(Runnable runnable) {
        Codec codec = RecordCodecBuilder.create((instance) -> {
            return instance.group(RecordCodecBuilder.point(runnable), Codec.BOOL.optionalFieldOf("Valid", false).forGetter((villageplacesection) -> {
                return villageplacesection.e;
            }), VillagePlaceRecord.a(runnable).listOf().fieldOf("Records").forGetter((villageplacesection) -> {
                return ImmutableList.copyOf(villageplacesection.b.values());
            })).apply(instance, VillagePlaceSection::new);
        });
        Logger logger = VillagePlaceSection.LOGGER;

        logger.getClass();
        return codec.orElseGet(SystemUtils.a("Failed to read POI section: ", logger::error), () -> {
            return new VillagePlaceSection(runnable, false, ImmutableList.of());
        });
    }

    public VillagePlaceSection(Runnable runnable) {
        this(runnable, true, ImmutableList.of());
    }

    private VillagePlaceSection(Runnable runnable, boolean flag, List<VillagePlaceRecord> list) {
        this.b = new Short2ObjectOpenHashMap();
        this.c = Maps.newHashMap();
        this.d = runnable;
        this.e = flag;
        list.forEach(this::a);
    }

    public Stream<VillagePlaceRecord> a(Predicate<VillagePlaceType> predicate, VillagePlace.Occupancy villageplace_occupancy) {
        return this.c.entrySet().stream().filter((entry) -> {
            return predicate.test(entry.getKey());
        }).flatMap((entry) -> {
            return ((Set) entry.getValue()).stream();
        }).filter(villageplace_occupancy.a());
    }

    public void a(BlockPosition blockposition, VillagePlaceType villageplacetype) {
        if (this.a(new VillagePlaceRecord(blockposition, villageplacetype, this.d))) {
            VillagePlaceSection.LOGGER.debug("Added POI of type {} @ {}", new Supplier[]{() -> {
                        return villageplacetype;
                    }, () -> {
                        return blockposition;
                    }});
            this.d.run();
        }

    }

    private boolean a(VillagePlaceRecord villageplacerecord) {
        BlockPosition blockposition = villageplacerecord.f();
        VillagePlaceType villageplacetype = villageplacerecord.g();
        short short0 = SectionPosition.b(blockposition);
        VillagePlaceRecord villageplacerecord1 = (VillagePlaceRecord) this.b.get(short0);

        if (villageplacerecord1 != null) {
            if (villageplacetype.equals(villageplacerecord1.g())) {
                return false;
            }

            String s = "POI data mismatch: already registered at " + blockposition;

            if (SharedConstants.d) {
                throw (IllegalStateException) SystemUtils.c((Throwable) (new IllegalStateException(s)));
            }

            VillagePlaceSection.LOGGER.error(s);
        }

        this.b.put(short0, villageplacerecord);
        ((Set) this.c.computeIfAbsent(villageplacetype, (villageplacetype1) -> {
            return Sets.newHashSet();
        })).add(villageplacerecord);
        return true;
    }

    public void a(BlockPosition blockposition) {
        VillagePlaceRecord villageplacerecord = (VillagePlaceRecord) this.b.remove(SectionPosition.b(blockposition));

        if (villageplacerecord == null) {
            VillagePlaceSection.LOGGER.error("POI data mismatch: never registered at " + blockposition);
        } else {
            ((Set) this.c.get(villageplacerecord.g())).remove(villageplacerecord);
            VillagePlaceSection.LOGGER.debug("Removed POI of type {} @ {}", new Supplier[]{villageplacerecord::g, villageplacerecord::f});
            this.d.run();
        }
    }

    public boolean c(BlockPosition blockposition) {
        VillagePlaceRecord villageplacerecord = (VillagePlaceRecord) this.b.get(SectionPosition.b(blockposition));

        if (villageplacerecord == null) {
            throw (IllegalStateException) SystemUtils.c((Throwable) (new IllegalStateException("POI never registered at " + blockposition)));
        } else {
            boolean flag = villageplacerecord.c();

            this.d.run();
            return flag;
        }
    }

    public boolean a(BlockPosition blockposition, Predicate<VillagePlaceType> predicate) {
        short short0 = SectionPosition.b(blockposition);
        VillagePlaceRecord villageplacerecord = (VillagePlaceRecord) this.b.get(short0);

        return villageplacerecord != null && predicate.test(villageplacerecord.g());
    }

    public Optional<VillagePlaceType> d(BlockPosition blockposition) {
        short short0 = SectionPosition.b(blockposition);
        VillagePlaceRecord villageplacerecord = (VillagePlaceRecord) this.b.get(short0);

        return villageplacerecord != null ? Optional.of(villageplacerecord.g()) : Optional.empty();
    }

    public void a(Consumer<BiConsumer<BlockPosition, VillagePlaceType>> consumer) {
        if (!this.e) {
            Short2ObjectMap<VillagePlaceRecord> short2objectmap = new Short2ObjectOpenHashMap(this.b);

            this.b();
            consumer.accept((blockposition, villageplacetype) -> {
                short short0 = SectionPosition.b(blockposition);
                VillagePlaceRecord villageplacerecord = (VillagePlaceRecord) short2objectmap.computeIfAbsent(short0, (i) -> {
                    return new VillagePlaceRecord(blockposition, villageplacetype, this.d);
                });

                this.a(villageplacerecord);
            });
            this.e = true;
            this.d.run();
        }

    }

    private void b() {
        this.b.clear();
        this.c.clear();
    }

    boolean a() {
        return this.e;
    }
}
