package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.server.packs.resources.ResourceDataJson;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableRegistry extends ResourceDataJson {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = LootSerialization.createLootTableSerializer().create();
    private Map<MinecraftKey, LootTable> tables = ImmutableMap.of();
    private final LootPredicateManager predicateManager;

    public LootTableRegistry(LootPredicateManager lootpredicatemanager) {
        super(LootTableRegistry.GSON, "loot_tables");
        this.predicateManager = lootpredicatemanager;
    }

    public LootTable get(MinecraftKey minecraftkey) {
        return (LootTable) this.tables.getOrDefault(minecraftkey, LootTable.EMPTY);
    }

    protected void apply(Map<MinecraftKey, JsonElement> map, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller) {
        Builder<MinecraftKey, LootTable> builder = ImmutableMap.builder();
        JsonElement jsonelement = (JsonElement) map.remove(LootTables.EMPTY);

        if (jsonelement != null) {
            LootTableRegistry.LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", LootTables.EMPTY);
        }

        map.forEach((minecraftkey, jsonelement1) -> {
            try {
                LootTable loottable = (LootTable) LootTableRegistry.GSON.fromJson(jsonelement1, LootTable.class);

                builder.put(minecraftkey, loottable);
            } catch (Exception exception) {
                LootTableRegistry.LOGGER.error("Couldn't parse loot table {}", minecraftkey, exception);
            }

        });
        builder.put(LootTables.EMPTY, LootTable.EMPTY);
        ImmutableMap<MinecraftKey, LootTable> immutablemap = builder.build();
        LootContextParameterSet lootcontextparameterset = LootContextParameterSets.ALL_PARAMS;
        LootPredicateManager lootpredicatemanager = this.predicateManager;

        Objects.requireNonNull(this.predicateManager);
        Function function = lootpredicatemanager::get;

        Objects.requireNonNull(immutablemap);
        LootCollector lootcollector = new LootCollector(lootcontextparameterset, function, immutablemap::get);

        immutablemap.forEach((minecraftkey, loottable) -> {
            validate(lootcollector, minecraftkey, loottable);
        });
        lootcollector.getProblems().forEach((s, s1) -> {
            LootTableRegistry.LOGGER.warn("Found validation problem in {}: {}", s, s1);
        });
        this.tables = immutablemap;
    }

    public static void validate(LootCollector lootcollector, MinecraftKey minecraftkey, LootTable loottable) {
        loottable.validate(lootcollector.setParams(loottable.getParamSet()).enterTable("{" + minecraftkey + "}", minecraftkey));
    }

    public static JsonElement serialize(LootTable loottable) {
        return LootTableRegistry.GSON.toJsonTree(loottable);
    }

    public Set<MinecraftKey> getIds() {
        return this.tables.keySet();
    }
}
