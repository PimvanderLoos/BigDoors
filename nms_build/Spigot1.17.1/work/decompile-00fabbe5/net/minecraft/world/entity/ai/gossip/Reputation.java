package net.minecraft.world.entity.ai.gossip;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;
import net.minecraft.core.MinecraftSerializableUUID;
import net.minecraft.util.VisibleForDebug;

public class Reputation {

    public static final int DISCARD_THRESHOLD = 2;
    private final Map<UUID, Reputation.a> gossips = Maps.newHashMap();

    public Reputation() {}

    @VisibleForDebug
    public Map<UUID, Object2IntMap<ReputationType>> a() {
        Map<UUID, Object2IntMap<ReputationType>> map = Maps.newHashMap();

        this.gossips.keySet().forEach((uuid) -> {
            Reputation.a reputation_a = (Reputation.a) this.gossips.get(uuid);

            map.put(uuid, reputation_a.entries);
        });
        return map;
    }

    public void b() {
        Iterator iterator = this.gossips.values().iterator();

        while (iterator.hasNext()) {
            Reputation.a reputation_a = (Reputation.a) iterator.next();

            reputation_a.a();
            if (reputation_a.b()) {
                iterator.remove();
            }
        }

    }

    private Stream<Reputation.b> c() {
        return this.gossips.entrySet().stream().flatMap((entry) -> {
            return ((Reputation.a) entry.getValue()).a((UUID) entry.getKey());
        });
    }

    private Collection<Reputation.b> a(Random random, int i) {
        List<Reputation.b> list = (List) this.c().collect(Collectors.toList());

        if (list.isEmpty()) {
            return Collections.emptyList();
        } else {
            int[] aint = new int[list.size()];
            int j = 0;

            for (int k = 0; k < list.size(); ++k) {
                Reputation.b reputation_b = (Reputation.b) list.get(k);

                j += Math.abs(reputation_b.a());
                aint[k] = j - 1;
            }

            Set<Reputation.b> set = Sets.newIdentityHashSet();

            for (int l = 0; l < i; ++l) {
                int i1 = random.nextInt(j);
                int j1 = Arrays.binarySearch(aint, i1);

                set.add((Reputation.b) list.get(j1 < 0 ? -j1 - 1 : j1));
            }

            return set;
        }
    }

    private Reputation.a a(UUID uuid) {
        return (Reputation.a) this.gossips.computeIfAbsent(uuid, (uuid1) -> {
            return new Reputation.a();
        });
    }

    public void a(Reputation reputation, Random random, int i) {
        Collection<Reputation.b> collection = reputation.a(random, i);

        collection.forEach((reputation_b) -> {
            int j = reputation_b.value - reputation_b.type.decayPerTransfer;

            if (j >= 2) {
                this.a(reputation_b.target).entries.mergeInt(reputation_b.type, j, Reputation::a);
            }

        });
    }

    public int a(UUID uuid, Predicate<ReputationType> predicate) {
        Reputation.a reputation_a = (Reputation.a) this.gossips.get(uuid);

        return reputation_a != null ? reputation_a.a(predicate) : 0;
    }

    public long a(ReputationType reputationtype, DoublePredicate doublepredicate) {
        return this.gossips.values().stream().filter((reputation_a) -> {
            return doublepredicate.test((double) (reputation_a.entries.getOrDefault(reputationtype, 0) * reputationtype.weight));
        }).count();
    }

    public void a(UUID uuid, ReputationType reputationtype, int i) {
        Reputation.a reputation_a = this.a(uuid);

        reputation_a.entries.mergeInt(reputationtype, i, (integer, integer1) -> {
            return this.a(reputationtype, integer, integer1);
        });
        reputation_a.a(reputationtype);
        if (reputation_a.b()) {
            this.gossips.remove(uuid);
        }

    }

    public void b(UUID uuid, ReputationType reputationtype, int i) {
        this.a(uuid, reputationtype, -i);
    }

    public void a(UUID uuid, ReputationType reputationtype) {
        Reputation.a reputation_a = (Reputation.a) this.gossips.get(uuid);

        if (reputation_a != null) {
            reputation_a.b(reputationtype);
            if (reputation_a.b()) {
                this.gossips.remove(uuid);
            }
        }

    }

