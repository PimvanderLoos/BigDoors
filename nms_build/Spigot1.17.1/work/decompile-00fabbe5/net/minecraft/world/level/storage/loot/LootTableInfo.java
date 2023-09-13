package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
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

    private final Random random;
    private final float luck;
    private final WorldServer level;
    private final Function<MinecraftKey, LootTable> lootTables;
    private final Set<LootTable> visitedTables = Sets.newLinkedHashSet();
    private final Function<MinecraftKey, LootItemCondition> conditions;
    private final Set<LootItemCondition> visitedConditions = Sets.newLinkedHashSet();
    private final Map<LootContextParameter<?>, Object> params;
    private final Map<MinecraftKey, LootTableInfo.b> dynamicDrops;

    LootTableInfo(Random random, float f, WorldServer worldserver, Function<MinecraftKey, LootTable> function, Function<MinecraftKey, LootItemCondition> function1, Map<LootContextParameter<?>, Object> map, Map<MinecraftKey, LootTableInfo.b> map1) {
        this.random = random;
        this.luck = f;
        this.level = worldserver;
        this.lootTables = function;
        this.conditions = function1;
        this.params = ImmutableMap.copyOf(map);
        this.dynamicDrops = ImmutableMap.copyOf(map1);
    }

    public boolean hasContextParameter(LootContextParameter<?> lootcontextparameter) {
        return this.params.containsKey(lootcontextparameter);
    }

    public <T> T b(LootContextParameter<T> lootcontextparameter) {
        T t0 = this.params.get(lootcontextparameter);

        if (t0 == null) {
            throw new NoSuchElementException(lootcontextparameter.a().toString());
        } else {
            return t0;
        }
    }

    public void a(MinecraftKey minecraftkey, Consumer<ItemStack> consumer) {
        LootTableInfo.b loottableinfo_b = (LootTableInfo.b) this.dynamicDrops.get(minecraftkey);

        if (loottableinfo_b != null) {
            loottableinfo_b.add(this, consumer);
        }

    }

    @Nullable
    public <T> T getContextParameter(LootContextParameter<T> lootcontextparameter) {
        return this.params.get(lootcontextparameter);
    }

    public boolean a(LootTable loottable) {
        return this.visitedTables.add(loottable);
    }

    public void b(LootTable loottable) {
        this.visitedTables.remove(loottable);
    }

    public boolean a(LootItemCondition lootitemcondition) {
        return this.visitedConditions.add(lootitemcondition);
    }

    public void b(LootItemCondition lootitemcondition) {
        this.visitedConditions.remove(lootitemcondition);
    }

    public LootTable a(MinecraftKey minecraftkey) {
        return (LootTable) this.lootTables.apply(minecraftkey);
    }

    public LootItemCondition b(MinecraftKey minecraftkey) {
        return (LootItemCondition) this.conditions.apply(minecraftkey);
    }

    public Random a() {
        return this.random;
    }

    public float getLuck() {
        return this.luck;
    }

    public WorldServer getWorld() {
        return this.level;
    }

    @FunctionalInterface
    public interface b {

        void add(LootTableInfo loottableinfo, Consumer<ItemStack> consumer);
    }

    public static enum EntityTarget {

        THIS("this", LootContextParameters.THIS_ENTITY), KILLER("killer", LootContextParameters.KILLER_ENTITY), DIRECT_KILLER("direct_killer", LootContextParameters.DIRECT_KILLER_ENTITY), KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER);

        final String name;
        private final LootContextParameter<? extends Entity> param;

        private EntityTarget(String s, LootContextParameter lootcontextparameter) {
            this.name = s;
            this.param = lootcontextparameter;
        }

        public LootContextParameter<? extends Entity> a() {
            return this.param;
        }

        public static LootTableInfo.EntityTarget a(String s) {
            LootTableInfo.EntityTarget[] aloottableinfo_entitytarget = values();
            int i = aloottableinfo_entitytarget.length;

            for (int j = 0; j < i; ++j) {
                LootTableInfo.EntityTarget loottableinfo_entitytarget = aloottableinfo_entitytarget[j];

                if (loottableinfo_entitytarget.name.equals(s)) {
                    return loottableinfo_entitytarget;
                }
            }

            throw new IllegalArgumentException("Invalid entity target " + s);
        }

        public static class a extends TypeAdapter<LootTableInfo.EntityTarget> {

            public a() {}

            public void write(JsonWriter jsonwriter, LootTableInfo.EntityTarget loottableinfo_entitytarget) throws IOException {
                jsonwriter.value(loottableinfo_entitytarget.name);
            }

            public LootTableInfo.EntityTarget read(JsonReader jsonreader) throws IOException {
                return LootTableInfo.EntityTarget.a(jsonreader.nextString());
            }
        }
    }

    public static class Builder {

        private final WorldServer level;
        private final Map<LootContextParameter<?>, Object> params = Maps.newIdentityHashMap();
        private final Map<MinecraftKey, LootTableInfo.b> dynamicDrops = Maps.newHashMap();
        private Random random;
        private float luck;

        public Builder(WorldServer worldserver) {
            this.level = worldserver;
        }

        public LootTableInfo.Builder a(Random random) {
            this.random = random;
            return this;
        }

        public LootTableInfo.Builder a(long i) {
            if (i != 0L) {
                this.random = new Random(i);
            }

            return this;
        }

        public LootTableInfo.Builder a(long i, Random random) {
            if (i == 0L) {
                this.random = random;
            } else {
                this.random = new Random(i);
            }

            return this;
        }

        public LootTableInfo.Builder a(float f) {
            this.luck = f;
            return this;
        }

        public <T> LootTableInfo.Builder set(LootContextParameter<T> lootcontextparameter, T t0) {
            this.params.put(lootcontextparameter, t0);
            return this;
        }

        public <T> LootTableInfo.Builder setOptional(LootContextParameter<T> lootcontextparameter, @Nullable T t0) {
            if (t0 == null) {
                this.params.remove(lootcontextparameter);
            } else {
                this.params.put(lootcontextparameter, t0);
            }

            return this;
        }

        public LootTableInfo.Builder a(MinecraftKey minecraftkey, LootTableInfo.b loottableinfo_b) {
            LootTableInfo.b loottableinfo_b1 = (LootTableInfo.b) this.dynamicDrops.put(minecraftkey, loottableinfo_b);

            if (loottableinfo_b1 != null) {
                throw new IllegalStateException("Duplicated dynamic drop '" + this.dynamicDrops + "'");
            } else {
                return this;
            }
        }

        public WorldServer a() {
            return this.level;
        }

        public <T> T a(LootContextParameter<T> lootcontextparameter) {
            T t0 = this.params.get(lootcontextparameter);

            if (t0 == null) {
                throw new IllegalArgumentException("No parameter " + lootcontextparameter);
            } else {
                return t0;
            }
        }

        @Nullable
        public <T> T b(LootContextParameter<T> lootcontextparameter) {
            return this.params.get(lootcontextparameter);
        }

        public LootTableInfo build(LootContextParameterSet lootcontextparameterset) {
            Set<LootContextParameter<?>> set = Sets.difference(this.params.keySet(), lootcontextparameterset.getOptional());

            if (!set.isEmpty()) {
                throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
            } else {
                Set<LootContextParameter<?>> set1 = Sets.difference(lootcontextparameterset.getRequired(), this.params.keySet());

                if (!set1.isEmpty()) {
                    throw new IllegalArgumentException("Missing required parameters: " + set1);
                } else {
                    Random random = this.random;

                    if (random == null) {
                        random = new Random();
                    }

                    MinecraftServer minecraftserver = this.level.getMinecraftServer();
                    float f = this.luck;
                    WorldServer worldserver = this.level;
                    LootTableRegistry loottableregistry = minecraftserver.getLootTableRegistry();

                    Objects.requireNonNull(loottableregistry);
                    Function function = loottableregistry::getLootTable;
                    LootPredicateManager lootpredicatemanager = minecraftserver.getLootPredicateManager();

                    Objects.requireNonNull(lootpredicatemanager);
                    return new LootTableInfo(random, f, worldserver, function, lootpredicatemanager::a, this.params, this.dynamicDrops);
                }
            }
        }
    }
}
