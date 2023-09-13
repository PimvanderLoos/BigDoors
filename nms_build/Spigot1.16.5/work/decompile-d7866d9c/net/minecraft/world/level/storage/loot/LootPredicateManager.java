package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.server.packs.resources.ResourceDataJson;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootPredicateManager extends ResourceDataJson {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson b = LootSerialization.a().create();
    private Map<MinecraftKey, LootItemCondition> c = ImmutableMap.of();

    public LootPredicateManager() {
        super(LootPredicateManager.b, "predicates");
    }

    @Nullable
    public LootItemCondition a(MinecraftKey minecraftkey) {
        return (LootItemCondition) this.c.get(minecraftkey);
    }

    protected void a(Map<MinecraftKey, JsonElement> map, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller) {
        Builder<MinecraftKey, LootItemCondition> builder = ImmutableMap.builder();

        map.forEach((minecraftkey, jsonelement) -> {
            try {
                if (jsonelement.isJsonArray()) {
                    LootItemCondition[] alootitemcondition = (LootItemCondition[]) LootPredicateManager.b.fromJson(jsonelement, LootItemCondition[].class);

                    builder.put(minecraftkey, new LootPredicateManager.a(alootitemcondition));
                } else {
                    LootItemCondition lootitemcondition = (LootItemCondition) LootPredicateManager.b.fromJson(jsonelement, LootItemCondition.class);

                    builder.put(minecraftkey, lootitemcondition);
                }
            } catch (Exception exception) {
                LootPredicateManager.LOGGER.error("Couldn't parse loot table {}", minecraftkey, exception);
            }

        });
        Map<MinecraftKey, LootItemCondition> map1 = builder.build();
        LootCollector lootcollector = new LootCollector(LootContextParameterSets.GENERIC, map1::get, (minecraftkey) -> {
            return null;
        });

        map1.forEach((minecraftkey, lootitemcondition) -> {
            lootitemcondition.a(lootcollector.b("{" + minecraftkey + "}", minecraftkey));
        });
        lootcollector.a().forEach((s, s1) -> {
            LootPredicateManager.LOGGER.warn("Found validation problem in " + s + ": " + s1);
        });
        this.c = map1;
    }

    public Set<MinecraftKey> a() {
        return Collections.unmodifiableSet(this.c.keySet());
    }

    static class a implements LootItemCondition {

        private final LootItemCondition[] a;
        private final Predicate<LootTableInfo> b;

        private a(LootItemCondition[] alootitemcondition) {
            this.a = alootitemcondition;
            this.b = LootItemConditions.a((Predicate[]) alootitemcondition);
        }

        public final boolean test(LootTableInfo loottableinfo) {
            return this.b.test(loottableinfo);
        }

        @Override
        public void a(LootCollector lootcollector) {
            LootItemCondition.super.a(lootcollector);

            for (int i = 0; i < this.a.length; ++i) {
                this.a[i].a(lootcollector.b(".term[" + i + "]"));
            }

        }

        @Override
        public LootItemConditionType b() {
            throw new UnsupportedOperationException();
        }
    }
}
