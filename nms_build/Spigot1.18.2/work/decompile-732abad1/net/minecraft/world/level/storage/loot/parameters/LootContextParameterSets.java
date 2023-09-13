package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;

public class LootContextParameterSets {

    private static final BiMap<MinecraftKey, LootContextParameterSet> REGISTRY = HashBiMap.create();
    public static final LootContextParameterSet EMPTY = register("empty", (lootcontextparameterset_builder) -> {
    });
    public static final LootContextParameterSet CHEST = register("chest", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.required(LootContextParameters.ORIGIN).optional(LootContextParameters.THIS_ENTITY);
    });
    public static final LootContextParameterSet COMMAND = register("command", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.required(LootContextParameters.ORIGIN).optional(LootContextParameters.THIS_ENTITY);
    });
    public static final LootContextParameterSet SELECTOR = register("selector", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.required(LootContextParameters.ORIGIN).required(LootContextParameters.THIS_ENTITY);
    });
    public static final LootContextParameterSet FISHING = register("fishing", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.required(LootContextParameters.ORIGIN).required(LootContextParameters.TOOL).optional(LootContextParameters.THIS_ENTITY);
    });
    public static final LootContextParameterSet ENTITY = register("entity", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.required(LootContextParameters.THIS_ENTITY).required(LootContextParameters.ORIGIN).required(LootContextParameters.DAMAGE_SOURCE).optional(LootContextParameters.KILLER_ENTITY).optional(LootContextParameters.DIRECT_KILLER_ENTITY).optional(LootContextParameters.LAST_DAMAGE_PLAYER);
    });
    public static final LootContextParameterSet GIFT = register("gift", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.required(LootContextParameters.ORIGIN).required(LootContextParameters.THIS_ENTITY);
    });
    public static final LootContextParameterSet PIGLIN_BARTER = register("barter", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.required(LootContextParameters.THIS_ENTITY);
    });
    public static final LootContextParameterSet ADVANCEMENT_REWARD = register("advancement_reward", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.required(LootContextParameters.THIS_ENTITY).required(LootContextParameters.ORIGIN);
    });
    public static final LootContextParameterSet ADVANCEMENT_ENTITY = register("advancement_entity", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.required(LootContextParameters.THIS_ENTITY).required(LootContextParameters.ORIGIN);
    });
    public static final LootContextParameterSet ALL_PARAMS = register("generic", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.required(LootContextParameters.THIS_ENTITY).required(LootContextParameters.LAST_DAMAGE_PLAYER).required(LootContextParameters.DAMAGE_SOURCE).required(LootContextParameters.KILLER_ENTITY).required(LootContextParameters.DIRECT_KILLER_ENTITY).required(LootContextParameters.ORIGIN).required(LootContextParameters.BLOCK_STATE).required(LootContextParameters.BLOCK_ENTITY).required(LootContextParameters.TOOL).required(LootContextParameters.EXPLOSION_RADIUS);
    });
    public static final LootContextParameterSet BLOCK = register("block", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.required(LootContextParameters.BLOCK_STATE).required(LootContextParameters.ORIGIN).required(LootContextParameters.TOOL).optional(LootContextParameters.THIS_ENTITY).optional(LootContextParameters.BLOCK_ENTITY).optional(LootContextParameters.EXPLOSION_RADIUS);
    });

    public LootContextParameterSets() {}

    private static LootContextParameterSet register(String s, Consumer<LootContextParameterSet.Builder> consumer) {
        LootContextParameterSet.Builder lootcontextparameterset_builder = new LootContextParameterSet.Builder();

        consumer.accept(lootcontextparameterset_builder);
        LootContextParameterSet lootcontextparameterset = lootcontextparameterset_builder.build();
        MinecraftKey minecraftkey = new MinecraftKey(s);
        LootContextParameterSet lootcontextparameterset1 = (LootContextParameterSet) LootContextParameterSets.REGISTRY.put(minecraftkey, lootcontextparameterset);

        if (lootcontextparameterset1 != null) {
            throw new IllegalStateException("Loot table parameter set " + minecraftkey + " is already registered");
        } else {
            return lootcontextparameterset;
        }
    }

    @Nullable
    public static LootContextParameterSet get(MinecraftKey minecraftkey) {
        return (LootContextParameterSet) LootContextParameterSets.REGISTRY.get(minecraftkey);
    }

    @Nullable
    public static MinecraftKey getKey(LootContextParameterSet lootcontextparameterset) {
        return (MinecraftKey) LootContextParameterSets.REGISTRY.inverse().get(lootcontextparameterset);
    }
}
