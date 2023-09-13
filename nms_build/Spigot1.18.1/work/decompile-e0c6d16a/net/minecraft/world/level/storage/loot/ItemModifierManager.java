package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.server.packs.resources.ResourceDataJson;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemModifierManager extends ResourceDataJson {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = LootSerialization.createFunctionSerializer().create();
    private final LootPredicateManager predicateManager;
    private final LootTableRegistry lootTables;
    private Map<MinecraftKey, LootItemFunction> functions = ImmutableMap.of();

    public ItemModifierManager(LootPredicateManager lootpredicatemanager, LootTableRegistry loottableregistry) {
        super(ItemModifierManager.GSON, "item_modifiers");
        this.predicateManager = lootpredicatemanager;
        this.lootTables = loottableregistry;
    }

    @Nullable
    public LootItemFunction get(MinecraftKey minecraftkey) {
        return (LootItemFunction) this.functions.get(minecraftkey);
    }

    public LootItemFunction get(MinecraftKey minecraftkey, LootItemFunction lootitemfunction) {
        return (LootItemFunction) this.functions.getOrDefault(minecraftkey, lootitemfunction);
    }

    protected void apply(Map<MinecraftKey, JsonElement> map, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller) {
        Builder<MinecraftKey, LootItemFunction> builder = ImmutableMap.builder();

        map.forEach((minecraftkey, jsonelement) -> {
            try {
                if (jsonelement.isJsonArray()) {
                    LootItemFunction[] alootitemfunction = (LootItemFunction[]) ItemModifierManager.GSON.fromJson(jsonelement, LootItemFunction[].class);

                    builder.put(minecraftkey, new ItemModifierManager.a(alootitemfunction));
                } else {
                    LootItemFunction lootitemfunction = (LootItemFunction) ItemModifierManager.GSON.fromJson(jsonelement, LootItemFunction.class);

                    builder.put(minecraftkey, lootitemfunction);
                }
            } catch (Exception exception) {
                ItemModifierManager.LOGGER.error("Couldn't parse item modifier {}", minecraftkey, exception);
            }

        });
        Map<MinecraftKey, LootItemFunction> map1 = builder.build();
        LootContextParameterSet lootcontextparameterset = LootContextParameterSets.ALL_PARAMS;
        LootPredicateManager lootpredicatemanager = this.predicateManager;

        Objects.requireNonNull(this.predicateManager);
        Function function = lootpredicatemanager::get;
        LootTableRegistry loottableregistry = this.lootTables;

        Objects.requireNonNull(this.lootTables);
        LootCollector lootcollector = new LootCollector(lootcontextparameterset, function, loottableregistry::get);

        map1.forEach((minecraftkey, lootitemfunction) -> {
            lootitemfunction.validate(lootcollector);
        });
        lootcollector.getProblems().forEach((s, s1) -> {
            ItemModifierManager.LOGGER.warn("Found item modifier validation problem in {}: {}", s, s1);
        });
        this.functions = map1;
    }

    public Set<MinecraftKey> getKeys() {
        return Collections.unmodifiableSet(this.functions.keySet());
    }

    private static class a implements LootItemFunction {

        protected final LootItemFunction[] functions;
        private final BiFunction<ItemStack, LootTableInfo, ItemStack> compositeFunction;

        public a(LootItemFunction[] alootitemfunction) {
            this.functions = alootitemfunction;
            this.compositeFunction = LootItemFunctions.compose(alootitemfunction);
        }

        public ItemStack apply(ItemStack itemstack, LootTableInfo loottableinfo) {
            return (ItemStack) this.compositeFunction.apply(itemstack, loottableinfo);
        }

        @Override
        public LootItemFunctionType getType() {
            throw new UnsupportedOperationException();
        }
    }
}
