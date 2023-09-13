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
    public static final LootItemFunctionType SET_COUNT = a("set_count", (LootSerializer) (new LootItemFunctionSetCount.a()));
    public static final LootItemFunctionType ENCHANT_WITH_LEVELS = a("enchant_with_levels", (LootSerializer) (new LootEnchantLevel.b()));
    public static final LootItemFunctionType ENCHANT_RANDOMLY = a("enchant_randomly", (LootSerializer) (new LootItemFunctionEnchant.b()));
    public static final LootItemFunctionType SET_ENCHANTMENTS = a("set_enchantments", (LootSerializer) (new SetEnchantmentsFunction.b()));
    public static final LootItemFunctionType SET_NBT = a("set_nbt", (LootSerializer) (new LootItemFunctionSetTag.a()));
    public static final LootItemFunctionType FURNACE_SMELT = a("furnace_smelt", (LootSerializer) (new LootItemFunctionSmelt.a()));
    public static final LootItemFunctionType LOOTING_ENCHANT = a("looting_enchant", (LootSerializer) (new LootEnchantFunction.b()));
    public static final LootItemFunctionType SET_DAMAGE = a("set_damage", (LootSerializer) (new LootItemFunctionSetDamage.a()));
    public static final LootItemFunctionType SET_ATTRIBUTES = a("set_attributes", (LootSerializer) (new LootItemFunctionSetAttribute.d()));
    public static final LootItemFunctionType SET_NAME = a("set_name", (LootSerializer) (new LootItemFunctionSetName.a()));
    public static final LootItemFunctionType EXPLORATION_MAP = a("exploration_map", (LootSerializer) (new LootItemFunctionExplorationMap.b()));
    public static final LootItemFunctionType SET_STEW_EFFECT = a("set_stew_effect", (LootSerializer) (new LootItemFunctionSetStewEffect.b()));
    public static final LootItemFunctionType COPY_NAME = a("copy_name", (LootSerializer) (new LootItemFunctionCopyName.b()));
    public static final LootItemFunctionType SET_CONTENTS = a("set_contents", (LootSerializer) (new LootItemFunctionSetContents.b()));
    public static final LootItemFunctionType LIMIT_COUNT = a("limit_count", (LootSerializer) (new LootItemFunctionLimitCount.a()));
    public static final LootItemFunctionType APPLY_BONUS = a("apply_bonus", (LootSerializer) (new LootItemFunctionApplyBonus.e()));
    public static final LootItemFunctionType SET_LOOT_TABLE = a("set_loot_table", (LootSerializer) (new LootItemFunctionSetTable.a()));
    public static final LootItemFunctionType EXPLOSION_DECAY = a("explosion_decay", (LootSerializer) (new LootItemFunctionExplosionDecay.a()));
    public static final LootItemFunctionType SET_LORE = a("set_lore", (LootSerializer) (new LootItemFunctionSetLore.b()));
    public static final LootItemFunctionType FILL_PLAYER_HEAD = a("fill_player_head", (LootSerializer) (new LootItemFunctionFillPlayerHead.a()));
    public static final LootItemFunctionType COPY_NBT = a("copy_nbt", (LootSerializer) (new LootItemFunctionCopyNBT.d()));
    public static final LootItemFunctionType COPY_STATE = a("copy_state", (LootSerializer) (new LootItemFunctionCopyState.b()));
    public static final LootItemFunctionType SET_BANNER_PATTERN = a("set_banner_pattern", (LootSerializer) (new SetBannerPatternFunction.b()));

    public LootItemFunctions() {}

    private static LootItemFunctionType a(String s, LootSerializer<? extends LootItemFunction> lootserializer) {
        return (LootItemFunctionType) IRegistry.a(IRegistry.LOOT_FUNCTION_TYPE, new MinecraftKey(s), (Object) (new LootItemFunctionType(lootserializer)));
    }

    public static Object a() {
        return JsonRegistry.a(IRegistry.LOOT_FUNCTION_TYPE, "function", "function", LootItemFunction::a).a();
    }

    public static BiFunction<ItemStack, LootTableInfo, ItemStack> a(BiFunction<ItemStack, LootTableInfo, ItemStack>[] abifunction) {
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
