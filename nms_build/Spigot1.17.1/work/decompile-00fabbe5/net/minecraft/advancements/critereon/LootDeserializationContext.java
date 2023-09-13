package net.minecraft.advancements.critereon;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.util.Objects;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootPredicateManager;
import net.minecraft.world.level.storage.loot.LootSerialization;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootDeserializationContext {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftKey id;
    private final LootPredicateManager predicateManager;
    private final Gson predicateGson = LootSerialization.a().create();

    public LootDeserializationContext(MinecraftKey minecraftkey, LootPredicateManager lootpredicatemanager) {
        this.id = minecraftkey;
        this.predicateManager = lootpredicatemanager;
    }

    public final LootItemCondition[] a(JsonArray jsonarray, String s, LootContextParameterSet lootcontextparameterset) {
        LootItemCondition[] alootitemcondition = (LootItemCondition[]) this.predicateGson.fromJson(jsonarray, LootItemCondition[].class);
        LootPredicateManager lootpredicatemanager = this.predicateManager;

        Objects.requireNonNull(this.predicateManager);
        LootCollector lootcollector = new LootCollector(lootcontextparameterset, lootpredicatemanager::a, (minecraftkey) -> {
            return null;
        });
        LootItemCondition[] alootitemcondition1 = alootitemcondition;
        int i = alootitemcondition.length;

        for (int j = 0; j < i; ++j) {
            LootItemCondition lootitemcondition = alootitemcondition1[j];

            lootitemcondition.a(lootcollector);
            lootcollector.a().forEach((s1, s2) -> {
                LootDeserializationContext.LOGGER.warn("Found validation problem in advancement trigger {}/{}: {}", s, s1, s2);
            });
        }

        return alootitemcondition;
    }

    public MinecraftKey a() {
        return this.id;
    }
}
