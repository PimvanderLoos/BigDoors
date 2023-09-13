package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootTableInfo {

    private final Random a;
    private final float luck;
    private final WorldServer world;
    private final Function<MinecraftKey, LootTable> d;
    private final Set<LootTable> e;
    private final Function<MinecraftKey, LootItemCondition> f;
    private final Set<LootItemCondition> g;
    private final Map<LootContextParameter<?>, Object> h;
    private final Map<MinecraftKey, LootTableInfo.b> i;

    private LootTableInfo(Random random, float f, WorldServer worldserver, Function<MinecraftKey, LootTable> function, Function<MinecraftKey, LootItemCondition> function1, Map<LootContextParameter<?>, Object> map, Map<MinecraftKey, LootTableInfo.b> map1) {
        this.e = Sets.newLinkedHashSet();
        this.g = Sets.newLinkedHashSet();
        this.a = random;
        this.luck = f;
        this.world = worldserver;
        this.d = function;
        this.f = function1;
        this.h = ImmutableMap.copyOf(map);
        this.i = ImmutableMap.copyOf(map1);
    }

    public boolean hasContextParameter(LootContextParameter<?> lootcontextparameter) {
        return this.h.containsKey(lootcontextparameter);
    }

    public void a(MinecraftKey minecraftkey, Consumer<ItemStack> consumer) {
        LootTableInfo.b loottableinfo_b = (LootTableInfo.b) this.i.get(minecraftkey);

        if (loottableinfo_b != null) {
            loottableinfo_b.add(this, consumer);
        }

    }

    @Nullable
    public <T> T getContextParameter(LootContextParameter<T> lootcontextparameter) {
        return this.h.get(lootcontextparameter);
    }

    public boolean a(LootTable loottable) {
        return this.e.add(loottable);
    }

    public void b(LootTable loottable) {
        this.e.remove(loottable);
    }

    public boolean a(LootItemCondition lootitemcondition) {
        return this.g.add(lootitemcondition);
    }

    public void b(LootItemCondition lootitemcondition) {
        this.g.remove(lootitemcondition);
    }

    public LootTable a(MinecraftKey minecraftkey) {
        return (LootTable) this.d.apply(minecraftkey);
    }

    public LootItemCondition b(MinecraftKey minecraftkey) {
        return (LootItemCondition) this.f.apply(minecraftkey);
    }

    public Random a() {
        return this.a;
    }

    public float getLuck() {
        return this.luck;
    }

    public WorldServer getWorld() {
        return this.world;
    }

    public static enum EntityTarget {

        THIS("this", LootContextParameters.THIS_ENTITY), KILLER("killer", LootContextParameters.KILLER_ENTITY), DIRECT_KILLER("direct_killer", LootContextParameters.DIRECT_KILLER_ENTITY), KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER);

        private final String e;
        private final LootContextParameter<? extends Entity> f;

        private EntityTarget(String s, LootContextParameter lootcontextparameter) {
            this.e = s;
            this.f = lootcontextparameter;
        }

        public LootContextParameter<? extends Entity> a() {
            return this.f;
        }

        public static LootTableInfo.EntityTarget a(String s) {
            LootTableInfo.EntityTarget[] aloottableinfo_entitytarget = values();
            int i = aloottableinfo_entitytarget.length;

            for (int j = 0; j < i; ++j) {
                LootTableInfo.EntityTarget loottableinfo_entitytarget = aloottableinfo_entitytarget[j];

                if (loottableinfo_entitytarget.e.equals(s)) {
                    return loottableinfo_entitytarget;
                }
            }

            throw new IllegalArgumentException("Invalid entity target " + s);
        }

        public static class a extends TypeAdapter<LootTableInfo.EntityTarget> {

            public a() {}

            public void write(JsonWriter jsonwriter, LootTableInfo.EntityTarget loottableinfo_entitytarget) throws IOException {
                jsonwriter.value(loottableinfo_entitytarget.e);
            }

            public LootTableInfo.EntityTarget read(JsonReader jsonreader) throws IOException {
                return LootTableInfo.EntityTarget.a(jsonreader.nextString());
            }
        }
    }

    public static class Builder {

        private final WorldServer a;
        private final Map<LootContextParameter<?>, Object> b = Maps.newIdentityHashMap();
        private final Map<MinecraftKey, LootTableInfo.b> c = Maps.newHashMap();
        private Random d;
        private float e;

        public Builder(WorldServer worldserver) {
            this.a = worldserver;
        }

        public LootTableInfo.Builder a(Random random) {
            this.d = random;
            return this;
        }

        public LootTableInfo.Builder a(long i) {
            if (i != 0L) {
                this.d = new Random(i);
            }

            return this;
        }

        public LootTableInfo.Builder a(long i, Random random) {
            if (i == 0L) {
                this.d = random;
            } else {
                this.d = new Random(i);
            }

            return this;
        }

        public LootTableInfo.Builder a(float f) {
            this.e = f;
            return this;
        }

        public <T> LootTableInfo.Builder set(LootContextParameter<T> lootcontextparameter, T t0) {
            this.b.put(lootcontextparameter, t0);
            return this;
        }

        public <T> LootTableInfo.Builder setOptional(LootContextParameter<T> lootcontextparameter, @Nullable T t0) {
            if (t0 == null) {
                this.b.remove(lootcontextparameter);
            } else {
                this.b.put(lootcontextparameter, t0);
            }

            return this;
        }

        public LootTableInfo.Builder a(MinecraftKey minecraftkey, LootTableInfo.b loottableinfo_b) {
            LootTableInfo.b loottableinfo_b1 = (LootTableInfo.b) this.c.put(minecraftkey, loottableinfo_b);

            if (loottableinfo_b1 != null) {
                throw new IllegalStateException("Duplicated dynamic drop '" + this.c + "'");
            } else {
                return this;
            }
        }

        public WorldServer a() {
            return this.a;
        }

        public <T> T a(LootContextParameter<T> lootcontextparameter) {
            T t0 = this.b.get(lootcontextparameter);

            if (t0 == null) {
                throw new IllegalArgumentException("No parameter " + lootcontextparameter);
            } else {
                return t0;
            }
        }

        @Nullable
        public <T> T b(LootContextParameter<T> lootcontextparameter) {
            return this.b.get(lootcontextparameter);
        }

        public LootTableInfo build(LootContextParameterSet lootcontextparameterset) {
            Set<LootContextParameter<?>> set = Sets.difference(this.b.keySet(), lootcontextparameterset.getOptional());

            if (!set.isEmpty()) {
                throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
            } else {
                Set<LootContextParameter<?>> set1 = Sets.difference(lootcontextparameterset.getRequired(), this.b.keySet());

                if (!set1.isEmpty()) {
                    throw new IllegalArgumentException("Missing required parameters: " + set1);
                } else {
                    Random random = this.d;

                    if (random == null) {
                        random = new Random();
                    }

                    MinecraftServer minecraftserver = this.a.getMinecraftServer();

                    return new LootTableInfo(random, this.e, this.a, minecraftserver.getLootTableRegistry()::getLootTable, minecraftserver.getLootPredicateManager()::a, this.b, this.c);
                }
            }
        }
    }

    @FunctionalInterface
    public interface b {

        void add(LootTableInfo loottableinfo, Consumer<ItemStack> consumer);
    }
}
