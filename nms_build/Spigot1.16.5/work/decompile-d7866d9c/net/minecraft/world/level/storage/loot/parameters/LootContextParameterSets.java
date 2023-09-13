package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;

public class LootContextParameterSets {

    private static final BiMap<MinecraftKey, LootContextParameterSet> m = HashBiMap.create();
    public static final LootContextParameterSet EMPTY = a("empty", (lootcontextparameterset_builder) -> {
    });
    public static final LootContextParameterSet CHEST = a("chest", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.addRequired(LootContextParameters.ORIGIN).addOptional(LootContextParameters.THIS_ENTITY);
    });
    public static final LootContextParameterSet COMMAND = a("command", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.addRequired(LootContextParameters.ORIGIN).addOptional(LootContextParameters.THIS_ENTITY);
    });
    public static final LootContextParameterSet SELECTOR = a("selector", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.addRequired(LootContextParameters.ORIGIN).addRequired(LootContextParameters.THIS_ENTITY);
    });
    public static final LootContextParameterSet FISHING = a("fishing", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.addRequired(LootContextParameters.ORIGIN).addRequired(LootContextParameters.TOOL).addOptional(LootContextParameters.THIS_ENTITY);
    });
    public static final LootContextParameterSet ENTITY = a("entity", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.addRequired(LootContextParameters.THIS_ENTITY).addRequired(LootContextParameters.ORIGIN).addRequired(LootContextParameters.DAMAGE_SOURCE).addOptional(LootContextParameters.KILLER_ENTITY).addOptional(LootContextParameters.DIRECT_KILLER_ENTITY).addOptional(LootContextParameters.LAST_DAMAGE_PLAYER);
    });
    public static final LootContextParameterSet GIFT = a("gift", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.addRequired(LootContextParameters.ORIGIN).addRequired(LootContextParameters.THIS_ENTITY);
    });
    public static final LootContextParameterSet BARTER = a("barter", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.addRequired(LootContextParameters.THIS_ENTITY);
    });
    public static final LootContextParameterSet ADVANCEMENT_REWARD = a("advancement_reward", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.addRequired(LootContextParameters.THIS_ENTITY).addRequired(LootContextParameters.ORIGIN);
    });
    public static final LootContextParameterSet j = a("advancement_entity", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.addRequired(LootContextParameters.THIS_ENTITY).addRequired(LootContextParameters.ORIGIN);
    });
    public static final LootContextParameterSet GENERIC = a("generic", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.addRequired(LootContextParameters.THIS_ENTITY).addRequired(LootContextParameters.LAST_DAMAGE_PLAYER).addRequired(LootContextParameters.DAMAGE_SOURCE).addRequired(LootContextParameters.KILLER_ENTITY).addRequired(LootContextParameters.DIRECT_KILLER_ENTITY).addRequired(LootContextParameters.ORIGIN).addRequired(LootContextParameters.BLOCK_STATE).addRequired(LootContextParameters.BLOCK_ENTITY).addRequired(LootContextParameters.TOOL).addRequired(LootContextParameters.EXPLOSION_RADIUS);
    });
    public static final LootContextParameterSet BLOCK = a("block", (lootcontextparameterset_builder) -> {
        lootcontextparameterset_builder.addRequired(LootContextParameters.BLOCK_STATE).addRequired(LootContextParameters.ORIGIN).addRequired(LootContextParameters.TOOL).addOptional(LootContextParameters.THIS_ENTITY).addOptional(LootContextParameters.BLOCK_ENTITY).addOptional(LootContextParameters.EXPLOSION_RADIUS);
    });

    private static LootContextParameterSet a(String s, Consumer<LootContextParameterSet.Builder> consumer) {
        LootContextParameterSet.Builder lootcontextparameterset_builder = new LootContextParameterSet.Builder();

        consumer.accept(lootcontextparameterset_builder);
        LootContextParameterSet lootcontextparameterset = lootcontextparameterset_builder.build();
        MinecraftKey minecraftkey = new MinecraftKey(s);
        LootContextParameterSet lootcontextparameterset1 = (LootContextParameterSet) LootContextParameterSets.m.put(minecraftkey, lootcontextparameterset);

        if (lootcontextparameterset1 != null) {
            throw new IllegalStateException("Loot table parameter set " + minecraftkey + " is already registered");
        } else {
            return lootcontextparameterset;
        }
    }

    @Nullable
    public static LootContextParameterSet a(MinecraftKey minecraftkey) {
        return (LootContextParameterSet) LootContextParameterSets.m.get(minecraftkey);
    }

    @Nullable
    public static MinecraftKey a(LootContextParameterSet lootcontextparameterset) {
        return (MinecraftKey) LootContextParameterSets.m.inverse().get(lootcontextparameterset);
    }
}
