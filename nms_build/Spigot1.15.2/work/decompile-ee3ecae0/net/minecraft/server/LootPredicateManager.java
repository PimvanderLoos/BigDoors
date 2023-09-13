package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootPredicateManager extends ResourceDataJson {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson b = (new GsonBuilder()).registerTypeAdapter(LootValueBounds.class, new LootValueBounds.a()).registerTypeAdapter(LootValueBinomial.class, new LootValueBinomial.a()).registerTypeAdapter(LootValueConstant.class, new LootValueConstant.a()).registerTypeHierarchyAdapter(LootItemCondition.class, new LootItemConditions.a()).registerTypeHierarchyAdapter(LootTableInfo.EntityTarget.class, new LootTableInfo.EntityTarget.a()).create();
    private Map<MinecraftKey, LootItemCondition> c = ImmutableMap.of();

    public LootPredicateManager() {
        super(LootPredicateManager.b, "predicates");
    }

    @Nullable
    public LootItemCondition a(MinecraftKey minecraftkey) {
        return (LootItemCondition) this.c.get(minecraftkey);
    }

    protected void a(Map<MinecraftKey, JsonObject> map, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller) {
        Builder<MinecraftKey, LootItemCondition> builder = ImmutableMap.builder();

        map.forEach((minecraftkey, jsonobject) -> {
            try {
                LootItemCondition lootitemcondition = (LootItemCondition) LootPredicateManager.b.fromJson(jsonobject, LootItemCondition.class);

                builder.put(minecraftkey, lootitemcondition);
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
}
