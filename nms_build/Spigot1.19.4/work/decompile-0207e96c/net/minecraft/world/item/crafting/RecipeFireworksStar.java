package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDye;
import net.minecraft.world.item.ItemFireworks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class RecipeFireworksStar extends IRecipeComplex {

    private static final RecipeItemStack SHAPE_INGREDIENT = RecipeItemStack.of(Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD, Items.PIGLIN_HEAD);
    private static final RecipeItemStack TRAIL_INGREDIENT = RecipeItemStack.of(Items.DIAMOND);
    private static final RecipeItemStack FLICKER_INGREDIENT = RecipeItemStack.of(Items.GLOWSTONE_DUST);
    private static final Map<Item, ItemFireworks.EffectType> SHAPE_BY_ITEM = (Map) SystemUtils.make(Maps.newHashMap(), (hashmap) -> {
        hashmap.put(Items.FIRE_CHARGE, ItemFireworks.EffectType.LARGE_BALL);
        hashmap.put(Items.FEATHER, ItemFireworks.EffectType.BURST);
        hashmap.put(Items.GOLD_NUGGET, ItemFireworks.EffectType.STAR);
        hashmap.put(Items.SKELETON_SKULL, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.WITHER_SKELETON_SKULL, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.CREEPER_HEAD, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.PLAYER_HEAD, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.DRAGON_HEAD, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.ZOMBIE_HEAD, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.PIGLIN_HEAD, ItemFireworks.EffectType.CREEPER);
    });
    private static final RecipeItemStack GUNPOWDER_INGREDIENT = RecipeItemStack.of(Items.GUNPOWDER);

    public RecipeFireworksStar(MinecraftKey minecraftkey, CraftingBookCategory craftingbookcategory) {
        super(minecraftkey, craftingbookcategory);
    }

    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;

        for (int i = 0; i < inventorycrafting.getContainerSize(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (!itemstack.isEmpty()) {
                if (RecipeFireworksStar.SHAPE_INGREDIENT.test(itemstack)) {
                    if (flag2) {
                        return false;
                    }

                    flag2 = true;
                } else if (RecipeFireworksStar.FLICKER_INGREDIENT.test(itemstack)) {
                    if (flag4) {
                        return false;
                    }

                    flag4 = true;
                } else if (RecipeFireworksStar.TRAIL_INGREDIENT.test(itemstack)) {
                    if (flag3) {
                        return false;
                    }

                    flag3 = true;
                } else if (RecipeFireworksStar.GUNPOWDER_INGREDIENT.test(itemstack)) {
                    if (flag) {
                        return false;
                    }

                    flag = true;
                } else {
                    if (!(itemstack.getItem() instanceof ItemDye)) {
                        return false;
                    }

                    flag1 = true;
                }
            }
        }

        return flag && flag1;
    }

    public ItemStack assemble(InventoryCrafting inventorycrafting, IRegistryCustom iregistrycustom) {
        ItemStack itemstack = new ItemStack(Items.FIREWORK_STAR);
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTagElement("Explosion");
        ItemFireworks.EffectType itemfireworks_effecttype = ItemFireworks.EffectType.SMALL_BALL;
        List<Integer> list = Lists.newArrayList();

        for (int i = 0; i < inventorycrafting.getContainerSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (!itemstack1.isEmpty()) {
                if (RecipeFireworksStar.SHAPE_INGREDIENT.test(itemstack1)) {
                    itemfireworks_effecttype = (ItemFireworks.EffectType) RecipeFireworksStar.SHAPE_BY_ITEM.get(itemstack1.getItem());
                } else if (RecipeFireworksStar.FLICKER_INGREDIENT.test(itemstack1)) {
                    nbttagcompound.putBoolean("Flicker", true);
                } else if (RecipeFireworksStar.TRAIL_INGREDIENT.test(itemstack1)) {
                    nbttagcompound.putBoolean("Trail", true);
                } else if (itemstack1.getItem() instanceof ItemDye) {
                    list.add(((ItemDye) itemstack1.getItem()).getDyeColor().getFireworkColor());
                }
            }
        }

        nbttagcompound.putIntArray("Colors", (List) list);
        nbttagcompound.putByte("Type", (byte) itemfireworks_effecttype.getId());
        return itemstack;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public ItemStack getResultItem(IRegistryCustom iregistrycustom) {
        return new ItemStack(Items.FIREWORK_STAR);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR;
    }
}