    public void a(ReputationType reputationtype) {
        Iterator iterator = this.gossips.values().iterator();

        while (iterator.hasNext()) {
            Reputation.a reputation_a = (Reputation.a) iterator.next();

            reputation_a.b(reputationtype);
            if (reputation_a.b()) {
                iterator.remove();
            }
        }

    }

    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        return new Dynamic(dynamicops, dynamicops.createList(this.c().map((reputation_b) -> {
            return reputation_b.a(dynamicops);
        }).map(Dynamic::getValue)));
    }

    public void a(Dynamic<?> dynamic) {
        dynamic.asStream().map(Reputation.b::a).flatMap((dataresult) -> {
            return SystemUtils.a(dataresult.result());
        }).forEach((reputation_b) -> {
            this.a(reputation_b.target).entries.put(reputation_b.type, reputation_b.value);
        });
    }

    private static int a(int i, int j) {
        return Math.max(i, j);
    }

    private int a(ReputationType reputationtype, int i, int j) {
        int k = i + j;

        return k > reputationtype.max ? Math.max(reputationtype.max, i) : k;
    }

    private static class a {

        final Object2IntMap<ReputationType> entries = new Object2IntOpenHashMap();

        a() {}

        public int a(Predicate<ReputationType> predicate) {
            return this.entries.object2IntEntrySet().stream().filter((entry) -> {
                return predicate.test((ReputationType) entry.getKey());
            }).mapToInt((entry) -> {
                return entry.getIntValue() * ((ReputationType) entry.getKey()).weight;
            }).sum();
        }

        public Stream<Reputation.b> a(UUID uuid) {
            return this.entries.object2IntEntrySet().stream().map((entry) -> {
                return new Reputation.b(uuid, (ReputationType) entry.getKey(), entry.getIntValue());
            });
        }

        public void a() {
            ObjectIterator objectiterator = this.entries.object2IntEntrySet().iterator();

            while (objectiterator.hasNext()) {
                Entry<ReputationType> entry = (Entry) objectiterator.next();
                int i = entry.getIntValue() - ((ReputationType) entry.getKey()).decayPerDay;

                if (i < 2) {
                    objectiterator.remove();
                } else {
                    entry.setValue(i);
                }
            }

        }

        public boolean b() {
            return this.entries.isEmpty();
        }

        public void a(ReputationType reputationtype) {
            int i = this.entries.getInt(reputationtype);

            if (i > reputationtype.max) {
                this.entries.put(reputationtype, reputationtype.max);
            }

            if (i < 2) {
                this.b(reputationtype);
            }

        }

        public void b(ReputationType reputationtype) {
            this.entries.removeInt(reputationtype);
        }
    }

    private static class b {

        public static final String TAG_TARGET = "Target";
        public static final String TAG_TYPE = "Type";
        public static final String TAG_VALUE = "Value";
        public final UUID target;
        public final ReputationType type;
        public final int value;

        public b(UUID uuid, ReputationType reputationtype, int i) {
            this.target = uuid;
            this.type = reputationtype;
            this.value = i;
        }

        public int a() {
            return this.value * this.type.weight;
        }

        public String toString() {
            return "GossipEntry{target=" + this.target + ", type=" + this.type + ", value=" + this.value + "}";
        }

        public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
            return new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("Target"), MinecraftSerializableUUID.CODEC.encodeStart(dynamicops, this.target).result().orElseThrow(RuntimeException::new), dynamicops.createString("Type"), dynamicops.createString(this.type.id), dynamicops.createString("Value"), dynamicops.createInt(this.value))));
        }

        public static DataResult<Reputation.b> a(Dynamic<?> dynamic) {
            return DataResult.unbox(DataResult.instance().group(dynamic.get("Target").read(MinecraftSerializableUUID.CODEC), dynamic.get("Type").asString().map(ReputationType::a), dynamic.get("Value").asNumber().map(Number::intValue)).apply(DataResult.instance(), Reputation.b::new));
        }
    }
}
