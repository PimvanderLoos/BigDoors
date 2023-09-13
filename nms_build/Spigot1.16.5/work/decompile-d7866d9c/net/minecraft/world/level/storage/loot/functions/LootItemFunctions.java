package net.minecraft.world.level.storage.loot.functions;

import java.util.function.BiFunction;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class LootItemFunctions {

    public static final BiFunction<ItemStack, LootTableInfo, ItemStack> a = (itemstack, loottableinfo) -> {
        return itemstack;
    };
    public static final LootItemFunctionType b = a("set_count", (LootSerializer) (new LootItemFunctionSetCount.a()));
    public static final LootItemFunctionType c = a("enchant_with_levels", (LootSerializer) (new LootEnchantLevel.b()));
    public static final LootItemFunctionType d = a("enchant_randomly", (LootSerializer) (new LootItemFunctionEnchant.b()));
    public static final LootItemFunctionType e = a("set_nbt", (LootSerializer) (new LootItemFunctionSetTag.a()));
    public static final LootItemFunctionType f = a("furnace_smelt", (LootSerializer) (new LootItemFunctionSmelt.a()));
    public static final LootItemFunctionType g = a("looting_enchant", (LootSerializer) (new LootEnchantFunction.b()));
    public static final LootItemFunctionType h = a("set_damage", (LootSerializer) (new LootItemFunctionSetDamage.a()));
    public static final LootItemFunctionType i = a("set_attributes", (LootSerializer) (new LootItemFunctionSetAttribute.d()));
    public static final LootItemFunctionType j = a("set_name", (LootSerializer) (new LootItemFunctionSetName.a()));
    public static final LootItemFunctionType k = a("exploration_map", (LootSerializer) (new LootItemFunctionExplorationMap.b()));
    public static final LootItemFunctionType l = a("set_stew_effect", (LootSerializer) (new LootItemFunctionSetStewEffect.b()));
    public static final LootItemFunctionType m = a("copy_name", (LootSerializer) (new LootItemFunctionCopyName.b()));
    public static final LootItemFunctionType n = a("set_contents", (LootSerializer) (new LootItemFunctionSetContents.b()));
    public static final LootItemFunctionType o = a("limit_count", (LootSerializer) (new LootItemFunctionLimitCount.a()));
    public static final LootItemFunctionType p = a("apply_bonus", (LootSerializer) (new LootItemFunctionApplyBonus.e()));
    public static final LootItemFunctionType q = a("set_loot_table", (LootSerializer) (new LootItemFunctionSetTable.a()));
    public static final LootItemFunctionType r = a("explosion_decay", (LootSerializer) (new LootItemFunctionExplosionDecay.a()));
    public static final LootItemFunctionType s = a("set_lore", (LootSerializer) (new LootItemFunctionSetLore.b()));
    public static final LootItemFunctionType t = a("fill_player_head", (LootSerializer) (new LootItemFunctionFillPlayerHead.a()));
    public static final LootItemFunctionType u = a("copy_nbt", (LootSerializer) (new LootItemFunctionCopyNBT.e()));
    public static final LootItemFunctionType v = a("copy_state", (LootSerializer) (new LootItemFunctionCopyState.b()));

    private static LootItemFunctionType a(String s, LootSerializer<? extends LootItemFunction> lootserializer) {
        return (LootItemFunctionType) IRegistry.a(IRegistry.ap, new MinecraftKey(s), (Object) (new LootItemFunctionType(lootserializer)));
    }

    public static Object a() {
        return JsonRegistry.a(IRegistry.ap, "function", "function", LootItemFunction::b).a();
    }

    public static BiFunction<ItemStack, LootTableInfo, ItemStack> a(BiFunction<ItemStack, LootTableInfo, ItemStack>[] abifunction) {
        switch (abifunction.length) {
            case 0:
                return LootItemFunctions.a;
            case 1:
                return abifunction[0];
            case 2:
                BiFunction<ItemStack, LootTableInfo, ItemStack> bifunction = abifunction[0];
                BiFunction<ItemStack, LootTableInfo, ItemStack> bifunction1 = abifunction[1];

                return (itemstack, loottableinfo) -> {
                    return (ItemStack) bifunction1.apply(bifunction.apply(itemstack, loottableinfo), loottableinfo);
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
