package net.minecraft.world.level.storage.loot.functions;

import java.util.function.BiFunction;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class LootItemFunctions {

    public static final BiFunction<ItemStack, LootTableInfo, ItemStack> IDENTITY = (itemstack, loottableinfo) -> {
        return itemstack;
    };
    public static final LootItemFunctionType SET_COUNT = register("set_count", new LootItemFunctionSetCount.a());
    public static final LootItemFunctionType ENCHANT_WITH_LEVELS = register("enchant_with_levels", new LootEnchantLevel.b());
    public static final LootItemFunctionType ENCHANT_RANDOMLY = register("enchant_randomly", new LootItemFunctionEnchant.b());
    public static final LootItemFunctionType SET_ENCHANTMENTS = register("set_enchantments", new SetEnchantmentsFunction.b());
    public static final LootItemFunctionType SET_NBT = register("set_nbt", new LootItemFunctionSetTag.a());
    public static final LootItemFunctionType FURNACE_SMELT = register("furnace_smelt", new LootItemFunctionSmelt.a());
    public static final LootItemFunctionType LOOTING_ENCHANT = register("looting_enchant", new LootEnchantFunction.b());
    public static final LootItemFunctionType SET_DAMAGE = register("set_damage", new LootItemFunctionSetDamage.a());
    public static final LootItemFunctionType SET_ATTRIBUTES = register("set_attributes", new LootItemFunctionSetAttribute.d());
    public static final LootItemFunctionType SET_NAME = register("set_name", new LootItemFunctionSetName.a());
    public static final LootItemFunctionType EXPLORATION_MAP = register("exploration_map", new LootItemFunctionExplorationMap.b());
    public static final LootItemFunctionType SET_STEW_EFFECT = register("set_stew_effect", new LootItemFunctionSetStewEffect.b());
    public static final LootItemFunctionType COPY_NAME = register("copy_name", new LootItemFunctionCopyName.b());
    public static final LootItemFunctionType SET_CONTENTS = register("set_contents", new LootItemFunctionSetContents.b());
    public static final LootItemFunctionType LIMIT_COUNT = register("limit_count", new LootItemFunctionLimitCount.a());
    public static final LootItemFunctionType APPLY_BONUS = register("apply_bonus", new LootItemFunctionApplyBonus.e());
    public static final LootItemFunctionType SET_LOOT_TABLE = register("set_loot_table", new LootItemFunctionSetTable.a());
    public static final LootItemFunctionType EXPLOSION_DECAY = register("explosion_decay", new LootItemFunctionExplosionDecay.a());
    public static final LootItemFunctionType SET_LORE = register("set_lore", new LootItemFunctionSetLore.b());
    public static final LootItemFunctionType FILL_PLAYER_HEAD = register("fill_player_head", new LootItemFunctionFillPlayerHead.a());
    public static final LootItemFunctionType COPY_NBT = register("copy_nbt", new LootItemFunctionCopyNBT.d());
    public static final LootItemFunctionType COPY_STATE = register("copy_state", new LootItemFunctionCopyState.b());
    public static final LootItemFunctionType SET_BANNER_PATTERN = register("set_banner_pattern", new SetBannerPatternFunction.b());
    public static final LootItemFunctionType SET_POTION = register("set_potion", new SetPotionFunction.a());

    public LootItemFunctions() {}

    private static LootItemFunctionType register(String s, LootSerializer<? extends LootItemFunction> lootserializer) {
        return (LootItemFunctionType) IRegistry.register(IRegistry.LOOT_FUNCTION_TYPE, new MinecraftKey(s), new LootItemFunctionType(lootserializer));
    }

    public static Object createGsonAdapter() {
        return JsonRegistry.builder(IRegistry.LOOT_FUNCTION_TYPE, "function", "function", LootItemFunction::getType).build();
    }

    public static BiFunction<ItemStack, LootTableInfo, ItemStack> compose(BiFunction<ItemStack, LootTableInfo, ItemStack>[] abifunction) {
        switch (abifunction.length) {
            case 0:
                return LootItemFunctions.IDENTITY;
            case 1:
                return abifunction[0];
            case 2:
                BiFunction<ItemStack, LootTableInfo, ItemStack> bifunction = abifunction[0];
                BiFunction<ItemStack, LootTableInfo, ItemStack> bifunction1 = abifunction[1];

                return (itemstack, loottableinfo) -> {
                    return (ItemStack) bifunction1.apply((ItemStack) bifunction.apply(itemstack, loottableinfo), loottableinfo);
                };
            default:
                return (itemstack, loottableinfo) -> {
                    BiFunction[] abifunction1 = abifunction;
                    int i = abifunction.length;

                    for (int j = 0; j < i; ++j) {
                        BiFunction<ItemStack, LootTableInfo, ItemStack> bifunction2 = abifunction1[j];

                        itemstack = (ItemStack) bifunction2.apply(itemstack, loottableinfo);
                    }

                    return itemstack;
                };
        }
    }
}
