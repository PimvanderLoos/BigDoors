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
    public Map<UUID, Object2IntMap<ReputationType>> getGossipEntries() {
        Map<UUID, Object2IntMap<ReputationType>> map = Maps.newHashMap();

        this.gossips.keySet().forEach((uuid) -> {
            Reputation.a reputation_a = (Reputation.a) this.gossips.get(uuid);

            map.put(uuid, reputation_a.entries);
        });
        return map;
    }

    public void decay() {
        Iterator iterator = this.gossips.values().iterator();

        while (iterator.hasNext()) {
            Reputation.a reputation_a = (Reputation.a) iterator.next();

            reputation_a.decay();
            if (reputation_a.isEmpty()) {
                iterator.remove();
            }
        }

    }

    private Stream<Reputation.b> unpack() {
        return this.gossips.entrySet().stream().flatMap((entry) -> {
            return ((Reputation.a) entry.getValue()).unpack((UUID) entry.getKey());
        });
    }

    private Collection<Reputation.b> selectGossipsForTransfer(Random random, int i) {
        List<Reputation.b> list = (List) this.unpack().collect(Collectors.toList());

        if (list.isEmpty()) {
            return Collections.emptyList();
        } else {
            int[] aint = new int[list.size()];
            int j = 0;

            for (int k = 0; k < list.size(); ++k) {
                Reputation.b reputation_b = (Reputation.b) list.get(k);

                j += Math.abs(reputation_b.weightedValue());
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

    private Reputation.a getOrCreate(UUID uuid) {
        return (Reputation.a) this.gossips.computeIfAbsent(uuid, (uuid1) -> {
            return new Reputation.a();
        });
    }

    public void transferFrom(Reputation reputation, Random random, int i) {
        Collection<Reputation.b> collection = reputation.selectGossipsForTransfer(random, i);

        collection.forEach((reputation_b) -> {
            int j = reputation_b.value - reputation_b.type.decayPerTransfer;

            if (j >= 2) {
                this.getOrCreate(reputation_b.target).entries.mergeInt(reputation_b.type, j, Reputation::mergeValuesForTransfer);
            }

        });
    }

    public int getReputation(UUID uuid, Predicate<ReputationType> predicate) {
        Reputation.a reputation_a = (Reputation.a) this.gossips.get(uuid);

        return reputation_a != null ? reputation_a.weightedValue(predicate) : 0;
    }

    public long getCountForType(ReputationType reputationtype, DoublePredicate doublepredicate) {
        return this.gossips.values().stream().filter((reputation_a) -> {
            return doublepredicate.test((double) (reputation_a.entries.getOrDefault(reputationtype, 0) * reputationtype.weight));
        }).count();
    }

    public void add(UUID uuid, ReputationType reputationtype, int i) {
        Reputation.a reputation_a = this.getOrCreate(uuid);

        reputation_a.entries.mergeInt(reputationtype, i, (j, k) -> {
            return this.mergeValuesForAddition(reputationtype, j, k);
        });
        reputation_a.makeSureValueIsntTooLowOrTooHigh(reputationtype);
        if (reputation_a.isEmpty()) {
            this.gossips.remove(uuid);
        }

    }

    public void remove(UUID uuid, ReputationType reputationtype, int i) {
        this.add(uuid, reputationtype, -i);
    }

    public void remove(UUID uuid, ReputationType reputationtype) {
        Reputation.a reputation_a = (Reputation.a) this.gossips.get(uuid);

        if (reputation_a != null) {
            reputation_a.remove(reputationtype);
            if (reputation_a.isEmpty()) {
                this.gossips.remove(uuid);
            }
        }

    }

    public void remove(ReputationType reputationtype) {
        Iterator iterator = this.gossips.values().iterator();

        while (iterator.hasNext()) {
            Reputation.a reputation_a = (Reputation.a) iterator.next();

            reputation_a.remove(reputationtype);
            if (reputation_a.isEmpty()) {
                iterator.remove();
            }
        }

    }

    public <T> Dynamic<T> store(DynamicOps<T> dynamicops) {
        return new Dynamic(dynamicops, dynamicops.createList(this.unpack().map((reputation_b) -> {
            return reputation_b.store(dynamicops);
        }).map(Dynamic::getValue)));
    }

    public void update(Dynamic<?> dynamic) {
        dynamic.asStream().map(Reputation.b::load).flatMap((dataresult) -> {
            return SystemUtils.toStream(dataresult.result());
        }).forEach((reputation_b) -> {
            this.getOrCreate(reputation_b.target).entries.put(reputation_b.type, reputation_b.value);
        });
    }

    private static int mergeValuesForTransfer(int i, int j) {
        return Math.max(i, j);
    }

    private int mergeValuesForAddition(ReputationType reputationtype, int i, int j) {
        int k = i + j;

        return k > reputationtype.max ? Math.max(reputationtype.max, i) : k;
    }

    private static class a {

        final Object2IntMap<ReputationType> entries = new Object2IntOpenHashMap();

        a() {}

        public int weightedValue(Predicate<ReputationType> predicate) {
            return this.entries.object2IntEntrySet().stream().filter((entry) -> {
                return predicate.test((ReputationType) entry.getKey());
            }).mapToInt((entry) -> {
                return entry.getIntValue() * ((ReputationType) entry.getKey()).weight;
            }).sum();
        }

        public Stream<Reputation.b> unpack(UUID uuid) {
            return this.entries.object2IntEntrySet().stream().map((entry) -> {
                return new Reputation.b(uuid, (ReputationType) entry.getKey(), entry.getIntValue());
            });
        }

        public void decay() {
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

        public boolean isEmpty() {
            return this.entries.isEmpty();
        }

        public void makeSureValueIsntTooLowOrTooHigh(ReputationType reputationtype) {
            int i = this.entries.getInt(reputationtype);

            if (i > reputationtype.max) {
                this.entries.put(reputationtype, reputationtype.max);
            }

            if (i < 2) {
                this.remove(reputationtype);
            }

        }

        public void remove(ReputationType reputationtype) {
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

        public int weightedValue() {
            return this.value * this.type.weight;
        }

        public String toString() {
            return "GossipEntry{target=" + this.target + ", type=" + this.type + ", value=" + this.value + "}";
        }

        public <T> Dynamic<T> store(DynamicOps<T> dynamicops) {
            return new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("Target"), MinecraftSerializableUUID.CODEC.encodeStart(dynamicops, this.target).result().orElseThrow(RuntimeException::new), dynamicops.createString("Type"), dynamicops.createString(this.type.id), dynamicops.createString("Value"), dynamicops.createInt(this.value))));
        }

        public static DataResult<Reputation.b> load(Dynamic<?> dynamic) {
            return DataResult.unbox(DataResult.instance().group(dynamic.get("Target").read(MinecraftSerializableUUID.CODEC), dynamic.get("Type").asString().map(ReputationType::byId), dynamic.get("Value").asNumber().map(Number::intValue)).apply(DataResult.instance(), Reputation.b::new));
        }
    }
}
